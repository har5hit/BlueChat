package model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by harshit on 16-07-2017.
 */

public class User extends RealmObject{


    public String name;
    public String device_name;
    public int messafe_id;

    @PrimaryKey
    public String macAddress;
}
