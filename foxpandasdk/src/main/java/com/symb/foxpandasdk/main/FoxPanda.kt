package com.symb.foxpandasdk.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.symb.foxpandasdk.R
import com.symb.foxpandasdk.applications.FoxApplication
import com.symb.foxpandasdk.constants.Constants
import com.symb.foxpandasdk.data.dbHelper.DBHelper
import com.symb.foxpandasdk.services.ConnectivityReceiver
import com.symb.foxpandasdk.services.FCMIdInstance
import com.symb.foxpandasdk.utils.CommonUtils
import com.symb.foxpandasdk.utils.DeviceInfoUtil
import com.symb.foxpandasdk.utils.NetworkUtil

@SuppressLint("Registered")
class FoxPanda{

    enum class LogLevel {
        BASIC,
        BODY,
        HEADER,
        NONE
    }

    companion object {

        private const val TAG = "DBElements"


        fun register(serverKey: String, pandaId: String) {

        }

//        fun setLog(logEnable: Boolean, loglevel: LogLevel?) {
//            if(loglevel != null) {
//                val level = getLevelString(loglevel)
//                NetworkUtil.initRetrofit(logEnable, level,)
//            } else {
//                NetworkUtil.initRetrofit(false, Constants.DEFAULT_LOG_LEVEL)
//            }
//        }

        fun initialize(context: Context) {
            NetworkUtil.initRetrofit(true, Constants.DEFAULT_LOG_LEVEL,context)
            val db = DBHelper(context)
            if (db.getIsInfoUpdated() == 1) {
                print("token already updated")
            }
            else {
                val token = db.getToken()
                val isConnected = FoxApplication.instance.isFoxConnectedToPanda
                if (isConnected && token != "") {
                    CommonUtils.registerTokenToServer(db.getToken(),  context)
                }

            }
            if (db.getUserActivityTime().size > 0) {
                CommonUtils.updateUserActivityTimeToServer(db.getUserActivityTime(),true,context)
            }

            val classes = CommonUtils.getClassesOfPackage(context)
            val dbClasses = db.getAllInternalClasses()
            if(dbClasses.size > 0) {
                if(dbClasses.size != classes.size) {
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
            context.registerReceiver(ConnectivityReceiver(),
                    IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
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
            if(loglevel == LogLevel.BASIC)
                return Constants.BASIC
            else if(loglevel == LogLevel.NONE)
                return Constants.NONE
            else if(loglevel == LogLevel.HEADER)
                return Constants.HEADER
            else
                return Constants.DEFAULT_LOG_LEVEL
        }

        internal fun FPLogger(tag: String, message: String) {
            Log.e(tag, message)
        }



    }





}
