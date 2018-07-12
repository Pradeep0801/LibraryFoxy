package com.symb.foxpandasdk.services

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.symb.foxpandasdk.constants.Constants
import com.symb.foxpandasdk.data.dbHelper.DBHelper
import com.symb.foxpandasdk.main.FoxPanda
import com.symb.foxpandasdk.utils.CommonUtils
import com.symb.foxpandasdk.utils.NetworkUtil

class FCMIdInstance: FirebaseInstanceIdService() {
    internal lateinit var dbHelper: DBHelper
    override fun onTokenRefresh() {
        super.onTokenRefresh()
        dbHelper = DBHelper(this)
        NetworkUtil.initRetrofit(true, Constants.DEFAULT_LOG_LEVEL,this)
        val refreshedToken = FirebaseInstanceId.getInstance().token
        dbHelper.registerToken(refreshedToken!!)
        CommonUtils.registerTokenToServer(refreshedToken, this)
    }

}
