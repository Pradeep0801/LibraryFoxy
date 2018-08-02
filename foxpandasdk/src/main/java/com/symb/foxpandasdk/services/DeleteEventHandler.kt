package com.symb.foxpandasdk.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.common.internal.service.Common
import com.symb.foxpandasdk.applications.FoxApplication
import com.symb.foxpandasdk.constants.Constants
import com.symb.foxpandasdk.data.dbHelper.DBHelper
import com.symb.foxpandasdk.data.models.NotificationActionModel
import com.symb.foxpandasdk.main.FoxPanda
import com.symb.foxpandasdk.utils.CommonUtils

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