package com.example.edric.blocksapp;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class BroadcastService extends Service {

    private final static String TAG = "BroadcastService";

    public static final String COUNTDOWN_BR = "com.example.edric.blocksapp.timer_br";
    Intent bi = new Intent(COUNTDOWN_BR);

    CountDownTimer cdt = null;

    private static final int MS_IN_1SEC = 1000; /*!< Constant used for onTick event*/
    private static final int MS_IN_10MIN = 600000; /*!< Constant used for onTick event */

    @Override
    public void onCreate() {
        super.onCreate();

        //Log.i(TAG, "Starting timer...");

        cdt = new CountDownTimer(MS_IN_10MIN, MS_IN_1SEC) {
            @Override
            public void onTick(long millisUntilFinished) {
                bi.putExtra("countdown", millisUntilFinished);
                sendBroadcast(bi);
            }

            @Override
            public void onFinish() {
                cdt.start();
            }
        }.start();

    }

    @Override
    public void onDestroy() {

        cdt.cancel();
        //Log.i(TAG, "Timer cancelled");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
