package helpers.bluetooth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.justadeveloper96.bluechat.Constants;

import org.greenrobot.eventbus.EventBus;

import helpers.Utils;
import model.ChatStatusEvent;

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
        EventBus.getDefault().postSticky(new ChatStatusEvent(Constants.STATUS_DISCONNECTED));
        Utils.log("app killed");
        stopSelf();
        super.onTaskRemoved(rootIntent);

    }
}
