package com.example.edric.blocksapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingsActivity extends AppCompatActivity {

    public static final String PLAN_KEY = "plan";
    public static final String PERIOD_KEY = "period";

    public static final String TASKS_KEY = "list";

    private RadioGroup group1, group2;
    private tasks t;
    private task ct;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        group1 = findViewById(R.id.radioGroup3);
        group2 = findViewById(R.id.radioGroup2);

        /*
        t = (tasks)getIntent().getSerializableExtra("list");

        ct = t.selectFirstTask();
        for(int x=0;x<t.size();x++)
        {
            ct = t.switchTask();
        }
        */
    }

    public void submit(View view) {
        finish();
    }

    @Override
    public void finish() {
        // Prepare data intent
        Intent data = new Intent();

        int radioButtonID = group1.getCheckedRadioButtonId();
        View radioButton = group1.findViewById(radioButtonID);
        int idx = group1.indexOfChild(radioButton);
        data.putExtra(PLAN_KEY, idx);

        radioButtonID = group2.getCheckedRadioButtonId();
        radioButton = group2.findViewById(radioButtonID);
        idx = group2.indexOfChild(radioButton);
        data.putExtra(PERIOD_KEY, idx);
        // Activity finished ok, return the data
        setResult(RESULT_OK, data);
        super.finish();
    }
}
