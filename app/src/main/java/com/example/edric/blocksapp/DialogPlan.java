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
import android.widget.EditText;
import android.widget.TextView;

public class DialogPlan extends DialogFragment {
    private static final String TAG = "MyCustomDialog";

    public interface OnInputListener{
        void savePlan(int input);
    }
    public OnInputListener mOnInputListener;

    //widgets
    private Button p1,p2,p3,p4,p5,cancel;


    //vars

    public DialogPlan () {
        //empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_plan, container, false);
        p1 = view.findViewById(R.id.button_plan1);
        p2 = view.findViewById(R.id.button_plan2);
        p3 = view.findViewById(R.id.button_plan3);
        p4 = view.findViewById(R.id.button_plan4);
        p5 = view.findViewById(R.id.button_plan5);
        cancel = view.findViewById(R.id.button_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing dialog");
                getDialog().dismiss();
            }
        });


        p1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: capturing input");

                mOnInputListener.savePlan(1);

                getDialog().dismiss();
            }
        });

        p2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: capturing input");

                mOnInputListener.savePlan(2);

                getDialog().dismiss();
            }
        });

        p3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: capturing input");

                mOnInputListener.savePlan(3);

                getDialog().dismiss();
            }
        });

        p4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: capturing input");

                mOnInputListener.savePlan(4);

                getDialog().dismiss();
            }
        });

        p5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: capturing input");

                mOnInputListener.savePlan(5);

                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mOnInputListener = (OnInputListener) getActivity();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
    }
}
