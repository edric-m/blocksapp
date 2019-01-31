package com.example.edric.blocksapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FeedReaderDbHelper extends SQLiteOpenHelper {

    //private SQLiteDatabase mydatabase;
    private static final String DATABASE_NAME = "allotawatch.db";
    private static final int DATABASE_VERSION = 1;
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

    /*
    @Override
    protected void onDestroy() {
        this.close();
        super.onDestroy();
    }
    */

    public boolean addTask() {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        boolean result = true;

                //contentValues.put(DbContract.FeedEntry.TASK_COLUMN_NAME, selectedTask.getName());
                contentValues.put(DbContract.FeedEntry.TASK_COLUMN_TIME_REMAINING, 600000);
                contentValues.put(DbContract.FeedEntry.TASK_COLUMN_TIME_SPENT, 0);
                contentValues.put(DbContract.FeedEntry.TASK_COLUMN_LIFETIME, 0);
                contentValues.put(DbContract.FeedEntry.TASK_COLUMN_NAME, "dbtest3 10min");
                db.insert(DbContract.FeedEntry.TASK_TABLE_NAME, null, contentValues);
        return result;
    }

    public boolean updateTask(int id) {
        return false;
    }

    public boolean deleteTask(int id) {
        return false;
    }

    public task readTask(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM tasks WHERE name=please_work";
        Cursor res =  db.rawQuery( query, null );
        task t = new task(res.getString(res.getColumnIndex(DbContract.FeedEntry.TASK_COLUMN_NAME)),
                          res.getInt(res.getColumnIndex(DbContract.FeedEntry.TASK_COLUMN_TIME_REMAINING)));
        return t;
    }

    /**
     * works so far
     * @return
     */
    public tasks readAllTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        tasks taskList = new tasks();
        //task focus;
        Cursor res =  db.rawQuery(
                "select * from " + DbContract.FeedEntry.TASK_TABLE_NAME, null );
        res.moveToFirst();
        while(!res.isAfterLast()){
            taskList.addTask(
                    res.getString(res.getColumnIndex(DbContract.FeedEntry.TASK_COLUMN_NAME)),
                    res.getInt(res.getColumnIndex(DbContract.FeedEntry.TASK_COLUMN_TIME_REMAINING)));
            res.moveToNext();
        }
        res.close();
        //db.close();
        return taskList;
    }

    /**
     * does not work so far?
     * @param taskList
     * @return
     */
    public boolean updateAllTasks(tasks taskList) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        boolean result = true;
        int rowsAffected = 0;

        task selectedTask = taskList.selectFirstTask();
        for(int x=1;x<taskList.size()+1;x++) {
            //try {
                //contentValues.put(DbContract.FeedEntry.TASK_COLUMN_NAME, selectedTask.getName());
                contentValues.put(DbContract.FeedEntry.TASK_COLUMN_TIME_REMAINING, selectedTask.getTimeAllocated());
                contentValues.put(DbContract.FeedEntry.TASK_COLUMN_TIME_SPENT, selectedTask.getTimeSpent());
                contentValues.put(DbContract.FeedEntry.TASK_COLUMN_LIFETIME, 0);
                rowsAffected = db.update(DbContract.FeedEntry.TASK_TABLE_NAME,
                        contentValues,
                        DbContract.FeedEntry.TASK_COLUMN_NAME + " = ? ",
                        //new String[]{Integer.toString(x)});
                        new String[] { selectedTask.getName()});

                if(rowsAffected != 1) {
                    contentValues.put(DbContract.FeedEntry.TASK_COLUMN_NAME, selectedTask.getName());
                    db.insert(DbContract.FeedEntry.TASK_TABLE_NAME, null, contentValues);
                }

            //} catch (Exception e) {
                //task name not found probably
                //result = false;
            //} finally {
                selectedTask = taskList.switchTask();
                contentValues.clear();
            //}
        }
        return result;
    }

    public void deleteAllTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM " + DbContract.FeedEntry.TASK_TABLE_NAME);
        db.execSQL("vacuum");
    }
    public int size(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, DbContract.FeedEntry.TASK_TABLE_NAME);
        return numRows;
    }
}
