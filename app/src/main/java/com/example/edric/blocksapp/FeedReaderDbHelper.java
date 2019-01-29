package com.example.edric.blocksapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FeedReaderDbHelper  extends SQLiteOpenHelper {

    //private SQLiteDatabase mydatabase;
    public static final String DATABASE_NAME = "allotawatch.db";
    public static final int DATABASE_VERSION = 1;
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TASK_TABLE_NAME + " (" +
            FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
            FeedReaderContract.FeedEntry.TASK_COLUMN_NAME + " TEXT," +
            FeedReaderContract.FeedEntry.TASK_COLUMN_TIME_SPENT + " INTEGER," +
            FeedReaderContract.FeedEntry.TASK_COLUMN_TIME_REMAINING + " INTEGER," +
            FeedReaderContract.FeedEntry.TASK_COLUMN_LIFETIME + " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TASK_TABLE_NAME;

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
}
