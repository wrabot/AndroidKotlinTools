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

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * Detects application background/foreground.
 *
 * registerActivityLifecycleCallbacks(ForegroundManager)
 */
@Suppress("unused")
class ForegroundDetector(val onChanged: (Boolean) -> Unit) : Application.ActivityLifecycleCallbacks {
    private var startedActivityCount = 0

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit

    override fun onActivityStarted(activity: Activity) {
        if (startedActivityCount++ == 0) {
            onChanged(true)
        }
    }

    override fun onActivityResumed(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivityStopped(activity: Activity) {
        if (--startedActivityCount == 0) {
            onChanged(false)
        }
    }

    override fun onActivityDestroyed(activity: Activity) = Unit
}