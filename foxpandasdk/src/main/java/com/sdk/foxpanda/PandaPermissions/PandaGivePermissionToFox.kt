package com.sdk.foxpanda.PandaPermissions

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.sdk.foxpanda.R
import com.sdk.foxpanda.applications.FoxApplication
import com.sdk.foxpanda.utils.CommonUtils
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest

class PandaGivePermissionToFox : AppCompatActivity(),EasyPermissions.PermissionCallbacks,EasyPermissions.RationaleCallbacks{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.background
        var perms = arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION)
        EasyPermissions.requestPermissions(
                this,
              "Location Permission",
                PandaShowLocationToFox.RC_LOCATION_PERM,
                *PandaShowLocationToFox.STORAGE)


    }



    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
//        Log.e("yo","denied---------->>>>>")
//      if(!CommonUtils.pandaHasUsagePermission(this)){
//          CommonUtils.pandaAskForUsagePermission(this)
//          finish()
//      }
//        else{
//          finish()
//      }
        finish()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.e("yo","granted---------->>>>>")
//        if(!CommonUtils.pandaHasUsagePermission(this)){
//            PandaShowLocationToFox.retrivePandaTrack(FoxApplication.instance.activity!!,applicationContext)
//            CommonUtils.pandaAskForUsagePermission(this)
//            finish()
//        }
//        else{
//            finish()
//        }
        PandaShowLocationToFox.retrivePandaTrack(FoxApplication.instance.activity!!,applicationContext)
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            @NonNull permissions: Array<String>,
                                            @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onRationaleDenied(requestCode: Int) {
        finish()
    }

    override fun onRationaleAccepted(requestCode: Int) {

    }

}