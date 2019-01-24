package com.example.edric.blocksapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import static java.lang.Math.round;

public class NewTask extends AppCompatActivity {

    public static final String NAME_KEY = "U-name";
    public static final String TIME_KEY = "add";

    private EditText name;
    private TextView mTimeLabel, mMinLabel;
    private SeekBar mSeekBarHours, mSeekBarMinutes;

    private float initialX, initialY;
    private int hoursSet, minSet;
    private boolean newActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        overridePendingTransition(R.anim.go_in, R.anim.go_out);

        newActivity = false;
        hoursSet = 0;
        minSet = 0;
        name = findViewById(R.id.editText);
        mTimeLabel = findViewById(R.id.textView2);
        mMinLabel = findViewById(R.id.textView4);
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

    public void submitEntry(View view) {
        if(name.getText().toString().equals("must_rename")) {
            hoursSet = 0;
            minSet = 0;
        }
        finish();
    }

    @Override
    public void finish() {
        // Prepare data intent
        Intent data = new Intent();
        int x = (hoursSet * 60) + minSet;
        if(x <= 0) {
            setResult(RESULT_CANCELED, data);
        }
        else {
            data.putExtra(NAME_KEY, name.getText().toString());
            data.putExtra(TIME_KEY, Integer.toString(x));
            data.putExtra("StartNew", newActivity);
            // Activity finished ok, return the data
            setResult(RESULT_OK, data);
        }

        super.finish();
        overridePendingTransition(R.anim.back_out, R.anim.back_in);
    }

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
}
