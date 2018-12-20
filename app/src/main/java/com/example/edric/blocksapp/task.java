package com.example.edric.blocksapp;

import java.util.Random;

public class task {
    //private int id; //not in constructor
    private String name;
    private long timeSpent; //mins
    private long timeAllocated; //mins
    private String colour; //String.format("#%02x%02x%02x", r, g, b);
    private Random random = new Random();

    public task (String _name, int _timeAllocated) {
        //id = _id;
        //Random random = new Random();
        this.name = _name;
        timeSpent = 0;
        timeAllocated = _timeAllocated;
        int max = 249;
        int min = 172;
        int r = random.nextInt(max - min + 1) + min; //between 172 and 249 random.nextInt(max - min + 1) + min
        int g = random.nextInt(max - min + 1) + min;
        int b = random.nextInt(max - min + 1) + min;
        colour = String.format("#%02x%02x%02x", r, g, b);
    }

    public void decrementTime() {
        if (timeAllocated >= 0) {
            timeAllocated = timeAllocated - 1000;
            timeSpent = timeSpent + 1000;
        }
    }

    public String getColour() {
        return colour;
    }
    public String getName() {
        return name;
    }

    public long getTimeSpent() {
        return timeSpent;
    }

    public long getTimeAllocated() {
        return timeAllocated;
    }

    public void setName(String _name) {
        this.name = _name;
    }

    /*
    public int getId() {
        return id;
    }

    public void setId(int _id) {
        this.id = _id;
    }
    */
}
