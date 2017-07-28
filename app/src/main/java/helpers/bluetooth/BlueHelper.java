package helpers.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

import java.util.Set;

/**
 * Created by harshit on 16-07-2017.
 */

public class BlueHelper {


    public static final int REQUEST_ENABLE_BT = 345;

    public static long counter=0;

    public static boolean isInChat=false;

    BlueHelper() {
    }

    public static BluetoothAdapter getBluetoothAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }


    public static void init(Activity ctx){
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ctx.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public static void setDiscoverable(Context ctx){
        if (isInChat)
        {
            return;
        }

        if (counter==0 || ((System.currentTimeMillis()/1000)-counter)>300) {
            Intent discoverableIntent =
                    new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            ctx.startActivity(discoverableIntent);
            counter=System.currentTimeMillis()/1000;
        }
    }

    public static Set<BluetoothDevice> getAllPairedDevices()
    {
         return getBluetoothAdapter().getBondedDevices();
    }

    public static void startDiscovery(){
        getBluetoothAdapter().startDiscovery();
    }

}
