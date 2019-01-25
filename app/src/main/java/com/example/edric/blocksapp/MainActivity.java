package com.example.edric.blocksapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Locale;
import android.widget.Toast;

//TODO: implement try-catch where needed

public class MainActivity extends AppCompatActivity {
    private tasks list; /*!< Contains the array list of the tasks */
    private task selectedTask; /*!< The task that the activity will focus on */

    private TextView taskName; /*!< Controls the TextView that displays the current action*/
    private TextView taskTime, mtextviewBreak; /*!< Controls the TextView that displays the time */
    private ConstraintLayout layout; /*!< Needed to set the background colour of the activity */
    private ImageView mImageView; /*!< Controls the background image */

    private CountDownTimer mOnTickTimer; /*!< Timer class to start an onTick event */
    private long timePaused; /*!< Counts the amount of time the pause timer has run */
    private boolean mTimerRunning; /*!< Bool for whether the timer for a task is running */
    private boolean mHasTasks; /*!< Bool for whether a task has been added to the activity */

    private float initialX, initialY; /*!< Screen x,y position used for screen touch events */

    //constant variables
    static final int MS_IN_1SEC = 1000; /*!< Constant used for onTick event*/
    static final int MS_IN_10MIN = 600000; /*!< Constant used for onTick event */
    static final int MS_IN_1MIN = 60000; /*!< Constant used for adding new tasks */
    static final String BREAK_TIME_TEXT = "break time";/*!< Constant used when setting a textview */

    /**
     * >Brief: onCreate method for MainActivity
     * >Param: savedInstanceState
     * >Note: Initialises views and globals except selectedTask
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initialise a list of tasks
        list = new tasks();
        //View variables that will be used
        taskName = findViewById(R.id.task_name);
        taskTime = findViewById(R.id.task_time);
        layout = findViewById(R.id.layout);
        mtextviewBreak = findViewById(R.id.textview_break);
        mImageView = findViewById(R.id.imageview_back);
        //Timer variables initialise
        mTimerRunning = false;
        mHasTasks = false;

        //start the on tick event
        startOnTickEvent();
    }

    /**
     * >Brief: responds to straight swipes from one side of the screen to the other.
     * >Param: MotionEvent
     * >Return: bool
     * >Note: requires mHasTasks boolean has been initialised
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
                    if(mHasTasks)
                        goLeft();
                }

                if (initialX > finalX && Math.abs(finalY - initialY) < Math.abs(initialX - finalX)) {
                    //Log.d(TAG, "Right to Left swipe performed");
                    goRight();
                }

                if (initialY < finalY && Math.abs(finalX - initialX) < Math.abs(initialY - finalY)) {
                    //Log.d(TAG, "Up to Down swipe performed");
                    if(mHasTasks)
                        goUp();
                }

                if (initialY > finalY && Math.abs(finalX - initialX) < Math.abs(initialY - finalY)) {
                    //Log.d(TAG, "Down to Up swipe performed");
                    if(mHasTasks)
                        goDown();
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

    /**
     * >Brief: Initialises infinite onTick event. Called by onCreate. Is called only once.
     * >Note: Depends on mHasTasks and mTimerRunning booleans.
     */
    public void startOnTickEvent() {
        mOnTickTimer = new CountDownTimer(MS_IN_10MIN, MS_IN_1SEC) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(mHasTasks) {
                    if(mTimerRunning) {
                        selectedTask.decrementTime();
                        updateCountDownText(false);
                    }
                    else {
                        timePaused = timePaused + 1000;
                        updateCountDownText(true);
                    }
                }
            }

            @Override
            public void onFinish() {
                mOnTickTimer.start();
            }
        }.start();
    }

    /**
     * >Brief: Updates the timer textView and formats it to hrs:min:sec, depending on the
     * mTimerRunning boolean.
     * >Param: boolean paused - if true displays alternate time for when all timers paused
     * >Note: requires selectedTask has a value
     */
    private void updateCountDownText(boolean paused) {
        long time = 0;

        if (paused) {
            time = timePaused;
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

    /**
     * >Brief: Pauses the timer. Called when onTouch detects a swipe from bottom to top of the
     * screen.
     * >Note: Changes mTimerRunning to false
     */
    public void goUp() {
        mTimerRunning = false;
        refreshDisplay(true);
        Toast toast = Toast.makeText(getApplicationContext(), "all timers paused", Toast.LENGTH_SHORT);
        //pause timer
        //pauseTimer();
        toast.show();
    }

    /**
     * >Brief: Resumes or switches tasks. Called when onTouch detects a swipe from top to bottom of
     * the screen.
     * >Note: Changes mTimerRunning to true if all timers paused.
     */
    public void goDown() { //theres a bug here on startup
        Context context = getApplicationContext();
        CharSequence text = "";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);

        //resume task or
        if (mTimerRunning){
            //switch task
            selectedTask = list.switchTask();
            refreshDisplay(false);
            toast.setText("switched to " + selectedTask.getName());
            if(list.size() > 1) {
                toast.show();
            }
        }
        else {
            mTimerRunning = true;
            refreshDisplay(false);
            toast.setText(selectedTask.getName() + " resumed");
            toast.show();
        }
    }

    /**
     * >Brief: Starts the add task activity. Called when onTouch detects a swipe from top to bottom
     * of the screen.
     * >Note: Changes mTimerRunning to false, pausing the tasks.
     */
    public void goLeft() {

        //settings
        Intent i = new Intent(this, SettingsActivity.class);
        int idx = 0;
        for (task t : list.getList()) {
            i.putExtra("item" + Integer.toString(idx), t.getName());
            //i.putExtra("itemValue" + Integer.toString(idx), t.getTimeAllocated());
            idx++;
        }
        i.putExtra("item_count", Integer.toString(idx));
        startActivityForResult(i, 2);

        mTimerRunning = false;
    }

    /**
     * >Brief: Starts the manage tasks activity. Called when onTouch detects a swipe from top to
     * bottom of the screen.
     * >Note: Changes mTimerRunning to false, pausing the tasks.
     */
    public void goRight() {
        //new task
        Intent i = new Intent(this, NewTask.class);
        startActivityForResult(i, 1); //request code is so we know who we are hearing back from
        mTimerRunning = false;
        //refreshDisplay(true);
    }

    /**
     * >Brief: Changes the background depending on whether tasks are paused or not.
     * >Param: boolean paused - if tasks are paused or not
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
     * >Brief: Refresh every view on display depending on whether tasks are paused or not
     * >Param: boolean paused - if tasks are paused or not
     */
    private void refreshDisplay(boolean paused) {
        if(mHasTasks) {
            if (paused) {
                taskName.setText("");
                mtextviewBreak.setText(BREAK_TIME_TEXT);
            } else {
                taskName.setText(selectedTask.getName());
                mtextviewBreak.setText("");
            }
            setBackground(paused);
            updateCountDownText(paused);
        }
    }

    /**
     * >Brief: Called when an activity has a result for MainActivity.
     * >Param: int requestCode - the code of the activity that has a result
     * >       int resultCode - success or failure of the calling activity
     * >       Intent data - the data passed from the calling activity
     * >Note: mTimerRunning should be false (paused) before this method is called.
     *        is the only method that changes mhasTasks
     *        must refreshDisplay() before first if statement, breaks if its after.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTimerRunning = true;
        refreshDisplay(!mTimerRunning); //fix this, its confusing
        if (resultCode == RESULT_OK) {
            switch(requestCode) {
                case 1:
                    if (data.hasExtra(NewTask.NAME_KEY) && data.hasExtra(NewTask.TIME_KEY)) {
                        String n = data.getExtras().getString(NewTask.NAME_KEY);
                        String t = data.getExtras().getString(NewTask.TIME_KEY);

                        list.addTask(n, Integer.parseInt(t) * MS_IN_1MIN); //t is in minutes, need to convert to ms
                        //pauseTimer();
                        selectedTask = list.selectNewTask();
                        mTimerRunning = true;
                        mHasTasks = true;
                        refreshDisplay(false);
                        //startTimer();

                        if(data.getBooleanExtra("StartNew", false)) {
                            goRight();
                        }
                    }
                    break;
                case 2:
                    selectedTask = list.selectFirstTask();
                    for(int x=0;x<list.size();x++) {
                        if(data.hasExtra("item"+Integer.toString(x))) {
                            selectedTask.setTimeAllocated(
                                    (long)data.getExtras().getDouble("item"+Integer.toString(x)));
                        }
                        selectedTask = list.switchTask();
                    }
                    selectedTask = list.selectFirstTask();
                    mTimerRunning = true;
                    refreshDisplay(false);
                    break;
                default:
                    break;
            }
        }
    }
}
