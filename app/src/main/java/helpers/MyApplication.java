package helpers;

import android.app.Application;
import android.content.Context;

/**
 * Created by harshit on 16-07-2017.
 */

class MyApplication extends Application {


    public static Context INSTANCE;
    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE=getApplicationContext();
    }

    public static Context getINSTANCE() {
        return INSTANCE;
    }
}
