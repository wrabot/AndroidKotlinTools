/**
 * Copyright (c) 2017-present, Wilfrid Rabot.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package com.wrabot.tools.rx


import android.app.DownloadManager
import android.database.Cursor
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit

/**
 * Max progress value.
 */
const val progressMax = 10000

/**
 * Indeterminate progress value.
 */
const val progressIndeterminate = -1

/**
 * RX flowable which observes several downloads.
 *
 * If download succeeds flowable completes
 * If download fails, flowable fails
 * Otherwise, a global progress (progressIndeterminate or value in 0..progressMax) is periodically emitted
 * NB: progress is emitted every second : faster is useless because DownloadManager data base is not updated frequently
 *
 * @param downloadIds the download ids returned by DownloadManager.enqueue
 */
@Suppress("unused")
fun DownloadManager.progress(vararg downloadIds: Long) = Flowable.interval(1, TimeUnit.SECONDS).map {
    query(DownloadManager.Query().setFilterById(*downloadIds)).use { cursor -> if (cursor.count < downloadIds.size) progressIndeterminate else cursor.progress() }
}.takeWhile { it <= progressMax }

private fun Cursor.progress(): Int {
    var successCount = 0
    var globalSize = 0L
    var globalDownloaded = 0L
    while (moveToNext()) {
        val size = getLong(getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
        if (size == -1L)
            return progressIndeterminate
        when (getInt(getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            DownloadManager.STATUS_SUCCESSFUL -> successCount++
            DownloadManager.STATUS_FAILED -> throw DownloadException(getInt(getColumnIndex(DownloadManager.COLUMN_REASON)))
        }
        globalSize += size
        globalDownloaded += getLong(getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
    }
    if (successCount == count)
        return progressMax + 1 // the flowable will complete
    if (globalSize == 0L) // if files are empty => it should succeed immediately but ...
        return progressIndeterminate
    return (progressMax * globalDownloaded / globalSize).toInt()
}

