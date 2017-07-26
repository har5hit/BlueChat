package com.justadeveloper96.bluechat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import helpers.SharedPrefs;
import model.Message;

/**
 * Created by harshith on 24/7/17.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    Context ctx;
    List<Message> list;

    private static final int SELF = 531;
    private static final int OTHER = 997;
    private static int USER_ID;

    public ChatAdapter(Context ctx, List<Message> list) {
        this.ctx = ctx;
        this.list = list;
        USER_ID= SharedPrefs.getPrefs().getInt(SharedPrefs.USER_ID);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType== SELF)
        {
            v= LayoutInflater.from(ctx).inflate(R.layout.chat_thread_self,parent,false);
        }else
        {
            v= LayoutInflater.from(ctx).inflate(R.layout.chat_thread_other,parent,false);
        }

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).user_id==USER_ID)
        {
            return SELF;
        }
        return OTHER;
    }
}
