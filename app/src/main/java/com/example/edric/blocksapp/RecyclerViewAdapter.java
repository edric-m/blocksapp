package com.example.edric.blocksapp;

import android.content.Context;
import android.support.annotation.NonNull;
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
    //list of seekbars?
    //variable to layout?
    private Context mContext;

    public RecyclerViewAdapter(Context context, ArrayList<String> nameList) {
        this.mNameList = nameList;
        this.mContext = context;
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

        viewHolder.itemName.setText(mNameList.get(i));
        viewHolder.itemSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                viewHolder.itemValue.setText(Integer.toString(progress));
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
        return mNameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemName;
        SeekBar itemSeekbar;
        TextView itemValue;
        RelativeLayout listitemLayout; //example uses it for onclick listener

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.list_name);
            itemSeekbar = itemView.findViewById(R.id.list_seekbar);
            itemValue = itemView.findViewById(R.id.list_value);
            listitemLayout = itemView.findViewById(R.id.listitem_layout);
        }
    }
}
