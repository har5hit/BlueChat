package model;

import android.bluetooth.BluetoothSocket;

/**
 * Created by harshith on 27/7/17.
 */

public class SocketEvent {
    public BluetoothSocket socket;
    public SocketEvent(BluetoothSocket socket) {
        this.socket=socket;
    }
}
