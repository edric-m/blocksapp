package com.example.edric.blocksapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Locale;
import android.view.View;
import android.widget.Toast;

//import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private tasks list;
    private task selectedTask;

    private TextView taskName;
    private TextView taskTime, mtextviewBreak;
    private ConstraintLayout layout;
    private ImageView mImageView;
    //private TextView[] taskNames = new TextView[6]; TODO: list all tasks queued
    //private TextView[] taskTimes = new TextView[6];

    private CountDownTimer mCountdownTimer;
    private boolean mTimerRunning;

    private CountDownTimer mPauseTimer;
    private long timePaused;
    private boolean mPaused;
    private float initialX, initialY;

    //private int setPlanPref;
    //private int setPeriodPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = new tasks();
        taskName = findViewById(R.id.task_name);
        taskTime = findViewById(R.id.task_time);
        layout = findViewById(R.id.layout);
        mtextviewBreak = findViewById(R.id.textview_break);
        mImageView = findViewById(R.id.imageview_back);

        mTimerRunning = false;
        mPaused = false;

        //Drawable id = R.drawable.background_pause;
        //layout.setBackgroundResource(R.drawable.background_pause);
        //setPeriodPref = 1;
        //setPlanPref = 1;
        //refresh display
        //selectedTask = list.selectNewTask();
        //refresh display
        //refreshText();
        //startTimer();
    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        int action = event.getActionMasked();
        //toast that works
/*
        Context context = getApplicationContext();
        CharSequence text = "Hello toast!";
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);

        LayoutInflater inflater = getLayoutInflater();
        View l = inflater.inflate(R.layout.help_toast,
                (ViewGroup) findViewById(R.id.layout));
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(l);*/
//start here
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                initialX = event.getX();
                initialY = event.getY();
                //toast.show();

                break;

            case MotionEvent.ACTION_MOVE: //drag?
                //toast.setText("");
                //toast.show();
                //toast.cancel();
                break;

            case MotionEvent.ACTION_UP:
                float finalX = event.getX();
                float finalY = event.getY();

                if (initialX < finalX && Math.abs(finalY - initialY) < Math.abs(initialX - finalX)) {
                    //Log.d(TAG, "Left to Right swipe performed");
                    goLeft();
                }

                if (initialX > finalX && Math.abs(finalY - initialY) < Math.abs(initialX - finalX)) {
                    //Log.d(TAG, "Right to Left swipe performed");
                    goRight();
                }

                if (initialY < finalY && Math.abs(finalX - initialX) < Math.abs(initialY - finalY)) {
                    //Log.d(TAG, "Up to Down swipe performed");
                    goUp();
                }

                if (initialY > finalY && Math.abs(finalX - initialX) < Math.abs(initialY - finalY)) {
                    //Log.d(TAG, "Down to Up swipe performed");
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

    public void startTimer() {
        mCountdownTimer = new CountDownTimer(selectedTask.getTimeAllocated(),1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //selectedTask.setTimeAllocated(millisUntilFinished); //replace with allocated time
                selectedTask.decrementTime();
                updateCountDownText(1);
            }

            @Override
            public void onFinish() {
                //mTimerRunning = false;
                //selectedTask = list.switchTask();
                //layout.setBackgroundColor(Color.parseColor(selectedTask.getColour()));
                pauseTimer();
            }
        }.start();

        mTimerRunning = true;
        if(mPaused) {
            mPauseTimer.cancel();
            mPaused = false;
        }
    }

    public void pauseTimer() {

        if (mTimerRunning) {
            mCountdownTimer.cancel();
            //taskName.setText("break time");
            taskName.setText("");
            mtextviewBreak.setText("break time");
            //layout.setBackgroundColor(Color.parseColor("#42f4c8"));
            //mImageView.setImageResource(R.drawable.background_pause_small);
            //layout.setBackgroundColor(Color.parseColor("#1B1F59"));
            layout.setBackgroundResource(R.drawable.background_pause); //TODO: reducing the size may improve speed
            mPauseTimer = new CountDownTimer(600000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timePaused = timePaused + 1000;
                    updateCountDownText(0);
                }

                @Override
                public void onFinish() {

                }
            }.start();
            mTimerRunning = false;
            mPaused = true;
        }

    }

    private void updateCountDownText(int x) {
        long time = 0;

        if (x == 0) {
            time = timePaused;
        }
        if (x == 1) {
            time = selectedTask.getTimeAllocated();
        }
        int hours = (int) (time / 3600000);
        int minutes = (int) (time / 60000) % 60;
        int seconds = (int) (time / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d:%02d",hours, minutes, seconds);

        taskTime.setText(timeLeftFormatted);
    }

    public void goUp() {
        Toast toast = Toast.makeText(getApplicationContext(), "all timers paused", Toast.LENGTH_SHORT);
        //pause timer
        pauseTimer();
        toast.show();
    }

    public void goDown() { //theres a bug here on startup
        Context context = getApplicationContext();
        CharSequence text = "";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);

        //resume task or
        if (mTimerRunning){
            pauseTimer();
            //switch task
            selectedTask = list.switchTask();
            //refresh display
            refreshText();
            startTimer();
            toast.setText("switched to " + selectedTask.getName());
            if(list.size() > 1)
                toast.show();
            //layout.setBackgroundColor(Color.parseColor(selectedTask.getColour()));
        }

        if (mPaused) {
            startTimer();
            refreshText();
            toast.setText(selectedTask.getName() + " resumed");
            toast.show();
            //mPaused = false;
        }


        //following should be in an else
        /*
        if(!mTimerRunning) {

            //selectedTask = list.resumeTask();
            startTimer();
        }*/

    }

    public void goLeft() {
        if(mTimerRunning || mPaused) {
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
        }
        
    }

    public void submitTest(View view) {

    }

    public void goRight() {
        //new task
        Intent i = new Intent(this, NewTask.class);
        //i.putExtra("Value1", "Task Name");
        //i.putExtra("Value2", 0);
        startActivityForResult(i, 1); //request code is so we know who we are hearing back from
        /*
        list.addTask("new task "+list.size(),600000); //time is in milliseconds
        selectedTask = list.selectNewTask();
        pauseTimer();
        //refresh display
        refreshText();
        startTimer();
        */
    }

    public void refreshText() {
        taskName.setText(selectedTask.getName());
        mtextviewBreak.setText("");
        updateCountDownText(1);
        //layout.setBackgroundColor(Color.parseColor(selectedTask.getColour()));
        layout.setBackgroundResource(R.drawable.background_resume);
        //mImageView.setImageResource(R.drawable.background_resume_small);
        //layout.setBackgroundColor(Color.parseColor("#1B1F59"));
    }

    //error checking on all user input code
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                if (data.hasExtra(NewTask.NAME_KEY) && data.hasExtra(NewTask.TIME_KEY)) {
                    String n = data.getExtras().getString(NewTask.NAME_KEY);
                    String t = data.getExtras().getString(NewTask.TIME_KEY);

                    list.addTask(n, Integer.parseInt(t) *60000); //t is in minutes, need to convert to ms
                    pauseTimer();
                    selectedTask = list.selectNewTask();
                    refreshText();
                    startTimer();

                    if(data.getBooleanExtra("StartNew", false)) {
                        goRight();
                    }
                }

            }

            if (requestCode == 2) {


                selectedTask = list.selectFirstTask();
                for(int x=0;x<list.size();x++) {
                    if(data.hasExtra("item"+Integer.toString(x))) {
                        selectedTask.setTimeAllocated(
                                (long)data.getExtras().getDouble("item"+Integer.toString(x)));
                    }
                    selectedTask = list.switchTask();
                }
                //change settings
                //change variables on all timers in the list
                pauseTimer();
                selectedTask = list.selectFirstTask();
                refreshText();
                startTimer();
            }
        }
    }
}
