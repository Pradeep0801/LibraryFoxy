package com.symb.foxpandasdk.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.app.NotificationManager
import com.symb.foxpandasdk.applications.FoxApplication
import com.symb.foxpandasdk.constants.Constants
import com.symb.foxpandasdk.data.dbHelper.DBHelper
import com.symb.foxpandasdk.data.models.NotificationActionModel
import com.symb.foxpandasdk.main.FoxPanda
import com.symb.foxpandasdk.ui.RichMediaNotification
import com.symb.foxpandasdk.utils.CommonUtils

class ClickEventHandler: BroadcastReceiver() {

    internal lateinit var dbHelper: DBHelper

    override fun onReceive(context: Context?, intent: Intent?) {
        dbHelper = DBHelper(context!!)
        FoxPanda.FPLogger("Pradeepq", System.currentTimeMillis().toString())
        val notificationId = intent!!.getIntExtra(Constants.NOTIFICATION_ID,0)
        val notificationActionModel = NotificationActionModel(FoxApplication.instance.deviceID,notificationId.toLong(),0L,System.currentTimeMillis(),0L)
       val isUpdated = dbHelper.updateNotificationActionToDB(notificationActionModel)
        if (isUpdated && FoxApplication.instance.isFoxConnectedToPanda){
            CommonUtils.updateNotificationActionToServer(dbHelper.getNotificationActionTime(),true,context)
        }


        FoxPanda.FPLogger("hellp","dsadfasdf")
        if(intent!!.action == Constants.OPEN_SHARE) {
            val shareMessage = intent.getStringExtra(Constants.SHARE_MESSAGE)
            if (shareMessage != null) {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage)
                val chooserIntent = Intent.createChooser(shareIntent, "Share using")
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooserIntent)
                closeNotificationTray(context)
            }
        } else if(intent.action == Constants.OPEN_ACTIVITY) {
            val result = dbHelper.registerEvent(Constants.NOTIFICATION_CLICKED)
            if(result)
                Log.e(Constants.NOTIFICATION_CLICKED, "data successfully logged")
            else
                Log.e(Constants.NOTIFICATION_CLICKED, "data logging failed")
            val notificationId = intent.getIntExtra(Constants.NOTIFICATION_ID, 0)
            val clickAction = intent.getStringExtra(Constants.CLICK_ACTION)
            FoxPanda.FPLogger("classNameAction", clickAction)
            val url = intent.getStringExtra("url")
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if(clickAction != null) {
                var intent: Intent? = null
                if(clickAction.equals("richMedia")) {
                    intent = Intent(context, RichMediaNotification::class.java)
                    intent.putExtra("url", url)
                }
                else {
                    try {
                        val cls = Class.forName(clickAction)
                        intent = Intent(context, cls)
                    } catch (e: ClassNotFoundException) {
                        e.printStackTrace()
                    }
                }
                intent!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
            notificationManager.cancel(notificationId)
            closeNotificationTray(context)
        }
        else {

        }
    }

    fun closeNotificationTray(context: Context) {
        val closeIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        context.sendBroadcast(closeIntent)
    }



}
