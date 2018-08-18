package com.sdk.foxpanda.WorkManager

import androidx.work.Worker
import com.sdk.foxpanda.PandaPermissions.PandaShowLocationToFox
import com.sdk.foxpanda.applications.FoxApplication


class LocationWorkManager : Worker() {

    override fun doWork(): Result {

       PandaShowLocationToFox.retrivePandaTrack(FoxApplication.instance.activity!!,applicationContext)
        return Result.SUCCESS
    }
}