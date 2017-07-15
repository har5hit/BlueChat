package helpers;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

import java.util.Set;

/**
 * Created by harshit on 16-07-2017.
 */

public class BlueToothManager {


    private static final int REQUEST_ENABLE_BT = 345;
    public static BluetoothAdapter mBluetoothAdapter;

    BlueToothManager() {
    }

    public static BluetoothAdapter getmBluetoothAdapter(Activity ctx) {
        if (mBluetoothAdapter==null)
        {
            mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ctx.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        return mBluetoothAdapter;
    }


    public static void setDiscoverable(Context ctx){
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        ctx.startActivity(discoverableIntent);
    }

    public static Set<BluetoothDevice> getAllPairedDevices(Activity ctx)
    {
         return getmBluetoothAdapter(ctx).getBondedDevices();
    }
}
