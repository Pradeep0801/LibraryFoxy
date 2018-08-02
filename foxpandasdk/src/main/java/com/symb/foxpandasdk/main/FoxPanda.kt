package com.symb.foxpandasdk.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.app.AppOpsManager.MODE_ALLOWED
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.provider.CalendarContract.Instances.BEGIN
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.util.SimpleArrayMap
import android.support.v7.app.AppCompatActivity
import android.util.Log
import androidx.work.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.RemoteMessage
import com.symb.foxpandasdk.MediaManager.FoxListening
import com.symb.foxpandasdk.R
import com.symb.foxpandasdk.WorkManager.FoxWorkManager
import com.symb.foxpandasdk.applications.FoxApplication
import com.symb.foxpandasdk.constants.Constants
import com.symb.foxpandasdk.data.dbHelper.DBHelper
import com.symb.foxpandasdk.data.models.AppPackageInfo
import com.symb.foxpandasdk.data.models.AppUsageStats
import com.symb.foxpandasdk.data.models.ListOfInstalledApp
import com.symb.foxpandasdk.data.models.NotificationModel
import com.symb.foxpandasdk.main.AppBackgroundHelper.Companion.context
import com.symb.foxpandasdk.services.ConnectivityReceiver
import com.symb.foxpandasdk.services.FCMIdInstance
import com.symb.foxpandasdk.ui.manager.FPNotificationManager
import com.symb.foxpandasdk.utils.CommonUtils
import com.symb.foxpandasdk.utils.DeviceInfoUtil
import com.symb.foxpandasdk.utils.NetworkUtil
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
        private var listOfInstalledApp = ArrayList<ListOfInstalledApp>()
        val foxListening = FoxListening()
        val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
        val REQUEST_APP_USAGE_PERMISSIONS = 2
        var handler = Handler()
       lateinit var checkSettingOn : Runnable


        fun initialize(context: Context) {
            FoxApplication.instance.deviceID = android.provider.Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)
            NetworkUtil.initRetrofit(true, Constants.DEFAULT_LOG_LEVEL, context)



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


            // GET LIST OF APPS INSTALLED BY USER
//            for (i in getAllInstalledApkInfo(context)) {
//                val listOfInstalledApp = ListOfInstalledApp(FoxApplication.instance.deviceID, i, getAppName(i, context))
//                db.saveInstalledApp(listOfInstalledApp)
//            }
//
//            listOfInstalledApp = db.getListOfInstallledApp()
//            for (list in listOfInstalledApp){
//                Log.e("appInfoPacakage", list.app_installed_name + "\n")
//                Log.e("deviceID", list.device_id+ "\n")
//            }


            // CODE FOR ACCESSING USAGE STATS
            // THIS FUNCTION REQUIRED ----->>>>>>> Android 5.0 Lollipop (API Level 21)
            // CHECK FOR PERMISSION -- ACTION USAGE ACCESS SETTING


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


        fun getAppName(ApkPackageName: String, context: Context): String {
            var Name: String = ""
            val applicationInfo: ApplicationInfo
            val packageManager = context.getPackageManager()
            try {
                applicationInfo = packageManager.getApplicationInfo(ApkPackageName, 0)
                if (applicationInfo != null) {
                    Name = packageManager.getApplicationLabel(applicationInfo) as String

                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return Name
        }

        fun getAllInstalledApkInfo(context: Context): List<String> {
            val ApkPackageName = java.util.ArrayList<String>()
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            val resolveInfoList = context.getPackageManager().queryIntentActivities(intent, 0)
            for (resolveInfo in resolveInfoList) {
                val activityInfo = resolveInfo.activityInfo
                if (!isSystemPackage(resolveInfo)) {
                    ApkPackageName.add(activityInfo.applicationInfo.packageName)
                    Log.e("appname",activityInfo.applicationInfo.packageName)

                }
            }
            return ApkPackageName
        }

        fun isSystemPackage(resolveInfo: ResolveInfo): Boolean {
            return ((resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) !== 0)
        }

        fun getAppIconByPackageName(ApkTempPackageName: String, context: Context): Drawable {
            var drawable: Drawable
            try {
                drawable = context.getPackageManager().getApplicationIcon(ApkTempPackageName)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                drawable = ContextCompat.getDrawable(context, R.mipmap.ic_launcher)!!
            }
            return drawable
        }


        @SuppressLint("WrongConstant")
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        public fun retrieveStats(context: Context) {
            val db = DBHelper(context)
            var usageStats: UsageStats
            var PackageName = "Nothing"
            var TimeInforground: Long = 500
            val mUsageStatsManager = context.getSystemService("usagestats") as UsageStatsManager
            val time = System.currentTimeMillis()
            val stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, getStartTime(), time)
            val event = mUsageStatsManager.queryEvents(getStartTime(), time)
            val config = mUsageStatsManager.queryConfigurations(UsageStatsManager.INTERVAL_WEEKLY, getStartTime(), time)
            if (stats != null) {

                for (i in listOfInstalledApp) {
                    for (usageStats in stats) {
                        if (i.app_package_name.equals(usageStats.packageName)) {
                            val appUsageStats = AppUsageStats(FoxApplication.instance.deviceID, usageStats.packageName, usageStats.firstTimeStamp, usageStats.lastTimeUsed)
                            db.saveInstalledAppUsage(appUsageStats)
                        } else {
                            // Log.e("System_app",usageStats.packageName)
                        }
                    }


                }
                var usageStats : ArrayList<AppUsageStats>
                usageStats = db.getListOfInstallledAppUsageStats()
                for(usage in usageStats)
                {
                    Log.e("savedUsage",usage.app_package_name + "last time " + usage.out_time)
                }
            }
            // schedule work periodically using Work Manager -- it will call within every X period of time
            val myWorkBuilder = PeriodicWorkRequestBuilder<FoxWorkManager>(16, TimeUnit.MINUTES)
            val myWork = myWorkBuilder.build()
            WorkManager.getInstance().enqueue(myWork)
            handler.removeCallbacksAndMessages(null)
        }


        private fun getStartTime(): Long {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            Log.e("calendar", calendar.timeInMillis.toString())
            return calendar.timeInMillis
        }

        fun showNotification(context: Context) {
            val random1 = (0..100).shuffled().last()
            // Prepare intent which is triggered if the
            // notification is selected
            val intent = Intent(context, FoxListening::class.java)
            val pIntent = PendingIntent.getActivity(context, System.currentTimeMillis().toInt(), intent, 0)
            // Build notification
            // Actions are just fake
            val noti = Notification.Builder(context)
                    .setContentTitle("New mail from " + "test@gmail.com" + System.currentTimeMillis())
                    .setContentText("Subject").setSmallIcon(R.drawable.icon_share)
                    .setContentIntent(pIntent)
                    .build()
            val notificationManager = context!!.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            // hide the notification after its selected
            noti.flags = noti.flags or Notification.FLAG_AUTO_CANCEL
            notificationManager.notify(random1, noti)
        }



    }

}
