package com.symb.foxpandasdk.WorkManager

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.work.Worker
import com.symb.foxpandasdk.R
import com.symb.foxpandasdk.main.FoxPanda
import com.symb.foxpandasdk.ui.manager.FPNotificationManager
import javax.xml.transform.Result
class FoxWorkManager : Worker() {

    override fun doWork(): Result {
        FoxPanda.showNotification(this.applicationContext)
        return Result.SUCCESS
    }

   private fun scheduleUpdate(){
       Log.e("Hello","work manager testing --->>>>" + System.currentTimeMillis()/1000);

    }


}

