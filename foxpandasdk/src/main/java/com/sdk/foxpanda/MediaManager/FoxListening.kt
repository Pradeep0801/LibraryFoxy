package com.sdk.foxpanda.MediaManager

import android.Manifest
import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.sdk.foxpanda.main.FoxPanda
import java.io.IOException
import java.util.*


class FoxListening  : AppCompatActivity(){

//    companion object {
//        var mediaManager = MediaRecorder()
//        var mediaPath : String = ""
//        var context1 : Context? = null
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        //initFeatures(this)
//    }

//     override fun onStart() {
//        super.onStart()
//         initFeatures(this)
//    }
//    fun startListening(context: Context) {
//        mediaPath = Environment.getExternalStorageDirectory().getAbsolutePath() + System.currentTimeMillis()+"/recording.3gp"
//        mediaManager = MediaRecorder()
//        mediaManager.setAudioSource(MediaRecorder.AudioSource.MIC)
//        mediaManager.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
//        mediaManager.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
//        mediaManager.setOutputFile(mediaPath)
//
//        try {
//            mediaManager.prepare()
//            mediaManager.start()
//        } catch (ise: IllegalStateException) {
//            // make something ...
//            Log.e("ise", ise.printStackTrace().toString())
//        } catch (ioe: IOException) {
//            // make something
//
//            Log.e("ioe", ioe.printStackTrace().toString())
//        }
//
//        Log.e("media-manager","media-manager-start-HAHAHAAHAHAHAHA")
//
//    }

//   fun initFeatures(context: Context){
//        context1 = context
//       // check for usage stat permission
//       @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//       if (!isAccessGranted(context1!!)){
//           val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
//            startActivityForResult(intent , FoxPanda.REQUEST_APP_USAGE_PERMISSIONS)
//       }
//       else{
//           Log.e("Permission Added","2------->>>>>>>>>>>>>>>>")
//           if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
//               FoxPanda.retrieveStats(context1!!)
//           }
//       }
//
//       // check for write external storage and media recorder permission
////       if (checkAndRequestPermissions(context1!!)){
////           Log.e("Permission Added","1------->>>>>>>>>>>>>>>>")
////           startListening(context1!!)
////       }
//    }

    // checking for Record Audio / Write external storage permission for Recording Audio

// fun checkAndRequestPermissions(context: Context): Boolean {
//        val writepermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        val permissionRecordAudio = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
//
//        val listPermissionsNeeded = ArrayList<String>()
//
//        if (writepermission != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        }
//        if (permissionRecordAudio != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO)
//        }
//
//        if (!listPermissionsNeeded.isEmpty()) {
//            ActivityCompat.requestPermissions(context as Activity, listPermissionsNeeded.toTypedArray(), FoxPanda.REQUEST_ID_MULTIPLE_PERMISSIONS)
//            return false
//        }
//        return true
//    }
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == FoxPanda.REQUEST_ID_MULTIPLE_PERMISSIONS){
//            Log.e("Permission Added1","1------->>>>>>>>>>>>>>>>")
//            startListening(context1!!)
//
//        }
//        else if (requestCode == FoxPanda.REQUEST_APP_USAGE_PERMISSIONS )
//        {
//            // GET USAGE STAT OF APPS INSTALLED BY USER
//           // checkAndRequestPermissions(context1!!)
//            Log.e("Permission Added2","2------->>>>>>>>>>>>>>>>")
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
//                FoxPanda.retrieveStats(context1!!)
//                finish()
//            }
//        }
//    }
//
//


}