package com.example.edric.blocksapp;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private ArrayList<String> mNameList = new ArrayList<>();
    private tasks mTaskList;
    private double mtotalMs;
    //list of seekbars?
    //variable to layout?
    private SettingsActivity mContext;

    public RecyclerViewAdapter(Context context, tasks taskList, int totalMs) {
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

    private String formatMsToTime(long ms) {
        int hours = (int) (ms / 3600000);
        int minutes = (int) (ms / 60000) % 60;
        return String.format(Locale.getDefault(),"%02d:%02d",hours, minutes);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        viewHolder.name = mTaskList.getList().get(i).getName();
        viewHolder.itemName.setText(mTaskList.getList().get(i).getName());
        if(mtotalMs > 0) {
            viewHolder.position = (int) Math.round((mTaskList.getList().get(i).getTimeAllocated() / mtotalMs) * 100);
            viewHolder.itemValue.setText(formatMsToTime(mTaskList.getList().get(i).getTimeAllocated()));
            //mContext.setPlanSeekBar((int)mtotalMs);
        }
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
        viewHolder.itemCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.removeTask(viewHolder.name);
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
        Button itemCloseBtn;
        RelativeLayout listitemLayout; //example uses it for onclick listener
        int position;
        String name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.list_name);
            itemSeekbar = itemView.findViewById(R.id.list_seekbar);
            itemValue = itemView.findViewById(R.id.list_value);
            itemCloseBtn = itemView.findViewById(R.id.list_close);
            listitemLayout = itemView.findViewById(R.id.listitem_layout);
            position = 1;
        }
    }
}
