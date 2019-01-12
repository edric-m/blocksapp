package com.example.edric.blocksapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.os.CountDownTimer;
import android.widget.TextView;
import java.util.Locale;
import android.view.View;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private tasks list;
    private task selectedTask;

    private TextView taskName;
    private TextView taskTime;
    private ConstraintLayout layout;

    private CountDownTimer mCountdownTimer;
    private boolean mTimerRunning;

    private CountDownTimer mPauseTimer;
    private long timePaused;
    private boolean mPaused;

    private int setPlanPref;
    private int setPeriodPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = new tasks();
        taskName = findViewById(R.id.task_name);
        taskTime = findViewById(R.id.task_time);
        layout = findViewById(R.id.layout);

        mTimerRunning = false;
        mPaused = false;

        setPeriodPref = 1;
        setPlanPref = 1;
        //refresh display
        selectedTask = list.selectNewTask();
        //refresh display
        refreshText();
        startTimer();
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
                selectedTask = list.switchTask();
                layout.setBackgroundColor(Color.parseColor(selectedTask.getColour()));
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
            taskName.setText("break time");
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

    public void goUp(View view) {
        //pause timer
        pauseTimer();
    }

    public void goDown(View view) { //theres a bug here on startup

        if (mPaused) {
            startTimer();
            refreshText();
            //mPaused = false;
        }

        //resume task or
        if (mTimerRunning){
            pauseTimer();
            //switch task
            selectedTask = list.switchTask();
            //refresh display
            refreshText();
            startTimer();

            //layout.setBackgroundColor(Color.parseColor(selectedTask.getColour()));
        }
        //following should be in an else
        /*
        if(!mTimerRunning) {

            //selectedTask = list.resumeTask();
            startTimer();
        }*/

    }

    public void goLeft(View view) {
        //settings
        Intent i = new Intent(this, SettingsActivity.class);
        int idx = 0;
        for (task t: list.getList()) {
            i.putExtra("item"+Integer.toString(idx),t.getName());
            idx++;
        }
        i.putExtra("item_count", Integer.toString(idx));
        startActivityForResult(i, 2);
        
    }

    public void submitTest(View view) {

    }

    public void goRight(View view) {
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
        updateCountDownText(1);
        layout.setBackgroundColor(Color.parseColor(selectedTask.getColour()));
    }

    //error checking on all user input code
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                if (data.hasExtra(NewTask.NAME_KEY) && data.hasExtra(NewTask.TIME_KEY)) {
                    String n = data.getExtras().getString(NewTask.NAME_KEY);
                    String t = data.getExtras().getString(NewTask.TIME_KEY);

                    list.addTask(n, Integer.parseInt(t));
                    pauseTimer();
                    selectedTask = list.selectNewTask();
                    refreshText();
                    startTimer();
                }

            }

            if (requestCode == 2) {
                if (data.hasExtra(SettingsActivity.PLAN_KEY) && data.hasExtra(SettingsActivity.PERIOD_KEY)) {
                    /*
                    String pl = data.getExtras().getString(SettingsActivity.PLAN_KEY);
                    String pd = data.getExtras().getString(SettingsActivity.PERIOD_KEY);

                    setPlanPref = Integer.parseInt(pl);
                    setPeriodPref = Integer.parseInt(pd);
                    //change settings
                    //change variables on all timers in the list
                    pauseTimer();
                    refreshText();
                    startTimer();
                    */
                }
            }
        }
    }
}
