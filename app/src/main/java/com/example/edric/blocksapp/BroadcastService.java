package com.example.edric.blocksapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
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
    NotificationManager notificationManager;

    private static final int MS_IN_1SEC = 1000; /*!< Constant used for onTick event*/
    private static final int MS_IN_10MIN = 600000;

    @Override
    public void onCreate() {
        super.onCreate();
        //paused = false;
        Log.d(TAG, "Starting service timer...");
        //get the time needed to set timer from an intents extra
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:bcwklck");
        wl.acquire(); //TODO: look into setting a timeout
    }

    private void timerStart(int ms) {
        cdt = new CountDownTimer(ms, MS_IN_1SEC) {
            @Override
            public void onTick(long millisUntilFinished) {
                //Log.d(TAG, "service tick");
                if(paused) {
                    pauseTime = pauseTime + 1000;
                    bi.putExtra("pause_time", pauseTime);

                    b.setContentText(convertMsToClock(pauseTime));
                    notificationManager.notify(NOTIFICATION_ID, b.build());
                } else {
                    taskTime = taskTime - 1000;
                    bi.putExtra("time_left", taskTime);

                    b.setContentText(convertMsToClock(taskTime));
                    notificationManager.notify(NOTIFICATION_ID, b.build());
                }
                sendBroadcast(bi); //TODO: change send local broadcast instead
            }

            @Override
            public void onFinish() {
                //display notification: "task finished at x o'clock, switching to break time"
                if(!paused) {
                    notifyTaskEnd();
                    //start the break timer on mainactivity
                    //if not paused, if paused just restart the timer
                    taskTime = 0;
                    bi.putExtra("time_left", taskTime);
                    sendBroadcast(bi);
                }
                taskName = "break time";
                b.setContentTitle(taskName);
                paused = true;
                //pauseTime = load in the pause time if not done so already
                cdt.start(); //call timerStart() instead?
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
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "default")
                        //.setSmallIcon(R.drawable.abc)
                        .setSmallIcon(R.drawable.ic_action_name)
                        .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                                R.drawable.aw_iconcheck))
                        .setContentTitle(taskName + " has ended")
                        .setContentText("app has switched to break time")
                        .setLights(Color.WHITE,1,1)
                        .setSound(alarmSound)
                        .setAutoCancel(true)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        //builder.build().flags |= Notification.FLAG_AUTO_CANCEL; //doesn't need this

        // Add as notification
        notificationManager.notify(0, builder.build());
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
        pauseTime = intent.getIntExtra("pause_time", 0);
        taskTime = intent.getIntExtra("set_time", 0);
        if(paused) {
            //load in and remember the current pause time instead
            taskName = "break time";
            timerStart(MS_IN_10MIN); //leave as this
        } else {

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

        Intent intent = new Intent(this, MainActivity.class); //goes back to main when clicked main must have single instance
        //Intent intent = new Intent();
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent,0); //TODO: check flag is ok

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"default");

        builder.setSmallIcon(R.drawable.ic_action_name);
        builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                R.drawable.largeicon));
        builder.setTicker("App info string");
        builder.setContentIntent(pi);
        builder.setOngoing(true);
        builder.setOnlyAlertOnce(true);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setContentTitle(taskName);
        builder.setShowWhen(false);
        builder.setContentText(convertMsToClock(taskTime));
        builder.setPriority(NotificationCompat.PRIORITY_LOW); //TODO: no difference?

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
