package com.sdk.foxpanda.services

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.sdk.foxpanda.constants.Constants
import com.sdk.foxpanda.data.dbHelper.DBHelper
import com.sdk.foxpanda.utils.CommonUtils
import com.sdk.foxpanda.utils.NetworkUtil

class FCMIdInstance: FirebaseInstanceIdService() {
    internal lateinit var dbHelper: DBHelper
    override fun onTokenRefresh() {
        super.onTokenRefresh()
        dbHelper = DBHelper(this)
        val refreshedToken = FirebaseInstanceId.getInstance().token
        dbHelper.registerToken(refreshedToken!!)
    }

}
