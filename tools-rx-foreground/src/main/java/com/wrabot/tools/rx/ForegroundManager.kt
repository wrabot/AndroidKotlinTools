package com.wrabot.tools.rx

import android.app.Activity
import android.app.Application
import android.os.Bundle
import io.reactivex.subjects.BehaviorSubject

@Suppress("unused")
object ForegroundManager : Application.ActivityLifecycleCallbacks {
    @Suppress("MemberVisibilityCanBePrivate")
    val foreground = BehaviorSubject.createDefault(false)!!

    private var startedActivityCount = 0

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit

    override fun onActivityStarted(activity: Activity) {
        if (startedActivityCount++ == 0)
            foreground.onNext(true)
    }

    override fun onActivityResumed(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivityStopped(activity: Activity) {
        if (--startedActivityCount == 0)
            foreground.onNext(false)
    }

    override fun onActivityDestroyed(activity: Activity) = Unit
}
