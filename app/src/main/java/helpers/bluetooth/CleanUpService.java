package helpers.bluetooth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import helpers.Utils;

import static helpers.MyApplication.BLUETOOTHSERVICE;

/**
 * Created by Harshith on 27/7/17.
 */
public class CleanUpService extends Service {
    public CleanUpService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        try {
            BLUETOOTHSERVICE.close();
            Utils.showToast(getApplicationContext(),"BlueChat Disconnected");
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        Utils.log("app killed");
        stopSelf();
        super.onTaskRemoved(rootIntent);

    }
}
