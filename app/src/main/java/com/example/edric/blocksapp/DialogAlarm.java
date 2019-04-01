package com.example.edric.blocksapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class DialogAlarm extends DialogFragment {
    public interface OnInputListener{
        void setQuickAlarm(int mins);
    }
    public DialogAlarm.OnInputListener mAlarmOnInputListener;

    //widgets
    private Button min1,min5,min10,min15,min30,min60,cancel;

    public DialogAlarm () {
        //empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_alarm, container, false);
        min1 = view.findViewById(R.id.button_1min);
        min5 = view.findViewById(R.id.button_5min);
        min10 = view.findViewById(R.id.button_10min);
        min15 = view.findViewById(R.id.button_15min);
        min30 = view.findViewById(R.id.button_30min);
        min60 = view.findViewById(R.id.button_60min);
        cancel = view.findViewById(R.id.button_alarm_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "onClick: closing dialog");
                getDialog().dismiss();
            }
        });


        min1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "onClick: capturing input");

                mAlarmOnInputListener.setQuickAlarm(1);

                getDialog().dismiss();
            }
        });

        min5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "onClick: capturing input");

                mAlarmOnInputListener.setQuickAlarm(5);

                getDialog().dismiss();
            }
        });

        min10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "onClick: capturing input");

                mAlarmOnInputListener.setQuickAlarm(10);

                getDialog().dismiss();
            }
        });

        min15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "onClick: capturing input");

                mAlarmOnInputListener.setQuickAlarm(15);

                getDialog().dismiss();
            }
        });

        min30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "onClick: capturing input");

                mAlarmOnInputListener.setQuickAlarm(30);

                getDialog().dismiss();
            }
        });

        min60.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "onClick: capturing input");

                mAlarmOnInputListener.setQuickAlarm(60);

                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mAlarmOnInputListener = (DialogAlarm.OnInputListener) getActivity();
        }catch (ClassCastException e){
            //Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
    }
}
