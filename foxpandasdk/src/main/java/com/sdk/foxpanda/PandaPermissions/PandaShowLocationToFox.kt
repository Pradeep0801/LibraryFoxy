package com.sdk.foxpanda.PandaPermissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.support.annotation.NonNull
import android.util.Log
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sdk.foxpanda.WorkManager.FoxWorkManager
import com.sdk.foxpanda.WorkManager.LocationWorkManager
import com.sdk.foxpanda.applications.FoxApplication
import com.sdk.foxpanda.data.dbHelper.DBHelper
import com.sdk.foxpanda.data.models.PandaLocationComponent
import com.sdk.foxpanda.utils.CommonUtils
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit


internal object  PandaShowLocationToFox {


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val STORAGE = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    val RC_LOCATION_PERM = 124

    fun retrivePandaTrack(activity: Activity,context: Context)
    {
        val dbHelper = DBHelper(context)
        if (EasyPermissions.hasPermissions(context, *STORAGE)) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation
                    .addOnSuccessListener { location : Location? ->
                        // Got last known location. In some rare situations this can be null.
                        Log.e("Current LocationLat",location!!.latitude.toString())
                        Log.e("Current LocationLong",location.longitude.toString())
                        val pandaLocationComponent = PandaLocationComponent(location.longitude.toString(),location.latitude.toString(), CommonUtils.getCurrentEpochTime().toString())
                        val pandaTracked =  dbHelper.savePandaLocation(pandaLocationComponent)
                        if (pandaTracked && FoxApplication.instance.isFoxConnectedToPanda){
                            val pandaFootPrint = dbHelper.getLazyPandaLocationTrack()
                            CommonUtils.updatePandaLocationFox(pandaFootPrint,true,context)
                        }

                    }

        } else {
            val intent = Intent(context,PandaGivePermissionToFox::class.java)
            activity.startActivity(intent)
        }

    }

}

internal object GetFoxUsageStat{
    fun updateFoxUsageToServer(context: Context){

    }
}