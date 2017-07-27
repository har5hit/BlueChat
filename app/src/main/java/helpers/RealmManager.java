package helpers;

import java.util.Collection;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import model.User;

/**
 * Created by sankalp on 20/7/17.
 */

public class RealmManager {



    public static Realm getRealm() {
        return RealmHelper.getRealm(MyApplication.getINSTANCE());
    }

    public static RealmQuery<User> getAllStoredContacts()
    {
        return getRealm().where(User.class);
    }

    public static RealmQuery<User> getAllPairedDevices()
    {
        return getRealm().where(User.class).equalTo("message_id",0);
    }

    public static <T extends RealmObject> void saveData(T data)
    {
        getRealm().beginTransaction();
        getRealm().insertOrUpdate(data);
        getRealm().commitTransaction();
    }

    public static <T extends RealmObject> void saveData(Collection<T> data)
    {
        getRealm().beginTransaction();
        getRealm().insertOrUpdate(data);
        getRealm().commitTransaction();
    }

    public static <E> void deleteAll(RealmQuery query)
    {
        getRealm().beginTransaction();
        query.findAll().deleteAllFromRealm();
        getRealm().commitTransaction();
    }
}
