package com.example.edric.blocksapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;

public class NewTask extends AppCompatActivity {

    public static final String NAME_KEY = "U-name";
    public static final String TIME_KEY = "add";

    private EditText name;
    private EditText time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        name = findViewById(R.id.editText);
        time = findViewById(R.id.editText2);
    }

    public void submitEntry(View view) {
        finish();
    }

    @Override
    public void finish() {
        // Prepare data intent
        Intent data = new Intent();
        data.putExtra(NAME_KEY, name.getText().toString());
        data.putExtra(TIME_KEY, time.getText().toString());
        // Activity finished ok, return the data
        setResult(RESULT_OK, data);
        super.finish();
    }
}
