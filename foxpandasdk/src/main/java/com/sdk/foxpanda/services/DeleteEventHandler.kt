package com.sdk.foxpanda.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sdk.foxpanda.applications.FoxApplication
import com.sdk.foxpanda.constants.Constants
import com.sdk.foxpanda.data.dbHelper.DBHelper
import com.sdk.foxpanda.data.models.NotificationActionModel
import com.sdk.foxpanda.main.FoxPanda
import com.sdk.foxpanda.utils.CommonUtils

class DeleteEventHandler : BroadcastReceiver(){


    override fun onReceive(p0: Context?, p1: Intent?) {
        val db = DBHelper(p0!!)
        FoxPanda.FPLogger("Pradeep", System.currentTimeMillis().toString())
        val notificationId = p1!!.getIntExtra(Constants.APP_NOTIFICATION_ID,0)

        val notificationActionModel = NotificationActionModel(FoxApplication.instance.deviceID,notificationId.toLong(),0L,0L,System.currentTimeMillis())
        val isUpdated = db.updateNotificationActionToDB(notificationActionModel)
        if (isUpdated && FoxApplication.instance.isFoxConnectedToPanda){
          CommonUtils.updateNotificationActionToServer(db.getNotificationActionTime(),true,p0)
        }
    }
}