package com.example.edric.blocksapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

public class SettingsActivity extends AppCompatActivity {

    public static final String PLAN_KEY = "plan";
    public static final String PERIOD_KEY = "period";

    private RadioButton reg, rel, alln;
    private RadioButton d, w, m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        reg = findViewById(R.id.regular);
        rel = findViewById(R.id.relaxed);
        alln = findViewById(R.id.allnight);

        d = findViewById(R.id.day);
        m = findViewById(R.id.month);
        w = findViewById(R.id.week);
    }

    public void submit(View view) {
        finish();
    }

    @Override
    public void finish() {
        // Prepare data intent
        Intent data = new Intent();
        //data.putExtra(PLAN_KEY, name.getText().toString());
        //data.putExtra(PERIOD_KEY, time.getText().toString());
        // Activity finished ok, return the data
        setResult(RESULT_OK, data);
        super.finish();
    }
}
