package com.sdk.foxpanda.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.sdk.foxpanda.applications.FoxApplication
import com.sdk.foxpanda.main.FoxPanda

class ConnectivityReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, arg1: Intent) {
        var connection  = FoxApplication.instance
        connection.isFoxConnectedToPanda = isConnectedOrConnecting(context)
      //  FoxPanda.FPLogger("online", FoxApplication.instance.isFoxConnectedToPanda.toString())
    }

    private fun isConnectedOrConnecting(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnectedOrConnecting
    }


}