package helpers.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.util.Log;

import com.justadeveloper96.bluechat.Constants;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import helpers.Utils;
import model.ChatStatusEvent;
import model.MessageEvent;

/**
 * Created by harshith on 24/7/17.
 */


public class BluetoothService {
    private static final String TAG = "MY_APP_DEBUG_TAG";
    ConnectedThread connectedThread;
    public final String macAddress;
    public final String macAddress_my;
    private String name_other;

    // handler that gets info from Bluetooth service

    // Defines several constants used when transmitting messages between the
    // service and the UI.
    public interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        // ... (Add other message types here as needed.)
    }


    public BluetoothService(BluetoothSocket socket, String macAddress,String macAddress_my,String name_other) {
        this.macAddress=macAddress;
        this.macAddress_my=macAddress_my;
        this.name_other=name_other;
        close();
        connectedThread=new ConnectedThread(socket);
        connectedThread.start();
    }


    public void write(String msg)
    {
        connectedThread.write(msg);
    }

    public void close(){
        if (connectedThread==null)
        {
            return;
        }
        try {
            connectedThread.cancel();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer;// mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {

            Utils.log("new connected thread with new socket");
            mmSocket = socket;
            boolean close=false;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                EventBus.getDefault().postSticky(new ChatStatusEvent(Constants.STATUS_DISCONNECTED));
                Log.e(TAG, "Error occurred when creating input stream", e);
                close=true;
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                EventBus.getDefault().postSticky(new ChatStatusEvent(Constants.STATUS_DISCONNECTED));
                Log.e(TAG, "Error occurred when creating output stream", e);
                close=true;
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;

            if (close)
            {
                cancel();
            }
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

                    /*Message readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1,
                            mmBuffer);*/

                    Message readMsg = new Message();
                    readMsg.what=MessageConstants.MESSAGE_READ;
                    readMsg.arg1=numBytes;
                    readMsg.obj=mmBuffer;


                    EventBus.getDefault().post(new MessageEvent(readMsg,name_other,macAddress,macAddress));


                    //storeMessage(readMsg);
                    Log.d(TAG, "run: readMsg is"+ new String(mmBuffer,0,numBytes));

                    //mmBuffer=new byte[1024];

                    //readMsg.sendToTarget();
                } catch (IOException e) {

                    EventBus.getDefault().postSticky(new ChatStatusEvent(Constants.STATUS_DISCONNECTED));
                    cancel();
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

/*
        private void storeMessage(android.os.Message msg) {

            String data;

            String who;
            if (msg.what== BluetoothService.MessageConstants.MESSAGE_READ)
            {
                data = new String(((byte[]) msg.obj),0,msg.arg1);
                who=macAddress;
            }else
            {
                data = new String((byte[]) msg.obj);
                who=macAddress_my;
            }

            Number n;
            int val;
            Realm.getDefaultInstance().beginTransaction();

            if (user==null)
            {
                n=realm.where(User.class).max("message_id");
                if (n==null)
                {
                    val=1;
                }else
                {
                    val=n.intValue()+1;
                }
                user=new User(name_other,macAddress,val);
            }
            user.last_message=data;
            Realm.getDefaultInstance().commitTransaction();
            RealmManager.saveData(new model.Message(data,who,System.currentTimeMillis(),user.message_id));
        }
*/

        // Call this from the main activity to send data to the remote device.
        public void write(String msg) {
            try {
                mmOutStream.write(msg.getBytes());
                // Share the sent message with the UI activity.
                /*Message writtenMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1,msg.getBytes());*/
                //writtenMsg.sendToTarget();
                Message writtenMsg = new Message();
                writtenMsg.what=MessageConstants.MESSAGE_WRITE;
                writtenMsg.arg1=-1;
                writtenMsg.obj=msg.getBytes();

                EventBus.getDefault().post(new MessageEvent(writtenMsg,name_other,macAddress_my, macAddress));
                //storeMessage(writtenMsg);
                Log.d(TAG, "write: writenmsg is"+writtenMsg.toString());
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);
                EventBus.getDefault().postSticky(new ChatStatusEvent(Constants.STATUS_DISCONNECTED));
                // Send a failure message back to the activity.
//                Message writeErrorMsg =
//                        mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
//                Bundle bundle = new Bundle();
//                bundle.putString("toast",
//                        "Couldn't send data to the other device");
//                writeErrorMsg.setData(bundle);
                //writeErrorMsg.sendToTarget();
                //mHandler.sendMessage(writeErrorMsg);
                cancel();
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            EventBus.getDefault().postSticky(new ChatStatusEvent(Constants.STATUS_DISCONNECTED));
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }

        @Override
        public void interrupt() {
            super.interrupt();
            cancel();
        }
    }
}