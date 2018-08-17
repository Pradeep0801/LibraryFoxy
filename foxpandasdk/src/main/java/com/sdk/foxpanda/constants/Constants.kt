package com.sdk.foxpanda.constants

import android.app.Notification

internal object Constants {
    val BASE_URL = "http://api.foxpanda.io/"
    val OPEN_SHARE = "openshare"
    val OPEN_ACTIVITY = "openactivity"
    val IN_APP = "IN_APP"
    val TABLE_NAME = "foxpanda_notification_table"
    val TOKEN_TABLE = "foxpanda_token_table"
    val PLATFORM_ID_TABLE = "foxpanda_platform_id"
    val CLASS_TABLE = "class_table"
    val USER_ACTIVITY_TIME_TABLE = "user_activity_table"
    val USER_ACTIVITY_START_TIME = "in_time"
    val USER_ACTIVITY_END_TIME = "out_time"
    val ALL_CLASS_TABLE = "all_class_table"
    val IS_INFO_UPDATED = "is_info_updated"
    val FIREBASE_TOKEN = "firebase_token"
    val CLASS_NAME = "class_token"
    val DEVICE_ID = "device_id"
    val EVENT_NAME = "event_name"
    val TIMESTAMP = "timestamp"
    val INFO_UPDATE_FLAG = "info_update_flag"
    val NOTIFICATION_RECEIVED = "notification_received"
    val NOTIFICATION_DISPLAYED = "notification_displayed"
    val NOTIFICATION_CLICKED = "notification_clicked"
    val EXTERNAL_URL = "EXTERNAL_URL"
    val ID = "id"
    val CLICK_EXTERNAL_URL = "clickActionUrl"
    val PLATFORM_ID = "platfrom_id"
    //val DEFAULT_NOTIFICATION = "default"


    //TABLE NAME FOR DATABASE -- APP INSTALLED BY USER TABLE
    val APP_INSTALLED_BY_USER_TABLE = "app_installed_by_user"
    // COLUMN NAME FOR DATABASE --- APP INSTALLED BY USER TABLE
    val APP_PACKAGE_NAME = "app_package_name"
    val APP_INSTALLED_NAME = "app_installed_name"
    val APP_INSTALLED_VERSION = "app_version"
    val APP_INSTALLED_AT = "installedAt"
    val APP_LAST_OPEN_AT = "lastOpenedAt"



    //TABLE NAME FOR DATABASE -- APP USAGE STATS
    val APP_USAGE_STATS_TABLE = "app_usage_stats"

    //COLUMN NAME FOR DATABASE -- APP USAGE STATS
    val APP_GET_FIRST_TIME_STAMP = "in_time"
    val APP_GET_LAST_TIME_USED = "out_time"
    val APP_FOREGROUND_TIME = "app_foreground_time"

    // TABLE NAME FOR NOTIFICATION HANDLE/RECEIVE/ACTION
    val NOTIFICATION_ACTION_TABLE = "notification_action"

    //COLUMN NAME FOR NOTIFICATION ACTION
    val APP_NOTIFICATION_ID = "notification_id"
    val APP_NOTIFICATION_RECIEVED_AT = "notification_recieved_at"
    val APP_NOTIFICATION_OPEN_AT = "notification_open_at"
    val APP_NOTIFICATION_CLEARED_AT = "notification_cleared_at"


    //NOTIFICATION LIST TABLE NAME
    val NOTIFICATION_LIST_TABLE_NAME = "notification_table_name"
    // COLUMN NAME FOR NOTIFICATION LIST
    val APP_NOTIFICATION_TITLE = "notification_title"
    val APP_NOTIFICATION_CONTENT  = "notification_content"
    val APP_NOTIFICATION_IMAGE = "notification_image"


    // table for stroing user interaction point(X/Y coordinates of screen) to database
    const val INTERACTION_COORDINATE_TABLE = "interaction_coordinate_table"
    val FOX_ACTIVITY_NAME = "fox_activity_name"
    val FOX_LAYOUT_NAME = "fox_layout_name"
    val FOX_ORIENTATION = "fox_orientation_name"
    val FOX_DEVICE_HEIGHT = "fox_device_height"
    val FOX_DEVICE_WIDTH = "fox_device_width"
    val FOX_SCROLL_POSITION = "fox_scroll_position"
    val FOX_MAX_SCROLL_POSITION = "fox_max_scroll_position"
    val FOX_INTERACTION_COORDINATE_X = "fox_interaction_coordinate_x"
    val FOX_INTERACTION_COORDINATE_Y = "fox_interaction_coordinate_y"

    // table for storing user location

    val USER_LOCATION_TABLE = "panda_location"
    val PANDA_LONGITUDE = "panda_longitude"
    val PANDA_LATITUDE = "panda_latitude"
    val PANDA_TIMESTAMP = "panda_timestamp"


    val TITLE = "title"
    val CONTENT = "content"
    val TEMPLATE = "template"
    val MEDIA_TYPE = "mediaType"
    val MEDIA_URL = "mediaUrl"
    val CLICK_ACTION = "clickAction"
    val SHARE_MESSAGE = "shareMessage"
    val SHOW_ICON = "showIcon"
    val SILENT = "silent"

    val DEFAULT_TEMPLATE = "default"

    val NOTIFICATION_ID = "notification_id"
    val ACTIVITY_NAME = "activity_name"
    val PACKAGE_NAME = "package_name"

    val DEFAULT_LOG_LEVEL = "BODY"
    val BASIC = "BASIC"
    val HEADER = "HEADER"
    val NONE = "NONE"

    val BIG_VIEW = "big_view"
    val SMALL_VIEW = "small_view"

    val RICH_MEDIA = "richMedia"

    val NOTIFICATION_PATH = "com.sdk.foxpanda.ui.notifications."

   // val CURRENT_APP_PACKAGE = "app.gossips.rumors"
     val CURRENT_APP_PACKAGE = "tk.sharebaba.app"





    /// 5q

    fun getClassName(template: String): String? {
        when(template) {
            "DEFAULT" -> {
                return "DefaultNotification"
            }
            "share" -> {
                return "ShareNotification"
            }
            "COVER_MEDIA" ->{
                return "LeftNotification"
            }
            "TWO_COLUMN_RIGHT " -> {
                return "LeftNotification"
            }
            "TWO_COLUMN_LEFT " -> {
                return "LeftNotification"
            }


        }
        return "DefaultNotification"
    }
}
