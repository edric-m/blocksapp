package com.example.edric.blocksapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GroupReaderDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "allotawatch.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DbContract.TaskGroupEntry.GROUP_TABLE_NAME + " (" +
            DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_ID + " INTEGER," +
            DbContract.TaskGroupEntry.GROUP_COLUMN_PLAN_ID + " INTEGER," +
                    "PRIMARY KEY (" +DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_ID+","+
                    DbContract.TaskGroupEntry.GROUP_COLUMN_PLAN_ID+"))";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DbContract.TaskGroupEntry.GROUP_TABLE_NAME;

    public GroupReaderDbHelper (Context context) {
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

    public boolean addToGroup(String name, int plan) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        int taskid = readTask(name);
        contentValues.put(DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_ID, taskid);
        contentValues.put(DbContract.TaskGroupEntry.GROUP_COLUMN_PLAN_ID, plan);
        long result = db.insert(DbContract.TaskGroupEntry.GROUP_TABLE_NAME,null,contentValues);
        if(result == -1) {
            return false;
        }
        return true;
    }

    public int readTask(String name) {
        int id = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + DbContract.FeedEntry._ID + " FROM tasks WHERE name="+name;
        Cursor res =  db.rawQuery( query, null );
        if(res.getCount() > 0) {
            id = res.getInt(res.getColumnIndex(DbContract.FeedEntry._ID));
        }
        return id;
    }
}
