package com.sdk.foxpanda.data.endpoint

import com.sdk.foxpanda.data.models.BaseResult
import com.sdk.foxpanda.data.models.NotificationActionModel
import com.sdk.foxpanda.data.models.RegisterDeviceModel
import com.sdk.foxpanda.data.models.UserActivityTimeModel
import io.reactivex.Observable
import retrofit2.http.*

internal interface Endpoint {

//    @FormUrlEncoded
//    @POST("foxpanda-api/public/api/debug/device")
//    fun registerToken(@FieldMap param: HashMap<String, String>): Observable<BaseResult>

    @POST("api/v1/sdk/platform/device/register")
    fun registerToken(@Body deviceData: RegisterDeviceModel): Observable<BaseResult>


    @POST("api/v1/sdk/platform/device/activities")
    fun updateUserActivityTimeToServer(@Body userActivityTimeModel: List<UserActivityTimeModel>): Observable<BaseResult>

    @POST("api/v1/sdk/platform/notification/stats")
    fun updateNotificationActionToServer(@Body notificationActionModel: List<NotificationActionModel>): Observable<BaseResult>




}
