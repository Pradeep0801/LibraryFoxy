package com.sdk.foxpanda.PandaPermissions

import android.app.Activity
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.sdk.foxpanda.utils.CommonUtils
import pub.devrel.easypermissions.EasyPermissions

class PandaGivePermissionToFox : Activity(),EasyPermissions.PermissionCallbacks{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.background
        EasyPermissions.requestPermissions(
                this,
                "Location Permission",
                PandaShowLocationToFox.RC_LOCATION_PERM,
                *PandaShowLocationToFox.STORAGE)
    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.e("yo","denied---------->>>>>")
      if(!CommonUtils.pandaHasUsagePermission(this)){
          CommonUtils.pandaAskForUsagePermission(this)
          finish()
      }
        else{
          finish()
      }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.e("yo","granted---------->>>>>")
        if(!CommonUtils.pandaHasUsagePermission(this)){
            CommonUtils.pandaAskForUsagePermission(this)
            finish()
        }
        else{
            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            @NonNull permissions: Array<String>,
                                            @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}