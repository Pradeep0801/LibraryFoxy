package com.sdk.foxpanda.main

import android.annotation.SuppressLint
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.ConnectivityManager
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.util.Log
import androidx.work.*
import com.sdk.foxpanda.MediaManager.FoxListening
import com.sdk.foxpanda.PandaPermissions.PandaShowLocationToFox
import com.sdk.foxpanda.WorkManager.FoxWorkManager
import com.sdk.foxpanda.WorkManager.LocationWorkManager
import com.sdk.foxpanda.applications.FoxApplication
import com.sdk.foxpanda.constants.Constants
import com.sdk.foxpanda.data.dbHelper.DBHelper
import com.sdk.foxpanda.data.models.AppUsageStats
import com.sdk.foxpanda.data.models.ListOfInstalledApp
import com.sdk.foxpanda.data.models.NotificationModel
import com.sdk.foxpanda.services.ConnectivityReceiver
import com.sdk.foxpanda.utils.CommonUtils
import com.sdk.foxpanda.utils.NetworkUtil
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList



@SuppressLint("Registered")
class FoxPanda {
    enum class LogLevel {
        BASIC,
        BODY,
        HEADER,
        NONE
    }

    companion object {
        private const val TAG = "DBElements"
        val foxListening = FoxListening()
        val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
        val REQUEST_APP_USAGE_PERMISSIONS = 2
        var handler = Handler()
        lateinit var checkSettingOn : Runnable


        fun initialize(context: Context,foxPlatFormID : String) {
            FoxApplication.instance.deviceID = android.provider.Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)
            FoxApplication.instance.FoxPlatformID = foxPlatFormID
            NetworkUtil.initRetrofit(true, Constants.DEFAULT_LOG_LEVEL, context)


            val myWorkBuilder = PeriodicWorkRequestBuilder<LocationWorkManager>(4, TimeUnit.HOURS)
            val myWork = myWorkBuilder.build()
            WorkManager.getInstance().enqueue(myWork)



            if(EasyPermissions.hasPermissions(context, *PandaShowLocationToFox.STORAGE) && !CommonUtils.pandaHasUsagePermission(context)){
                        CommonUtils.pandaAskForUsagePermission(context)
                    }
            else{
                val myWorkBuilder1 = PeriodicWorkRequestBuilder<FoxWorkManager>(5, TimeUnit.HOURS)
                val myWork1 = myWorkBuilder1.build()
                WorkManager.getInstance().enqueue(myWork1)
            }






//            handler.postDelayed({
//                Log.d(TAG, "run: 1")
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
//                {
//                    Log.d(TAG, "run: 2")
//                    handler.removeCallbacksAndMessages(null)
//                }
//                if (foxListening.isAccessGranted(context))
//                {
//                    Log.d(TAG, "run: 3")
//                    //You have the permission, re-launch MainActivity
//                    Log.d(TAG, "run: 4")
//                    retrieveStats(context)
//                    handler.removeCallbacksAndMessages(null)
//
//                }
//            }, 10000)

//             checkSettingOn = object:Runnable {
//                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//                public override//@TargetApi(23)
//                fun run() {
//                    Log.d(TAG, "run: 1")
//                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
//                    {
//                        Log.d(TAG, "run: 2")
//                        return
//                    }
//                    if (foxListening.isAccessGranted(context))
//                    {
//                        Log.d(TAG, "run: 3")
//                        //You have the permission, re-launch MainActivity
//                        Log.d(TAG, "run: 4")
//                        retrieveStats(context)
//
//
//                    }
//                    handler.postDelayed(this, 2000)
//                }
//            }
            // check for permission used by SDK
//            if(!foxListening.isAccessGranted(context)) {
//                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
//                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
//                context.startActivity(intent)
//            }
//
//


            val db = DBHelper(context)
            if (db.getIsInfoUpdated() == 1) {
                print("token already updated")
            } else {
                val token = db.getToken()
                val isConnected = FoxApplication.instance.isFoxConnectedToPanda
                if (isConnected && token != "") {
                    CommonUtils.registerTokenToServer(db.getToken(), context)
                }

            }
            if (db.getUserActivityTime().size > 0) {
                CommonUtils.updateUserActivityTimeToServer(db.getUserActivityTime(), true, context)
            }

            val classes = CommonUtils.getClassesOfPackage(context)
            val dbClasses = db.getAllInternalClasses()
            if (dbClasses.size > 0) {
                if (dbClasses.size != classes.size) {
                    //update on server
                    FPLogger(TAG, "sizes don't match")
                    updateClassesToServer(context, classes)
                } else {
                    for (i in classes.indices) {
                        if (!classes[i].equals(dbClasses[i])) {
                            //update on server
                            FPLogger(TAG, "classes don't match")
                            updateClassesToServer(context, classes)
                        }
                    }
                }
            } else {
                FPLogger(TAG, "first time insertion")
                updateClassesToServer(context, classes)
            }


            // START SERVICE FOR CHECKING FOREGROUND TIME OF APPLICATION
            context.registerReceiver(ConnectivityReceiver(),
                    IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))







        }

         fun getNotificationList(context: Context): List<NotificationModel> {
            var notificationModel = ArrayList<NotificationModel>()
            val db = DBHelper(context)
            notificationModel = db.getNotification()
            return notificationModel
        }







        //update classes to the server
        private fun updateClassesToServer(context: Context, classes: Array<String>) {
            val db = DBHelper(context)
            db.deleteAllClass()
            db.deleteAllInternalClass()
            classes.forEach {
                db.saveInternalClassNameIntoDB(it)
                if (!it.contains("$") && !it.contains("foxpandasdk"))
                    db.saveClassNameIntoDB(it)
            }
            val cls = db.getAllClasses()
            cls.forEach {
                FPLogger("clsName", it)
            }
            val icls = db.getAllInternalClasses()
            icls.forEach {
                FPLogger("iclsName", it)
            }


        }

        private fun getLevelString(loglevel: LogLevel): String {
            if (loglevel == LogLevel.BASIC)
                return Constants.BASIC
            else if (loglevel == LogLevel.NONE)
                return Constants.NONE
            else if (loglevel == LogLevel.HEADER)
                return Constants.HEADER
            else
                return Constants.DEFAULT_LOG_LEVEL
        }

        internal fun FPLogger(tag: String, message: String) {
            Log.e(tag, message)
        }



    }




}
