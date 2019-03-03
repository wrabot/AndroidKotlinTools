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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Detects application background/foreground.
 *
 * registerActivityLifecycleCallbacks(ForegroundManager)
 */
@Suppress("unused")
object ForegroundManager : Application.ActivityLifecycleCallbacks {
    private val isForeground = MutableLiveData<Boolean>()
    private var startedActivityCount = 0

    /**
     * Subscribe to this subject to receive background/foreground events.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val foreground: LiveData<Boolean> = isForeground

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit

    override fun onActivityStarted(activity: Activity) {
        if (startedActivityCount++ == 0)
            isForeground.postValue(true)
    }

    override fun onActivityResumed(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivityStopped(activity: Activity) {
        if (--startedActivityCount == 0)
            isForeground.postValue(false)
    }

    override fun onActivityDestroyed(activity: Activity) = Unit
}
