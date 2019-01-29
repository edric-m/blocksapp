package com.example.edric.blocksapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FeedReaderDbHelper extends SQLiteOpenHelper {

    //private SQLiteDatabase mydatabase;
    public static final String DATABASE_NAME = "allotawatch.db";
    public static final int DATABASE_VERSION = 1;
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DbContract.FeedEntry.TASK_TABLE_NAME + " (" +
            DbContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
            DbContract.FeedEntry.TASK_COLUMN_NAME + " TEXT," +
            DbContract.FeedEntry.TASK_COLUMN_TIME_SPENT + " INTEGER," +
            DbContract.FeedEntry.TASK_COLUMN_TIME_REMAINING + " INTEGER," +
            DbContract.FeedEntry.TASK_COLUMN_LIFETIME + " INTEGER)";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DbContract.FeedEntry.TASK_TABLE_NAME;

    public FeedReaderDbHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean addTask() {
        return false;
    }

    public boolean updateTask(int id) {
        return false;
    }

    public boolean deleteTask(int id) {
        return false;
    }

    public boolean readTask(int id) {
        return false;
    }

    public tasks readAllTasks() {
        return null;
    }

    public boolean updateAllTasks(tasks list) {
        return false;
    }
    public int size(){
        return 0;
    }
}
