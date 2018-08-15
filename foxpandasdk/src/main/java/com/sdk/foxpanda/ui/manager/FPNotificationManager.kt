package com.sdk.foxpanda.ui.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.widget.RemoteViews
import com.google.firebase.messaging.RemoteMessage
import com.sdk.foxpanda.R
import com.sdk.foxpanda.applications.FoxApplication
import com.sdk.foxpanda.constants.Constants
import com.sdk.foxpanda.data.dbHelper.DBHelper
import com.sdk.foxpanda.data.models.NotificationActionModel
import com.sdk.foxpanda.data.models.NotificationModel
import com.sdk.foxpanda.main.FoxPanda
import com.sdk.foxpanda.services.DeleteEventHandler
import com.sdk.foxpanda.utils.CommonUtils
import java.util.*

internal class FPNotificationManager(var context: Context, var remoteMessage: RemoteMessage) {
    var notificationManagerManager: NotificationManager? = null
    var ADMIN_CHANNEL_ID = "23"
    var views: RemoteViews? = null
    var bigViews: RemoteViews? = null
    var clickIntent: Intent? = null
    var deleteIntent: Intent? = null
    var template: String? = null
    var notificationId: Int = 0
    internal lateinit var dbHelper: DBHelper
    var context1 : Context? = null
    var appIcon : Int? = null


    fun showNotification() {
        context1 = context

        appIcon = context.resources.getIdentifier("drawable","ic_launcher_background",context.packageName)

        dbHelper = DBHelper(context)
        template = remoteMessage.data.get(Constants.TEMPLATE)
        notificationId = remoteMessage.data.get(Constants.ID)!!.toInt()
        //save notification to show list
        val notificationModel = NotificationModel(notificationId.toLong(),remoteMessage.data.get(Constants.TITLE)!!,remoteMessage.data.get(Constants.CONTENT)!!,remoteMessage.data.get(Constants.MEDIA_URL)!!,System.currentTimeMillis())
        val list = dbHelper.saveNotificationList(notificationModel)
        if (list){
            FoxPanda.FPLogger("Hey","Saved notification")
        }

        notificationManagerManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels()
        }


         if(template != null) {
            val strings = template!!.split(".")
            val sortedStrings = strings.reversed()
            sortedStrings.forEach {
                val className = Constants.getClassName(it)
                FoxPanda.FPLogger("className", "$className $it")
                try {
                    val cls = Class.forName(Constants.NOTIFICATION_PATH + className)
                    val cons = cls.getConstructor(Context::class.java, RemoteMessage::class.java)
                    val obj = cons.newInstance(context, remoteMessage)
                    val method = cls.getMethod("getInitView", Context::class.java, Int::class.java, String::class.java)

                    views = method.invoke(obj, context, notificationId, Constants.SMALL_VIEW) as RemoteViews
                    bigViews = method.invoke(obj, context, notificationId, Constants.BIG_VIEW) as RemoteViews
                    notifyNotification(false)
                    return
                } catch (e: ClassNotFoundException) {
                    FoxPanda.FPLogger("Naah", "It aint working")
                    e.printStackTrace()
                }
            }
        }
        else {
             notifyNotification(true)
         }
    }

    private fun buildNotification(context: Context, pendingIntent: PendingIntent, delPendingIntent: PendingIntent,isDefault: Boolean): NotificationCompat.Builder {
       if (isDefault){
           return NotificationCompat.Builder(context, ADMIN_CHANNEL_ID)
                   .setContentTitle(remoteMessage.data.get(Constants.TITLE)!!)
                   .setContentText(remoteMessage.data.get(Constants.CONTENT)!!)
                   .setLargeIcon(CommonUtils.getBitmapfromUrl(remoteMessage.data.get(Constants.MEDIA_URL)!!))
                   .setSmallIcon(CommonUtils.getDrawableIconFromPackage(context1!!,"ic_stat_name"))
                   .setAutoCancel(true)
                   .setOngoing(false)
                   .setContentIntent(pendingIntent)
                   .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                   .setDeleteIntent(delPendingIntent)
       }
        else{
           return NotificationCompat.Builder(context, ADMIN_CHANNEL_ID)
                   .setContentTitle(remoteMessage.data.get(Constants.TITLE)!!)
                   .setContentText(remoteMessage.data.get(Constants.CONTENT)!!)
                   .setLargeIcon(CommonUtils.getBitmapfromUrl(remoteMessage.data.get(Constants.MEDIA_URL)!!))
                   .setSmallIcon(CommonUtils.getDrawableIconFromPackage(context1!!,"ic_stat_name"))
                   .setAutoCancel(true)
                   .setOngoing(false)
                   .setContentIntent(pendingIntent)
                   .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                   .setStyle(NotificationCompat.BigPictureStyle())
                   .setCustomContentView(views)
                   .setCustomBigContentView(bigViews)
                   .setDeleteIntent(delPendingIntent)
       }
    }

    private fun notifyNotification(isDefault : Boolean) {
        dbHelper = DBHelper(context)
        clickIntent = Intent(context.packageName)
        clickIntent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(context, notificationId, clickIntent,
            PendingIntent.FLAG_ONE_SHOT)

        deleteIntent = Intent(context, DeleteEventHandler::class.java)
        deleteIntent!!.putExtra(Constants.APP_NOTIFICATION_ID, notificationId)
        val delPendingIntent = PendingIntent.getBroadcast(context, notificationId, deleteIntent,
                PendingIntent.FLAG_ONE_SHOT)

        val notificationBuilder = buildNotification(context, pendingIntent, delPendingIntent,isDefault)
        notificationManagerManager!!.notify(notificationId, notificationBuilder.build())
        val result = dbHelper.registerEvent(Constants.NOTIFICATION_DISPLAYED)
        FoxPanda.FPLogger("Pradeep1", System.currentTimeMillis().toString())
        var notificationActionModel = NotificationActionModel(FoxApplication.instance.deviceID,notificationId.toLong(),System.currentTimeMillis(),0L,0L)

        val foxAction = dbHelper.saveNotificationAction(notificationActionModel)

        if (foxAction)
            FoxPanda.FPLogger(Constants.NOTIFICATION_DISPLAYED, "data successfully logged")
        else
            FoxPanda.FPLogger(Constants.NOTIFICATION_DISPLAYED, "data logging failed")
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels() {
        val adminChannelName = context.getString(R.string.notifications_admin_channel_name)
        val adminChannelDescription = context.getString(R.string.notifications_admin_channel_description)
        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW)
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        if (notificationManagerManager != null) {
            notificationManagerManager!!.createNotificationChannel(adminChannel)
        }
    }

}
