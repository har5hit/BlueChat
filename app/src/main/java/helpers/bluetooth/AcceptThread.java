package helpers.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.justadeveloper96.bluechat.Constants;

import java.io.IOException;

/**
 * Created by harshith on 24/7/17.
 */

public class AcceptThread extends Thread {
    private final BluetoothServerSocket mmServerSocket;
    private static final String TAG = "AcceptThread";
    public AcceptThread() {
        // Use a temporary object that is later assigned to mmServerSocket
        // because mmServerSocket is final.
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code.
            tmp = BluetoothAdapter.getDefaultAdapter()
                    .listenUsingRfcommWithServiceRecord(Constants.app_name,Constants.uuid);
        } catch (IOException e) {
            Log.e(TAG, "Socket's listen() method failed", e);
        }
        mmServerSocket = tmp;
    }

    public void run() {
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
                manageMyConnectedSocket(socket);
                try {
                    mmServerSocket.close();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void manageMyConnectedSocket(BluetoothSocket socket) {

    }

    // Closes the connect socket and causes the thread to finish.
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }
}