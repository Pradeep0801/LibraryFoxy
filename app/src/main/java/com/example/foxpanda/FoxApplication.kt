package com.example.foxpanda

import android.app.Application
import android.view.ViewGroup

public  class FoxApplication : Application() {

    companion object {
        var instance = FoxApplication()



    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    internal fun instance(): FoxApplication {
        return instance
    }
}