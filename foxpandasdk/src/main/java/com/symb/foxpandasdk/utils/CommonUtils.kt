package com.symb.foxpandasdk.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.symb.foxpandasdk.data.dbHelper.DBHelper
import com.symb.foxpandasdk.data.models.DeviceInfoModel
import com.symb.foxpandasdk.data.models.NotificationActionModel
import com.symb.foxpandasdk.data.models.RegisterDeviceModel
import com.symb.foxpandasdk.data.models.UserActivityTimeModel
import com.symb.foxpandasdk.main.FoxPanda
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.net.HttpURLConnection
import java.net.URL
import dalvik.system.DexFile
import java.io.IOException


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
                        dbHelper.saveIsInfoUpdated(1)
                        Log.e("result",result.message)
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

    fun updateUserActivityTimeToServer(userActivityTimeModel: List<UserActivityTimeModel>,deleteEnable : Boolean,context: Context) {
        compositeDisposable.add(NetworkUtil.getEndpoint()!!.updateUserActivityTimeToServer(userActivityTimeModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    if (result != null) {

                        if (deleteEnable) {
                            val db = DBHelper(context)
                            db.deleteUserActivityTime()
                        }
                        Log.e("result",result.message)
                    }
                }, this::handleError))
    }

    fun updateNotificationActionToServer(notificationActionModel:List<NotificationActionModel>,deleteEnable : Boolean,context: Context) {
        compositeDisposable.add(NetworkUtil.getEndpoint()!!.updateNotificationActionToServer(notificationActionModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    if (result != null) {

                        if (deleteEnable) {
                            val db = DBHelper(context)
                            db.deleteNotificationAction()
                        }
                        Log.e("result",result.message)
                    }
                }, this::handleError))
    }


}
