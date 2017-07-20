package model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by harshit on 16-07-2017.
 */

public class User extends RealmObject{
    public User() {
    }

    public User(String device_name, String macAddress) {
        this.device_name = device_name;
        this.macAddress = macAddress;
    }

    public String name;
    public String profile_pic;
    public String device_name;
    public int message_id;

    @PrimaryKey
    public String macAddress;
}
