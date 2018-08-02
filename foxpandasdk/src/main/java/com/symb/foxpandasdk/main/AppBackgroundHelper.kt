package com.symb.foxpandasdk.main

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.symb.foxpandasdk.applications.FoxApplication
import com.symb.foxpandasdk.data.dbHelper.DBHelper
import com.symb.foxpandasdk.data.models.UserActivityTimeModel
import com.symb.foxpandasdk.utils.CommonUtils
import java.util.concurrent.TimeUnit

class AppBackgroundHelper private constructor() : Application.ActivityLifecycleCallbacks {

    companion object {
        private val TAG = AppBackgroundHelper.javaClass.name

        var singleton: AppBackgroundHelper? = null
        var context : Context? = null

        fun init(application: Application,context1: Context) {
            if (singleton == null) {
                singleton = AppBackgroundHelper()
                context = context1
                application.registerActivityLifecycleCallbacks(singleton)
            } else {
                Log.v(TAG, "AppBackgroundHelper already initialized")
            }
        }
    }

    private var numberOfActivities = 0
    private var startTime = 0L

    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
        if (numberOfActivities == 0) {
            startTime = System.currentTimeMillis()
        }
        numberOfActivities++

    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
        numberOfActivities--
        if (numberOfActivities == 0) {
            val appUsedTime = System.currentTimeMillis() - startTime
            val timeString = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(appUsedTime),
                    TimeUnit.MILLISECONDS.toMinutes(appUsedTime) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(appUsedTime) % TimeUnit.MINUTES.toSeconds(1))

            val db = DBHelper(activity!!.applicationContext)
            if (FoxApplication.instance.isFoxConnectedToPanda) {
                var userActivityTimeModelList = ArrayList<UserActivityTimeModel>()
                var userActivityTimeModel = UserActivityTimeModel()
                userActivityTimeModel.in_time = startTime
                userActivityTimeModel.out_time = System.currentTimeMillis()

                userActivityTimeModelList.add(userActivityTimeModel)
                CommonUtils.updateUserActivityTimeToServer(userActivityTimeModelList,false, context!!)
            }
            else
            {
                db.saveUserActivityTime(startTime.toString()  ,System.currentTimeMillis().toString())
            }

        }
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {

    }


}