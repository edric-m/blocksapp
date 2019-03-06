package com.example.edric.blocksapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

//TODO: dont need this class
public class GroupReaderDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "allotawatch.db";
    private static final int DATABASE_VERSION = 1;

    //TODO: not null to planid and taskid
    /*private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DbContract.TaskGroupEntry.GROUP_TABLE_NAME + " (" +
            DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_ID + " INTEGER," +
            DbContract.TaskGroupEntry.GROUP_COLUMN_PLAN_ID + " INTEGER," +
                    "PRIMARY KEY (" +DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_ID+", "+
                    DbContract.TaskGroupEntry.GROUP_COLUMN_PLAN_ID+"))";
                    */

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DbContract.TaskGroupEntry.GROUP_TABLE_NAME + " (" +
                    DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_ID + " INTEGER," +
                    DbContract.TaskGroupEntry.GROUP_COLUMN_PLAN_ID + " INTEGER)";

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

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean addToGroup(String name, int plan) {
        Log.d("GroupHelper", "addToGroup() called name:" + name + " plan:" + Integer.toString(plan));
        SQLiteDatabase db = this.getReadableDatabase();

        int taskid = readTask(name, db);
        Log.d("GroupHelper", "readTask() finished id:" + Integer.toString(taskid));
        if(taskid == -1) {
            return false;
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_ID, taskid);
            contentValues.put(DbContract.TaskGroupEntry.GROUP_COLUMN_PLAN_ID, plan);
            long result = db.insert(DbContract.TaskGroupEntry.GROUP_TABLE_NAME, null, contentValues);

            if (result == -1) {
                return false;
            }
        }
        return true;
    }

    private int readTask(String name, SQLiteDatabase db) { //TODO rename gettaskid
        int id = -1;
        Log.d("GroupHelper", "readTask() entered");
        //SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + DbContract.FeedEntry._ID + " FROM "+ DbContract.FeedEntry.TASK_TABLE_NAME+" WHERE " +
                DbContract.FeedEntry.TASK_COLUMN_NAME+ "='"+name+"'";
        Cursor res =  db.rawQuery( query, null );
        Log.d("GroupHelper", "readTask() query executed");
        if(res.moveToFirst()) {
            id = res.getInt(res.getColumnIndex(DbContract.FeedEntry._ID));
            Log.d("GroupHelper", "readTask() id fetched");
        }
        return id;
    }

    public ArrayList<task> readPlan(int planId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<task> taskList = new ArrayList<task>();
        //SELECT name, time from tasks_tbl INNER JOIN group_tbl ON group_.tbl.taskid = tasks_tbl._id
        String query = "SELECT " + DbContract.FeedEntry.TASK_COLUMN_NAME + "," +
                DbContract.FeedEntry.TASK_COLUMN_TIME_REMAINING + " FROM " +
                DbContract.FeedEntry.TASK_TABLE_NAME + "INNER JOIN " +
                DbContract.TaskGroupEntry.GROUP_TABLE_NAME + " ON " +
                DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_ID + "=" +
                DbContract.FeedEntry._ID + " WHERE " +
                DbContract.TaskGroupEntry.GROUP_COLUMN_PLAN_ID + " = " +
                Integer.toString(planId);
        Cursor res =  db.rawQuery( query, null );
        res.moveToFirst();
        while(!res.isAfterLast()){
            taskList.add(new task(
                    res.getString(res.getColumnIndex(DbContract.FeedEntry.TASK_COLUMN_NAME)),
                    res.getInt(res.getColumnIndex(DbContract.FeedEntry.TASK_COLUMN_TIME_REMAINING))));
            res.moveToNext();
        }
        return taskList;
    }

    public boolean deleteTask(String name) {//delete from group table
        SQLiteDatabase db = this.getReadableDatabase();
        int id = readTask(name, db);
        //String[] selectionArgs = { id };
        /*String query = "DELETE FROM" + DbContract.TaskGroupEntry.GROUP_TABLE_NAME +
                "WHERE " + DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_ID +
                "=" + Integer.toString(id);
        Cursor res = db.rawQuery(query, null);*/
        return db.delete(DbContract.TaskGroupEntry.GROUP_TABLE_NAME,
                DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_ID + "=" + Integer.toString(id),
                null) > 0;
    }
}
