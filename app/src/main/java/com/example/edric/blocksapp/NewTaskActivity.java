package com.example.edric.blocksapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import static java.lang.Math.min;
import static java.lang.Math.round;

public class NewTaskActivity extends AppCompatActivity {

    public static final String NAME_KEY = "U-name"; /*!< String associated with this activities result */
    public static final String TIME_KEY = "add"; /*!< String associated with this activities result */

    private EditText name; /*!< User text input for the new tasks name */
    private TextView mTimeLabel, mMinLabel; /*!< Label that shows the user input */
    private SeekBar mSeekBarHours, mSeekBarMinutes; /*!< Seekbar input for the desired hours and mins to set */
    private Button mBtnHrM, mBtnHrA, mBtnMinM, mBtnMinA;

    //private float initialX, initialY; /*!< Co-ordinates for the onTouch event */
    private int hoursSet, minSet, planSet; /*!< Time selected by user variables */
    private boolean newActivity; /*!< Used to determine if the user wants to make another task */
    private String newTaskName;

    //constants
    private static final int MS_IN_1MIN = 60000;

    /**
     * @Brief: Initialises the variables and sets up the view events
     * @Param: Bundle savedInstanceState
     * @Note: This class is only called by the MainActivity class
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        //overridePendingTransition(R.anim.go_in, R.anim.go_out);
        Intent i = getIntent();

        newActivity = false;
        hoursSet = 0;
        minSet = 0;
        planSet = 0;
        newTaskName = "Alarm";
        name = findViewById(R.id.editText);
        mTimeLabel = findViewById(R.id.textView2);
        mMinLabel = findViewById(R.id.textView4);
        setupSeekbars();
        setUpFineButtons();
        planSet = i.getIntExtra("plan", 0);
        Log.d("NewTaskActivity", "onCreate called");
    }

    private void setUpFineButtons() {
        mBtnHrM = findViewById(R.id.btn_hr_minus);
        mBtnHrM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hoursSet = (int)Math.round((mSeekBarHours.getProgress()) -(1/0.16));
                mSeekBarHours.setProgress(hoursSet);
            }
        });
        mBtnHrA = findViewById(R.id.btn_hr_add);
        mBtnHrA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hoursSet = (int)Math.round((mSeekBarHours.getProgress()) +(1/0.16));
                mSeekBarHours.setProgress(hoursSet);
            }
        });
        mBtnMinM = findViewById(R.id.btn_min_minus);
        mBtnMinM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minSet = (int)Math.round((mSeekBarMinutes.getProgress()) -(1/0.6));
                mSeekBarMinutes.setProgress(minSet);
            }
        });
        mBtnMinA = findViewById(R.id.btn_min_plus);
        mBtnMinA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minSet = (int)Math.round((mSeekBarMinutes.getProgress()) +(1/0.6));
                mSeekBarMinutes.setProgress(minSet);
            }
        });
    }

    /**
     * @Brief: Attatches the seekbar to the view and sets up the progress changed event
     */
    private void setupSeekbars() {
        mSeekBarHours = findViewById(R.id.newtime_seekbar);
        mSeekBarHours.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                hoursSet = (int) Math.round((double)progress * 0.16);;
                mTimeLabel.setText("Hrs: " + Integer.toString(hoursSet));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSeekBarMinutes = findViewById(R.id.seekBar_min);
        mSeekBarMinutes.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                minSet = (int) Math.round((double)progress * 0.6);
                mMinLabel.setText("Mins: "+Double.toString(minSet));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * @Brief: Called by the finish buttons onClick event. And processes the users input for a
     * valid one.
     */
    public void submitEntry(View view) {
        if(name.getText().toString().equals("rename")) {
            newTaskName = "Alarm ";
        } else {
            newTaskName = name.getText().toString();
        }
        FeedReaderDbHelper db = new FeedReaderDbHelper(this);
        //check that the task name doesn't already exist in the database
        if(db.readTask(newTaskName)>0)
        {
            //delete task from group if its there
            db.deleteTaskFromGroup(newTaskName, planSet);
            //add task to group table
            long x = (hoursSet * 60) + minSet;
            db.addToGroup(newTaskName, x*MS_IN_1MIN, planSet);

            //invalidate entry //TODO there is a cleaner way to do this
            hoursSet = 0;
            minSet = 0; //this invalidates the creation of a task
        }
        finish();
    }

    /**
     * @Brief: When the task is confirmed it calculates the time to send back to the MainActivity
     * as well as the name to give the task. It checks that the values were changed from 0,
     * so a task will have time.
     */
    @Override
    public void finish() {
        // Prepare data intent
        Intent data = new Intent();
        int x = (hoursSet * 60) + minSet;
        if(x <= 0) {
            setResult(RESULT_CANCELED, data);
            //TODO notify user that creation failed
        }
        else {
            //data.putExtra(NAME_KEY, newTaskName);
            //data.putExtra(TIME_KEY, Integer.toString(x));
            //data.putExtra("StartNew", newActivity);
            FeedReaderDbHelper db = new FeedReaderDbHelper(this);
            if(planSet > 5 || planSet < 0)
                planSet = 0;
            db.addNewTask(newTaskName, x * MS_IN_1MIN, planSet);
            // Activity finished ok, return the data
            setResult(RESULT_OK, data);
        }

        super.finish();
        //overridePendingTransition(R.anim.back_out, R.anim.back_in);
    }

    /**
     * @Brief: responds to straight swipes from one side of the screen to the other.
     * @Param: MotionEvent
     * @Return: bool
     */
    /*
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

                    if(name.getText().toString().equals("must_rename")) {
                        //dont finish();
                    }
                    else {
                        newActivity = false;
                        finish();
                    }


                }

                if (initialX > finalX && Math.abs(finalY - initialY) < Math.abs(initialX - finalX)) {
                    //Log.d(TAG, "Right to Left swipe performed");

                    if(name.getText().toString().equals("must_rename")) {
                        //dont finish();
                    }
                    else {
                        newActivity = true;
                        finish();
                    }

                }

                if (initialY < finalY && Math.abs(finalX - initialX) < Math.abs(initialY - finalY)) {
                    //Log.d(TAG, "Up to Down swipe performed");
                }

                if (initialY > finalY && Math.abs(finalX - initialX) < Math.abs(initialY - finalY)) {
                    //Log.d(TAG, "Down to Up swipe performed");
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
    */
}
