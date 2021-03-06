package helpers;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import com.justadeveloper96.bluechat.ChatActivity;
import com.justadeveloper96.bluechat.Constants;
import com.justadeveloper96.bluechat.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import helpers.bluetooth.BlueHelper;
import helpers.bluetooth.BluetoothService;
import io.realm.Realm;
import model.ChatStatusEvent;
import model.MessageEvent;
import model.User;

/**
 * Created by Harshith on 16-07-2017.
 */

public class MyApplication extends Application {

    public static Context INSTANCE;

    public static BluetoothService BLUETOOTHSERVICE;

    public static String currentWindow;

    private static String macAdress_my;

    private static User user;

    private static Map<String,String> addressName;
    public static Map<String,Integer> notify_id;
    public static Map<String,Integer> notify_count;
    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE=getApplicationContext();
        currentWindow="";
        macAdress_my=BlueHelper.getBluetoothAdapter().getAddress();
        addressName=new HashMap<>();
        notify_id=new HashMap<>();
        notify_count=new HashMap<>();
        EventBus.getDefault().register(this);
    }


    public static BluetoothService getBLUETOOTHSERVICE() {
        return BLUETOOTHSERVICE;
    }


    public static BluetoothService getBLUETOOTHSERVICE(BluetoothSocket socket, String macAddress,String name_other)
    {

        if (BLUETOOTHSERVICE==null)
        {
            Utils.log("Creating new Bluetooth Service");
            BLUETOOTHSERVICE=new BluetoothService(socket,macAddress,macAdress_my,name_other);
            addressName.put(macAddress,name_other);
            if (!notify_id.containsKey(macAddress))
            {
                notify_id.put(macAddress,new Random().nextInt());
            }
            user= Realm.getDefaultInstance().where(User.class).equalTo("macAddress",macAddress).findFirst();
            return BLUETOOTHSERVICE;
        }

        if (BLUETOOTHSERVICE.macAddress.equals(macAddress))
        {
            Utils.log("bservice already exists, giving back");
            return BLUETOOTHSERVICE;
        }else {
            closeBluetoothService(BLUETOOTHSERVICE.macAddress);
            return getBLUETOOTHSERVICE(socket,macAddress,name_other);
        }
    }

    public static Context getINSTANCE() {
        return INSTANCE;
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void storeMessage(MessageEvent me) {
        long current_time=System.currentTimeMillis();
        Utils.log("storing in application"+me.toString());
        String data;
        if (me.message.what== BluetoothService.MessageConstants.MESSAGE_READ)
        {
            data = new String(((byte[]) me.message.obj),0,me.message.arg1);

        }else
        {
            data = new String((byte[]) me.message.obj);
        }

        if (user==null)
        {
            Number n;
            final int m_id;

            RealmManager.getRealm().beginTransaction();

            n=RealmManager.getRealm().where(User.class).max("message_id");
            if (n==null)
            {
                m_id=1;
            }else
            {
                m_id=n.intValue()+1;
            }
            user=new User(me.user_name,me.macAddress_other,m_id);
            user=RealmManager.getRealm().copyToRealm(user);
            Utils.log("user created");
            RealmManager.getRealm().commitTransaction();
        }

        RealmManager.getRealm().beginTransaction();
        user.last_message = data;
        user.last_msg_time = current_time;
        RealmManager.getRealm().commitTransaction();

        RealmManager.saveData(new model.Message(data,me.macAddress,current_time,user.message_id));
        if (me.message.what== BluetoothService.MessageConstants.MESSAGE_READ)
        {
            showNotification(me.macAddress_other,me.user_name,data);
        }
    }



    public void showNotification(String macAddress, String user_name, String data)
    {
        if (currentWindow.equals(macAddress))
        {
            return;
        }


        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int count=0;
        try {
            count=notify_count.get(macAddress);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(user_name)
                        .setContentText(data)
                        .setNumber(++count)
                        .setAutoCancel(true);

        notify_count.put(macAddress,count);
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ChatActivity.class);
        resultIntent.putExtra(Constants.NAME,user_name);
        resultIntent.putExtra(Constants.MAC_ADDRESS,macAddress);
// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ChatActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

// mNotificationId is a unique integer your app uses to identify the
// notification. For example, to cancel the notification, you can pass its ID
// number to NotificationManager.cancel().
        int id=notify_id.get(macAddress);
        mNotificationManager.notify(id,mBuilder.build());
    }


    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onStatusEvent(ChatStatusEvent statusEvent)
    {
        if (statusEvent.status==Constants.STATUS_DISCONNECTED)
        {
            closeBluetoothService(statusEvent.macAddress);
        }
    }

    public static void closeBluetoothService(String macAddress)
    {
        if (BLUETOOTHSERVICE==null)
        {
            return;
        }


        try {
            if (!BLUETOOTHSERVICE.macAddress.equals(macAddress))
            {
                return;
            }

            String s=macAddress;
            if (s.isEmpty())
            {
                s=BLUETOOTHSERVICE.macAddress;
            }
            Utils.showToast(getINSTANCE().getApplicationContext(),addressName.get(s)+" "+Constants.ERROR_MSG[Constants.STATUS_DISCONNECTED]);
            Utils.log("application bservice close");
            BLUETOOTHSERVICE.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        BLUETOOTHSERVICE=null;
        Utils.log("application bservice null");
    }
}
