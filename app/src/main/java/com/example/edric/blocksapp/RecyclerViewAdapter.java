package com.example.edric.blocksapp;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private ArrayList<String> mNameList = new ArrayList<>();
    private ArrayList<task> mTaskList;
    private double mtotalMs;
    //list of seekbars?
    //variable to layout?
    private SettingsActivity mContext;

    public RecyclerViewAdapter(Context context, ArrayList<task> taskList, int totalMs) {
        this.mTaskList = taskList;
        this.mContext = (SettingsActivity)context;
        this.mtotalMs = totalMs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        viewHolder.itemName.setText(mTaskList.get(i).getName());
        viewHolder.position = (int)Math.round((mTaskList.get(i).getTimeAllocated()/mtotalMs)*100);
        viewHolder.itemSeekbar.setProgress(viewHolder.position); //TODO: not converted to hours
        //viewHolder.itemSeekbar.setProgress(50);
        //viewHolder.position = 50; //TODO: works but cannot change text
        viewHolder.itemSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //viewHolder.itemValue.setText(Integer.toString(progress));
                viewHolder.position = progress;
                mContext.setChangesMade();
                mContext.calculateTime();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mTaskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemName;
        SeekBar itemSeekbar;
        TextView itemValue;
        RelativeLayout listitemLayout; //example uses it for onclick listener
        int position;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.list_name);
            itemSeekbar = itemView.findViewById(R.id.list_seekbar);
            itemValue = itemView.findViewById(R.id.list_value);
            listitemLayout = itemView.findViewById(R.id.listitem_layout);
            position = 1;
        }
    }
}
