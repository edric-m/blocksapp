package com.example.edric.blocksapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
    //multi activity variables
    public static final String PLAN_KEY = "plan";
    public static final String PERIOD_KEY = "period";
    public static final String TASKS_KEY = "list";
    //class variables
    private int count = 0;
    private double period = 1, plan = 48+46, blockSize = 10, mintoms = 60000; //46 blocks for r&r
    private ArrayList<String> strList = new ArrayList<String>();
    //widget variables
    private RecyclerView recyclerView;
    private SeekBar mTimeSeekbar, mPeriodSeekbar;
    private TextView mTimeText, mPeriodText;
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

        /*
        group1 = findViewById(R.id.radioGroup3);
        group2 = findViewById(R.id.radioGroup2);
        b = findViewById(R.id.button2);
        */
        mPeriodText = findViewById(R.id.period_text);
        mPeriodSeekbar = findViewById(R.id.seekbar_period);
        mPeriodSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                period = (int)Math.round(progress/7);
                if(period == 0)
                    period = 1;
                mPeriodText.setText("Total days: "+Double.toString(period));
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
                mTimeText.setText("Total time per day: "+new DecimalFormat("#.#").format(plan*0.16666)+" hrs");
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
        for(int x=0;x<y;x++) {
            strList.add(i.getStringExtra("item"+Integer.toString(x)));
        }

        initRecyclerView();
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

    private void initRecyclerView(){
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, strList);
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

    public void calculateTime () {
        //calculation variables
        int total = 0;
        double[] returnTimeList = new double[count];
        int[] valueList = new int[count];
        // Prepare data intent
        Intent data = new Intent();
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
            returnTimeList[x] = (period * plan * blockSize *0.01666) * ((double)valueList[x]/(double)total);
            item = (RecyclerViewAdapter.ViewHolder)recyclerView.findViewHolderForAdapterPosition(x);
            item.itemValue.setText(df.format(returnTimeList[x])+ " hrs");
            //data.putExtra("item"+Integer.toString(x), returnTimeList[x]);
        }
    }

    public void closeSettings(View view) {
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
        }
        //calculate break time
        //use a struct
        //calculate extra time

        // Activity finished ok, return the data
        setResult(RESULT_OK, data);
        super.finish();
    }
}
