package com.symb.foxpanda

import android.app.Application

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