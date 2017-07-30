package helpers;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Harshith on 17/4/17.
 */

public class RealmHelper {

    public static Realm realm;

    public static RealmHelper realmInstance;

    RealmHelper(Context ctx)
    {
        Realm.init(ctx);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
    }


    public static RealmHelper getRealmInstance(Context ctx)
    {
        if (realmInstance==null)
        {
            realmInstance=new RealmHelper(ctx);
        }
        return realmInstance;
    }

    public static Realm getRealm(Context ctx) {
        if (realmInstance==null)
        {
            realmInstance=new RealmHelper(ctx);
        }
        return realm;
    }



}
