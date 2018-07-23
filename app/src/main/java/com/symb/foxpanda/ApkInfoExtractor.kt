package com.symb.foxpanda

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import java.util.ArrayList

class ApkInfoExtractor(context2:Context) {
    internal var context1: Context
    init{
        context1 = context2
    }
    fun GetAllInstalledApkInfo():List<String> {
        var ApkPackageName = ArrayList<String>()
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        val resolveInfoList = context1.getPackageManager().queryIntentActivities(intent, 0)
        for (resolveInfo in resolveInfoList)
        {
            val activityInfo = resolveInfo.activityInfo
            if (!isSystemPackage(resolveInfo))
            {
                ApkPackageName.add(activityInfo.applicationInfo.packageName)
            }
        }
        return ApkPackageName
    }
    fun isSystemPackage(resolveInfo: ResolveInfo):Boolean {
        return ((resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) !== 0)
    }
    fun getAppIconByPackageName(ApkTempPackageName:String): Drawable {
        var drawable:Drawable
        try
        {
            drawable = context1.getPackageManager().getApplicationIcon(ApkTempPackageName)
        }
        catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            drawable = ContextCompat.getDrawable(context1, R.mipmap.ic_launcher)!!
        }
        return drawable
    }
    fun GetAppName(ApkPackageName:String):String {
        var Name = " "
        val applicationInfo:ApplicationInfo
        val packageManager = context1.getPackageManager()
        try
        {
            applicationInfo = packageManager.getApplicationInfo(ApkPackageName, 0)
            if (applicationInfo != null)
            {
                Name = packageManager.getApplicationLabel(applicationInfo) as String
            }
        }
        catch (e:PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return Name
    }
}