package com.sdk.foxpanda.utils

import com.sdk.foxpanda.constants.Constants
import com.sdk.foxpanda.data.models.ErrorModel
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import java.io.IOException

object ErrorParseUtil {
    internal fun parseError(response: Response<*>): ErrorModel? {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val converter: Converter<ResponseBody, ErrorModel> = retrofit.responseBodyConverter(ErrorModel::class.java, arrayOfNulls<Annotation>(0))

        var errorModel: ErrorModel? = null

        try {
            errorModel = converter.convert(response.errorBody())
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return errorModel
    }
}
