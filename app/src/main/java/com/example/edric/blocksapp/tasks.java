package com.example.edric.blocksapp;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.LinkedList;

public class tasks implements Serializable {
    private LinkedList<task> taskList = new LinkedList<task>();
    private int current;

    public tasks() {
        current = 0;
        taskList.add(new task("music", 7200000));
        taskList.add(new task("machine code", 10800000));
        taskList.add(new task("app", 18000000));
        taskList.add(new task("exercise / yoga", 3600000));
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

    public task switchTask() {
        if(current < taskList.size()-1) {
            current++;
        } else {
            current = 0;
        }
        return taskList.get(current);
    }

    public void addTask(String name, int time) {
        taskList.add(new task(name,time));
    }

    public void removeTask(String name) {
        taskList.remove(name);   //test this
    }
}

