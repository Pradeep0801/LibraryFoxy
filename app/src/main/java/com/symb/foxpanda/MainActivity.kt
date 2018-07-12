package com.symb.foxpanda

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.symb.foxpandasdk.main.AppBackgroundHelper
import com.symb.foxpandasdk.main.FoxPanda
import java.util.*

class MainActivity : AppCompatActivity() {
    var param = HashMap<String, String>()
    var start : Long = 0
    var end : Long = 0
    val myApp = FoxApplication().instance()

    companion object {
        var uniqueID : String? = null
       var  PREF_UNIQUE_ID : String = "PREF_UNIQUE_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FoxPanda.initialize(this)
        AppBackgroundHelper.init(myApp,this)



    }

    @Synchronized fun id(context:Context):String {
        if (uniqueID == null)
        {
            val sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE)
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null)
            if (uniqueID == null)
            {
                uniqueID = UUID.randomUUID().toString()
                val editor = sharedPrefs.edit()
                editor.putString(PREF_UNIQUE_ID, uniqueID)
                editor.commit()
            }
        }
        return uniqueID as String
    }

}
