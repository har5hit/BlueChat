package com.justadeveloper96.bluechat;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by harshit on 27-07-2017.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {
    private final Context context;
    List<Boolean> states;
    List<BluetoothDevice> devices;
    private final ItemClickListener listener;

    @DrawableRes int state;
    @DrawableRes int icon;

    public SearchAdapter(List<BluetoothDevice> devices, List<Boolean> states, ItemClickListener listener, Context context) {
        this.devices = devices;
        this.listener = listener;
        this.states=states;
        this.context=context;
    }

    @Override
    public SearchAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1,parent,false));
    }

    @Override
    public void onBindViewHolder(SearchAdapter.MyViewHolder holder, int position) {
        holder.text.setText(devices.get(position).getName());
        if (states.get(position)) {
            state= android.R.drawable.presence_online;
        }else
        {
            state= android.R.drawable.presence_invisible;
        }
        holder.text.setCompoundDrawablesWithIntrinsicBounds(0, 0, state,0);

    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView text;
        public MyViewHolder(View itemView) {
            super(itemView);
            text= (TextView) itemView.findViewById(android.R.id.text1);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(getAdapterPosition());
        }
    }


}
