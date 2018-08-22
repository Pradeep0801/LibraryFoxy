package com.sdk.foxpanda.utils

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.text.TextUtils
import android.util.Log
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.sdk.foxpanda.R
import com.sdk.foxpanda.WorkManager.FoxWorkManager
import com.sdk.foxpanda.WorkManager.LocationWorkManager
import com.sdk.foxpanda.applications.FoxApplication
import com.sdk.foxpanda.constants.Constants
import com.sdk.foxpanda.data.dbHelper.DBHelper
import com.sdk.foxpanda.data.models.*
import com.sdk.foxpanda.main.FoxPanda
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.net.HttpURLConnection
import java.net.URL
import dalvik.system.DexFile
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


internal object CommonUtils {
    var compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun registerTokenToServer(token: String, context: Context) {
        Log.e("deviceDAtaModel1",token)
        val dbHelper = DBHelper(context)
        val deviceParams = DeviceInfoUtil.getDeviceInfo(context)
        val registerDeviceModel = RegisterDeviceModel(token,deviceParams)
        compositeDisposable.add(NetworkUtil.getEndpoint()!!.registerToken(registerDeviceModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    if (result != null) {
                        if(result.status){
                            dbHelper.saveIsInfoUpdated(1)
                        }
                        else{
                            dbHelper.saveIsInfoUpdated(0)
                        }

                        //Log.e("result",result.message)
                    }
                }, this::handleError))
    }

    fun handleError(error: Throwable) {
        when (error) {
            is HttpException -> {

                error.printStackTrace()
            }
            is Exception -> {
                error.printStackTrace()
            }
        }
    }

    fun getBitmapfromUrl(imageUrl: String): Bitmap? {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            return BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getClassesOfPackage(context: Context): Array<String> {
        val classes = ArrayList<String>()
        FoxPanda.FPLogger("package", context.packageName)
        try {
            val packageCodePath = context.packageCodePath
            @Suppress("DEPRECATION")
            val df = DexFile(packageCodePath)
            val iter = df.entries()
            while (iter.hasMoreElements()) {
                val className = iter.nextElement()
                if (className.contains(context.packageName)) {
                    classes.add(className)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return classes.toArray(arrayOfNulls(classes.size))
    }

    fun updateUserActivityTimeToServer(userActivityTimeModel: List<UserActivityTimeModel>, deleteEnable : Boolean, context: Context) {
        compositeDisposable.add(NetworkUtil.getEndpoint()!!.updateUserActivityTimeToServer(userActivityTimeModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    if (result != null && result.status) {

                        if (deleteEnable) {
                            val db = DBHelper(context)
                            db.deleteUserActivityTime()
                        }
                      //  Log.e("result",result.message)
                    }
                }, this::handleError))
    }

    fun updatePandaTag(pandaModel:  List<TagPandaModel>, deleteEnable : Boolean, context: Context) {
        compositeDisposable.add(NetworkUtil.getEndpoint()!!.updatePandaTagToServer(pandaModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    if (result != null && result.status) {

                        if (deleteEnable) {
                            val db = DBHelper(context)
                           // db.deleteUserActivityTime()
                        }
                        //  Log.e("result",result.message)
                    }
                }, this::handleError))
    }

    fun updateFoxListToServer(foxList: List<ListOfInstalledApp>, deleteEnable : Boolean, context: Context) {
        compositeDisposable.add(NetworkUtil.getEndpoint()!!.updateFoxListToServer(foxList)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    if (result != null && result.status) {

                        if (deleteEnable) {
                            val db = DBHelper(context)
                            db.deleteListOfInstalledApp()
                        }
                       // Log.e("result",result.message)
                    }
                }, this::handleError))
    }

    fun updateFoxUsageToServer(foxusage: List<AppUsageStats>, deleteEnable : Boolean, context: Context) {
        compositeDisposable.add(NetworkUtil.getEndpoint()!!.updateFoxUsageStatToServer(foxusage)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    if (result != null && result.status) {

                        if (deleteEnable) {
                            val db = DBHelper(context)
                            db.deleteAppUsageTable()
                        }
                       // Log.e("result",result.message)
                    }
                }, this::handleError))
    }

    fun updateNotificationActionToServer(notificationActionModel:List<NotificationActionModel>, deleteEnable : Boolean, context: Context) {
        compositeDisposable.add(NetworkUtil.getEndpoint()!!.updateNotificationActionToServer(notificationActionModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    if (result != null && result.status) {

                        if (deleteEnable) {
                            val db = DBHelper(context)
                            db.deleteNotificationAction()
                        }
                      //  Log.e("result",result.message)
                    }
                }, this::handleError))
    }

    fun updatePandaLocationFox(pandaLocationComponent:List<PandaLocationComponent>, deleteEnable : Boolean, context: Context) {
        compositeDisposable.add(NetworkUtil.getEndpoint()!!.updatePandaLocationToServer(pandaLocationComponent)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    if (result != null && result.status) {
                        if (deleteEnable) {
                            val db = DBHelper(context)
                            db.deletePandaLocationTrack()
                        }
                       // Log.e("result",result.message)
                    }
                }, this::handleError))
    }



    fun getCurrentEpochTime():Long{
        return System.currentTimeMillis()/1000
    }

    fun converToEpoch(time:Long): Long{
        return  time/1000
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun pandaHasUsagePermission(context: Context) : Boolean{
            try
            {
                val packageManager = context.getPackageManager()
                val applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0)
                val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                var mode = 0
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT)
                {
                    mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                            applicationInfo.uid, applicationInfo.packageName)
                }
                return (mode == AppOpsManager.MODE_ALLOWED)
            }
            catch (e: PackageManager.NameNotFoundException) {
                return false
            }

    }
    fun pandaAskForUsagePermission(context: Context){
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                context.startActivity(intent)
    }



    // CODE FOR ACCESSING USAGE STATS
    // THIS FUNCTION REQUIRED ----->>>>>>> Android 5.0 Lollipop (API Level 21)
    // CHECK FOR PERMISSION -- ACTION USAGE ACCESS SETTING
    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun foxGetAppUsageTrack(context: Context) {
        var listOfInstalledApp = ArrayList<ListOfInstalledApp>()
        val db = DBHelper(context)
        var usageStats: UsageStats
        // GET LIST OF APPS INSTALLED BY USER
            for (i in getAllInstalledApkInfo(context)) {
                val installedApp = getAppName(i,context)
                db.saveInstalledApp(installedApp)
            }

            listOfInstalledApp = db.getListOfInstallledApp()
//            for (list in listOfInstalledApp){
//                Log.e("appInfoPacakage", list.app_installed_name + "\n")
//            }

        val mUsageStatsManager = context.getSystemService("usagestats") as UsageStatsManager
        val time = System.currentTimeMillis()
        val stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, getStartTime(), time)
        if (stats != null) {

            for (i in listOfInstalledApp) {
                for (usageStats in stats) {
                    if (i.app_package_name.equals(usageStats.packageName)) {
                        var lastTimeUsed = usageStats.lastTimeStamp
                        var totalTimeForeground = usageStats.totalTimeInForeground
                        if(lastTimeUsed <= 0){
                            lastTimeUsed = System.currentTimeMillis()
                        }
                        if (totalTimeForeground < 0){
                            totalTimeForeground = 0
                        }
                        val appUsageStats = AppUsageStats(usageStats.packageName, converToEpoch(lastTimeUsed-totalTimeForeground), converToEpoch(lastTimeUsed), totalTimeForeground)
                        db.saveInstalledAppUsage(appUsageStats)
                    } else {
                        // Log.e("System_app",usageStats.packageName)
                    }
                }


            }
//            var usageStats : ArrayList<AppUsageStats>
//            usageStats = db.getListOfInstallledAppUsageStats()
//            for(usage in usageStats)
//            {
//              //  Log.e("savedUsage",usage.app_package_name + "last time " + usage.out_time)
//            }
        }


        val installedAppForServer = db.getListOfInstallledApp()
        val appUsageForServer = db.getListOfInstallledAppUsageStats()
        val notificationActionModel = db.getNotificationActionTime()


        if(installedAppForServer.size > 0 && FoxApplication.instance.isFoxConnectedToPanda){
            updateFoxListToServer(installedAppForServer,true,context)
        }
        if (appUsageForServer.size > 0 && FoxApplication.instance.isFoxConnectedToPanda){
            updateFoxUsageToServer(appUsageForServer,true,context)
        }





        // schedule work periodically using Work Manager -- it will call within every X period of time
//        val myWorkBuilder = PeriodicWorkRequestBuilder<FoxWorkManager>(16, TimeUnit.MINUTES)
//        val myWork = myWorkBuilder.build()
//        WorkManager.getInstance().enqueue(myWork)

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
               // Log.e("appname",activityInfo.applicationInfo.packageName)

            }
        }
        return ApkPackageName
    }

    fun isSystemPackage(resolveInfo: ResolveInfo): Boolean {
        return ((resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) !== 0)
    }

    fun getAppName(ApkPackageName: String, context: Context): ListOfInstalledApp {
        val installedApp = ListOfInstalledApp()
        var Name: String = ""
        var installedAt = 0L
        var versionName  = ""
        val applicationInfo: ApplicationInfo
        val packageManager = context.getPackageManager()
        val packageInfo = packageManager.getPackageInfo(ApkPackageName,0)
        try {
            applicationInfo = packageManager.getApplicationInfo(ApkPackageName, 0)
            if (applicationInfo != null) {
                Name = packageManager.getApplicationLabel(applicationInfo) as String
                var appFile = applicationInfo.sourceDir
                installedAt = File(appFile).lastModified()
                versionName = packageInfo.versionName

            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        installedApp.app_installed_name = Name
        installedApp.appVersion = versionName
        installedApp.installedAt = converToEpoch(installedAt)
        installedApp.app_package_name = ApkPackageName
        installedApp.lastOpenedAt = 0
        return installedApp
    }

    private fun getStartTime(): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        //Log.e("calendar", calendar.timeInMillis.toString())
        return calendar.timeInMillis
    }


  fun getDrawableIconFromPackage(context: Context,name:String) : Int{
      val resources = context.getResources()
      val resourceId = resources.getIdentifier(name, "drawable",
              Constants.CURRENT_APP_PACKAGE)

      if (resourceId != null && resourceId > 0){
          return resourceId
      }
      else{
          return (R.mipmap.ic_launcher)
      }

    }

    fun isLocationEnabled(context:Context):Boolean {
        var locationMode = 0
        val locationProviders:String
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            try
            {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE)
            }
            catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
                return false
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF
        }
        else
        {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED)
            return !TextUtils.isEmpty(locationProviders)
        }
    }

    fun updateDeviceInfo(context: Context){
        NetworkUtil.initRetrofit(true, Constants.DEFAULT_LOG_LEVEL,context)
        val db = DBHelper(context)
        val refreshedToken = db.getToken()
        CommonUtils.registerTokenToServer(refreshedToken, context)
    }
}
