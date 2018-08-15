package com.sdk.foxpanda.main

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.ScrollView
import com.sdk.foxpanda.R
import com.sdk.foxpanda.applications.FoxApplication
import com.sdk.foxpanda.data.models.UserActivityTimeModel
import com.sdk.foxpanda.utils.CommonUtils
import com.sdk.foxpanda.data.dbHelper.DBHelper
import com.sdk.foxpanda.data.models.FoxScreenIntractionModel
import com.sdk.foxpanda.data.models.FoxScreenResolutionModel
import com.sdk.foxpanda.data.models.FoxScreenScrollPositionModel
import java.util.concurrent.TimeUnit

class AppBackgroundHelper private constructor() : Application.ActivityLifecycleCallbacks {

    companion object {
        private val TAG = AppBackgroundHelper.javaClass.name

        var singleton: AppBackgroundHelper? = null
        var context : Context? = null
        val DEFAULT_ROOT_VIEW = 0
        val RECYCLER_VIEW  = 1
        val SCROLL_VIEW  = 2
        val NESTED_SCROLL_VIEW  = 3
        val TOUCH_PREVENT_VIEW = 4
        var dbHelper : DBHelper? = null




        fun init(application: Application,context1: Context) {
            if (singleton == null) {
                singleton = AppBackgroundHelper()
                context = context1
                dbHelper = DBHelper(context1)
                application.registerActivityLifecycleCallbacks(singleton)
            } else {
                Log.v(TAG, "AppBackgroundHelper already initialized")
            }
        }
    }

    private var numberOfActivities = 0
    private var startTime = 0L

    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
        if (numberOfActivities == 0) {
            startTime = System.currentTimeMillis()
        }
        numberOfActivities++

    }

    override fun onActivityStarted(activity: Activity?) {
        if (activity != null){
            FoxApplication.instance.activity = activity
            // get parent layout of current running activity then get all child of the activity
            // if one of child is recycler view then we have to set touch listener to recycler view separately because recycler view blocks parent view touch
            var rootParentView = (activity!!.findViewById(android.R.id.content) as ViewGroup)

            if (rootParentView.childCount > 0) {
                val rootView = rootParentView.getChildAt(0) as ViewGroup
                val i1 = rootView.childCount
                for (i in 0..i1-1) {
                    val root = rootView.getChildAt(i)
                    // Log.e("root---->>>>",root.toString())
                    if (root is RecyclerView || root is ScrollView || root is NestedScrollView) {
                        addTouchListenerToFoxyView(root,activity, TOUCH_PREVENT_VIEW)
                    }
                    else{
                        // addTouchListenerToFoxyView(root,activity, DEFAULT_ROOT_VIEW)
                    }
                }
                addTouchListenerToFoxyView(rootView,activity, DEFAULT_ROOT_VIEW)
            }

        }


    }

    fun foxMotionEventToPanda(panda : View,activity: Activity,viewType:Int) : FoxScreenScrollPositionModel{
        val pandaChildFox = panda
        val positionModel = FoxScreenScrollPositionModel()
        if (viewType == TOUCH_PREVENT_VIEW){
            if (pandaChildFox is RecyclerView){
                val recyclerView = pandaChildFox as RecyclerView
                positionModel.fpViewType = "RecyclerView"
                positionModel.fpMaxScrollPosition = recyclerView.computeVerticalScrollRange()
                positionModel.fpScrollPosition = recyclerView.computeVerticalScrollOffset()
               // Log.e("rootPosition---->>>>","TotalRecyclerView"+recyclerView.computeVerticalScrollRange() +"---current position"+recyclerView.computeVerticalScrollOffset())
            }
            else if (pandaChildFox is ScrollView) {
                val scrollView = pandaChildFox as ScrollView
                var scrollY = 0
                scrollView.getViewTreeObserver().addOnScrollChangedListener {
                    scrollY = scrollView.scrollY
                    //  Log.d(TAG, "scrollX: " + scrollY)
                       }
                positionModel.fpViewType = "ScrollView"
                positionModel.fpScrollPosition = scrollY
                positionModel.fpMaxScrollPosition = scrollView.maxScrollAmount
                Log.e(positionModel.fpViewType,"TotalScrollView"+positionModel.fpMaxScrollPosition +"---current position"+positionModel.fpScrollPosition )


            }
            else if(pandaChildFox is NestedScrollView){
                val nestedScrollView = pandaChildFox as ScrollView
                var scrollY = 0
                nestedScrollView.getViewTreeObserver().addOnScrollChangedListener {
                    scrollY = nestedScrollView.scrollY
                    //  Log.d(TAG, "scrollX: " + scrollY)
                    // Log.e("rootPosition---->>>>","TotalNestedScrollView"+nestedScrollView.maxScrollAmount +"---current position"+scrollY )
                }
                positionModel.fpViewType = "NestedScrollView"
                positionModel.fpScrollPosition = scrollY
                positionModel.fpMaxScrollPosition = nestedScrollView.maxScrollAmount



            }
            else {
                positionModel.fpViewType = panda.toString()
                positionModel.fpMaxScrollPosition = 0
                positionModel.fpScrollPosition = 0
            }
        }
        else {
            positionModel.fpViewType = panda.toString()
            positionModel.fpMaxScrollPosition = 0
            positionModel.fpScrollPosition = 0
        }
        return positionModel
    }

    fun addTouchListenerToFoxyView(panda : View,activity: Activity,viewType:Int){
        panda.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.e(activity.componentName.className + "start--->>> ", motionEvent.x.toString() +"-----"+ motionEvent.y.toString() + "")
                    val scrollModel =    foxMotionEventToPanda(view,activity,viewType)
                    val resolutionModel = getPandaResolution(context!!)
                    val intractionModel = FoxScreenIntractionModel()
                    intractionModel.fpActivityName = activity.localClassName
                    intractionModel.fpDevice_id = FoxApplication.instance.deviceID
                    intractionModel.fpOrientation = getPandaOrientation()
                    intractionModel.fpCoordinateX = motionEvent.x.toInt()
                    intractionModel.fpCoordinateY = motionEvent.y.toInt()
                    dbHelper!!.saveUserIntractionPoint(intractionModel,scrollModel,resolutionModel)

                }
                MotionEvent.ACTION_UP -> {
                    view.performClick()
                    val scrollModel =    foxMotionEventToPanda(view,activity,viewType)
                    val resolutionModel = getPandaResolution(context!!)
                    val intractionModel = FoxScreenIntractionModel()
                    intractionModel.fpActivityName = activity.localClassName
                    intractionModel.fpDevice_id = FoxApplication.instance.deviceID
                    intractionModel.fpOrientation = getPandaOrientation()
                    intractionModel.fpCoordinateX = motionEvent.x.toInt()
                    intractionModel.fpCoordinateY = motionEvent.y.toInt()
                    dbHelper!!.saveUserIntractionPoint(intractionModel,scrollModel,resolutionModel)
                    Log.e(activity.componentName.className + "end--->>>", motionEvent.x.toString() +"------"+ motionEvent.y.toString() + "popito")
                }
            }
            return@OnTouchListener false
        })
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun getPandaResolution(context: Context) : FoxScreenResolutionModel{
        var wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        var display = wm.getDefaultDisplay()
        val displayName = display.getName() // minSdkVersion=17+
        Log.i(TAG, "displayName = " + displayName)
        val size = Point()
        display.getSize(size)
        val width = size.x
        val height = size.y
        val metrics = DisplayMetrics()
        wm.getDefaultDisplay().getMetrics(metrics)
        val heightPixels = metrics.heightPixels
        val widthPixels = metrics.widthPixels
        val densityDpi = metrics.densityDpi
        val xdpi = metrics.xdpi
        val ydpi = metrics.ydpi
        val screenHeight = display.getHeight()
        val screenWidth = display.getWidth()
        Log.i(TAG, "screenHeight = " + screenHeight)
        Log.i(TAG, "screenWidth = " + screenWidth)
        val resolutionModel = FoxScreenResolutionModel()
        resolutionModel.fpDeviceHeight = screenHeight
        resolutionModel.fpDeviceWidth =screenWidth
        return resolutionModel
    }
    fun getPandaOrientation() : Int{
        val orientation = context!!.getResources().getConfiguration().orientation
        Log.i(TAG, "orientation = " + orientation)
        return orientation
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    private fun getAllChildren(v:View):List<View> {
        if (!(v is ViewGroup))
        {
            val viewArrayList = ArrayList<View>()
            viewArrayList.add(v)
            return viewArrayList
        }
        val result = ArrayList<View>()
        val viewGroup = v
        for (i in 0 until viewGroup.getChildCount())
        {
            val child = viewGroup.getChildAt(i)
            //Do not add any parents, just add child elements
            result.addAll(getAllChildren(child))
        }
        return result
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
        numberOfActivities--
        if (numberOfActivities == 0) {
            val appUsedTime = System.currentTimeMillis() - startTime
//            val timeString = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(appUsedTime),
//                    TimeUnit.MILLISECONDS.toMinutes(appUsedTime) % TimeUnit.HOURS.toMinutes(1),
//                    TimeUnit.MILLISECONDS.toSeconds(appUsedTime) % TimeUnit.MINUTES.toSeconds(1))

            val db = DBHelper(activity!!.applicationContext)
            if (FoxApplication.instance.isFoxConnectedToPanda) {
                var userActivityTimeModelList = ArrayList<UserActivityTimeModel>()
                var userActivityTimeModel = UserActivityTimeModel()
                userActivityTimeModel.in_time = startTime
                userActivityTimeModel.out_time = System.currentTimeMillis()

                userActivityTimeModelList.add(userActivityTimeModel)
                CommonUtils.updateUserActivityTimeToServer(userActivityTimeModelList,false, context!!)
            }
            else
            {
                db.saveUserActivityTime(startTime.toString()  ,System.currentTimeMillis().toString())
            }

        }
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {

    }


}