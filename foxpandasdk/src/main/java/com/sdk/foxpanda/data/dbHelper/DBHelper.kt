package com.sdk.foxpanda.data.dbHelper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.provider.Settings
import com.google.firebase.iid.FirebaseInstanceId
import com.sdk.foxpanda.applications.FoxApplication
import com.sdk.foxpanda.constants.Constants
import com.sdk.foxpanda.data.models.*


class DBHelper(var context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE)
        db.execSQL(SQL_CREATE_TOKEN_TABLE)
        db.execSQL(SQL_CREATE_CLASS_TABLE)
        db.execSQL(SQL_CREATE_ALL_CLASS_TABLE)
        db.execSQL(SQL_CREATE_IS_INFO_UPDATED)
        db.execSQL(SQL_CREATE_USER_ACTIVITY_TIME)
        db.execSQL(SQL_CREATE_LIST_OF_USER_INSTALLED_APP)
        db.execSQL(SQL_CREATE_APP_USAGE_STATS_TABLE)
        db.execSQL(SQL_CREATE_NOTIFICATION_ACTION_TABLE)
        db.execSQL(SQL_CREATE_NOTIFICATION_LIST_TABLE)
        db.execSQL(SQL_CREATE_USER_INTERACTION_COORDINATE_TABLE)
        db.execSQL(SQL_CREATE_PANDA_LOCATION_TABLE)
        db.execSQL(SQL_CREATE_PLATFORM_ID)
        db.execSQL(SQL_CREATE_PACKAGE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL(SQL_DELETE_TABLE)
        db.execSQL(SQL_DELETE_TOKEN_TABLE)
        db.execSQL(SQL_DELETE_CLASS_TABLE)
        db.execSQL(SQL_DELETE_ALL_CLASS_TABLE)
        db.execSQL(SQL_DELETE_USER_ACTIVITY_TIME)
        db.execSQL(SQL_DELETE_USER_INSTALLED_APP)
        db.execSQL(SQL_DELETE_USAGE_STATS_TABLE)
        db.execSQL(SQL_DELETE_NOTIFICATION_ACTION_TABLE)
        db.execSQL(SQL_DELETE_INFO_UPDATED_FLAG)
        db.execSQL(SQL_DELETE_USER_INTERACTION_COORDINATE_TABLE)
        db.execSQL(SQL_DELETE_NOTIFICATION_LIST_TABLE)
        db.execSQL(SQL_DELETE_PANDA_LOCATION_TABLE)
        db.execSQL(SQL_DELETE_PLATFORM_ID_TABLE)
        db.execSQL(SQL_DELETE_PACKAGE_NAME_TABLE)
        onCreate(db)
    }

    @Throws(SQLiteConstraintException::class)
    fun registerToken(token: String): Boolean {
        val db = writableDatabase

        val value = ContentValues()
        value.put(Constants.FIREBASE_TOKEN, token)

        val result = db.insert(Constants.TOKEN_TABLE, null, value)
        db.close()
        return result.toInt() != -1
    }

    @Throws(SQLiteConstraintException::class)
    fun savePlatFormID(platfomID: String): Boolean {
        val db = writableDatabase

        val value = ContentValues()
        value.put(Constants.PLATFORM_ID, platfomID)

        val result = db.insert(Constants.PLATFORM_ID_TABLE, null, value)
        db.close()
        return result.toInt() != -1
    }

    @Throws(SQLiteConstraintException::class)
    fun saveParentPackage(packageName: String): Boolean {
        val db = writableDatabase

        val value = ContentValues()
        value.put(Constants.PARENT_PACKAGE_NAME, packageName)

        val result = db.insert(Constants.PACKAGE_TABLE, null, value)
        db.close()
        return result.toInt() != -1
    }



    // save panda location to database
    @Throws(SQLiteConstraintException::class)
    fun savePandaLocation(pandaLocationComponent: PandaLocationComponent): Boolean {
        val db = writableDatabase
        val value = ContentValues()
        value.put(Constants.DEVICE_ID, FoxApplication.instance.deviceID)
        value.put(Constants.PANDA_LONGITUDE,pandaLocationComponent.pandaLongitude)
        value.put(Constants.PANDA_LATITUDE,pandaLocationComponent.pandaLatitude)
        value.put(Constants.PANDA_TIMESTAMP,pandaLocationComponent.pandaTimeStamp)

        val result = db.insert(Constants.USER_LOCATION_TABLE, null, value)
        db.close()
        return result.toInt() != -1
    }



// save fox interaction data to db
    @Throws(SQLiteConstraintException::class)
    fun saveUserIntractionPoint(intractionModel: FoxScreenIntractionModel,
                                scrollModel: FoxScreenScrollPositionModel,
                                resolutionModel: FoxScreenResolutionModel): Boolean {
        val db = writableDatabase
        val value = ContentValues()
        value.put(Constants.DEVICE_ID, intractionModel.fpDevice_id)
        value.put(Constants.FOX_ACTIVITY_NAME,intractionModel.fpActivityName)
        value.put(Constants.FOX_LAYOUT_NAME,scrollModel.fpViewType)
        value.put(Constants.FOX_ORIENTATION,intractionModel.fpOrientation)
        value.put(Constants.FOX_DEVICE_HEIGHT,resolutionModel.fpDeviceHeight)
        value.put(Constants.FOX_DEVICE_WIDTH,resolutionModel.fpDeviceWidth)
        value.put(Constants.FOX_SCROLL_POSITION,scrollModel.fpScrollPosition)
        value.put(Constants.FOX_MAX_SCROLL_POSITION,scrollModel.fpMaxScrollPosition)
        value.put(Constants.FOX_INTERACTION_COORDINATE_X,intractionModel.fpCoordinateX)
        value.put(Constants.FOX_INTERACTION_COORDINATE_Y,intractionModel.fpCoordinateY)

        val result = db.insert(Constants.INTERACTION_COORDINATE_TABLE, null, value)
        db.close()
        return result.toInt() != -1
    }

    @Throws(SQLiteConstraintException::class)
    internal fun saveNotificationAction(notificationAction : NotificationActionModel): Boolean {
        val db = writableDatabase
        val value = ContentValues()
        value.put(Constants.DEVICE_ID, FoxApplication.instance.deviceID)
        value.put(Constants.APP_NOTIFICATION_ID,notificationAction.notificationId)
        value.put(Constants.APP_NOTIFICATION_RECIEVED_AT,notificationAction.receivedAt)
        value.put(Constants.APP_NOTIFICATION_OPEN_AT,notificationAction.openedAt)
        value.put(Constants.APP_NOTIFICATION_CLEARED_AT,notificationAction.clearedAt)


        val result = db.insert(Constants.NOTIFICATION_ACTION_TABLE, null, value)
        db.close()
        return result.toInt() != -1
    }


    @Throws(SQLiteConstraintException::class)
   public fun saveNotificationList(notification : NotificationModel): Boolean {
        val db = writableDatabase
        val value = ContentValues()
        value.put(Constants.APP_NOTIFICATION_ID, notification.notificationId)
        value.put(Constants.APP_NOTIFICATION_TITLE,notification.notificationTitle)
        value.put(Constants.APP_NOTIFICATION_CONTENT,notification.notificationContent)
        value.put(Constants.APP_NOTIFICATION_IMAGE,notification.notificationImage)
        value.put(Constants.APP_NOTIFICATION_RECIEVED_AT,notification.notificationTime)

        val result = db.insert(Constants.NOTIFICATION_LIST_TABLE_NAME, null, value)
        db.close()
        return result.toInt() != -1
    }

    // INSERT VALUE IN DATABASE --- APP USAGE -- APP INSTALLED BY USER -- REQUIRED API LEVEL - 21 - LOLLIPOP
    @Throws(SQLiteConstraintException::class)
    internal fun saveInstalledAppUsage(appUsageStats: AppUsageStats): Boolean {
        val db = writableDatabase
        val value = ContentValues()
        value.put(Constants.APP_PACKAGE_NAME,appUsageStats.app_package_name)
        value.put(Constants.APP_GET_FIRST_TIME_STAMP,appUsageStats.in_time)
        value.put(Constants.APP_GET_LAST_TIME_USED,appUsageStats.out_time)
        value.put(Constants.APP_FOREGROUND_TIME,appUsageStats.app_total_time)

        val result = db.insert(Constants.APP_USAGE_STATS_TABLE, null, value)
        db.close()
        return result.toInt() != -1
    }

    //INSERT VALUE IN DATABASE --- LIST OF APPLICATION INSTALLED BY USER -- REQUIRED API LEVEL -- 16(FOR ALL VALUES ) -- 21 (FOR APP FOREGROUND TIME)
    @Throws(SQLiteConstraintException::class)
    fun saveInstalledApp(installedApp: ListOfInstalledApp): Boolean {
        val db = writableDatabase
        val value = ContentValues()
        value.put(Constants.APP_PACKAGE_NAME,installedApp.app_package_name)
        value.put(Constants.APP_INSTALLED_NAME,installedApp.app_installed_name)
        value.put(Constants.APP_LAST_OPEN_AT,installedApp.lastOpenedAt)
        value.put(Constants.APP_INSTALLED_AT,installedApp.installedAt)
        value.put(Constants.APP_INSTALLED_VERSION,installedApp.appVersion)



        val result = db.insert(Constants.APP_INSTALLED_BY_USER_TABLE, null, value)
        db.close()
        return result.toInt() != -1
    }



    @Throws(SQLiteConstraintException::class)
    fun saveUserActivityTime(startTime : String , endTime : String): Boolean {
        val db = writableDatabase

        val value = ContentValues()
        value.put(Constants.USER_ACTIVITY_START_TIME, startTime)
        value.put(Constants.USER_ACTIVITY_END_TIME,endTime)

        val result = db.insert(Constants.USER_ACTIVITY_TIME_TABLE, null, value)
        db.close()
        return result.toInt() != -1
    }

    @Throws(SQLiteConstraintException::class)
    fun saveIsInfoUpdated(isUpdated: Int ): Boolean {
        val db = writableDatabase

        val value = ContentValues()
        value.put(Constants.INFO_UPDATE_FLAG, isUpdated)

        val result = db.insert(Constants.IS_INFO_UPDATED, null, value)
        db.close()
        return result.toInt() != -1
    }

    fun getIsInfoUpdated(): Int {
        var cursor: Cursor? = null
        val db = writableDatabase

        var isUpdated : Int = 0
        try {
            cursor = db.rawQuery("select * from " + Constants.IS_INFO_UPDATED, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_IS_INFO_UPDATED)
            return 0
        }

        if(cursor!!.moveToFirst()) {
            isUpdated = cursor!!.getInt(cursor!!.getColumnIndex(Constants.INFO_UPDATE_FLAG))
        }
        db.close()
        return isUpdated
    }


    fun getToken(): String {
        var cursor1: Cursor? = null
        val db = writableDatabase
        var token = ""
        try {
            cursor1 = db.rawQuery("select * from " + Constants.TOKEN_TABLE, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_TOKEN_TABLE)
            return ""
        }

        if(cursor1!!.moveToFirst()) {
            token = cursor1!!.getString(cursor1!!.getColumnIndex(Constants.FIREBASE_TOKEN))
        }
        db.close()
        return token
    }

    fun getPlatFormID(): String {
        var cursor12: Cursor? = null
        val db = writableDatabase
        var platFormID = ""
        try {
            cursor12 = db.rawQuery("select * from " + Constants.PLATFORM_ID_TABLE, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_PLATFORM_ID)
            return ""
        }

        if(cursor12!!.moveToFirst()) {
           platFormID = cursor12!!.getString(cursor12!!.getColumnIndex(Constants.PLATFORM_ID))
        }
        db.close()
        return platFormID
    }

    fun getParentPackage(): String {
        var cursor12: Cursor? = null
        val db = writableDatabase
        var packageName = ""
        try {
            cursor12 = db.rawQuery("select * from " + Constants.PACKAGE_TABLE, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_PACKAGE_TABLE)
            return ""
        }

        if(cursor12!!.moveToFirst()) {
            packageName = cursor12!!.getString(cursor12!!.getColumnIndex(Constants.PARENT_PACKAGE_NAME))
        }
        db.close()
        return packageName
    }


    @Throws(SQLiteConstraintException::class)
    fun deleteTokens(token: String): Boolean {
        val db = writableDatabase
        val selection = Constants.FIREBASE_TOKEN + " LIKE ?"
        val selectionArgs = arrayOf(token)
        val result = db.delete(Constants.TOKEN_TABLE, selection, selectionArgs)
        db.close()
        return result != -1
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteUserActivityTime() {
        val db = writableDatabase
        db.execSQL("delete from " + Constants.USER_ACTIVITY_TIME_TABLE)
    }
    @Throws(SQLiteConstraintException::class)
    fun deletePlatFormID() {
        val db = writableDatabase
        db.execSQL("delete from " + Constants.PLATFORM_ID_TABLE)
    }
    @Throws(SQLiteConstraintException::class)
    fun deleteParentPackage() {
        val db = writableDatabase
        db.execSQL("delete from " + Constants.PACKAGE_TABLE)
    }

    fun updateNotificationActionToDB(notificationAction: NotificationActionModel): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(Constants.APP_NOTIFICATION_OPEN_AT,notificationAction.openedAt)
        values.put(Constants.APP_NOTIFICATION_CLEARED_AT, notificationAction.clearedAt)
       // val retVal = db.update("USER", values, "userID = " + users.userID, null
        val _success = db.update(Constants.NOTIFICATION_ACTION_TABLE, values, Constants.APP_NOTIFICATION_ID + " = "+ notificationAction.notificationId,null)
        db.close()
        return Integer.parseInt("$_success") != -1
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteNotificationAction() {
        val db = writableDatabase
        db.execSQL("delete from " + Constants.NOTIFICATION_ACTION_TABLE)
    }

    @Throws(SQLiteConstraintException::class)
    fun deletePandaLocationTrack() {
        val db = writableDatabase
        db.execSQL("delete from " + Constants.USER_LOCATION_TABLE)
    }


    @Throws(SQLiteConstraintException::class)
    fun registerEvent(eventName: String): Boolean {
        val db = writableDatabase
        val d_id = android.provider.Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)

        val values = ContentValues()
        values.put(Constants.FIREBASE_TOKEN, FirebaseInstanceId.getInstance().token)
        values.put(Constants.DEVICE_ID, d_id)
        values.put(Constants.EVENT_NAME, eventName)
        values.put(Constants.TIMESTAMP, System.currentTimeMillis())

        val newRowId = db.insert(Constants.TABLE_NAME, null, values)
        db.close()
        return newRowId.toInt() != -1
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteEvent(eventName: String): Boolean {
        val db = writableDatabase
        val selection = Constants.EVENT_NAME + " LIKE ?"
        val selectionArgs = arrayOf(eventName)
        val result = db.delete(Constants.TABLE_NAME, selection, selectionArgs)
        db.close()
        return result != -1
    }

    fun getAllEvents(): ArrayList<FirebaseInfo> {
        var cursor2: Cursor? = null
        val firebaseInfo = ArrayList<FirebaseInfo>()
        val db = writableDatabase
        try {
            cursor2 = db.rawQuery("select * from " + Constants.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_TABLE)
            return ArrayList<FirebaseInfo>()
        }

        var token: String
        var deviceId: String
        var event: String
        if (cursor2!!.moveToFirst()) {
            while (cursor2!!.isAfterLast == false) {
                token = cursor2!!.getString(cursor2!!.getColumnIndex(Constants.FIREBASE_TOKEN))
                deviceId = cursor2!!.getString(cursor2!!.getColumnIndex(Constants.DEVICE_ID))
                event = cursor2!!.getString(cursor2!!.getColumnIndex(Constants.EVENT_NAME))

                firebaseInfo.add(FirebaseInfo(token, deviceId, event))
                cursor2!!.moveToNext()
            }
        }
        db.close()
        return firebaseInfo
    }

    fun getLazyPandaLocationTrack(): ArrayList<PandaLocationComponent> {
        var cursor3: Cursor? = null
        val locationComponent = ArrayList<PandaLocationComponent>()
        val db = writableDatabase
        try {
            cursor3 = db.rawQuery("select * from " + Constants.USER_LOCATION_TABLE, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_PANDA_LOCATION_TABLE)
            return ArrayList<PandaLocationComponent>()
        }

        var deviceId: String = ""
        var lazyPandaLat : String = ""
        var lazyPandaLong : String = ""
        var lazyPandaTimeStamp : String = ""
        if (cursor3!!.moveToFirst()) {
            while (cursor3!!.isAfterLast == false) {
                lazyPandaLat = cursor3!!.getString(cursor3!!.getColumnIndex(Constants.PANDA_LATITUDE))
                 deviceId = cursor3!!.getString(cursor3!!.getColumnIndex(Constants.DEVICE_ID))
                lazyPandaLong = cursor3!!.getString(cursor3!!.getColumnIndex(Constants.PANDA_LONGITUDE))
                lazyPandaTimeStamp = cursor3!!.getString(cursor3!!.getColumnIndex(Constants.PANDA_TIMESTAMP))
                locationComponent.add(PandaLocationComponent(lazyPandaLat, lazyPandaLong,lazyPandaTimeStamp))
                cursor3!!.moveToNext()
            }
        }
        db.close()
        return locationComponent
    }

    fun getUserActivityTime(): ArrayList<UserActivityTimeModel> {
        var cursor4: Cursor? = null
        val userActivityTimeModel = ArrayList<UserActivityTimeModel>()
        val db = writableDatabase
        try {
            cursor4 = db.rawQuery("select * from " + Constants.USER_ACTIVITY_TIME_TABLE, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_TABLE)
            return ArrayList<UserActivityTimeModel>()
        }

        var in_time: Long
        var out_time: Long
        if (cursor4!!.moveToFirst()) {
            while (cursor4!!.isAfterLast == false) {
                in_time = cursor4!!.getColumnIndex(Constants.USER_ACTIVITY_START_TIME).toLong()
                out_time = cursor4!!.getColumnIndex(Constants.USER_ACTIVITY_END_TIME).toLong()


                userActivityTimeModel.add(UserActivityTimeModel(in_time,out_time))
                cursor4!!.moveToNext()
            }
        }
        db.close()
        return userActivityTimeModel
    }

    fun getNotificationActionTime(): ArrayList<NotificationActionModel> {
        var cursor5: Cursor? = null
        val notificationAction = ArrayList<NotificationActionModel>()
        val db = writableDatabase
        try {
            cursor5 = db.rawQuery("select * from " + Constants.NOTIFICATION_ACTION_TABLE, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_NOTIFICATION_ACTION_TABLE)
            return ArrayList<NotificationActionModel>()
        }

        var notificationId: Long
        var recievedAt : Long
        var openedAt : Long
        var clearedAt : Long
        var device_id : String

        if (cursor5!!.moveToFirst()) {
            while (cursor5!!.isAfterLast == false) {
                device_id = cursor5!!.getString(cursor5!!.getColumnIndex(Constants.DEVICE_ID))
                notificationId = cursor5!!.getLong(cursor5!!.getColumnIndex(Constants.APP_NOTIFICATION_ID))
                recievedAt = cursor5!!.getLong(cursor5!!.getColumnIndex(Constants.APP_NOTIFICATION_RECIEVED_AT))
                openedAt = cursor5!!.getLong(cursor5!!.getColumnIndex(Constants.APP_NOTIFICATION_OPEN_AT))
                clearedAt = cursor5!!.getLong(cursor5!!.getColumnIndex(Constants.APP_NOTIFICATION_CLEARED_AT))
                notificationAction.add(NotificationActionModel(FoxApplication.instance.deviceID,notificationId,recievedAt,openedAt,clearedAt))
                cursor5!!.moveToNext()
            }
        }
        db.close()
        return notificationAction
    }

    fun getListOfInstallledApp(): ArrayList<ListOfInstalledApp> {
        var cursor6: Cursor? = null
        val installedApp = ArrayList<ListOfInstalledApp>()
        val db = writableDatabase
        try {
            cursor6 = db.rawQuery("select * from " + Constants.APP_INSTALLED_BY_USER_TABLE, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_LIST_OF_USER_INSTALLED_APP)
            return ArrayList<ListOfInstalledApp>()
        }


        var app_package_name: String
        var app_installed_name : String
        var app_version : String
        var installedAt : Long
        var lastOpenedAt : Long

        if (cursor6!!.moveToFirst()) {
            while (cursor6!!.isAfterLast == false) {
                app_package_name = cursor6!!.getString(cursor6!!.getColumnIndex(Constants.APP_PACKAGE_NAME))
                app_installed_name = cursor6!!.getString(cursor6!!.getColumnIndex(Constants.APP_INSTALLED_NAME))
                installedAt = cursor6!!.getLong(cursor6!!.getColumnIndex(Constants.APP_INSTALLED_AT))
                app_version = cursor6!!.getString(cursor6!!.getColumnIndex(Constants.APP_INSTALLED_VERSION))
                lastOpenedAt = cursor6!!.getLong(cursor6!!.getColumnIndex(Constants.APP_LAST_OPEN_AT))

                installedApp.add(ListOfInstalledApp(app_package_name,app_installed_name,installedAt,app_version,lastOpenedAt))
                cursor6!!.moveToNext()
            }
        }
        db.close()
        return installedApp
    }

    fun getNotification(): ArrayList<NotificationModel> {
        var cursor8: Cursor? = null
        val notification = ArrayList<NotificationModel>()
        val db = writableDatabase
        try {
            cursor8 = db.rawQuery("select * from " + Constants.NOTIFICATION_LIST_TABLE_NAME, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_NOTIFICATION_LIST_TABLE)
            return ArrayList<NotificationModel>()
        }

        var notificationId: Long
        var notificationTitle: String
        var notificationContent : String
        var notificationImage : String
        var notificationTime : Long



        if (cursor8!!.moveToFirst()) {
            while (cursor8!!.isAfterLast == false) {
                notificationId  = cursor8!!.getLong(cursor8!!.getColumnIndex(Constants.APP_NOTIFICATION_ID))
                notificationTitle = cursor8!!.getString(cursor8!!.getColumnIndex(Constants.APP_NOTIFICATION_TITLE))
                notificationContent = cursor8!!.getString(cursor8!!.getColumnIndex(Constants.APP_NOTIFICATION_CONTENT))
                notificationImage = cursor8!!.getString(cursor8!!.getColumnIndex(Constants.APP_NOTIFICATION_IMAGE))
                notificationTime = cursor8!!.getLong(cursor8!!.getColumnIndex(Constants.APP_NOTIFICATION_RECIEVED_AT))


                notification.add(NotificationModel(notificationId,notificationTitle,notificationContent,notificationImage,notificationTime))
                cursor8!!.moveToNext()
            }
        }
        db.close()
        return notification
    }

    fun getListOfInstallledAppUsageStats(): ArrayList<AppUsageStats> {
        var cursor9: Cursor? = null
        val usageStats = ArrayList<AppUsageStats>()
        val db = writableDatabase
        try {
            cursor9 = db.rawQuery("select * from " + Constants.APP_USAGE_STATS_TABLE, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_APP_USAGE_STATS_TABLE)
            return ArrayList<AppUsageStats>()
        }

        var app_package_name: String
        var in_time :  Long
        var out_time : Long
        var app_total_time : Long

        if (cursor9!!.moveToFirst()) {
            while (cursor9!!.isAfterLast == false) {
                app_package_name = cursor9!!.getString(cursor9!!.getColumnIndex(Constants.APP_PACKAGE_NAME))
                in_time = cursor9!!.getLong(cursor9!!.getColumnIndex(Constants.APP_GET_FIRST_TIME_STAMP))
                out_time = cursor9!!.getLong(cursor9!!.getColumnIndex(Constants.APP_GET_LAST_TIME_USED))
                app_total_time = cursor9!!.getLong(cursor9!!.getColumnIndex(Constants.APP_FOREGROUND_TIME))

                usageStats.add(AppUsageStats(app_package_name,in_time,out_time,app_total_time))
                cursor9!!.moveToNext()
            }
        }
        db.close()
        return usageStats
    }



    @Throws(SQLiteConstraintException::class)
    fun saveClassNameIntoDB(className: String): Boolean {
        val db = writableDatabase

        val values = ContentValues()
        values.put(Constants.CLASS_NAME, className)

        val newRowId = db.insert(Constants.CLASS_TABLE, null, values)
        db.close()
        return newRowId.toInt() != -1
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteClass(className: String): Boolean {
        val db = writableDatabase
        val selection = Constants.CLASS_NAME + " LIKE ?"
        val selectionArgs = arrayOf(className)
        val result = db.delete(Constants.CLASS_TABLE, selection, selectionArgs)
        db.close()
        return result != -1
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteAllClass(): Boolean {
        val db = writableDatabase
        val result = db.delete(Constants.CLASS_TABLE, null, null)
        db.close()
        return result != -1
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteAppUsageTable(): Boolean {
        val db = writableDatabase
        val result = db.delete(Constants.APP_USAGE_STATS_TABLE, null, null)
        db.close()
        return result != -1
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteListOfInstalledApp(): Boolean {
        val db = writableDatabase
        val result = db.delete(Constants.APP_INSTALLED_BY_USER_TABLE, null, null)
        db.close()
        return result != -1
    }

    fun getAllClasses(): ArrayList<String> {
        var cursorA: Cursor? = null
        val classes = ArrayList<String>()
        val db = writableDatabase
        try {
            cursorA = db.rawQuery("select * from " + Constants.CLASS_TABLE, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_CLASS_TABLE)
            return ArrayList<String>()
        }

        if (cursorA!!.moveToFirst()) {
            while (cursorA!!.isAfterLast == false) {
                classes.add(cursorA!!.getString(cursorA!!.getColumnIndex(Constants.CLASS_NAME)))
                cursorA!!.moveToNext()
            }
        }
        db.close()
        return classes
    }

    @Throws(SQLiteConstraintException::class)
    fun saveInternalClassNameIntoDB(className: String): Boolean {
        val db = writableDatabase

        val values = ContentValues()
        values.put(Constants.CLASS_NAME, className)

        val newRowId = db.insertWithOnConflict(Constants.ALL_CLASS_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
        return newRowId.toInt() != -1
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteAllInternalClass(): Boolean {
        val db = writableDatabase
        val result = db.delete(Constants.ALL_CLASS_TABLE, null, null)
        db.close()
        return result != -1
    }

    fun getAllInternalClasses(): ArrayList<String> {
        var cursorB: Cursor? = null
        val classes = ArrayList<String>()
        val db = writableDatabase
        try {
            cursorB = db.rawQuery("select * from " + Constants.ALL_CLASS_TABLE, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ALL_CLASS_TABLE)
            return ArrayList<String>()
        }

        if (cursorB!!.moveToFirst()) {
            while (cursorB!!.isAfterLast == false) {
                classes.add(cursorB!!.getString(cursorB!!.getColumnIndex(Constants.CLASS_NAME)))
                cursorB!!.moveToNext()
            }
        }
        db.close()
        return classes
    }

    companion object {
       // var cursor: Cursor? = null
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "foxpanda.db"

        private val SQL_CREATE_TABLE =
            "CREATE TABLE " + Constants.TABLE_NAME + " (" +
                Constants.FIREBASE_TOKEN + " TEXT," +
                Constants.DEVICE_ID + " TEXT," +
                Constants.EVENT_NAME + " TEXT," +
                Constants.TIMESTAMP + " TEXT)"

        private val SQL_CREATE_TOKEN_TABLE =
            "CREATE TABLE " + Constants.PLATFORM_ID_TABLE+ " (" +
                Constants.PLATFORM_ID + " TEXT)"


        private val SQL_CREATE_PACKAGE_TABLE =
                "CREATE TABLE " + Constants.PACKAGE_TABLE+ " (" +
                        Constants.PARENT_PACKAGE_NAME + " TEXT)"


        private val SQL_CREATE_PLATFORM_ID =
                "CREATE TABLE " + Constants.TOKEN_TABLE + " (" +
                        Constants.FIREBASE_TOKEN + " TEXT)"

        private val SQL_CREATE_CLASS_TABLE =
            "CREATE TABLE " + Constants.CLASS_TABLE + " (" +
                Constants.CLASS_NAME + " TEXT)"

        private val SQL_CREATE_ALL_CLASS_TABLE =
            "CREATE TABLE " + Constants.ALL_CLASS_TABLE + " (" +
                Constants.CLASS_NAME + " TEXT)"


        // TABLE FOR CHECKING IF DEVICE INFO IS STORED ON BACKED -- FLAG TRUE IF API RESPONSE == 200
        private val SQL_CREATE_IS_INFO_UPDATED =
                "CREATE TABLE " + Constants.IS_INFO_UPDATED + " (" +
                        Constants.INFO_UPDATE_FLAG + " INTEGER)"

        // TABLE FOR TRACKING USER ACTIVITY IN APP --- TOTAL TIME SPEND IN FOREGROUND BY USER
        private val SQL_CREATE_USER_ACTIVITY_TIME = "CREATE TABLE " + Constants.USER_ACTIVITY_TIME_TABLE + " (" +
                Constants.USER_ACTIVITY_START_TIME + " INTEGER,"+
                Constants.USER_ACTIVITY_END_TIME + " INTEGER)"


        //TABLE FOR LIST OF APPS INSTALLED BY USER
        private val SQL_CREATE_LIST_OF_USER_INSTALLED_APP = "CREATE TABLE " + Constants.APP_INSTALLED_BY_USER_TABLE+ " (" +
                Constants.APP_PACKAGE_NAME + " TEXT,"+
                Constants.APP_INSTALLED_NAME + " TEXT,"+
                Constants.APP_LAST_OPEN_AT + " INTEGER,"+
                Constants.APP_INSTALLED_AT + " INTEGER,"+
                Constants.APP_INSTALLED_VERSION + " TEXT)"

        //TABLE FOR USAGE STATS OF APP INSTALLED BY USER
        private val SQL_CREATE_APP_USAGE_STATS_TABLE = "CREATE TABLE " + Constants.APP_USAGE_STATS_TABLE+ " (" +
                Constants.APP_PACKAGE_NAME + " TEXT,"+
                Constants.APP_GET_FIRST_TIME_STAMP + " INTEGER,"+
                Constants.APP_GET_LAST_TIME_USED + " INTEGER,"+
                Constants.APP_FOREGROUND_TIME + " INTEGER)"

                //TABLE FOR NOTIFICATION ENGAGEMENT BY USER
        private val SQL_CREATE_NOTIFICATION_ACTION_TABLE = "CREATE TABLE " + Constants.NOTIFICATION_ACTION_TABLE+ " (" +
                Constants.DEVICE_ID + " TEXT,"+
                Constants.APP_NOTIFICATION_ID + " INTEGER,"+
                Constants.APP_NOTIFICATION_RECIEVED_AT + " INTEGER,"+
                        Constants.APP_NOTIFICATION_OPEN_AT + " INTEGER,"+
                Constants.APP_NOTIFICATION_CLEARED_AT+ " INTEGER)"

       //table for notification_list
        private val SQL_CREATE_NOTIFICATION_LIST_TABLE = "CREATE TABLE " + Constants.NOTIFICATION_LIST_TABLE_NAME+ " (" +
                Constants.APP_NOTIFICATION_ID + " INTEGER,"+
                Constants.APP_NOTIFICATION_TITLE + " TEXT,"+
                Constants.APP_NOTIFICATION_CONTENT + " TEXT,"+
                Constants.APP_NOTIFICATION_IMAGE + " TEXT,"+
                Constants.APP_NOTIFICATION_RECIEVED_AT+ " INTEGER)"


        //table for Panda Location
        private val SQL_CREATE_PANDA_LOCATION_TABLE = "CREATE TABLE " + Constants.USER_LOCATION_TABLE+ " (" +
                Constants.DEVICE_ID + " TEXT,"+
                Constants.PANDA_LONGITUDE + " TEXT,"+
                Constants.PANDA_LATITUDE + " TEXT,"+
                Constants.PANDA_TIMESTAMP+ " INTEGER)"

               //create table for intraction-coordinate
       private val SQL_CREATE_USER_INTERACTION_COORDINATE_TABLE = "CREATE TABLE " + Constants.INTERACTION_COORDINATE_TABLE+ " (" +
                       Constants.DEVICE_ID + " TEXT,"+
                       Constants.FOX_ACTIVITY_NAME + " TEXT,"+
                       Constants.FOX_LAYOUT_NAME + " TEXT,"+
                       Constants.FOX_ORIENTATION + " INTEGER,"+
                       Constants.FOX_DEVICE_HEIGHT + " INTEGER,"+
                       Constants.FOX_DEVICE_WIDTH + " INTEGER,"+
                       Constants.FOX_SCROLL_POSITION+ " INTEGER,"+
                       Constants.FOX_MAX_SCROLL_POSITION + " INTEGER,"+
                       Constants.FOX_INTERACTION_COORDINATE_X + " INTEGER,"+
                       Constants.FOX_INTERACTION_COORDINATE_Y+ " INTEGER)"



        private val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + Constants.TABLE_NAME

        private val SQL_DELETE_PLATFORM_ID_TABLE = "DROP TABLE IF EXISTS " + Constants.PLATFORM_ID_TABLE

        private val SQL_DELETE_PACKAGE_NAME_TABLE = "DROP TABLE IF EXISTS " + Constants.PACKAGE_TABLE

        private val SQL_DELETE_TOKEN_TABLE = "DROP TABLE IF EXISTS " + Constants.TOKEN_TABLE

        private val SQL_DELETE_CLASS_TABLE = "DROP TABLE IF EXISTS " + Constants.CLASS_TABLE

        private val SQL_DELETE_ALL_CLASS_TABLE = "DROP TABLE IF EXISTS " + Constants.ALL_CLASS_TABLE

        private val SQL_DELETE_INFO_UPDATED_FLAG = "DROP TABLE IF EXISTS"  + Constants.IS_INFO_UPDATED

        private val SQL_DELETE_USER_ACTIVITY_TIME = "DROP TABLE IF EXISTS" + Constants.USER_ACTIVITY_TIME_TABLE

        private val SQL_DELETE_USER_INSTALLED_APP = "DROP TABLE IF EXISTS" + Constants.APP_INSTALLED_BY_USER_TABLE

        private val SQL_DELETE_USAGE_STATS_TABLE = "DROP TABLE IF EXISTS" + Constants.APP_USAGE_STATS_TABLE

        private val SQL_DELETE_NOTIFICATION_ACTION_TABLE = "DROP TABLE IF EXISTS" + Constants.NOTIFICATION_ACTION_TABLE

        private val SQL_DELETE_NOTIFICATION_LIST_TABLE = "DROP TABLE IF EXISTS" + Constants.NOTIFICATION_LIST_TABLE_NAME

        private val SQL_DELETE_USER_INTERACTION_COORDINATE_TABLE = "DROP TABLE IF EXISTS" + Constants.INTERACTION_COORDINATE_TABLE

        private val SQL_DELETE_PANDA_LOCATION_TABLE = "DROP TABLE IF EXISTS" + Constants.USER_LOCATION_TABLE




    }

}
