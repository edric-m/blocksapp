package com.example.edric.blocksapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Locale;

public class BroadcastService extends Service {

    private final static String TAG = "BroadcastService"; //debug tag

    public static final String COUNTDOWN_BR = "com.example.edric.blocksapp.timer_br";
    private final static int NOTIFICATION_ID = 7373;
    Intent bi = new Intent(COUNTDOWN_BR);

    CountDownTimer cdt = null;
    private int taskTime;
    private String taskName;
    private int pauseTime;
    private boolean paused = false;
    //private Notification n;
    private NotificationCompat.Builder b;
    PowerManager.WakeLock wl;

    private static final int MS_IN_1SEC = 1000; /*!< Constant used for onTick event*/
    private static final int MS_IN_10MIN = 600000;

    @Override
    public void onCreate() {
        super.onCreate();
        //paused = false;
        Log.d(TAG, "Starting service timer...");
        //get the time needed to set timer from an intents extra
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:bcwklck");
        wl.acquire();
    }

    private void timerStart(int ms) {
        cdt = new CountDownTimer(ms, MS_IN_1SEC) {
            @Override
            public void onTick(long millisUntilFinished) {
                //Log.d(TAG, "service tick");
                if(paused) {
                    pauseTime = pauseTime + 1000;
                    bi.putExtra("pause_time", pauseTime);
                } else {
                    taskTime = taskTime - 1000;
                    bi.putExtra("time_left", taskTime);
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    b.setContentText(convertMsToClock(taskTime));
                    notificationManager.notify(NOTIFICATION_ID, b.build());
                }
                sendBroadcast(bi); //TODO: change send local broadcast instead
            }

            @Override
            public void onFinish() {
                //display notification: "task finished at x o'clock, switching to break time"
                notifyTaskEnd();
                //start the break timer on mainactivity
                //if not paused, if paused just restart the timer
                taskTime = 0;
                paused = true;
                //pauseTime = load in the pause time if not done so already
                cdt.start();
            }
        }.start();
    }

    private String convertMsToClock(int ms) {
        long time = ms;

        int hours = (int) (time / 3600000);
        int minutes = (int) (time / 60000) % 60;
        int seconds = (int) (time / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d:%02d",hours, minutes, seconds);

        return timeLeftFormatted;
    }

    private void notifyTaskEnd() {
        /* //does not work
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "default")
                        //.setSmallIcon(R.drawable.abc)
                        .setContentTitle("Notifications Example")
                        .setContentText("This is a test notification");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
        */
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDestroy() {
        //broadcast the time to mainactivity using localbroadcastmanager
        //stopForeground(STOP_FOREGROUND_REMOVE); //causes error?
        //unregister receiver?
        wl.release();
        cdt.cancel();
        Log.d(TAG, "service timer cancelled");
        super.onDestroy();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        paused = intent.getBooleanExtra("paused", false);
        if(paused) {
            pauseTime = 0;
            timerStart(MS_IN_10MIN);
            //load in and remember the current pause time instead
        } else {
            taskTime = intent.getIntExtra("set_time", 0);
            taskName = intent.getStringExtra("task_name");
            //cdt.cancel();
            if(taskTime > 0)
                Log.d(TAG, "time good");
            timerStart(taskTime);
        }
        //Log.d(TAG, "onStartCommand called");
        setNotification();
        return START_STICKY;
    }

    public void setNotification() {

        Intent intent = new Intent(this, BroadcastService.class); //TODO: to test
        //Intent intent = new Intent();
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"default");

        builder.setSmallIcon(R.mipmap.ic_launcher_foreground);
        builder.setTicker("App info string");
        builder.setContentIntent(pi);
        builder.setOngoing(true);
        builder.setOnlyAlertOnce(true);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setContentTitle(taskName);
        builder.setShowWhen(false);
        builder.setContentText(convertMsToClock(taskTime));
        builder.setPriority(NotificationCompat.PRIORITY_LOW); //TODO: to test

        b = builder;
        Notification notification = builder.build();
        // optionally set a custom view

        startForeground(NOTIFICATION_ID, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
