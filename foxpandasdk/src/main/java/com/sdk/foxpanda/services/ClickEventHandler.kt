package com.sdk.foxpanda.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.app.NotificationManager
import android.net.Uri
import com.sdk.foxpanda.applications.FoxApplication
import com.sdk.foxpanda.constants.Constants
import com.sdk.foxpanda.data.dbHelper.DBHelper
import com.sdk.foxpanda.data.models.NotificationActionModel
import com.sdk.foxpanda.main.FoxPanda
import com.sdk.foxpanda.ui.RichMediaNotification
import com.sdk.foxpanda.utils.CommonUtils
import android.support.v4.content.ContextCompat.startActivity



class ClickEventHandler: BroadcastReceiver() {

    internal lateinit var dbHelper: DBHelper

    override fun onReceive(context: Context?, intent: Intent?) {
        dbHelper = DBHelper(context!!)
        val notificationID = intent!!.getIntExtra(Constants.NOTIFICATION_ID,0)
        val notificationActionModel = NotificationActionModel(FoxApplication.instance.deviceID,notificationID.toLong(),0L,System.currentTimeMillis(),0L)

      //update notification click to database
       val isUpdated = dbHelper.updateNotificationActionToDB(notificationActionModel)

        // check for internet connection
        if (isUpdated && FoxApplication.instance.isFoxConnectedToPanda){
           //update notification click to server
            CommonUtils.updateNotificationActionToServer(dbHelper.getNotificationActionTime(),true,context)
        }

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
        }

        else if(intent.getStringExtra(Constants.CLICK_ACTION) == Constants.EXTERNAL_URL){
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(intent.getStringExtra(Constants.CLICK_EXTERNAL_URL))))
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationID)
            closeNotificationTray(context)
        }
        else if(intent.action == Constants.OPEN_ACTIVITY) {
            val result = dbHelper.registerEvent(Constants.NOTIFICATION_CLICKED)
//            if(result)
//                Log.e(Constants.NOTIFICATION_CLICKED, "data successfully logged")
//            else
//                Log.e(Constants.NOTIFICATION_CLICKED, "data logging failed")

            val notificationId = intent.getIntExtra(Constants.NOTIFICATION_ID, 0)

            val clickAction = intent.getStringExtra(Constants.CLICK_ACTION)
           // FoxPanda.FPLogger("classNameAction", clickAction)
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
                intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
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
