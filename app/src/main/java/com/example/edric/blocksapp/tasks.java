package com.example.edric.blocksapp;
import java.util.LinkedList;

public class tasks {
    private LinkedList<task> taskList = new LinkedList<task>();
    private int current;

    public tasks() {
        current = 0;
        taskList.add(new task("first task", 60000));
        taskList.add(new task("second task", 6000));
        //constructor
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

