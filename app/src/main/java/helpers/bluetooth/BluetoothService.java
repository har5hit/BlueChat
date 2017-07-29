package helpers.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.justadeveloper96.bluechat.Constants;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import helpers.Utils;
import model.ChatStatusEvent;

/**
 * Created by harshith on 24/7/17.
 */

public class BluetoothService {
    private static final String TAG = "MY_APP_DEBUG_TAG";
    private Handler mHandler;
    ConnectedThread connectedThread;
    private String latest_macAdress="";
    private static BluetoothService INSTANCE;
    // handler that gets info from Bluetooth service

    // Defines several constants used when transmitting messages between the
    // service and the UI.
    public interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        // ... (Add other message types here as needed.)
    }


    public BluetoothService(BluetoothSocket socket,Handler mHandler,String macAddress) {
        this.mHandler = mHandler;
        this.latest_macAdress=macAddress;
        connectedThread=new ConnectedThread(socket);
        connectedThread.start();
    }

    public void write(String msg)
    {
        connectedThread.write(msg);
    }

    public void close(){
        connectedThread.cancel();
        mHandler=null;
        INSTANCE=null;
    }

    public BluetoothService getInstance(BluetoothSocket socket,Handler mHandler,String macAddress)
    {
        if (INSTANCE==null)
        {
            INSTANCE=new BluetoothService(socket,mHandler,macAddress);
            return INSTANCE;
        }

        if (latest_macAdress.equals(macAddress))
        {
            return INSTANCE;
        }else {
            INSTANCE.close();
            return getInstance(socket,mHandler,macAddress);
        }
    }

    private class ConnectedThread extends Thread implements IConnectionThread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.

                    Utils.log("reading from buffer send to ui");

                    Message readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1,
                            mmBuffer);

                    Log.d(TAG, "run: readMsg is"+ new String(mmBuffer,0,numBytes));

                    //mmBuffer=new byte[1024];

                    readMsg.sendToTarget();
                } catch (IOException e) {

                    EventBus.getDefault().post(new ChatStatusEvent(Constants.STATUS_DISCONNECTED));
                    cancel();
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(String msg) {
            try {
                mmOutStream.write(msg.getBytes());
                // Share the sent message with the UI activity.

                Message writtenMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1,msg.getBytes());
                writtenMsg.sendToTarget();
                Log.d(TAG, "write: writenmsg is"+writtenMsg.toString());
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);
                EventBus.getDefault().post(new ChatStatusEvent(Constants.STATUS_DISCONNECTED));
                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                writeErrorMsg.sendToTarget();
                //mHandler.sendMessage(writeErrorMsg);
                cancel();
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
                interrupt();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }
}