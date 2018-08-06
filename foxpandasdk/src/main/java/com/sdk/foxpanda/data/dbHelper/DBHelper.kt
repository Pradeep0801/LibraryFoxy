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


internal class DBHelper(var context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

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

    //INSERT VALUE IN DATABASE --- LIST OF APPLICATION INSTALLED BY USER -- REQUIRED API LEVEL -- 16(FOR ALL VALUES ) -- 21 (FOR APP FOREGROUND TIME)
    @Throws(SQLiteConstraintException::class)
    fun saveInstalledApp(installedApp: ListOfInstalledApp): Boolean {
        val db = writableDatabase
        val value = ContentValues()
        value.put(Constants.DEVICE_ID, installedApp.device_id)
        value.put(Constants.APP_PACKAGE_NAME,installedApp.app_package_name)
        value.put(Constants.APP_INSTALLED_NAME,installedApp.app_installed_name)


        val result = db.insert(Constants.APP_INSTALLED_BY_USER_TABLE, null, value)
        db.close()
        return result.toInt() != -1
    }

    @Throws(SQLiteConstraintException::class)
    fun saveNotificationAction(notificationAction : NotificationActionModel): Boolean {
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
    fun saveInstalledAppUsage(appUsageStats: AppUsageStats): Boolean {
        val db = writableDatabase
        val value = ContentValues()
        value.put(Constants.DEVICE_ID, appUsageStats.device_id)
        value.put(Constants.APP_PACKAGE_NAME,appUsageStats.app_package_name)
        value.put(Constants.APP_GET_FIRST_TIME_STAMP,appUsageStats.in_time)
        value.put(Constants.APP_GET_LAST_TIME_USED,appUsageStats.out_time)
        value.put(Constants.APP_FOREGROUND_TIME,appUsageStats.app_total_time)

        val result = db.insert(Constants.APP_USAGE_STATS_TABLE, null, value)
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
        val db = writableDatabase
        var cursor: Cursor? = null
        var isUpdated : Int = 0
        try {
            cursor = db.rawQuery("select * from " + Constants.IS_INFO_UPDATED, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_IS_INFO_UPDATED)
            return 0
        }

        if(cursor!!.moveToFirst()) {
            isUpdated = cursor.getInt(cursor.getColumnIndex(Constants.INFO_UPDATE_FLAG))
        }
        db.close()
        return isUpdated
    }


    fun getToken(): String {
        val db = writableDatabase
        var cursor: Cursor? = null
        var token = ""
        try {
            cursor = db.rawQuery("select * from " + Constants.TOKEN_TABLE, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_TOKEN_TABLE)
            return ""
        }

        if(cursor!!.moveToFirst()) {
            token = cursor.getString(cursor.getColumnIndex(Constants.FIREBASE_TOKEN))
        }
        db.close()
        return token
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
        val firebaseInfo = ArrayList<FirebaseInfo>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + Constants.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_TABLE)
            return ArrayList<FirebaseInfo>()
        }

        var token: String
        var deviceId: String
        var event: String
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                token = cursor.getString(cursor.getColumnIndex(Constants.FIREBASE_TOKEN))
                deviceId = cursor.getString(cursor.getColumnIndex(Constants.DEVICE_ID))
                event = cursor.getString(cursor.getColumnIndex(Constants.EVENT_NAME))

                firebaseInfo.add(FirebaseInfo(token, deviceId, event))
                cursor.moveToNext()
            }
        }
        db.close()
        return firebaseInfo
    }

    fun getUserActivityTime(): ArrayList<UserActivityTimeModel> {
        val userActivityTimeModel = ArrayList<UserActivityTimeModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + Constants.USER_ACTIVITY_TIME_TABLE, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_TABLE)
            return ArrayList<UserActivityTimeModel>()
        }

        var in_time: Long
        var out_time: Long
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                in_time = cursor.getColumnIndex(Constants.USER_ACTIVITY_START_TIME).toLong()
                out_time = cursor.getColumnIndex(Constants.USER_ACTIVITY_END_TIME).toLong()


                userActivityTimeModel.add(UserActivityTimeModel(in_time,out_time))
                cursor.moveToNext()
            }
        }
        db.close()
        return userActivityTimeModel
    }

    fun getNotificationActionTime(): ArrayList<NotificationActionModel> {
        val notificationAction = ArrayList<NotificationActionModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + Constants.NOTIFICATION_ACTION_TABLE, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_NOTIFICATION_ACTION_TABLE)
            return ArrayList<NotificationActionModel>()
        }

        var notificationId: Long
        var recievedAt : Long
        var openedAt : Long
        var clearedAt : Long
        var device_id : String

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                device_id = cursor.getString(cursor.getColumnIndex(Constants.DEVICE_ID))
                notificationId = cursor.getLong(cursor.getColumnIndex(Constants.APP_NOTIFICATION_ID))
                recievedAt = cursor.getLong(cursor.getColumnIndex(Constants.APP_NOTIFICATION_RECIEVED_AT))
                openedAt = cursor.getLong(cursor.getColumnIndex(Constants.APP_NOTIFICATION_OPEN_AT))
                clearedAt = cursor.getLong(cursor.getColumnIndex(Constants.APP_NOTIFICATION_CLEARED_AT))
                notificationAction.add(NotificationActionModel(FoxApplication.instance.deviceID,notificationId,recievedAt,openedAt,clearedAt))
                cursor.moveToNext()
            }
        }
        db.close()
        return notificationAction
    }

    fun getListOfInstallledApp(): ArrayList<ListOfInstalledApp> {
        val installedApp = ArrayList<ListOfInstalledApp>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + Constants.APP_INSTALLED_BY_USER_TABLE, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_LIST_OF_USER_INSTALLED_APP)
            return ArrayList<ListOfInstalledApp>()
        }

        var deviceId: String
        var app_package_name: String
        var app_installed_name : String

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                deviceId  = cursor.getString(cursor.getColumnIndex(Constants.DEVICE_ID))
                app_package_name = cursor.getString(cursor.getColumnIndex(Constants.APP_PACKAGE_NAME))
                app_installed_name = cursor.getString(cursor.getColumnIndex(Constants.APP_INSTALLED_NAME))


                installedApp.add(ListOfInstalledApp(deviceId,app_package_name,app_installed_name))
                cursor.moveToNext()
            }
        }
        db.close()
        return installedApp
    }

    fun getNotification(): ArrayList<NotificationModel> {
        val notification = ArrayList<NotificationModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + Constants.NOTIFICATION_LIST_TABLE_NAME, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_NOTIFICATION_LIST_TABLE)
            return ArrayList<NotificationModel>()
        }

        var notificationId: Long
        var notificationTitle: String
        var notificationContent : String
        var notificationImage : String
        var notificationTime : Long



        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                notificationId  = cursor.getLong(cursor.getColumnIndex(Constants.APP_NOTIFICATION_ID))
                notificationTitle = cursor.getString(cursor.getColumnIndex(Constants.APP_NOTIFICATION_TITLE))
                notificationContent = cursor.getString(cursor.getColumnIndex(Constants.APP_NOTIFICATION_CONTENT))
                notificationImage = cursor.getString(cursor.getColumnIndex(Constants.APP_NOTIFICATION_IMAGE))
                notificationTime = cursor.getLong(cursor.getColumnIndex(Constants.APP_NOTIFICATION_RECIEVED_AT))


                notification.add(NotificationModel(notificationId,notificationTitle,notificationContent,notificationImage,notificationTime))
                cursor.moveToNext()
            }
        }
        db.close()
        return notification
    }

    fun getListOfInstallledAppUsageStats(): ArrayList<AppUsageStats> {
        val usageStats = ArrayList<AppUsageStats>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + Constants.APP_USAGE_STATS_TABLE, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_APP_USAGE_STATS_TABLE)
            return ArrayList<AppUsageStats>()
        }

        var deviceId: String
        var app_package_name: String
        var in_time :  Long
        var out_time : Long
        var app_total_time : Long

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                deviceId  = cursor.getString(cursor.getColumnIndex(Constants.DEVICE_ID))
                app_package_name = cursor.getString(cursor.getColumnIndex(Constants.APP_PACKAGE_NAME))
                in_time = cursor.getColumnIndex(Constants.APP_GET_FIRST_TIME_STAMP).toLong()
                out_time = cursor.getColumnIndex(Constants.APP_GET_LAST_TIME_USED).toLong()
                app_total_time = cursor.getColumnIndex(Constants.APP_FOREGROUND_TIME).toLong()

                usageStats.add(AppUsageStats(deviceId,app_package_name,in_time,out_time,app_total_time))
                cursor.moveToNext()
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
        val classes = ArrayList<String>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + Constants.CLASS_TABLE, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_CLASS_TABLE)
            return ArrayList<String>()
        }

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                classes.add(cursor.getString(cursor.getColumnIndex(Constants.CLASS_NAME)))
                cursor.moveToNext()
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
        val classes = ArrayList<String>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + Constants.ALL_CLASS_TABLE, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ALL_CLASS_TABLE)
            return ArrayList<String>()
        }

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                classes.add(cursor.getString(cursor.getColumnIndex(Constants.CLASS_NAME)))
                cursor.moveToNext()
            }
        }
        db.close()
        return classes
    }

    companion object {
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "foxpanda.db"

        private val SQL_CREATE_TABLE =
            "CREATE TABLE " + Constants.TABLE_NAME + " (" +
                Constants.FIREBASE_TOKEN + " TEXT," +
                Constants.DEVICE_ID + " TEXT," +
                Constants.EVENT_NAME + " TEXT," +
                Constants.TIMESTAMP + " TEXT)"

        private val SQL_CREATE_TOKEN_TABLE =
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
                Constants.DEVICE_ID + " TEXT,"+
                Constants.APP_PACKAGE_NAME + " TEXT,"+
                Constants.APP_INSTALLED_NAME + " TEXT)"

        //TABLE FOR USAGE STATS OF APP INSTALLED BY USER
        private val SQL_CREATE_APP_USAGE_STATS_TABLE = "CREATE TABLE " + Constants.APP_USAGE_STATS_TABLE+ " (" +
                Constants.DEVICE_ID + " TEXT,"+
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





        private val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + Constants.TABLE_NAME

        private val SQL_DELETE_TOKEN_TABLE = "DROP TABLE IF EXISTS " + Constants.TOKEN_TABLE

        private val SQL_DELETE_CLASS_TABLE = "DROP TABLE IF EXISTS " + Constants.CLASS_TABLE

        private val SQL_DELETE_ALL_CLASS_TABLE = "DROP TABLE IF EXISTS " + Constants.ALL_CLASS_TABLE

        private val SQL_DELETE_INFO_UPDATED_FLAG = "DROP TABLE IF EXISTS"  + Constants.IS_INFO_UPDATED

        private val SQL_DELETE_USER_ACTIVITY_TIME = "DROP TABLE IF EXISTS" + Constants.USER_ACTIVITY_TIME_TABLE

        private val SQL_DELETE_USER_INSTALLED_APP = "DROP TABLE IF EXISTS" + Constants.APP_INSTALLED_BY_USER_TABLE

        private val SQL_DELETE_USAGE_STATS_TABLE = "DROP TABLE IF EXISTS" + Constants.APP_USAGE_STATS_TABLE

        private val SQL_DELETE_NOTIFICATION_ACTION_TABLE = "DROP TABLE IF EXISTS" + Constants.NOTIFICATION_ACTION_TABLE

        private val SQL_DELETE_NOTIFICATION_LIST_TABLE = "DROP TABLE IF EXISTS" + Constants.NOTIFICATION_LIST_TABLE_NAME

    }

}
