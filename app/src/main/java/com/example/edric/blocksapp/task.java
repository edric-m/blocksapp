package com.example.edric.blocksapp;

import java.util.Random;

public class task {
    //private int id; //not in constructor
    private String name;
    private long timeSpent; //mins
    private long timeAllocated; //mins
    //private String colour; //String.format("#%02x%02x%02x", r, g, b);
    //private Random random = new Random();

    public task (String _name, int _timeAllocated, int _timeSpent) {
        //id = _id;
        //Random random = new Random();
        this.name = _name;
        timeSpent = _timeSpent;
        timeAllocated = _timeAllocated;

        //set random colour that lies within specific range
        //int max = 249;
        //int min = 172;
        //int r = random.nextInt(135 - 79 + 1) + 79; //between 172 and 249 random.nextInt(max - min + 1) + min
        //int g = random.nextInt(181 - 146 + 1) + 146;
        //int b = 255;//random.nextInt(max - min + 1) + min;
        //colour = String.format("#%02x%02x%02x", r, g, b);
    }
    /*
    public void setPeriod(int setPref) {
    }
    */
    public void decrementTime() {
        if (timeAllocated >= 0) {
            timeAllocated = timeAllocated - 1000;
            timeSpent = timeSpent + 1000;
        } else {
            //show notification
        }
    }

    //public String getColour() {
        //return colour;
    //}
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

    public void setTimeAllocated(long newTime) {
        if(newTime < timeAllocated) {
            this.timeSpent = timeSpent + (timeAllocated-newTime); //TODO: change reciever code and dont to this here
        }
        this.timeAllocated = newTime;
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
