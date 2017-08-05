package com.justadeveloper96.bluechat;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import model.User;

/**
 * Created by Harshith on 20/7/17.
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
        View v= LayoutInflater.from(ctx).inflate(R.layout.list_user,parent,false);
        return new MyViewVolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewVolder holder, int position) {
        User user=list.get(position);
        holder.name.setText(user.name);
        holder.last_message.setText(user.last_message);
        if (user.last_msg_time>user.last_read_time)
        {
            holder.name.setTypeface(null, Typeface.BOLD);
            holder.last_message.setTypeface(null, Typeface.BOLD_ITALIC);
        }else {
            holder.name.setTypeface(null, Typeface.NORMAL);
            holder.last_message.setTypeface(null, Typeface.NORMAL);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewVolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView name, last_message;

        public MyViewVolder(View itemView) {
            super(itemView);
            name= (TextView) itemView.findViewById(R.id.text1);
            last_message = (TextView) itemView.findViewById(R.id.text2);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {

            mListener.onItemLongClick(getAdapterPosition());
            return true;
        }
    }
}
