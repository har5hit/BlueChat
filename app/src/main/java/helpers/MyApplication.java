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
import com.justadeveloper96.bluechat.MainActivity;
import com.justadeveloper96.bluechat.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Random;

import helpers.bluetooth.BlueHelper;
import helpers.bluetooth.BluetoothService;
import io.realm.Realm;
import model.ChatStatusEvent;
import model.MessageEvent;
import model.User;

import static helpers.RealmHelper.realm;

/**
 * Created by harshit on 16-07-2017.
 */

public class MyApplication extends Application {

    public static Context INSTANCE;

    public static BluetoothService BLUETOOTHSERVICE;

    public static String currentWindow;

    private static String macAdress_my;

    private static User user;
    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE=getApplicationContext();
        currentWindow="";
        macAdress_my=BlueHelper.getBluetoothAdapter().getAddress();
        EventBus.getDefault().register(this);
    }


    public static BluetoothService getBLUETOOTHSERVICE() {
        return BLUETOOTHSERVICE;
    }


    public static BluetoothService getBLUETOOTHSERVICE(BluetoothSocket socket, String macAddress,String name_other)
    {
        if (BLUETOOTHSERVICE==null)
        {
            BLUETOOTHSERVICE=new BluetoothService(socket,macAddress,macAdress_my,name_other);
            user= Realm.getDefaultInstance().where(User.class).equalTo("macAddress",macAddress).findFirst();
            return BLUETOOTHSERVICE;
        }

        if (BLUETOOTHSERVICE.macAddress.equals(macAddress))
        {
            Utils.log("bservice already exists, giving back");
            return BLUETOOTHSERVICE;
        }else {
            closeBluetoothService();
            return getBLUETOOTHSERVICE(socket,macAddress,name_other);
        }
    }

    public static Context getINSTANCE() {
        return INSTANCE;
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void storeMessage(MessageEvent me) {

        Utils.log("storing in application"+me.toString());
        String data;
        if (me.message.what== BluetoothService.MessageConstants.MESSAGE_READ)
        {
            data = new String(((byte[]) me.message.obj),0,me.message.arg1);
        }else
        {
            data = new String((byte[]) me.message.obj);
        }

        Number n;
        final int val;
        if (user==null)
        {
            RealmManager.getRealm().beginTransaction();

            n=realm.where(User.class).max("message_id");
            if (n==null)
            {
                val=1;
            }else
            {
                val=n.intValue()+1;
            }
            user=new User(me.user_name,me.macAddress_other,val);
            user.last_message=data;
            user.last_msg_time=System.currentTimeMillis();
            realm.insertOrUpdate(user);
            Utils.log("user created");
            RealmManager.getRealm().commitTransaction();

        }
        RealmManager.getRealm().beginTransaction();
        user.last_message=data;
        user.last_msg_time=System.currentTimeMillis();
        RealmManager.getRealm().commitTransaction();

        RealmManager.saveData(new model.Message(data,me.macAddress,System.currentTimeMillis(),user.message_id));
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

        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_action_send)
                        .setContentTitle(user_name)
                        .setContentText(data);
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
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// mNotificationId is a unique integer your app uses to identify the
// notification. For example, to cancel the notification, you can pass its ID
// number to NotificationManager.cancel().
        mNotificationManager.notify(new Random().nextInt(),mBuilder.build());
    }


    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onStatusEvent(ChatStatusEvent statusEvent)
    {
        if (statusEvent.status==Constants.STATUS_DISCONNECTED)
        {
            closeBluetoothService();
        }
    }

    public static void closeBluetoothService()
    {

        if (BLUETOOTHSERVICE==null)
        {
            return;
        }
        try {
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
