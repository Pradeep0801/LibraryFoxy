package com.symb.foxpandasdk.data.models

import com.google.gson.annotations.SerializedName
import com.symb.foxpandasdk.main.FoxPanda
import com.symb.foxpandasdk.utils.DeviceInfoUtil

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
        var in_time : String = "",
        var out_time : String = ""
)
