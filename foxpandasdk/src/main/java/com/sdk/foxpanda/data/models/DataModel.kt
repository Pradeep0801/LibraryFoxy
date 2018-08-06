package com.sdk.foxpanda.data.models

import android.graphics.drawable.Drawable

internal data class FirebaseInfo(
    var token: String = "",
    var deviceId: String = "",
    var eventName: String = ""
)

internal data class BaseResult(
    var message: String = "",
    var code: String = "",
    var status: Boolean = false
)

internal data class ErrorModel(
    var errorCode: Int,
    var errors: String,
    var error: String,
    var message: String
)

internal data class  DeviceInfoModel(
var d_name : String = "",//
var d_manufacturer : String = "",//
var d_os : String = "",//
var d_os_version : String = "",//
var d_locale : String = "",//
var d_ram : String = "",//
var d_internal_storage : String = "",//
var d_free_storage : String = "",//
//var app_version : String = "",
//var device_id : String = "",
//var d_ip_address : String = "",
var d_model : String = "",//
//var d_product : String = "",
//var d_type : String = "",
//var d_root : String = "",
var d_year : String = "",//
//var internet_service_provider : String = "",
//var network_type : String = "",
//var country : String = "",
//var d_sdk : String = "",
//var timezone : String = "",
//var user_langauge : String = ""
//var token : String = ""
var d_language : String = ""//
)

internal data class RegisterDeviceModel(
        var token: String = "",
        var deviceData: DeviceInfoModel

)

internal data class  UserActivityTimeModel(
        var in_time : Long = 0L,
        var out_time : Long = 0L
)

internal data class AppPackageInfo(
        var packageName : String = "",
        var packageAppName : String = "",
        var packageAppIcon : Drawable? = null

)

internal data class AppUsageStats(
        var device_id : String = "",
        var app_package_name : String = "",
        var in_time: Long = 0L,
        var out_time : Long = 0L,
        var app_total_time : Long = 0L
)

internal data class ListOfInstalledApp(
        var device_id : String = "",
        var app_package_name : String = "",
        var app_installed_name : String = ""
)

internal data class NotificationActionModel(
        var device_id: String = "",
        var notificationId : Long = 0L,
        var receivedAt : Long = 0L,
        var openedAt : Long = 0L,
        var clearedAt : Long = 0L
)

data class NotificationModel(
        var notificationId: Long = 0L,
        var notificationTitle : String = "",
        var notificationContent : String = "",
        var notificationImage : String = "",
        var notificationTime : Long = 0L
)
