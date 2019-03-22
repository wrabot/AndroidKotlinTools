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

package com.wrabot.tools

import androidx.lifecycle.MutableLiveData

/**
 * Detects application background/foreground.
 *
 * registerActivityLifecycleCallbacks(ForegroundManager)
 */
@Suppress("unused")
object ForegroundManager {
    private val detector = ForegroundDetector { foreground.postValue(it) }

    /**
     * Observe to receive background/foreground events.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val foreground = MutableLiveData<Boolean>()
}
