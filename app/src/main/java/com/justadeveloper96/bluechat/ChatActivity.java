package com.justadeveloper96.bluechat;

import android.app.NotificationManager;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.transition.TransitionManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import helpers.MyApplication;
import helpers.RealmManager;
import helpers.Utils;
import helpers.bluetooth.AcceptThread;
import helpers.bluetooth.BlueHelper;
import helpers.bluetooth.BluetoothService;
import helpers.bluetooth.ConnectThread;
import io.realm.RealmResults;
import io.realm.Sort;
import model.ChatStatusEvent;
import model.Message;
import model.MessageEvent;
import model.SocketEvent;
import model.User;

/**
 * Created by Harshith on 20/7/17.
 */

public class ChatActivity extends BlueActivity implements View.OnClickListener, View.OnLayoutChangeListener {

    private RecyclerView rv;
    private EditText message;
    private TextView status,title;
    private ImageButton send;
    private MenuItem btn_connect;
    private Toolbar toolbar;

    private ChatAdapter cAdapter;
    private List<Object> list;
    BluetoothService connection;


    private static final int MODE_ACCEPT = 813;
    private static final int MODE_CONNECT = 875;


    private static final String TAG = "ChatActivity";

    User user;


    private String macAddress_my;
    private String macAddress_other;
    private String name_other;

    private BluetoothDevice device_other;

    Thread thread;

    private boolean isConnected;
    private LinearLayoutManager manager;

    int current_msg_count;

    int total_msg_count;

    RealmResults<Message> chat_db;

    boolean pagination_done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();

        setUpListeners();

        getUserAndMessages();

    }

    private void setUpListeners() {

        send.setOnClickListener(this);
        rv.addOnLayoutChangeListener(this);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(manager.findLastCompletelyVisibleItemPosition()==list.size()-1)
                {
                    loadMore();
                }
            }
        });

    }

    private void getUserAndMessages() {
      /*  user=RealmManager.getAllStoredContacts().equalTo("macAddress",macAddress_other).findFirst();
        if (user!=null) {
            chat_db = RealmManager.getRealm().where(Message.class).equalTo("id", user.message_id).findAllSorted("timestamp", Sort.DESCENDING);
            total_msg_count =chat_db.size();
            loadMore();
            rv.scrollToPosition(0);
        }*/
    }

    private void init() {

        rv= (RecyclerView) findViewById(R.id.recyclerView);
        message= (EditText) findViewById(R.id.ed_msg);
        send= (ImageButton) findViewById(R.id.btn_send);

        toolbar= (Toolbar) findViewById(R.id.toolbar);
        title= (TextView) toolbar.findViewById(R.id.tv_name);
        status= (TextView) toolbar.findViewById(R.id.tv_status);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list=new ArrayList<>();

        manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        rv.setLayoutManager(manager);


        name_other=getIntent().getStringExtra(Constants.NAME);
        macAddress_other =getIntent().getStringExtra(Constants.MAC_ADDRESS);
        macAddress_my= BlueHelper.getBluetoothAdapter().getAddress();

        device_other=BlueHelper.getBluetoothAdapter().getRemoteDevice(macAddress_other);

        title.setText(name_other);

        cAdapter=new ChatAdapter(this,list,macAddress_my);

        rv.setAdapter(cAdapter);

        current_msg_count = total_msg_count =0;

        pagination_done=false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat,menu);
        btn_connect=menu.findItem(R.id.action_connect);
        if (isConnected)
        {
            btn_connect.setTitle(getString(R.string.menu_disconnect));
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.action_connect) {
            if (isConnected) {
                MyApplication.closeBluetoothService(macAddress_other);
                connection=null;
            } else {
                startThread(MODE_CONNECT);
            }

            btn_connect.setEnabled(false);
        }else if (item.getItemId()==android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void storeMessage(MessageEvent me) {

        if (!me.macAddress_other.equals(macAddress_other))
        {
            return;
        }

        String data;

        String who;
        if (me.message.what== BluetoothService.MessageConstants.MESSAGE_READ)
        {
            data = new String(((byte[]) me.message.obj),0,me.message.arg1);
            who=macAddress_other;
        }else
        {
            data = new String((byte[]) me.message.obj);
            who=macAddress_my;
        }

        list.add(0,new Message(data,who,System.currentTimeMillis(),0));
        cAdapter.notifyItemInserted(0);
    }

    private void startThread(int threadMode) {

        if (threadMode==MODE_ACCEPT)
        {
            thread=new AcceptThread(macAddress_other);

        }else
        {
            thread=new ConnectThread(device_other);
        }
        thread.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocketReceived(SocketEvent socketEvent)
    {
//        if (connection==null) {

            Utils.log("requesting new bservice on socket received");
            connection = MyApplication.getBLUETOOTHSERVICE(socketEvent.socket,macAddress_other,name_other);
        //}

        onChatConnected();
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onStatusEvent(ChatStatusEvent statusEvent)
    {
        if (!statusEvent.macAddress.equals(macAddress_other)){
            return;
        }

        if (status.getText().length()==0) {
            TransitionManager.beginDelayedTransition(toolbar);
            status.setVisibility(View.VISIBLE);
        }

        status.setText(Constants.ERROR_MSG[statusEvent.status]);


        Log.d(TAG, "onStatusEvent: "+Constants.ERROR_MSG[statusEvent.status]);

        switch (statusEvent.status)
        {
            case Constants.STATUS_CONNECTING_FAILED:
                startThread(MODE_ACCEPT);

                break;
            case Constants.STATUS_CONNECTED:
                //useless as when connected in chat screen, the connection will be done in onSocketReceived and is handled else it will be on at onResume.
               onChatConnected();

                break;
            case Constants.STATUS_DISCONNECTED:
                onChatDisconnected();
                break;

            case Constants.STATUS_LISTENING_FAILED:
                onChatDisconnected();
        }
    }

    private void scrollToBottom() {
        rv.smoothScrollToPosition(0);
    }

    private void storeMessage(android.os.Message msg) {

        if (msg.what== BluetoothService.MessageConstants.MESSAGE_TOAST)
        {
            //Utils.showToast(this,msg.getData().getString("toast"));
            return;
        }
        String data;

        String who;
        if (msg.what== BluetoothService.MessageConstants.MESSAGE_READ)
        {
            data = new String(((byte[]) msg.obj),0,msg.arg1);
            who=macAddress_other;
        }else
        {
            data = new String((byte[]) msg.obj);
            who=macAddress_my;
        }


        list.add(0,new Message(data,who,System.currentTimeMillis(),2439168));
        cAdapter.notifyItemInserted(0);
    }


    public void onChatConnected()
    {
        isConnected=true;
        if (status.getText().length()==0) {
            TransitionManager.beginDelayedTransition(toolbar);
            status.setVisibility(View.VISIBLE);
        }

        if (btn_connect!=null) {
            btn_connect.setTitle(R.string.menu_disconnect);
            btn_connect.setEnabled(true);
        }
        Utils.showToast(this,name_other+" "+Constants.ERROR_MSG[Constants.STATUS_CONNECTED]);
        status.setText(Constants.ERROR_MSG[Constants.STATUS_CONNECTED]);

        title.setCompoundDrawablesWithIntrinsicBounds(0,0,android.R.drawable.presence_online,0);
    }

    public void onChatDisconnected()
    {
        connection=null;
        isConnected=false;
        if (btn_connect!=null) {
            btn_connect.setTitle(R.string.menu_connect);
            btn_connect.setEnabled(true);
        }//Utils.showToast(this,name_other+" "+Constants.ERROR_MSG[Constants.STATUS_DISCONNECTED]);
        title.setCompoundDrawablesWithIntrinsicBounds(0,0,android.R.drawable.presence_invisible,0);
    }
    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        MyApplication.currentWindow="";
        saveData();

        try{
            if (thread instanceof AcceptThread)
            {
                ((AcceptThread) thread).cancel();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        super.onPause();
    }

    private void saveData() {
        if (list.size()>0)
        {
            RealmManager.getRealm().beginTransaction();
            RealmManager.getRealm().where(User.class).equalTo("macAddress",macAddress_other).findFirst().last_read_time=System.currentTimeMillis();
            RealmManager.getRealm().where(User.class).equalTo("macAddress",macAddress_other).findFirst().name=name_other;
            RealmManager.getRealm().commitTransaction();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.currentWindow=macAddress_other;
        EventBus.getDefault().register(this);

        user=RealmManager.getAllStoredContacts().equalTo("macAddress",macAddress_other).findFirst();

        if (user!=null) {
            list.clear();
            chat_db = RealmManager.getRealm().where(Message.class).equalTo("id", user.message_id).findAllSorted("timestamp", Sort.DESCENDING);
            total_msg_count =chat_db.size();
            Utils.log("Chat Activity on resume user found"+total_msg_count);
            loadMore();
        }

        try{
            int notify_id=MyApplication.notify_id.get(macAddress_other);
            NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(notify_id);
            MyApplication.notify_count.put(macAddress_other,0);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        if (MyApplication.getBLUETOOTHSERVICE()!=null && MyApplication.getBLUETOOTHSERVICE().macAddress.equals(macAddress_other))
        {
            connection=MyApplication.getBLUETOOTHSERVICE();
            TransitionManager.beginDelayedTransition(toolbar);
            status.setVisibility(View.VISIBLE);
            title.setCompoundDrawablesWithIntrinsicBounds(0,0,android.R.drawable.presence_online,0);
            status.setText(Constants.ERROR_MSG[Constants.STATUS_CONNECTED]);
            isConnected=true;
        }
    }

    @Override
    public void onClick(View v) {

        if(!isConnected)
        {
            Utils.showToast(this,"Not Connected to "+name_other);
            return;
        }
        if (Utils.getText(message).isEmpty())
        {
            return;
        }
        connection.write(Utils.getText(message));
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (pagination_done)
        {
            pagination_done=false;
        }else
        {
            scrollToBottom();
        }
    }

    private void loadMore() {
        Utils.log("load more called");
        if (total_msg_count > current_msg_count) {
            current_msg_count =  current_msg_count+20;
            current_msg_count=  (current_msg_count<total_msg_count)?current_msg_count:total_msg_count;
            list.addAll(chat_db.subList(list.size(),current_msg_count));
            pagination_done=true;
            cAdapter.notifyDataSetChanged();
        }
    }
}
