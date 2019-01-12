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

    private RadioGroup group1, group2;
    private Button b;
    private tasks t;
    private task ct;

    private ArrayList<String> strList = new ArrayList<String>();
    private ArrayList<String> testList = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        /*
        group1 = findViewById(R.id.radioGroup3);
        group2 = findViewById(R.id.radioGroup2);
        b = findViewById(R.id.button2);
        */

        Intent i = getIntent();
        int y = Integer.parseInt(i.getStringExtra("item_count"));
        for(int x=0;x<y;x++) {
            strList.add(i.getStringExtra("item"+Integer.toString(x)));
        }

        testList.add("blah");
        testList.add("blahblah"); //this works
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
        RecyclerView recyclerView = findViewById(R.id.recyclerv_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, strList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /*
    public void submit(View view) {
        b.setText("clicked");
        //finish();
    }*/

    public void closeSettings(View view) {finish();}

    @Override
    public void finish() {
        // Prepare data intent
        Intent data = new Intent();
/*
        int radioButtonID = group1.getCheckedRadioButtonId();
        View radioButton = group1.findViewById(radioButtonID);
        int idx = group1.indexOfChild(radioButton);*/
        data.putExtra(PLAN_KEY, 0);
/*
        radioButtonID = group2.getCheckedRadioButtonId();
        radioButton = group2.findViewById(radioButtonID);
        idx = group2.indexOfChild(radioButton);*/
        data.putExtra(PERIOD_KEY, 0);
        // Activity finished ok, return the data
        setResult(RESULT_OK, data);
        super.finish();
    }
}
