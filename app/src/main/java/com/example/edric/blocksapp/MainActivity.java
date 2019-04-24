package com.example.edric.blocksapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
//import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
//import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
//import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Process;

//import java.io.IOException;
import java.util.Locale;
import android.widget.Toast;
import android.support.design.widget.FloatingActionButton;

//TODO: implement try-catch where needed

public class MainActivity extends AppCompatActivity implements DialogAlarm.OnInputListener{
    private tasks list; /*!< Contains the array list of the tasks */
    private task selectedTask; /*!< The task that the activity will focus on */

    private TextView taskName; /*!< Controls the TextView that displays the current action*/
    private TextView taskTime, mtextviewBreak, mtaskIndex; /*!< Controls the TextView that displays the time */
    private ConstraintLayout layout; /*!< Needed to set the background colour of the activity */
    private ImageView mImageView; /*!< Controls the background image */
    private Button mClearBtn, mAlarmBtn, mStartBtn;
    //private CountDownTimer mOnTickTimer; /*!< Timer class to start an onTick event */

    private long timePaused; /*!< Counts the amount of time the pause timer has run */
    private int switchedTime=0;
    private long breakRecommend=0;
    private boolean mTimerRunning; /*!< Bool for whether the timer for a task is running */
    private boolean mHasTasks; /*!< Bool for whether a task has been added to the activity */
    private boolean mServiceStarted;
    private boolean alarmSet=false;
    private int alarmMin=0;
    private NotificationManager notificationManager;
    //PowerManager.WakeLock wl;

    private float initialX, initialY; /*!< Screen x,y position used for screen touch events */

    //constant variables
    //private static final int MS_IN_1SEC = 1000; /*!< Constant used for onTick event*/
    //private static final int MS_IN_10MIN = 600000; /*!< Constant used for onTick event */
    //private static final int MS_IN_1MIN = 60000; /*!< Constant used for adding new tasks */
    private static final String BREAK_TIME_TEXT = "work break";/*!< Constant used when setting a textview */

    private enum Direction {
        PREVIOUS,
        NEXT
    }

    //Broadcast receivers
    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //recieve data from service
            /*
            int arg = intent.getIntExtra("time_left",0);

            if (arg > 0) {
                selectedTask.setTimeAllocated((long) arg);
            } else {
                selectedTask.setTimeAllocated(0);
                mTimerRunning = false;
                timePaused = intent.getIntExtra("pause_time", (int)timePaused); //TODO: pause time is doubled when service is running
                refreshDisplay(mTimerRunning);
            }
            */

            int x = 3;
            x = intent.getIntExtra("start_pause", 3);
            if(x!=3){
                switch(x){
                    case 0:
                        startWorkBreak();
                        break;
                    case 1:
                        switchedTime = 0;
                        toggleTimer();
                        mTimerRunning = true;
                        toggleTimer();
                        refreshDisplay(false);
                        break;
                    case 2:
                        switchTask(Direction.NEXT);
                        break;
                    default:
                        break;
                }


                intent.putExtra("start_pause",3);
                intent.removeExtra("start_pause");
            }
            /*
            if(intent.getBooleanExtra("resume_task", false)){
                switchedTime = 0;
                toggleTimer();
                mTimerRunning = true;
                toggleTimer();
                refreshDisplay(false);
                //intent.removeExtra("resume_task");
                intent.putExtra("resume_task",false);
            }
            if(intent.getBooleanExtra("next_task", false)){
                switchTask(Direction.NEXT);
                //intent.removeExtra("next_task");
                intent.putExtra("next_task",false);
            }
            */
            selectedTask.setTimeAllocated((long) intent.getIntExtra("time_left",(int)selectedTask.getTimeAllocated()));
            timePaused = intent.getIntExtra("pause_time", (int)timePaused);
            checkQuickAlarm();
            timerTick();
            Log.d("MyActivity", "onRecieve called");
        }
    };
    /*private BroadcastReceiver alarmBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(alarmMin==0) {
                //notify
                notifyAlarmEnd();
                //turn off reciever
                setQuickAlarm();
                Log.d("alarm", "alarm receiver done");
            } else {
                alarmMin--;
                mAlarmBtn.setText(Integer.toString(alarmMin) + "mins left");
                Log.d("alarm", "alarm receiver tick");
            }
        }
    };*/

    private void checkQuickAlarm() {
        if(alarmSet) {
            if (alarmMin == 0) {
                //notify
                notifyAlarmEnd();
                //turn off reciever
                mAlarmBtn.setText("set alarm");
                //this.unregisterReceiver(alarmBroadcast);
                //wl.release();
                //Log.d("alarm", "alarm un-register receiver");
                alarmSet = false;

                //TODO repeater alarm
                /*
                if(repeater == true) alarmMin = repeaterSetValue
                 */
                Log.d("alarm", "alarm receiver done");
            } else {
                alarmMin = alarmMin - 1000;
                //mAlarmBtn.setText(formatMsToTime(alarmMin) + " mins left");
                Log.d("alarm", "alarm receiver tick");
            }
        }
    }
    /**
     * @Brief: onCreate method for MainActivity
     * @Param: savedInstanceState
     * @Note: Initialises views and globals except selectedTask
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FeedReaderDbHelper db = new FeedReaderDbHelper(this);
        init();
        listeners();


    }

    private void listeners() {
        //start the on tick event
        mClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask();
            }
        });

        //START TIMER
        //startOnTickEvent();


        //not start service here

        //floating action button setup listener
        FloatingActionButton fab = findViewById(R.id.fab); //TODO should this be a class variable?
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();
            }
        });
    }

    private void deleteTask() {
        if(mTimerRunning) {
            FeedReaderDbHelper db = new FeedReaderDbHelper(this);
            db.deleteTask(selectedTask.getName());
            //list.moveToNextTask();
            list.removeTask(selectedTask);
            if(list.size()>0) {
                //selectedTask = list.selectFirstTask();
                //stopTimer(); //TODO test toggle
                toggleTimer();
                selectedTask = list.moveToPrevTask();  //bug fix done here <<<<<<<<<
                //startTimer();
                toggleTimer();
                breakRecommend = ((list.getTotalMs()/3600000)*600000);
                switchedTime = 0;
                refreshDisplay(false);
            } else {
                mTimerRunning = false;
                mHasTasks = false;
                taskName.setText("create a new timer");
                taskTime.setText("to begin");
                layout.setBackgroundColor(Color.parseColor("#4576c1"));
                mClearBtn.setVisibility(View.INVISIBLE);
                mtaskIndex.setVisibility(View.INVISIBLE);
                mtaskIndex.setVisibility(View.INVISIBLE);
                mAlarmBtn.setVisibility(View.INVISIBLE);
                mAlarmBtn.setEnabled(false);
                mImageView.setImageDrawable(null);
                stopTimer();
                //refreshDisplay(true); //TODO do this instead?
            }
        }
        /* //delete the whole list of tasks. then exit app
        FeedReaderDbHelper db = new FeedReaderDbHelper(this);
        db.deleteAllTasks();
        this.finish();
        System.exit(0);
        */
    }

    private void init() {
        //Initialise a list of tasks
        //load tasks from the database

        //View variables that will be used
        mStartBtn = findViewById(R.id.btn_startTimer);
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTimer();
            }
        });
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mAlarmBtn = findViewById(R.id.brn_alarm);
        mAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlarmDialog();
            }
        });
        mAlarmBtn.setVisibility(View.INVISIBLE);
        mAlarmBtn.setEnabled(false);
        mtaskIndex = findViewById(R.id.textView_taskIndex);
        taskName = findViewById(R.id.task_name);
        taskTime = findViewById(R.id.task_time);
        layout = findViewById(R.id.layout);
        mtextviewBreak = findViewById(R.id.textview_break);
        mImageView = findViewById(R.id.imageview_back);
        mClearBtn = findViewById(R.id.button_clear_tasks);
        //Timer variables initialise
        mTimerRunning = false;
        mHasTasks = false;
        mServiceStarted = false;

        if(!readDb()) {
            list = new tasks();
            //taskName.setText("bad load db");
            mClearBtn.setVisibility(View.INVISIBLE);
            mClearBtn.setEnabled(false);
            mtaskIndex.setVisibility(View.INVISIBLE);
            mAlarmBtn.setVisibility(View.INVISIBLE);
            mAlarmBtn.setEnabled(false);
            Log.d("MyActivity", "db not loaded" );
        } else {
            //mHasTasks = true;
            //mTimerRunning = true;
            //selectedTask = list.selectFirstTask();
            //taskName.setText("good load db");
            Log.d("MyActivity", "db loaded" );
        }
        breakRecommend = ((list.getTotalMs()/3600000)*600000);
    }

    private boolean readDb() {
        boolean result = true;
        try {
            FeedReaderDbHelper db = new FeedReaderDbHelper(this);
            list = db.readAllTasks();
            if(list.size() > 0) {
                selectedTask = list.selectFirstTask();
                mTimerRunning = true; //start broadcast at mTimerRunning = true
                mHasTasks = true;
                result = true;
                refreshDisplay(false);
            } else {
                result = false;
            }
        } catch (Exception e) {
            //result = false;
        }
        return result;
    }

    private void saveDb() {
        //super.finish();
        Log.d("MyActivity", "save called" );
        //tasks t = new tasks();
        //t.getList() = list.getList();
        try {
            FeedReaderDbHelper db = new FeedReaderDbHelper(this);
            if(list.size() > 0)
                db.updateAllTasks(list.getList());
        } catch (Exception e) {
            Log.d("MyActivity", e.getMessage());
        }
    }

    /* not enough time to save db
     *
     */
    @Override
    protected void onDestroy() {
        //unregisterReceiver(alarmBroadcast); //TODO good idea?
        //Log.d("alarm", "alarm unregister receiver");
        //stop service
        if(mServiceStarted) {
            unregisterReceiver(br); //TODO: this is not a good idea, ondestroy is not always called
            this.stopService(new Intent(this, BroadcastService.class));
        }
        //Log.d("MyActivity", "destroy called" );
        Process.killProcess(Process.myPid()); //app wasnt terminating properly in android studio debug
        super.onDestroy();
        //saveDb();
    }

    private void showAlarmDialog() {
        if(!alarmSet) {
            DialogAlarm dialog = new DialogAlarm();
            dialog.show(getSupportFragmentManager(), "DialogAlarm");
        } else {
            mAlarmBtn.setText("set alarm");
            //this.unregisterReceiver(alarmBroadcast);
            //wl.release();
            //Log.d("alarm", "alarm un-register receiver");
            alarmSet = false;
        }
    }

    @Override
    public void setQuickAlarm(int mins) {
        //if(!alarmSet) {
            alarmMin = mins * 1000*60; //repeater = alarmMin
            alarmSet = true;
            mAlarmBtn.setText("stop "+ Integer.toString(mins)+" min alarm");
            //this.registerReceiver(alarmBroadcast, new IntentFilter(Intent.ACTION_TIME_TICK));
            //PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            //wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:alrmwklck");
            //wl.acquire(); //TODO: look into setting a timeout
            Log.d("alarm", "alarm set");
        //} else {
            //mAlarmBtn.setText("set alarm");
            //this.unregisterReceiver(alarmBroadcast);
            //wl.release();
            //Log.d("alarm", "alarm un-register receiver");
            //alarmSet = false;
        //}
    }

    private void notifyAlarmEnd() {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "default")
                        //.setSmallIcon(R.drawable.abc)
                        .setSmallIcon(R.drawable.ic_action_name)
                        .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                                R.drawable.aw_iconcheck))
                        .setContentTitle("alarm has ended")
                        .setContentText("do what gotta be done...")
                        .setLights(Color.WHITE,1,1)
                        .setSound(alarmSound)
                        .setAutoCancel(true)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL; //doesn't need this

        // Add as notification
        notificationManager.notify(0, notification);
    }

    @Override
    protected void onPause() {
        /*
        if(mHasTasks && selectedTask.getTimeAllocated() > 1) {
            mServiceStarted = true;
            //start service
            Intent i = new Intent(this, BroadcastService.class);
            i.putExtra("pause_time", (int)switchedTime);
            i.putExtra("total_pause", (int)timePaused);
            i.putExtra("set_time", (int) selectedTask.getTimeAllocated());
            i.putExtra("task_name", selectedTask.getName());
            if(mTimerRunning) {

                Log.d("MyActivity", "time send");
            } else {
                i.putExtra("paused",true);

            }
            this.startService(i);
            //register reciever
            this.registerReceiver(br, new IntentFilter(BroadcastService.COUNTDOWN_BR));

            //no need to pause timer
        }
        */

        super.onPause(); //TODO: should these be at the beginning of the function?
        Log.d("MyActivity", "pause called" );
        saveDb();
    }

    @Override
    protected void onResume() {
        /*
        if (mServiceStarted) {
            stopService(new Intent(this, BroadcastService.class));
            //unregister reciever
            this.unregisterReceiver(br);
            //stop service
            Log.d("MyActivity", "service canceled" );
            mServiceStarted = false;
        }
        */
        super.onResume();
        refreshDisplay(!mTimerRunning);
    }

    /**
     * @Brief: responds to straight swipes from one side of the screen to the other.
     * @Param: MotionEvent
     * @Return: bool
     * @Note: requires mHasTasks boolean has been initialised
     */
    @Override
    public boolean onTouchEvent (MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                initialX = event.getX();
                initialY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE: //drag?
                break;

            case MotionEvent.ACTION_UP:
                float finalX = event.getX();
                float finalY = event.getY();

                if (initialX < finalX && Math.abs(finalY - initialY) < Math.abs(initialX - finalX)) {
                    //Log.d(TAG, "Left to Right swipe performed");
                    switchTask(Direction.PREVIOUS);
                }

                if (initialX > finalX && Math.abs(finalY - initialY) < Math.abs(initialX - finalX)) {
                    //Log.d(TAG, "Right to Left swipe performed");
                    switchTask(Direction.NEXT); //switch task next
                }

                if (initialY < finalY && Math.abs(finalX - initialX) < Math.abs(initialY - finalY)) {
                    //Log.d(TAG, "Up to Down swipe performed");
                    if(mHasTasks)
                        startWorkBreak(); //work break
                }

                if (initialY > finalY && Math.abs(finalX - initialX) < Math.abs(initialY - finalY)) {
                    //Log.d(TAG, "Down to Up swipe performed");
                    //if(mHasTasks)
                        if(mTimerRunning || !mHasTasks) {  //TODO double check this
                            switchedTime = 0; //TODO: not good to do this here
                            openSettings();
                        } else {
                            switchedTime = 0;
                            toggleTimer();
                            mTimerRunning = true;
                            toggleTimer();
                            refreshDisplay(false);
                            //toast.setText(selectedTask.getName() + " resumed");
                            //toast.show();
                        }
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                //Log.d(TAG,"Action was CANCEL");
                break;

            case MotionEvent.ACTION_OUTSIDE:
                //Log.d(TAG, "Movement occurred outside bounds of current screen element");
                break;
        }
        return super.onTouchEvent(event);
    }

    private void toggleTimer() { //TODO confusing (function) names
        if(mHasTasks) {
            if (mServiceStarted) {
                stopTimer();
                mStartBtn.setText("Start");

            } else {
                startTimer();
                mStartBtn.setText("Stop");

            }
        }
    }

    private void startTimer() {
        if(mHasTasks && selectedTask.getTimeAllocated() > 1) {
            //mServiceStarted = true; //TODO test moving this outside. originaly here
            //start service
            Intent i = new Intent(this, BroadcastService.class);
            i.putExtra("pause_time", (int)switchedTime);
            i.putExtra("total_pause", (int)timePaused);
            i.putExtra("set_time", (int) selectedTask.getTimeAllocated());
            i.putExtra("task_name", selectedTask.getName());
            if(mTimerRunning) {

                Log.d("MyActivity", "time send");
            } else {
                i.putExtra("paused",true);

            }
            this.startService(i);
            //register reciever
            this.registerReceiver(br, new IntentFilter(BroadcastService.COUNTDOWN_BR));
            mAlarmBtn.setVisibility(View.VISIBLE);
            mAlarmBtn.setEnabled(true);
            //no need to pause timer
        }
        mServiceStarted = true; //because the timer is starting when switched from a finished activity
    }

    private void stopTimer() {
        if (mServiceStarted) {
            stopService(new Intent(this, BroadcastService.class));
            //unregister reciever
            this.unregisterReceiver(br);
            //stop service
            Log.d("MyActivity", "service canceled" );
            mAlarmBtn.setVisibility(View.INVISIBLE);
            mAlarmBtn.setEnabled(false);
            mServiceStarted = false;
        }
    }
    /**
     * @Brief: Initialises infinite onTick event. Called by onCreate. Is called only once.
     * @Note: Depends on mHasTasks and mTimerRunning booleans.
     */
    /*
    public void startOnTickEvent() {
        mOnTickTimer = new CountDownTimer(MS_IN_10MIN, MS_IN_1SEC) {
            @Override
            public void onTick(long millisUntilFinished) {
                //checkQuickAlarm();
                if(mHasTasks) { //TODO: move this check to broadcast receiver
                    switchedTime = switchedTime + 1000;
                    if(mTimerRunning) {
                        selectedTask.decrementTime();
                        updateCountDownText(false);
                        showTaskInfo();
                    }
                    else {
                        timePaused = timePaused + 1000;
                        updateCountDownText(true);
                        showBreakInfo();
                    }
                }
            }

            @Override
            public void onFinish() {
                mOnTickTimer.start();
            }
        }.start();
    }
    */

    private void timerTick() {
        if(mHasTasks) {
            switchedTime = switchedTime + 1000;
            if(mTimerRunning) {
                //selectedTask.decrementTime();
                updateCountDownText(false);
                showTaskInfo();
            }
            else {
                //timePaused = timePaused + 1000;
                updateCountDownText(true);
                showBreakInfo();
            }
        }
    }

    /**
     * @Brief: Updates the timer textView and formats it to hrs:min:sec, depending on the
     * mTimerRunning boolean.
     * @Param: boolean paused - if true displays alternate time for when all timers paused
     * @Note: requires selectedTask has a value
     *      - change param to an enum structure
     */
    private void updateCountDownText(boolean paused) {
        long time = 0;

        if (paused) {
            time = switchedTime;
        }
        if (!paused) {
            time = selectedTask.getTimeAllocated();
        }
        int hours = (int) (time / 3600000);
        int minutes = (int) (time / 60000) % 60;
        int seconds = (int) (time / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d:%02d",hours, minutes, seconds);

        taskTime.setText(timeLeftFormatted);
    }

    private String formatMsToTime(long ms) {
        int hours = (int) (ms / 3600000);
        int minutes = (int) (ms / 60000) % 60;
        int seconds = (int) (ms / 1000) % 60;
        return String.format(Locale.getDefault(),"%02d:%02d:%02d",hours, minutes, seconds);
    }

    /**
     * @Brief: Pauses the timer. Called when onTouch detects a swipe from bottom to top of the
     * screen.
     * @Note: Changes mTimerRunning to false
     */
    public void startWorkBreak() {
        toggleTimer();
        switchedTime = 0;
        mTimerRunning = false;
        refreshDisplay(true);
        toggleTimer();

        //Toast toast = Toast.makeText(getApplicationContext(), "all timers paused", Toast.LENGTH_SHORT);

        //pause timer
        //pauseTimer();
        //toast.show();
    }

    /**
     * @Brief: Resumes or switches tasks. Called when onTouch detects a swipe from top to bottom of
     * the screen.
     * @Note: Changes mTimerRunning to true if all timers paused.
     */
    public void openSettings() { //open task settings
        /*
        Context context = getApplicationContext();
        CharSequence text = "";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        */
        toggleTimer();
        mTimerRunning = false;
        toggleTimer();
        refreshDisplay(mTimerRunning);
        //settings //TODO: load current tasks into settings
        Intent i = new Intent(this, SettingsActivity.class);
        int idx = 0;
        for (task t : list.getList()) {
            i.putExtra("item" + Integer.toString(idx), t.getName());
            i.putExtra("itemValue" + Integer.toString(idx), t.getTimeAllocated());
            i.putExtra("itemSpent" + Integer.toString(idx), t.getTimeSpent());
            idx++;
        }
        i.putExtra("item_count", Integer.toString(idx));
        startActivityForResult(i, 2);
    }

    /**
     * @Brief: Starts the add task activity. Called when onTouch detects a swipe from top to bottom
     * of the screen.
     * @Note: Changes mTimerRunning to false, pausing the tasks. //TODO: rewrite documentation
     */
    public void switchTask(Direction direction) {
        if(mTimerRunning) {
            toggleTimer();
            //switchedTime = 0;
            switch (direction) {
                case PREVIOUS:
                    selectedTask = list.moveToPrevTask();
                    break;
                case NEXT:
                    selectedTask = list.moveToNextTask();
                    break;
                default:
            }
            toggleTimer();
            Toast toast = Toast.makeText(getApplicationContext(), "Starting "+selectedTask.getName(), Toast.LENGTH_SHORT);
            toast.show();
            refreshDisplay(false);
        }
    }

    /**
     * @Brief: Starts the manage tasks activity. Called when onTouch detects a swipe from top to
     * bottom of the screen.
     * @Note: Changes mTimerRunning to false, pausing the tasks.
     */
    public void addTask() {
        //new task
        toggleTimer();
        mTimerRunning = false;
        toggleTimer();
        refreshDisplay(mTimerRunning);
        Intent i = new Intent(this, NewTaskActivity.class);
        startActivityForResult(i, 1); //request code is so we know who we are hearing back from
        //mTimerRunning = false;
        //refreshDisplay(true);
    }

    /**
     * @Brief: Changes the background depending on whether tasks are paused or not.
     * @Param: boolean paused - if tasks are paused or not
     */
    private void setBackground(boolean paused) {
        layout.setBackgroundColor(Color.parseColor("#1B1F59")); //in colors.xml
        if(paused) {
            mImageView.setImageResource(R.drawable.background_pause_small);
        }
        else {
            mImageView.setImageResource(R.drawable.background_resume_small);
        }
    }

    /**
     * @Brief: Refresh every view on display depending on whether tasks are paused or not
     * @Param: boolean paused - if tasks are paused or not
     */
    private void refreshDisplay(boolean paused) {
        if(mHasTasks) { //TODO might need this
            if (paused) {
                taskName.setText("");
                mtextviewBreak.setText(BREAK_TIME_TEXT);
                mClearBtn.setVisibility(View.INVISIBLE);
                mClearBtn.setEnabled(false);
                //mtaskIndex.setVisibility(View.INVISIBLE);
                //mAlarmBtn.setVisibility(View.INVISIBLE);
                //mAlarmBtn.setEnabled(false);
                showBreakInfo();
            } else {
                taskName.setText(selectedTask.getName());
                mtextviewBreak.setText("");
                mClearBtn.setVisibility(View.VISIBLE);
                mClearBtn.setEnabled(true);
                mtaskIndex.setVisibility(View.VISIBLE);
                if(mServiceStarted) {
                    mAlarmBtn.setVisibility(View.VISIBLE);
                    mAlarmBtn.setEnabled(true);
                }
                //mtaskIndex.setVisibility(View.VISIBLE);
                showTaskInfo();
            }
            setBackground(paused);
            updateCountDownText(paused);
        }
    }

    private void showBreakInfo() {
        mtaskIndex.setText("\n\nTotal break time: " + formatMsToTime(timePaused));
        mtaskIndex.append("\nRecommended break time: " + formatMsToTime(breakRecommend-timePaused));
    }

    private void showTaskInfo() {
        int index = list.getCurrentTaskIndex();
        //show "Task 1 of 5"
        mtaskIndex.setText("#" + Integer.toString(index) + "/" + Integer.toString(list.size()));
        //finish time and date for this task + time left
        mtaskIndex.append("\n\nTime since last break: " + formatMsToTime(switchedTime));
        //mtaskIndex.append("\nTotal time spent this session: " + formatMsToTime(list.getList().get(index-1).getTimeSpent()));
        mtaskIndex.append("\nTime left for all tasks: " + formatMsToTime(list.getTotalMs()));
        //mtaskIndex.append("\n\nFinish time including breaks: " + formatMsToTime(list.getTotalMs()+((list.getTotalMs()/3600000)*600000)));
        //time left for all tasks + finish time and date
        //time spent since last break
        //DAY TRACKER
        //time spent working today
        //recommended time to spend working (based on philosophy)
        //free time left today asuuming you sleep at 10pm
        //if free time left below recommended say 'not on track to accomplish this 24 hour period'
        //calculated sleep time this 24 hour set
    }

    /*
    @Override
    protected void onPostResume() {
        super.onPostResume();
    }
    */


    /**
     * @Brief: Called when an activity has a result for MainActivity.
     * @Param: -int requestCode - the code of the activity that has a result.
     * -int resultCode - success or failure of the calling activity.
     * -Intent data - the data passed from the calling activity.
     * @Note: -mTimerRunning should be false (paused) before this method is called.
     * -is the only method that changes mhasTasks
     * -must refreshDisplay() before first if statement, breaks if its after.
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        toggleTimer();
        mTimerRunning = true;

        Log.d("MyActivity", "requestCode:"+Integer.toString(requestCode) + " resultCode:"+Integer.toString(resultCode));
        refreshDisplay(!mTimerRunning); //fix this, its confusing
        if (resultCode == RESULT_OK) {
            switch(requestCode) {
                case 1:
                    //if (data.hasExtra(NewTaskActivity.NAME_KEY) && data.hasExtra(NewTaskActivity.TIME_KEY)) {
                        //String n = data.getExtras().getString(NewTaskActivity.NAME_KEY);
                        //String t = data.getExtras().getString(NewTaskActivity.TIME_KEY);

                        //TODO: this is wrong, it must check with the db not the 'list' variable
                        //for(int x=0;x<list.size();x++) {
                            //if(list.getList().get(x).getName().equals(n)) {
                            //    n = n + "i";
                            //}
                            //list.moveToNextTask();
                        //}
                    if(readDb()) {
                        //list.addTask(n, Integer.parseInt(t) * MS_IN_1MIN, 0); //t is in minutes, need to convert to ms
                        //pauseTimer();
                        selectedTask = list.selectNewTask(); //TODO: will this work?
                        //mTimerRunning = true;
                        mHasTasks = true;
                        breakRecommend = ((list.getTotalMs() / 3600000) * 600000);
                        refreshDisplay(false);
                        //startTimer();

                        //if(data.getBooleanExtra("StartNew", false)) {
                        //addTask(); //TODO: remove this functionality?
                        //}
                        //}
                    }
                    break;
                case 2: //TODO: instead read from database to get tasks
                    //selectedTask = list.selectFirstTask();
                    list.clear();
                    /*
                    int count = data.getIntExtra("return_count", 0);
                    for(int x=0;x<count;x++) {
                        if(data.hasExtra("item"+Integer.toString(x))) {
                            list.addTask(data.getStringExtra("item"+Integer.toString(x)+"name"),
                                    (int)data.getExtras().getDouble("item"+Integer.toString(x)),
                                    (int)data.getExtras().getLong("item"+Integer.toString(x)+"spent"));
                        }
                        //selectedTask = list.moveToNextTask();
                    }
                    */
                    if(readDb()) {
                        //list.clear();
                        selectedTask = list.selectFirstTask();
                        mTimerRunning = true;
                        breakRecommend = ((list.getTotalMs() / 3600000) * 600000);
                        timePaused = 0;

                    } else {
                        mTimerRunning = false;
                        mHasTasks = false;
                        taskName.setText("create a new timer");
                        taskTime.setText("to begin");
                        layout.setBackgroundColor(Color.parseColor("#4576c1"));
                        mClearBtn.setVisibility(View.INVISIBLE);
                        mtaskIndex.setVisibility(View.INVISIBLE);
                        mtaskIndex.setVisibility(View.INVISIBLE);
                        mAlarmBtn.setVisibility(View.INVISIBLE);
                        mAlarmBtn.setEnabled(false);
                        mImageView.setImageDrawable(null);
                        //mStartBtn.setVisibility(View.INVISIBLE);
                        //mStartBtn.setEnabled(false);
                        stopTimer();
                    }
                    refreshDisplay(false);
                    break;
                default:
                    break;
            }
        }
        toggleTimer();
    }

}
