package com.example.edric.blocksapp;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity implements DialogPlan.OnInputListener {
    //multi activity variables
    //public static final String PLAN_KEY = "plan";
    //public static final String PERIOD_KEY = "period";
    //public static final String TASKS_KEY = "list";
    //class variables
    private int count = 0;
    private double period = 1, plan = 48+31, blockSize = 10, mintoms = 60000; //46 blocks for r&r
    //private int planLimit = 48+31; //48 blocks is 8 hours
    //private ArrayList<String> strList = new ArrayList<String>();
    private tasks taskList = new tasks();
    private boolean changesMade = false;
    private float initialX, initialY;
    //widget variables
    private RecyclerView recyclerView;
    private SeekBar mTimeSeekbar, mPeriodSeekbar;
    private TextView mTimeText, mPeriodText, mPlanText;
    private Button mLoadBtn, mPlanDelBtn, mFinishBrn;

    private int selectedPlan;
    /*
    private RadioGroup group1, group2;
    private Button b;
    private tasks t;
    private task ct;
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //overridePendingTransition(R.anim.left_in, R.anim.left_out); //TODO: change animation to down to up
        /*
        group1 = findViewById(R.id.radioGroup3);
        group2 = findViewById(R.id.radioGroup2);
        b = findViewById(R.id.button2);
        */
        mPlanText = findViewById(R.id.textView_planNum);
        mFinishBrn = findViewById(R.id.button_settings);
        mFinishBrn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPlanToMain();
            }
        });
        selectedPlan = 1;
        mPlanDelBtn = findViewById(R.id.button_deleteplans);
        mPlanDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePlans();
            }
        });
        mLoadBtn = findViewById(R.id.button_load);
        mLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPlan();
            }
        });
        mPeriodText = findViewById(R.id.period_text);
        mPeriodSeekbar = findViewById(R.id.seekbar_period);
        mPeriodSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                period = (int)Math.round(progress/7);
                if(period == 0)
                    period = 1;
                mPeriodText.setText("      over "+Integer.toString((int)period)+ " days");
                calculateTime();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mTimeText = findViewById(R.id.time_text);
        mTimeSeekbar = findViewById(R.id.seekbar_time);
        mTimeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                plan = progress;
                if(plan == 0)
                    plan = 1;
                /* //choose to limit how much time one can allot for them self
                if(plan > planLimit)
                    plan = planLimit;
                */
                mTimeText.setText(" "+new DecimalFormat("#.#").format(plan*0.16666)+" hrs       work per day");
                calculateTime();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        recyclerView = findViewById(R.id.recyclerv_view);
        Intent i = getIntent();
        int y = Integer.parseInt(i.getStringExtra("item_count"));
        count = y;
        int totalMsLoaded = 0;

        for(int x=0;x<y;x++) {
            //strList.add(i.getStringExtra("item"+Integer.toString(x)));

            taskList.addTask(i.getStringExtra("item"+Integer.toString(x)),
                    (int)i.getLongExtra("itemValue"+Integer.toString(x),0),
                    (int)i.getLongExtra("itemSpent"+Integer.toString(x),0));

            totalMsLoaded += (int)i.getLongExtra("itemValue"+Integer.toString(x),0);
        }

        plan = (totalMsLoaded/(mintoms*10));
        mTimeText.setText(" "+new DecimalFormat("#.#").format(plan*0.16666)+" hrs       work per day");
        initRecyclerView(totalMsLoaded);
        changesMade = true;

        //RecyclerViewAdapter.ViewHolder item = (RecyclerViewAdapter.ViewHolder)recyclerView.findViewHolderForAdapterPosition(1);
        //item.itemValue.setText("poo hrs");
        //calculateTime();
        //calculateTime(); //breaks
        /*
        t = (tasks)getIntent().getSerializableExtra("list");

        ct = t.selectFirstTask();
        for(int x=0;x<t.size();x++)
        {
            ct = t.switchTask();
        }
        */
    }

    public void setPlanSeekBar(int totalMs) {
        //mTimeSeekbar.setProgress(totalMs);
    }

    private void loadPlan() {
        //create fragment
        DialogPlan dialog = new DialogPlan();
        dialog.show(getSupportFragmentManager(), "DialogPlan");
    }

    private void deletePlans() {
        FeedReaderDbHelper db = new FeedReaderDbHelper(this);
        db.clearPlanTable();
    }

    private void initRecyclerView(int totalMs){
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, taskList, totalMs);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        /* //use intents inside the viewholder?
        recyclerView.setOnDragListener(new RecyclerView.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                TextView t = findViewById(R.id.debug_text_settings);
                t.setText("clicked");
                return false;
            }
        });
        */
    }

    public void setChangesMade() {
        changesMade = true;
    }

    /*
    public void testGetValue(View view) {
        RecyclerViewAdapter.ViewHolder item;
        int[] valueList = new int[count];
        int y=0, total=0;
        for(int x=0;x<count;x++)  {
            item = (RecyclerViewAdapter.ViewHolder)recyclerView.findViewHolderForAdapterPosition(x);
            //process the task
            valueList[x] = Integer.parseInt((String)item.itemValue.getText());
            total = total + valueList[x];
            item.itemName.setText(Double.toString(3d/(double)total));
        }
    }
    */

    public void calculateTime () {
        //calculation variables
        int total = 0;
        //double[] returnTimeList = new double[count];
        int[] valueList = new int[count];
        // Prepare data intent
        //Intent data = new Intent();
        RecyclerViewAdapter.ViewHolder item;
        DecimalFormat df = new DecimalFormat("#.#");

        for(int x=0;x<count;x++)  {
            item = (RecyclerViewAdapter.ViewHolder)recyclerView.findViewHolderForAdapterPosition(x);
            //process the task
            valueList[x] = item.position;
            total = total + valueList[x];
        }
        //calculate task weight, then calculate the time
        for(int x=0;x<count;x++) {
            //returnTimeList[x] = (period * plan * blockSize *0.01666) * ((double)valueList[x]/(double)total);
            taskList.getList().get(x).setTimeAllocated(
                    (int)Math.round((period * plan * blockSize * mintoms) * ((double)valueList[x]/(double)total))); //TODO: fix this ugly code
            item = (RecyclerViewAdapter.ViewHolder)recyclerView.findViewHolderForAdapterPosition(x);
            //item.itemValue.setText(df.format(returnTimeList[x])+ " hrs");
            item.itemValue.setText(formatMsToTime(taskList.getList().get(x).getTimeAllocated()));
            //data.putExtra("item"+Integer.toString(x), returnTimeList[x]);
        }
    }

    private String formatMsToTime(long ms) { //TODO: this function is pasted over multiple classes, this should not be so
        int hours = (int) (ms / 3600000);
        int minutes = (int) (ms / 60000) % 60;
        return String.format(Locale.getDefault(),"%02d:%02d",hours, minutes);
    }

    public void closeSettings(View view) {
        finish();
    }
    public void cancelSettings(View view) {
        changesMade = false;
        finish();
    }

    @Override
    public void finish() {
        //calculation variables
        int total = 0;
        double[] returnTimeList = new double[count];
        int[] valueList = new int[count];
        // Prepare data intent
        Intent data = new Intent();
        RecyclerViewAdapter.ViewHolder item;

        for(int x=0;x<count;x++)  {
            item = (RecyclerViewAdapter.ViewHolder)recyclerView.findViewHolderForAdapterPosition(x);
            //process the task
            valueList[x] = item.position;
            total = total + valueList[x];
        }
        //calculate task weight, then calculate the time
        for(int x=0;x<count;x++) {
            returnTimeList[x] = (int)Math.round((period * plan * blockSize * mintoms) * ((double)valueList[x]/(double)total));
            data.putExtra("item"+Integer.toString(x), returnTimeList[x]);
            data.putExtra("item"+Integer.toString(x)+"name", taskList.getList().get(x).getName());
            data.putExtra("item"+Integer.toString(x)+"spent", taskList.getList().get(x).getTimeSpent());
        }
        data.putExtra("return_count", count);
        //calculate break time
        //use a struct
        //calculate extra time

        // Activity finished ok, return the data
        if(changesMade) {
            setResult(RESULT_OK, data);
        }
        else {
            setResult(RESULT_CANCELED, data);
        }
        super.finish();
    }

    @Override
    public void savePlan(int planNum) {
        Log.d("SavePlan", "write started");
        FeedReaderDbHelper db = new FeedReaderDbHelper(this);
        db.deletePlan(planNum);
        for(int x=0;x<count;x++){
            if(!db.addToGroup(taskList.getList().get(x).getName(), taskList.getList().get(x).getTimeAllocated(), planNum))
                Log.d("SavePlan", "write to group db failed");
        }

    }

    public void loadPlanToMain() {//TODO: actual load function
        //remove previous with 0 plan
        FeedReaderDbHelper db = new FeedReaderDbHelper(this);
        db.deletePlan(0);
        //save tasks to 0 plan
        savePlan(0);
        //close settingsActivity
        finish();
    }

    public void switchPlan() {
        //selectedPlan++;
        //if(selectedPlan > 5) {
        //    selectedPlan = 0;
        //}
        Log.d("SwitchPlan", "started");
        //load tasklist with those from next plan
        try {
            FeedReaderDbHelper db = new FeedReaderDbHelper(this);
            do {
                taskList.clear();
                taskList = db.readPlan(selectedPlan);
                if(selectedPlan == 0) {
                    mPlanText.setText("Loaded Plan");
                } else {
                    mPlanText.setText("Plan: " + Integer.toString(selectedPlan));
                }
                selectedPlan++;
                if(selectedPlan > 5) {
                    selectedPlan = 0;
                }
            } while (taskList.size() == 0);
            count = taskList.size();
            changesMade = true;
            plan = (taskList.getTotalMs()/(mintoms*10));
            mTimeText.setText(" "+new DecimalFormat("#.#").format(plan*0.16666)+" hrs       work per day");
            initRecyclerView(taskList.getTotalMs());
        } catch (Exception e) {
            Log.d("SwitchPlan", "error in readPlan()");
        }
    }

    //probably best not to have this functionality TODO: load the time values in
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

                /*
                if (initialX < finalX && Math.abs(finalY - initialY) < Math.abs(initialX - finalX)) {
                    //Log.d(TAG, "Left to Right swipe performed");
                    //goLeft();
                }

                if (initialX > finalX && Math.abs(finalY - initialY) < Math.abs(initialX - finalX)) {
                    //Log.d(TAG, "Right to Left swipe performed");
                    //goRight();

                }

                if (initialY < finalY && Math.abs(finalX - initialX) < Math.abs(initialY - finalY)) {
                    //Log.d(TAG, "Up to Down swipe performed");
                    //goUp();
                }
                */

                if (initialY > finalY && Math.abs(finalX - initialX) < Math.abs(initialY - finalY)) {
                    //Log.d(TAG, "Down to Up swipe performed");
                    //goDown();
                    switchPlan();
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
