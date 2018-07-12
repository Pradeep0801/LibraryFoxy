package com.symb.foxpandasdk.data.endpoint

import android.media.session.MediaSession
import com.symb.foxpandasdk.data.models.BaseResult
import com.symb.foxpandasdk.data.models.DeviceInfoModel
import com.symb.foxpandasdk.data.models.RegisterDeviceModel
import com.symb.foxpandasdk.data.models.UserActivityTimeModel
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




}
