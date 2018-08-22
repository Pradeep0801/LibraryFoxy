package com.example.foxpanda

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import com.sdk.foxpanda.main.AppBackgroundHelper
import com.sdk.foxpanda.main.FoxPanda
import java.util.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import java.net.HttpURLConnection
import java.net.URL
import android.os.StrictMode
import android.support.v4.content.ContextCompat
import android.support.v4.view.VelocityTrackerCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.view.*


class MainActivity : AppCompatActivity() {
    var param = HashMap<String, String>()
    var start : Long = 0
    var end : Long = 0
    val myApp = FoxApplication().instance()
    var recyclerView: RecyclerView? = null
    var adapter: RecyclerView.Adapter<*>? = null
    var recyclerViewLayoutManager: RecyclerView.LayoutManager? = null
  // lateinit var rLayout : RelativeLayout
    companion object {
        var uniqueID : String? = null
       var  PREF_UNIQUE_ID : String = "PREF_UNIQUE_ID"
        private val DEBUG_TAG = "Velocity"
        private var mVelocityTracker: VelocityTracker? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FoxPanda.initialize(this,"342874982374923847")
        AppBackgroundHelper.init(myApp,this)
        val map = HashMap<String,String>()
        map.put("prefered_language_one","Hindi")
        map.put("prefered_language_three","English")
        FoxPanda.setTagToPanda(this,map)

        val notification = FoxPanda.getNotificationList(this)
        for(i in notification){
            Log.e("title",i.notificationTitle)
            Log.e("image",i.notificationImage)
        }

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
       // scheduleNotification(getNotification("Test Schedule Notification"), 5000);

        recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        //rLayout = findViewById(R.id.rl)

        // Passing the column number 1 to show online one column in each row.
        recyclerViewLayoutManager = GridLayoutManager(this, 1)

        recyclerView!!.setLayoutManager(recyclerViewLayoutManager)

        adapter = AppsAdapter(this, ApkInfoExtractor(this).GetAllInstalledApkInfo())

        recyclerView!!.setAdapter(adapter)




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


    private fun scheduleNotification(notification: Notification, delay:Int) {
        val notificationIntent = Intent(this, NotificationPublisher::class.java)
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1)
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val futureInMillis = SystemClock.elapsedRealtime() + delay
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent)
    }

//    private fun getNotification(content: String): Notification {
//        val icon = BitmapFactory.decodeResource(resources, R.drawable.large_icon)
//        val builder = Notification.Builder(this)
//        builder.setContentTitle("Scheduled Notification")
//        builder.setContentText(content)
//        builder.setSmallIcon(R.drawable.app_icon)
//        //builder.setLargeIcon(getBitmapfromUrl("https://s3-us-west-2.amazonaws.com/foxpanda-documentavion/documentation/android/IMG-20180717-WA0006.png"))
//        return builder.build()
//    }




    fun getBitmapfromUrl(imageUrl: String): Bitmap? {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            return BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }






}
