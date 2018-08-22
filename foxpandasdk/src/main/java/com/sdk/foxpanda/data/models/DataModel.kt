package com.sdk.foxpanda.data.models

import android.graphics.drawable.Drawable
import com.google.gson.annotations.SerializedName

data class FirebaseInfo(
    var token: String = "",
    var deviceId: String = "",
    var eventName: String = ""
)

 data class BaseResult(
    var message: String = "",
    var code: String = "",
    var status: Boolean = false
)

data class ErrorModel(
    var errorCode: Int,
    var errors: String,
    var error: String,
    var message: String
)

data class  DeviceInfoModel(
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
var d_language : String = "",
var app_version : String = "",
var sdk_version : String = ""
)
 data class RegisterDeviceModel(
        var token: String = "",
        var deviceData: DeviceInfoModel

)

 data class  UserActivityTimeModel(
        var in_time : Long = 0L,
        var out_time : Long = 0L
)

 data class AppPackageInfo(
        var packageName : String = "",
        var packageAppName : String = "",
        var packageAppIcon : Drawable? = null

)

 data class AppUsageStats(
         @SerializedName("appPackageName")
        var app_package_name : String = "",
        @SerializedName("fInTime")
        var in_time: Long = 0L,
        @SerializedName("fOutTime")
        var out_time : Long = 0L,
        var app_total_time : Long = 0L
)
 data class ListOfInstalledApp(
         @SerializedName("appPackageName")
        var app_package_name : String = "",
        var app_installed_name : String = "",
        var installedAt : Long = 0L,
        var appVersion: String = "",
        var lastOpenedAt : Long = 0L
)

 data class NotificationActionModel(
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
data class FoxScreenResolutionModel(
    var fpDeviceHeight : Int = 0,
    var fpDeviceWidth : Int = 0
)
data class FoxScreenIntractionModel (
        var fpActivityName : String = "",
        var fpDevice_id: String = "",
        var fpOrientation : Int = 0,
        var fpCoordinateX : Int = 0,
        var fpCoordinateY : Int = 0
)
data class FoxScreenScrollPositionModel(
        var fpViewType : String = "",
        var fpScrollPosition : Int = 0,
        var fpMaxScrollPosition: Int = 0

)
data class PandaLocationComponent(
        @SerializedName("lng")
        var pandaLongitude : String = "",
        @SerializedName("lat")
        var pandaLatitude : String = "",
        @SerializedName("ts")
        var pandaTimeStamp : String = ""
)
data class TagPandaModel(
        var key : String = "",
        var value : String = ""
)