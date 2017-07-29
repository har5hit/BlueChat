package com.justadeveloper96.bluechat;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
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
import java.util.LinkedList;
import java.util.List;

import helpers.RealmManager;
import helpers.Utils;
import helpers.bluetooth.AcceptThread;
import helpers.bluetooth.BlueHelper;
import helpers.bluetooth.BluetoothService;
import helpers.bluetooth.ConnectThread;
import helpers.bluetooth.IConnectionThread;
import io.realm.Realm;
import io.realm.Sort;
import model.ChatStatusEvent;
import model.Message;
import model.SocketEvent;
import model.User;

public class ChatActivity extends BlueActivity implements View.OnClickListener, View.OnLayoutChangeListener,Handler.Callback {

    private RecyclerView rv;
    private EditText message;
    private TextView status;
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

    private boolean FIRST_USER=false;

    Thread thread;
    Handler mHandler;

    private boolean isConnected;

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

    }

    private void getUserAndMessages() {
        user=RealmManager.getAllStoredContacts().equalTo("macAddress",macAddress_other).findFirst();

        if (user==null)
        {
            RealmManager.getRealm().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Number n;
                    int val;
                    n=realm.where(User.class).max("message_id");

                    if (n==null)
                    {
                        val=1;
                    }else
                    {
                        val=n.intValue()+1;
                    }

                    user=new User(name_other,macAddress_other,val);
                }
            });
            FIRST_USER=true;
        }else {
            list.addAll(RealmManager.getRealm().where(Message.class).equalTo("id",user.message_id).findAllSorted("timestamp", Sort.DESCENDING));
        }

    }

    private void init() {

        rv= (RecyclerView) findViewById(R.id.recyclerView);
        message= (EditText) findViewById(R.id.ed_msg);
        send= (ImageButton) findViewById(R.id.btn_send);

        toolbar= (Toolbar) findViewById(R.id.toolbar);
        status= (TextView) toolbar.findViewById(R.id.tv_status);
        setSupportActionBar(toolbar);

        list=new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        rv.setLayoutManager(manager);

        mHandler=new Handler(this);


        name_other=getIntent().getStringExtra(Constants.NAME);
        macAddress_my= BlueHelper.getBluetoothAdapter().getAddress();
        macAddress_other =getIntent().getStringExtra(Constants.MAC_ADDRESS);


        device_other=BlueHelper.getBluetoothAdapter().getRemoteDevice(macAddress_other);

        ((TextView)toolbar.findViewById(R.id.tv_name)).setText(name_other);

        //getSupportActionBar().setTitle(name_other);

        cAdapter=new ChatAdapter(this,list,macAddress_my);

        rv.setAdapter(cAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat,menu);
        btn_connect=menu.findItem(R.id.action_connect);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.action_connect)
        {
            //this.btn_connect=item;
            btn_connect.setEnabled(false);
            startThread(MODE_CONNECT);
        }

        return super.onOptionsItemSelected(item);
    }

    private void startThread(int threadMode) {
        if (threadMode==MODE_ACCEPT)
        {
            thread=new AcceptThread();

        }else
        {
            thread=new ConnectThread(device_other);
        }
        thread.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocketReceived(SocketEvent socketEvent)
    {
        send.setEnabled(true);
        isConnected=true;
        if (connection==null) {
            connection = new BluetoothService(socketEvent.socket, mHandler,macAddress_other);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStatusEvent(ChatStatusEvent statusEvent)
    {
        Log.d(TAG, "onStatusEvent: "+Constants.ERROR_MSG[statusEvent.status]);

        if (status.getText().length()==0) {
            TransitionManager.beginDelayedTransition(toolbar);
            status.setVisibility(View.VISIBLE);
        }


        status.setText(Constants.ERROR_MSG[statusEvent.status]);

        if (statusEvent.status==Constants.STATUS_CONNECTING_FAILED)
        {
            ((IConnectionThread)thread).cancel();
            startThread(MODE_ACCEPT);
        }

        if (statusEvent.status==Constants.STATUS_DISCONNECTED)
        {
            btn_connect.setEnabled(true);
            send.setEnabled(false);
            isConnected=false;
            Utils.showToast(this,name_other+" Disconnected");
        }



        scrollToBottom();
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


        list.add(0,new Message(data,who,System.currentTimeMillis(),user.message_id));
        cAdapter.notifyItemInserted(0);
        rv.smoothScrollToPosition(0);
    }

    @Override
    protected void onPause() {
        saveData();
        super.onPause();
    }

    private void saveData() {
        if (list.size()>0)
        {
            RealmManager.getRealm().beginTransaction();
            user.last_message=((Message)list.get(0)).message;
            RealmManager.getRealm().commitTransaction();
            RealmManager.saveData(user);
        }

        List<Message> finalList=new LinkedList<>();

        for (Object m:list)
        {
            if (m instanceof Message )
            {
                finalList.add((Message) m);
            }
        }

        RealmManager.saveData(finalList);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        BlueHelper.isInChat=true;
    }

    @Override
    protected void onStop() {
        BlueHelper.isInChat=false;
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        try{
            ((IConnectionThread)thread).cancel();
            if (connection!=null)
            {
                connection.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        super.onDestroy();
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
        scrollToBottom();
    }

    @Override
    public boolean handleMessage(android.os.Message msg) {
        storeMessage(msg);
        return true;
    }
}
