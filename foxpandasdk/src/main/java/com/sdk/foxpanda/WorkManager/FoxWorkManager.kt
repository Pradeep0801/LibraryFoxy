package com.sdk.foxpanda.WorkManager

import android.util.Log
import androidx.work.Worker
import com.sdk.foxpanda.main.FoxPanda
import com.sdk.foxpanda.utils.CommonUtils

class FoxWorkManager : Worker() {

    override fun doWork(): Result {
        if (CommonUtils.pandaHasUsagePermission(applicationContext)){
            CommonUtils.foxGetAppUsageTrack(applicationContext)
        }
        return Result.SUCCESS
    }

   private fun scheduleUpdate(){
       Log.e("Hello","work manager testing --->>>>" + System.currentTimeMillis()/1000);

    }


}

