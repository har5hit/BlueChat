package com.justadeveloper96.bluechat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import helpers.SharedPrefs;
import helpers.Utils;
import model.Message;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rv;
    private ChatAdapter cAdapter;
    private List<Message> list;


    private EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        rv= (RecyclerView) findViewById(R.id.recyclerView);
        message= (EditText) findViewById(R.id.ed_msg);

        list=new ArrayList<>();

        cAdapter=new ChatAdapter(this,list);


        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        rv.setLayoutManager(manager);
        rv.setAdapter(cAdapter);
    }


    public void sendMessage(View v)
    {
        list.add(new Message
                (Utils.getText(message),
                SharedPrefs.getPrefs().getInt(SharedPrefs.USER_ID),
                (System.currentTimeMillis()/1000))
        );
        cAdapter.notifyDataSetChanged();
    }



}
