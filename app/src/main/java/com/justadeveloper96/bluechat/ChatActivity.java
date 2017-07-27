package com.justadeveloper96.bluechat;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import helpers.RealmManager;
import helpers.Utils;
import helpers.bluetooth.AcceptThread;
import helpers.bluetooth.BlueHelper;
import helpers.bluetooth.ConnectThread;
import io.realm.Realm;
import io.realm.Sort;
import model.Message;
import model.SocketEvent;
import model.User;

public class ChatActivity extends BlueActivity {

    private RecyclerView rv;
    private ChatAdapter cAdapter;
    private List<Message> list;

    private EditText message;

    private static final String TAG = "ChatActivity";

    User user;

    private String macAddress_my;
    private String macAddress_other;
    private String name_other;

    private BluetoothDevice device_other;

    private boolean FIRST_USER=false;

    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        rv= (RecyclerView) findViewById(R.id.recyclerView);
        message= (EditText) findViewById(R.id.ed_msg);
        macAddress_other =getIntent().getStringExtra(Constants.MAC_ADDRESS);
        name_other=getIntent().getStringExtra(Constants.NAME);

        device_other=BlueHelper.getBluetoothAdapter().getRemoteDevice(macAddress_other);


        try {
            Log.d(TAG, "onCreate: device connecting");
            device_other.createRfcommSocketToServiceRecord(Constants.uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }


        getSupportActionBar().setTitle(name_other);

        macAddress_my= BlueHelper.getBluetoothAdapter().getAddress();

        user=RealmManager.getAllStoredContacts().equalTo("macAddress",macAddress_other).findFirst();
        list=new ArrayList<>();

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
            Log.d(TAG, "onCreate: new user created"+user.toString());
        }else {
            list.addAll(RealmManager.getRealm().where(Message.class).equalTo("id",user.message_id).findAllSorted("timestamp", Sort.DESCENDING));
            Log.d(TAG, "onCreate: Existing messages"+list.toString());
        }

        cAdapter=new ChatAdapter(this,list,macAddress_my);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        rv.setLayoutManager(manager);
        rv.setAdapter(cAdapter);

    }

    public void sendMessage(View v)
    {
        list.add(0,new Message
                (Utils.getText(message),
                        macAddress_my,
                        System.currentTimeMillis(),
                        user.message_id)
        );
        cAdapter.notifyItemInserted(0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.action_accept)
        {
            thread=new AcceptThread();
        }

        if (item.getItemId()==R.id.action_connect)
        {
            thread=new ConnectThread(device_other);
        }

        thread.start();
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onSocketReceived(SocketEvent socketEvent)
    {
        Log.d(TAG, "onSocketReceived: socket received"+socketEvent.socket.toString());
    }

    @Override
    protected void onPause() {

        if (FIRST_USER && list.size()>0)
        {
            RealmManager.saveData(user);
        }
        RealmManager.saveData(list);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        thread.interrupt();
        super.onDestroy();
    }
    /*
    class ConnectThread extends AsyncTask<BluetoothDevice,Void,BluetoothSocket>
    {

        private static final String TAG = "ConnectThread";
        private  BluetoothSocket mmSocket;
        private  BluetoothDevice mmDevice;


        @Override
        protected void onCancelled() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
            super.onCancelled();

        }

        @Override
        protected BluetoothSocket doInBackground(BluetoothDevice... params) {
            try {
                mmSocket = params[0].createRfcommSocketToServiceRecord(Constants.uuid);
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
            }
            return mmSocket;
        }


        @Override
        protected void onPostExecute(BluetoothSocket bluetoothSocket) {
            super.onPostExecute(bluetoothSocket);
        }

    }

    class AcceptThread extends AsyncTask<BluetoothDevice,Void,BluetoothSocket>{
        private BluetoothServerSocket mmServerSocket;

        @Override
        protected void onCancelled() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }

            super.onCancelled();
        }

        @Override
        protected BluetoothSocket doInBackground(BluetoothDevice... params) {
            try {
                mmServerSocket=BluetoothAdapter.getDefaultAdapter()
                        .listenUsingRfcommWithServiceRecord(Constants.app_name,Constants.uuid);
            } catch (IOException e) {
                e.printStackTrace();
            }

            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    try {
                        mmServerSocket.close();
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    break;
                }
            }

            return socket;
        }

        @Override
        protected void onPostExecute(BluetoothSocket bluetoothSocket) {
            super.onPostExecute(bluetoothSocket);
        }
    }*/
}
