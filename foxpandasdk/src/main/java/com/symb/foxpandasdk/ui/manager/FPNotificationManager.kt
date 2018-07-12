package com.symb.foxpandasdk.ui.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.widget.RemoteViews
import com.google.firebase.messaging.RemoteMessage
import com.symb.foxpandasdk.R
import com.symb.foxpandasdk.constants.Constants
import com.symb.foxpandasdk.data.dbHelper.DBHelper
import com.symb.foxpandasdk.main.FoxPanda
import java.util.*

internal class FPNotificationManager(var context: Context, var remoteMessage: RemoteMessage) {
    var notificationManagerManager: NotificationManager? = null
    var ADMIN_CHANNEL_ID = "23"
    var views: RemoteViews? = null
    var bigViews: RemoteViews? = null
    var clickIntent: Intent? = null
    var template: String? = null
    var notificationId: Int = 0
    internal lateinit var dbHelper: DBHelper

    fun showNotification() {
        template = remoteMessage.data.get(Constants.TEMPLATE)
        notificationId = Random().nextInt(60000)
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
                    notifyNotification()
                    return
                } catch (e: ClassNotFoundException) {
                    FoxPanda.FPLogger("Naah", "It aint working")
                    e.printStackTrace()
                }
            }
        }
    }

    private fun buildNotification(context: Context, pendingIntent: PendingIntent): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, ADMIN_CHANNEL_ID)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.app_name))
            .setAutoCancel(true)
            .setOngoing(false)
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setStyle(NotificationCompat.BigPictureStyle())
            .setCustomContentView(views)
            .setCustomBigContentView(bigViews)
    }

    private fun notifyNotification() {
        dbHelper = DBHelper(context)
        clickIntent = Intent(context.packageName)
        clickIntent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(context, notificationId, clickIntent,
            PendingIntent.FLAG_ONE_SHOT)

        val notificationBuilder = buildNotification(context, pendingIntent)
        notificationManagerManager!!.notify(notificationId, notificationBuilder.build())
        val result = dbHelper.registerEvent(Constants.NOTIFICATION_DISPLAYED)
        if (result)
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
