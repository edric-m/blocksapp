package com.example.edric.blocksapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    public static final String PLAN_KEY = "plan";
    public static final String PERIOD_KEY = "period";

    public static final String TASKS_KEY = "list";

    private int count;
    private int period = 1, plan = 100, blockSize = 10;

    private RecyclerView recyclerView;
    private RadioGroup group1, group2;
    private Button b;
    private tasks t;
    private task ct;

    private ArrayList<String> strList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        /*
        group1 = findViewById(R.id.radioGroup3);
        group2 = findViewById(R.id.radioGroup2);
        b = findViewById(R.id.button2);
        */
        recyclerView = findViewById(R.id.recyclerv_view);
        Intent i = getIntent();
        int y = Integer.parseInt(i.getStringExtra("item_count"));
        count = y;
        for(int x=0;x<y;x++) {
            strList.add(i.getStringExtra("item"+Integer.toString(x)));
        }

        initRecyclerView();
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
    }

    /*
    public void submit(View view) {
        b.setText("clicked");
        //finish();
    }*/

    public void closeSettings(View view) {
        finish();
    }

    @Override
    public void finish() {
        //calculation variables
        int total = 0;
        float[] returnTimeList = new float[count];
        int[] valueList = new int[count];
        // Prepare data intent
        Intent data = new Intent();
        RecyclerViewAdapter.ViewHolder item;

        for(int x=0;x<count;x++)  {
            item = (RecyclerViewAdapter.ViewHolder)recyclerView.findViewHolderForAdapterPosition(x);
            //process the task
            valueList[x] = Integer.parseInt((String)item.itemValue.getText());
            total = total + valueList[x];
        }
        //calculate task weight, then calculate the time
        for(int x=0;x<count;x++) {
            returnTimeList[x] = ((period * plan * blockSize) * (valueList[x]/total));
            data.putExtra("item"+Integer.toString(x),
                    ((period * plan * blockSize) * (valueList[x]/total)));
        }
        //calculate break time
        //use a struct
        //calculate extra time

        // Activity finished ok, return the data
        setResult(RESULT_OK, data);
        super.finish();
    }
}
