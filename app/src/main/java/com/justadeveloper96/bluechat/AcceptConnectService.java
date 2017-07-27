package com.justadeveloper96.bluechat;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

/**
 * Created by harshith on 27/7/17.
 */

public class AcceptConnectService extends Service{
    private  BluetoothSocket mmSocket;
    private  BluetoothDevice mmDevice;
    private  BluetoothServerSocket mmServerSocket;
    private static final String TAG = "AcceptConnectService";



    AcceptConnectService()
    {

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

     Thread acceptThread=new Thread(new Runnable() {

         private  BluetoothServerSocket mmServerSocket;

         @Override
         public void run() {
             try {
                 // MY_UUID is the app's UUID string, also used by the client code.
                 mmServerSocket = BluetoothAdapter.getDefaultAdapter()
                         .listenUsingRfcommWithServiceRecord(Constants.app_name,Constants.uuid);
             } catch (IOException e) {
                 Log.e(TAG, "Socket's listen() method failed", e);
             }




         }
     });
     Thread connectThread;


    public void startAcceptThread(){

    }


}
