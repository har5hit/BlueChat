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

    public ContactsAdapter(Context ctx,List<User> list) {
        this.list = list;
        this.ctx = ctx;
    }

    @Override
    public MyViewVolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(ctx).inflate(R.layout.list_user,parent,false);
        return new MyViewVolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewVolder holder, int position) {
        User user=list.get(position);
        holder.name.setText(user.name);
        holder.device_name.setText(user.device_name);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewVolder extends RecyclerView.ViewHolder {

        TextView name,device_name;

        public MyViewVolder(View itemView) {
            super(itemView);
            name= (TextView) itemView.findViewById(R.id.tv_name);
            device_name= (TextView) itemView.findViewById(R.id.tv_device_name);
        }
    }
}
