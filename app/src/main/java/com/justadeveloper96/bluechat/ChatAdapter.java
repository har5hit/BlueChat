package com.justadeveloper96.bluechat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import model.Message;

/**
 * Created by harshith on 24/7/17.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context ctx;
    List<Object> list;
    String message;


    private static final int SELF = 531;
    private static final int OTHER = 997;
    private static final int STATUS_MESSAGE = 456;
    private static String MY_MAC;

    public ChatAdapter(Context ctx, List<Object> list, String my_mac) {
        this.ctx = ctx;
        this.list = list;
        MY_MAC = my_mac;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        if (viewType==STATUS_MESSAGE)
        {
            return new StatusHolder(LayoutInflater.from(ctx).inflate(R.layout.chat_thread_status, parent, false));
        }
        if (viewType == SELF) {
            v = LayoutInflater.from(ctx).inflate(R.layout.chat_thread_self, parent, false);
        } else {
            v = LayoutInflater.from(ctx).inflate(R.layout.chat_thread_other, parent, false);
        }
        return new MessageHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MessageHolder) {
            message = ((Message) list.get(position)).message;
        }else
        {
            message= (String) list.get(position);
        }

        ((TextHolder)holder).setText(message);
    }


    @Override
    public int getItemCount () {
        return list.size();
    }

    public class MessageHolder extends RecyclerView.ViewHolder implements TextHolder {
        TextView text;

        public MessageHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);
        }

        @Override
        public void setText(String s) {
            text.setText(s);
        }
    }

    public class StatusHolder extends RecyclerView.ViewHolder implements TextHolder {
        TextView text;

        public StatusHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);
        }

        @Override
        public void setText(String s) {
            text.setText(s);
        }
    }

    public interface TextHolder {
        void setText(String s);
    }

    @Override
    public int getItemViewType ( int position){
        if (list.get(position) instanceof String) {
            return STATUS_MESSAGE;
        }
        if (((Message) list.get(position)).user_mac.equals(MY_MAC)) {
            return SELF;
        }
        return OTHER;
    }
}