package helpers;

import android.app.Application;
import android.content.Context;

import java.util.Random;

/**
 * Created by harshit on 16-07-2017.
 */

public class MyApplication extends Application {


    public static Context INSTANCE;
    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE=getApplicationContext();

        if (SharedPrefs.getPrefs().getInt(SharedPrefs.USER_ID)==0)
        {
            SharedPrefs.getPrefs().save(SharedPrefs.USER_ID,new Random().nextInt());
        }
    }


    public static Context getINSTANCE() {
        return INSTANCE;
    }
}
