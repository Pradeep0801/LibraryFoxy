package com.symb.foxpandasdk.Interface

import android.app.Activity
import android.os.Bundle


interface ActivityLifecycleCallbacks {
    fun onActivityCreated(activity: Activity, savedInstanceState: Bundle)
    fun onActivityStarted(activity: Activity)
    fun onActivityResumed(activity: Activity)
    fun onActivityPaused(activity: Activity)
    fun onActivityStopped(activity: Activity)
    fun onActivitySaveInstanceState(activity: Activity, outState: Bundle)
    fun onActivityDestroyed(activity: Activity)
}