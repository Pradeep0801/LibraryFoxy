package com.sdk.foxpanda.applications


public class FoxApplication private constructor() {
        init { println("This ($this) is a singleton") }

        private object Holder { val INSTANCE = FoxApplication() }

        companion object {
            val instance: FoxApplication by lazy { Holder.INSTANCE }
        }
        var isFoxConnectedToPanda:Boolean = false
        var deviceID : String = ""

    }

