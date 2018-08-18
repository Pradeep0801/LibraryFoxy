package com.sdk.foxpanda.WorkManager

import androidx.work.Worker
import com.sdk.foxpanda.utils.CommonUtils


class PandaInfoWorkManager : Worker() {

    override fun doWork(): Result {
        CommonUtils.updateDeviceInfo(applicationContext)
        return Result.SUCCESS
    }
}