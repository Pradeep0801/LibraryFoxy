package com.sdk.foxpanda.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sdk.foxpanda.constants.Constants
import com.sdk.foxpanda.data.dbHelper.DBHelper
import com.sdk.foxpanda.main.FoxPanda
import com.sdk.foxpanda.ui.manager.FPNotificationManager

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
