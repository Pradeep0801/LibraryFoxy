package com.sdk.foxpanda.ui.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.text.format.DateFormat
import android.view.View
import android.widget.RemoteViews
import com.google.firebase.messaging.RemoteMessage
import com.sdk.foxpanda.R
import com.sdk.foxpanda.constants.Constants
import com.sdk.foxpanda.data.dbHelper.DBHelper
import com.sdk.foxpanda.main.FoxPanda
import com.sdk.foxpanda.services.ClickEventHandler
import com.sdk.foxpanda.utils.CommonUtils
import java.net.URL
import java.util.*

internal open class LeftNotification(var context: Context, var remoteMessage: RemoteMessage) {

    private var bitmap: Bitmap? = null
    var title: String? = remoteMessage.data.get(Constants.TITLE)
    var message: String? = remoteMessage.data.get(Constants.CONTENT)
    private var activity: String? = remoteMessage.data.get(Constants.CLICK_ACTION)
    private var clickActionUrl : String?= remoteMessage.data.get(Constants.CLICK_EXTERNAL_URL)
    var image: String? = remoteMessage.data.get(Constants.MEDIA_URL)
    private var url: String? = remoteMessage.data.get("url")
    private val db = DBHelper(context)

    open fun getInitView(context: Context, notificationId: Int, viewType: String): RemoteViews {
        val views: RemoteViews?
        if(viewType.equals(Constants.SMALL_VIEW))
            views = RemoteViews(context.packageName, R.layout.default_left_notification_sv)
        else
            views = RemoteViews(context.packageName, R.layout.default_left_notification_bv)
        configureViews(views)
        initOpenIntent(context, views, notificationId, activity!!,clickActionUrl!!)

        return views
    }

    fun configureViews(views: RemoteViews) {
        views.setViewVisibility(R.id.share, View.GONE)
        if (image != null) {
            bitmap = CommonUtils.getBitmapfromUrl(image!!)
            if (bitmap != null) {
                views.setImageViewBitmap(R.id.app_icon,bitmap)
                views.setImageViewBitmap(R.id.noti_image, bitmap)
            }
        }
        views.setTextViewText(R.id.noti_time, DateFormat.format("hh:mm a", Calendar.getInstance().getTime()))
        if (title != null)
            views.setTextViewText(R.id.noti_title, title)
        if (message != null)
            views.setTextViewText(R.id.noti_content, message)
    }

    fun initShareIntent(context: Context, views: RemoteViews, shareMessage: String,notificationId: Int) {
        val shareIntent = Intent(context, ClickEventHandler::class.java)
        shareIntent.action = Constants.OPEN_SHARE
        shareIntent.putExtra(Constants.SHARE_MESSAGE, shareMessage)
        shareIntent.putExtra(Constants.NOTIFICATION_ID,notificationId)
        val pShareIntent = PendingIntent.getBroadcast(context, 0, shareIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        views.setOnClickPendingIntent(R.id.share, pShareIntent)
    }

    fun initOpenIntent(context: Context, views: RemoteViews, notificationId: Int, activity: String,clickActionUrl: String) {
        FoxPanda.FPLogger("open_at",System.currentTimeMillis().toString())
        if(activity.equals(Constants.RICH_MEDIA) || activity.equals(Constants.EXTERNAL_URL))
            setOpenPendingIntent(context, views, notificationId, activity,clickActionUrl)
        else {
            val classes = db.getAllClasses()
            classes.forEach {
                val className = it.split(".").reversed()
                FoxPanda.FPLogger("actName", className[0].capitalize() +"\n")
                if(className[0].equals(clickActionUrl,true)) {
                    FoxPanda.FPLogger("actName1", clickActionUrl.capitalize() +"\n")
                    setOpenPendingIntent(context, views, notificationId, it,"")
                    return
                }
                else {

                }
            }
        }
    }

    private fun setOpenPendingIntent(context: Context, views: RemoteViews, notificationId: Int, activity: String,clickActionUrl:String) {
        FoxPanda.FPLogger("yay", activity)
        val openNotificationIntent = Intent(context, ClickEventHandler::class.java)
        openNotificationIntent.action = Constants.OPEN_ACTIVITY
        openNotificationIntent.putExtra(Constants.NOTIFICATION_ID, notificationId)
        openNotificationIntent.putExtra(Constants.CLICK_ACTION, activity)
        openNotificationIntent.putExtra(Constants.CLICK_EXTERNAL_URL,clickActionUrl)
        openNotificationIntent.putExtra("url", url)
        val pOpenNotificationIntent = PendingIntent.getBroadcast(context, notificationId, openNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        views.setOnClickPendingIntent(R.id.noti_ll, pOpenNotificationIntent)
    }

}
