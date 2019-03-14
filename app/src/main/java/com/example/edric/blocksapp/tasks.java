package com.example.edric.blocksapp;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.LinkedList;

public class tasks implements Serializable {
    private LinkedList<task> taskList = new LinkedList<task>();
    private int current;
    private static final int TASK_LIMIT = 40;

    public tasks() {
        current = 0;
        //taskList.add(new task("set up some tasks", 40000)); //0.5 blocks
        //taskList.add(new task("machine code", 10800000));
        //taskList.add(new task("app", 18000000));
        //taskList.add(new task("exercise / yoga", 3600000));
        //constructor
    }

    public LinkedList<task> getList () {
        return taskList;
    }
    //public void edit
    public task selectFirstTask() {
        current = 0;
        return taskList.get(current);
    }

    public int size(){
        return taskList.size();
    }

    public task selectNewTask() {
        current = taskList.size()-1;
        return taskList.getLast();
    }

    public task moveToNextTask() {
        if(current < taskList.size()-1) {
            current++;
        } else {
            current = 0;
        }
        return taskList.get(current);
    }

    public task moveToPrevTask() {
        if(current == 0) {
            current = taskList.size()-1; //TODO: double check
        } else {
            current--;
        }
        return taskList.get(current);
    }

    public boolean addTask(String name, int time) {
        boolean tooManyTasks;
        if(this.size() <= TASK_LIMIT)
        {
            tooManyTasks = false;
            taskList.add(new task(name,time));
        } else {
            tooManyTasks = true;
        }
        return tooManyTasks;
    }

    public void removeTask(task t) {
        taskList.remove(t);   //test this
    }

    public void clear() {
        taskList.clear();
    }

    public int getTotalMs() {
        int total =0 ;
        for(int x=0;x<taskList.size();x++) {
            total = total + (int)taskList.get(x).getTimeAllocated();
        }
        return total;
    }
}

