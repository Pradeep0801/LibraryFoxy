package com.symb.foxpandasdk.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.symb.foxpandasdk.R
import com.symb.foxpandasdk.constants.Constants
import com.symb.foxpandasdk.data.dbHelper.DBHelper
import com.symb.foxpandasdk.main.FoxPanda
import com.symb.foxpandasdk.ui.manager.FPNotificationManager

class FCMService: FirebaseMessagingService() {

    internal lateinit var dbHelper: DBHelper

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        dbHelper = DBHelper(this)
        val result = dbHelper.registerEvent(Constants.NOTIFICATION_RECEIVED)
        if(result)
            FoxPanda.FPLogger(Constants.NOTIFICATION_RECEIVED, "data successfully logged")
        else
            FoxPanda.FPLogger(Constants.NOTIFICATION_RECEIVED, "data logging failed")

        val notificationManager = FPNotificationManager(this, remoteMessage!!)
        notificationManager.showNotification()
    }

}
