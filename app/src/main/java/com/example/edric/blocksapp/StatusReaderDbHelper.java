package com.example.edric.blocksapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StatusReaderDbHelper extends SQLiteOpenHelper {

    //private SQLiteDatabase mydatabase;
    public static final String DATABASE_NAME = "allotawatch.db";
    public static final int DATABASE_VERSION = 1;
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DbContract.StatusEntry.STATUS_TABLE_NAME + " (" +
            DbContract.StatusEntry._ID + " INTEGER PRIMARY KEY," +
            DbContract.StatusEntry.STATUS_COLUMN_ACTIVE_TASK + " TEXT," +
            DbContract.StatusEntry.STATUS_COLUMN_BREAK_TIME + " INTEGER," +
            DbContract.StatusEntry.STATUS_COLUMN_DESTROY_DATE + " INTEGER," +
            DbContract.StatusEntry.STATUS_COLUMN_DESTROY_TIME_MIN + "INTEGER," +
            DbContract.StatusEntry.STATUS_COLUMN_TIME_PERIOD + " INTEGER)";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DbContract.StatusEntry.STATUS_TABLE_NAME;

    public StatusReaderDbHelper (Context context) {
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

    public boolean updateStatus() {
        return false;
    }

    public boolean getStatus() {
        return false;
    }
}
