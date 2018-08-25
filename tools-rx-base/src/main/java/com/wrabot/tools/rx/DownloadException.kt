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

/**
 * Exception for DownloadManager when download fails.
 * Used by DownloadManager.progress
 *
 * @param reasonCode the reason of failure
 */
@Suppress("MemberVisibilityCanBePrivate")
class DownloadException(val reasonCode: Int) : Exception() {
    override val message: String?
        get() = "Download error: " + when (reasonCode) {
            DownloadManager.ERROR_CANNOT_RESUME -> "CANNOT_RESUME"
            DownloadManager.ERROR_DEVICE_NOT_FOUND -> "DEVICE_NOT_FOUND"
            DownloadManager.ERROR_FILE_ALREADY_EXISTS -> "FILE_ALREADY_EXISTS"
            DownloadManager.ERROR_FILE_ERROR -> "FILE_ERROR"
            DownloadManager.ERROR_HTTP_DATA_ERROR -> "HTTP_DATA_ERROR"
            DownloadManager.ERROR_INSUFFICIENT_SPACE -> "INSUFFICIENT_SPACE"
            DownloadManager.ERROR_TOO_MANY_REDIRECTS -> "TOO_MANY_REDIRECTS"
            DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> "UNHANDLED_HTTP_CODE"
            else -> "Unexpected code: $reasonCode"
        }
}
