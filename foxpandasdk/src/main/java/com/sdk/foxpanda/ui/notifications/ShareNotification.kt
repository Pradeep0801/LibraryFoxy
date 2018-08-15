package com.sdk.foxpanda.ui.notifications

import android.content.Context
import android.view.View
import android.widget.RemoteViews
import com.google.firebase.messaging.RemoteMessage
import com.sdk.foxpanda.R
import com.sdk.foxpanda.constants.Constants

internal class ShareNotification(context: Context, remoteMessage: RemoteMessage) : LeftNotification(context, remoteMessage) {

    val shareMessage = remoteMessage.data.get(Constants.SHARE_MESSAGE)
    val clickAction = remoteMessage.data.get(Constants.CLICK_ACTION)
    val clickExternalUrl = remoteMessage.data.get(Constants.CLICK_EXTERNAL_URL)

    override fun getInitView(context: Context, notificationId: Int, viewType: String): RemoteViews {
        val views: RemoteViews?
        if(viewType.equals(Constants.SMALL_VIEW))
            views = RemoteViews(context.packageName, R.layout.default_left_notification_sv)
        else
            views = RemoteViews(context.packageName, R.layout.default_left_notification_bv)

        configureViews(views)

        if(shareMessage != null) {
            views.setViewVisibility(R.id.share, View.VISIBLE)
            initShareIntent(context, views, shareMessage,notificationId)
        }
        else {
            views.setViewVisibility(R.id.share, View.GONE)
        }

        if(clickAction != null)
            initOpenIntent(context, views, notificationId, clickAction,clickExternalUrl!!)

        return views
    }

}
