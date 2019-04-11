package com.example.edric.blocksapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;

public class FeedReaderDbHelper extends SQLiteOpenHelper {

    //private SQLiteDatabase mydatabase;
    private static final String DATABASE_NAME = "allotawatch.db";
    private static final int DATABASE_VERSION = 3;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DbContract.FeedEntry.TASK_TABLE_NAME + " (" +
            DbContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
            DbContract.FeedEntry.TASK_COLUMN_NAME + " TEXT," +
            DbContract.FeedEntry.TASK_COLUMN_TIME_SPENT + " INTEGER," +
            DbContract.FeedEntry.TASK_COLUMN_TIME_REMAINING + " INTEGER," +
            DbContract.FeedEntry.TASK_COLUMN_LIFETIME + " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DbContract.FeedEntry.TASK_TABLE_NAME;

    private static final String SQL_CREATE_ENTRIES_GROUP =
            "CREATE TABLE " + DbContract.TaskGroupEntry.GROUP_TABLE_NAME + " (" +
                    DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_ID + " INTEGER NOT NULL," +
                    DbContract.TaskGroupEntry.GROUP_COLUMN_PLAN_ID + " INTEGER NOT NULL," +
                    DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_TIME + " INTEGER NOT NULL," +
                    "PRIMARY KEY (" +DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_ID+", "+
                    DbContract.TaskGroupEntry.GROUP_COLUMN_PLAN_ID+"))";

    private static final String SQL_DELETE_ENTRIES_GROUP =
            "DROP TABLE IF EXISTS " + DbContract.TaskGroupEntry.GROUP_TABLE_NAME;

    public FeedReaderDbHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES_GROUP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_DELETE_ENTRIES_GROUP);
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

    ///////GROUP TABLE FUNCTIONS
    public boolean addToGroup(String name, long time, int plan) {
        Log.d("GroupHelper", "addToGroup() called name:" + name + " plan:" + Integer.toString(plan));
        SQLiteDatabase db = this.getReadableDatabase();
        //int time = 0;


        int taskid = readTask(name, db); //TODO: just use inner join here

        /*
        String query = "SELECT " + DbContract.FeedEntry.TASK_COLUMN_TIME_REMAINING +
                " FROM "+ DbContract.FeedEntry.TASK_TABLE_NAME +
                " WHERE " + DbContract.FeedEntry._ID + "="+Integer.toString(taskid);
        Cursor res =  db.rawQuery( query, null );
        if(res.moveToFirst()) {
            time = res.getInt(res.getColumnIndex(DbContract.FeedEntry.TASK_COLUMN_TIME_REMAINING));
            //Log.d("GroupHelper", "readTask() id fetched");
        }
        */

        Log.d("GroupHelper", "readTask() finished id:" + Integer.toString(taskid));
        if(taskid == -1) {
            return false;
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_ID, taskid);
            contentValues.put(DbContract.TaskGroupEntry.GROUP_COLUMN_PLAN_ID, plan);
            contentValues.put(DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_TIME, (int)time);
            long result = db.insert(DbContract.TaskGroupEntry.GROUP_TABLE_NAME, null, contentValues);

            /*
            if(plan == 0){
                contentValues.clear();
                contentValues.put(DbContract.FeedEntry.TASK_COLUMN_NAME, name);
                contentValues.put(DbContract.FeedEntry.TASK_COLUMN_TIME_REMAINING, (int)time);
                db.insert(DbContract.FeedEntry.TASK_TABLE_NAME, null, contentValues);
            }*/

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

    public tasks readPlan(int planId) {
        SQLiteDatabase db = this.getReadableDatabase();
        tasks taskList = new tasks();
        //SELECT name, time from tasks_tbl INNER JOIN group_tbl ON group_.tbl.taskid = tasks_tbl._id
        String query = "SELECT " + DbContract.FeedEntry.TASK_COLUMN_NAME + "," +
                DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_TIME + "," +
                DbContract.FeedEntry.TASK_COLUMN_TIME_SPENT + " FROM " +
                DbContract.FeedEntry.TASK_TABLE_NAME + "[INNER] JOIN " +
                DbContract.TaskGroupEntry.GROUP_TABLE_NAME + " ON " +
                DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_ID + " = " +
                DbContract.FeedEntry._ID + " WHERE " +
                DbContract.TaskGroupEntry.GROUP_COLUMN_PLAN_ID + " = " +
                Integer.toString(planId);
        Log.d("SwitchPlan", "try query");
        Cursor res =  db.rawQuery( query, null );
        Log.d("SwitchPlan", "query ok query count: " + Integer.toString(res.getCount()));
        res.moveToFirst();
        while(!res.isAfterLast()){
            taskList.addTask(
                    res.getString(res.getColumnIndex(DbContract.FeedEntry.TASK_COLUMN_NAME)),
                    res.getInt(res.getColumnIndex(DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_TIME)),
                    res.getInt(res.getColumnIndex(DbContract.FeedEntry.TASK_COLUMN_TIME_SPENT)));
            Log.d("SwitchPlan", "task added to array");
            res.moveToNext();
        }
        Log.d("SwitchPlan", "readPlan ended ok");
        return taskList;
    }

    public boolean deleteTaskFromGroup(String name, int planid) {//delete from group table
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d("delete", "Name: "+name);
        Log.d("delete", "Plan ID: "+Integer.toString(planid));
        int id = readTask(name, db);
        Log.d("delete", "ID: "+Integer.toString(id));
        //String[] selectionArgs = { id };
        /*String query = "DELETE FROM" + DbContract.TaskGroupEntry.GROUP_TABLE_NAME +
                "WHERE " + DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_ID +
                "=" + Integer.toString(id);
        Cursor res = db.rawQuery(query, null);*/
        return db.delete(DbContract.TaskGroupEntry.GROUP_TABLE_NAME,
                DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_ID + "='" + Integer.toString(id)
                + "' AND " + DbContract.TaskGroupEntry.GROUP_COLUMN_PLAN_ID + "='" + Integer.toString(planid) + "'",
                null) > 0;
    }

    public void deletePlan(int planID) {
        SQLiteDatabase db = getReadableDatabase();
        //TODO: if task does not exist in any other plan delete task from task_tbl
        //select tasks.name
        //from tasks
        //inner join group_tbl on tasks._id = group_tbl.task_id
        //group by tasks.name having count(group_tbl.group_id) < 2 and group_tbl.group_id = planID;
        String query = "SELECT " + DbContract.FeedEntry._ID +
                " FROM " + DbContract.FeedEntry.TASK_TABLE_NAME +
                "[INNER] JOIN " + DbContract.TaskGroupEntry.GROUP_TABLE_NAME +
                " ON " + DbContract.FeedEntry._ID + " = " + DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_ID +
                " GROUP BY " + DbContract.FeedEntry._ID +
                " HAVING COUNT(" + DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_ID + ") < 2" +
                " AND " + DbContract.TaskGroupEntry.GROUP_COLUMN_PLAN_ID + " = " + Integer.toString(planID);
        Cursor res = db.rawQuery(query, null);
        //delete the unused plans from the tasks table
        //Log.d("SavePlan", "returned from query:" + Integer.toString(res.getCount()));
        res.moveToFirst();
        while(!res.isAfterLast()){
            db.delete(DbContract.FeedEntry.TASK_TABLE_NAME,
                    DbContract.FeedEntry._ID +
                    "=" + Integer.toString(res.getInt(res.getColumnIndex(DbContract.FeedEntry._ID))),
                    null);
            res.moveToNext();
        }
        //delete the plan from the plan table
        db.delete(DbContract.TaskGroupEntry.GROUP_TABLE_NAME,
                DbContract.TaskGroupEntry.GROUP_COLUMN_PLAN_ID +"="+ Integer.toString(planID),
                null);
        db.execSQL("vacuum");
    }

    public void clearPlanTable() {
        SQLiteDatabase db = getReadableDatabase();
        db.delete(DbContract.TaskGroupEntry.GROUP_TABLE_NAME, null, null);
        db.execSQL("vacuum");
    }

    ///////TASK TABLE FUNCTIONS
    public boolean addNewTask(String name, int time, int plan) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        ContentValues planValues = new ContentValues();
        boolean result = true;

                //contentValues.put(DbContract.FeedEntry.TASK_COLUMN_NAME, selectedTask.getName());
        try {
            contentValues.put(DbContract.FeedEntry.TASK_COLUMN_TIME_REMAINING, time);
            contentValues.put(DbContract.FeedEntry.TASK_COLUMN_TIME_SPENT, 0);
            contentValues.put(DbContract.FeedEntry.TASK_COLUMN_LIFETIME, 0);
            contentValues.put(DbContract.FeedEntry.TASK_COLUMN_NAME, name);
            long id = db.insert(DbContract.FeedEntry.TASK_TABLE_NAME, null, contentValues);
            if(id != -1) {
                planValues.put(DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_TIME, time);
                planValues.put(DbContract.TaskGroupEntry.GROUP_COLUMN_PLAN_ID, plan);
                planValues.put(DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_ID, id);
                db.insert(DbContract.TaskGroupEntry.GROUP_TABLE_NAME, null, planValues);
            } else {
                result = false;
            }
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    public boolean updateTask(String name, long time) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();

        //contentValues.put(DbContract.FeedEntry.TASK_COLUMN_NAME, selectedTask.getName());
        contentValues.put(DbContract.FeedEntry.TASK_COLUMN_TIME_REMAINING, time);
        //contentValues.put(DbContract.FeedEntry.TASK_COLUMN_TIME_SPENT, selectedTask.getTimeSpent());
        //contentValues.put(DbContract.FeedEntry.TASK_COLUMN_LIFETIME, 0);
        db.update(DbContract.FeedEntry.TASK_TABLE_NAME,
                contentValues,
                DbContract.FeedEntry.TASK_COLUMN_NAME + " = ? ",
                //new String[]{Integer.toString(x)});
                new String[] { name});
        return false;
    }

    public boolean deleteTask(String name) {//TODO: remove from the group table as well
        SQLiteDatabase db = this.getReadableDatabase();
        // Define 'where' part of query.
        String selection = DbContract.FeedEntry.TASK_COLUMN_NAME + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { name };
        String id = Integer.toString(readTask(name, db));
        //select planid from group inner join task on taskid = group.taskid where taskname = _name
        String query = "SELECT " + DbContract.TaskGroupEntry.GROUP_COLUMN_PLAN_ID + " FROM " +
                DbContract.TaskGroupEntry.GROUP_TABLE_NAME + "[INNER] JOIN " +
                DbContract.FeedEntry.TASK_TABLE_NAME + " ON " +
                DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_ID + " = " +
                DbContract.FeedEntry._ID + " WHERE " +
                DbContract.FeedEntry.TASK_COLUMN_NAME + " = '" +
                name + "'";
        Cursor res = db.rawQuery(query, null);
        //only delete the task if it is not part of any plan
        if(res.getCount() == 1) {
            // Issue SQL statement.
            int deletedRows = db.delete(DbContract.FeedEntry.TASK_TABLE_NAME, selection, selectionArgs);
            db.delete(DbContract.TaskGroupEntry.GROUP_TABLE_NAME,
                    DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_ID + "=" + id,
                            null);
            db.execSQL("vacuum");
            if (deletedRows != 0) {
                return false;
            } else {

                return true;
            }
        } else {
            db.delete(DbContract.TaskGroupEntry.GROUP_TABLE_NAME,
                    DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_ID + "=" + id + " AND " +
                    DbContract.TaskGroupEntry.GROUP_COLUMN_PLAN_ID + "=0",
                    null);
            return false;
        }
    }


    public int readTask(String name) { //TODO rename to taskExists and return boolean
        int id = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + DbContract.FeedEntry._ID +
                " FROM "+DbContract.FeedEntry.TASK_TABLE_NAME +
                " WHERE "+DbContract.FeedEntry.TASK_COLUMN_NAME+"='"+name+"'";
        Cursor res =  db.rawQuery( query, null );
        if(res.getCount() > 0) {
            //id = res.getInt(res.getColumnIndex(DbContract.FeedEntry._ID));
            id = 1;
        }
        return id;
    }


    /**
     * works so far
     * @return
     */
    public tasks readAllTasks() { //TODO: have to use join with group table
        SQLiteDatabase db = this.getReadableDatabase();
        tasks taskList = new tasks();
        //task focus;
        //select * from tasktable inner join group on id = id where plan = 0
        String query = "SELECT * FROM " + DbContract.FeedEntry.TASK_TABLE_NAME +
                " [INNER] JOIN " + DbContract.TaskGroupEntry.GROUP_TABLE_NAME +
                " ON " + DbContract.FeedEntry._ID + " = " +
                DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_ID + " WHERE " +
                DbContract.TaskGroupEntry.GROUP_COLUMN_PLAN_ID + " = 0";
        Cursor res =  db.rawQuery(query, null );
        res.moveToFirst();
        while(!res.isAfterLast()){
            taskList.addTask(
                    res.getString(res.getColumnIndex(DbContract.FeedEntry.TASK_COLUMN_NAME)),
                    res.getInt(res.getColumnIndex(DbContract.FeedEntry.TASK_COLUMN_TIME_REMAINING)),
                    //res.getInt(res.getColumnIndex(DbContract.FeedEntry.TASK_COLUMN_TIME_SPENT))
                    0
            );
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
    public boolean updateAllTasks(LinkedList<task> taskList) { //TODO update all tasks where plan = 0
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues(), newtask = new ContentValues();
        boolean result = true;
        int rowsAffected = 0;

        task selectedTask = taskList.get(0);
        for(int x=1;x<taskList.size();x++) {
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

                if(rowsAffected == 0) {
                    contentValues.put(DbContract.FeedEntry.TASK_COLUMN_NAME, selectedTask.getName());
                    long id = db.insert(DbContract.FeedEntry.TASK_TABLE_NAME, null, contentValues);
                    //TODO: insert task into group table plan 0 also
                    newtask.put(DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_ID, (int)id); //TODO: casting may cause future error if id is larger than integer.MAX_VALUE
                    newtask.put(DbContract.TaskGroupEntry.GROUP_COLUMN_PLAN_ID,0);
                    newtask.put(DbContract.TaskGroupEntry.GROUP_COLUMN_TASK_TIME, (int)selectedTask.getTimeAllocated());
                    db.insert(DbContract.TaskGroupEntry.GROUP_TABLE_NAME, null, newtask);
                }

            //} catch (Exception e) {
                //task name not found probably
                //result = false;
            //} finally {
                selectedTask = taskList.get(x);
                contentValues.clear();
            //}
        }
        return result;
    }

    public void deleteAllTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        //db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL("DELETE FROM " + DbContract.FeedEntry.TASK_TABLE_NAME);
        db.execSQL("vacuum");
    }
    public int size(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, DbContract.FeedEntry.TASK_TABLE_NAME);
        return numRows;
    }


}
