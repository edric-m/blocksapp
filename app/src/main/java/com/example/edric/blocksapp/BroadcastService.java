package com.example.edric.blocksapp;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class BroadcastService extends Service {

    private final static String TAG = "BroadcastService"; //debug tag

    public static final String COUNTDOWN_BR = "com.example.edric.blocksapp.timer_br";
    Intent bi = new Intent(COUNTDOWN_BR);

    CountDownTimer cdt = null;
    private int taskTime;

    private static final int MS_IN_1SEC = 1000; /*!< Constant used for onTick event*/

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Starting service timer...");
        //get the time needed to set timer from an intents extra


    }

    private void timerStart(int ms) {
        cdt = new CountDownTimer(ms, MS_IN_1SEC) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "service tick");
                taskTime = taskTime - 1000;
            }

            @Override
            public void onFinish() {
                //cdt.start(); //display notification: "task finished, switching to break time"
                //start the break timer on mainactivity
            }
        }.start();
    }

    @Override
    public void onDestroy() {

        //broadcast the time to mainactivity using localbroadcastmanager
        bi.putExtra("time_left", taskTime);
        sendBroadcast(bi); //send local broadcast instead
        cdt.cancel();
        Log.d(TAG, "service timer cancelled");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        taskTime = intent.getIntExtra("set_time", 0);
        //cdt.cancel();
        timerStart(taskTime);
        Log.d(TAG, "onStartCommand called");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
