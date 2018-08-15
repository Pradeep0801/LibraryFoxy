package com.sdk.foxpanda.data.endpoint

import com.sdk.foxpanda.data.models.*
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

    @POST("api/v1/sdk/platform/device/locations")
    fun updatePandaLocationToServer(@Body pandaLocationComponent: List<PandaLocationComponent>): Observable<BaseResult>


    @POST("api/v1/sdk/platform/device/apps/installed")
    fun updateFoxListToServer(@Body foxList: List<ListOfInstalledApp>): Observable<BaseResult>


    @POST("api/v1/sdk/platform/device/apps/usage")
    fun updateFoxUsageStatToServer(@Body foxUsageStats: List<AppUsageStats>): Observable<BaseResult>


}
