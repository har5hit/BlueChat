package com.justadeveloper96.bluechat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import model.User;

/**
 * Created by sankalp on 20/7/17.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewVolder> {

    List<User> list;
    Context ctx;

    ItemClickListener mListener;

    public ContactsAdapter(Context ctx,List<User> list, ItemClickListener mListener) {
        this.list = list;
        this.ctx = ctx;
        this.mListener=mListener;
    }

    @Override
    public MyViewVolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(ctx).inflate(android.R.layout.simple_list_item_2,parent,false);
        return new MyViewVolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewVolder holder, int position) {
        User user=list.get(position);
        holder.name.setText(user.name);
        holder.device_name.setText(user.last_message);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewVolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name,device_name;

        public MyViewVolder(View itemView) {
            super(itemView);
            name= (TextView) itemView.findViewById(android.R.id.text1);
            device_name= (TextView) itemView.findViewById(android.R.id.text2);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(getAdapterPosition());
        }
    }
}
