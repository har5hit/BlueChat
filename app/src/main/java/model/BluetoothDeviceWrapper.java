package model;

import android.bluetooth.BluetoothDevice;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by harshit on 27-07-2017.
 */

public class BluetoothDeviceWrapper extends RealmObject {

    @PrimaryKey
    int id;

    public BluetoothDevice device;

    public BluetoothDeviceWrapper(int id,BluetoothDevice device) {
        this.device = device;
        this.id=id;
    }

    public BluetoothDeviceWrapper() {
    }
}
