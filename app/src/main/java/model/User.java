package model;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Harshith on 16-07-2017.
 */

public class User extends RealmObject{
    public User() {
    }

    public User(String name,String macAddress,int message_id) {
        this.name=name;
        this.macAddress = macAddress;
        this.message_id=message_id;
    }

    public String name;
    public String profile_pic;
    public String device_name;
    public int message_id;

    @Index
    @PrimaryKey
    public String macAddress;

    public String last_message;

    public long last_msg_time;
    public long last_read_time;

    public long last_seen_time;

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", profile_pic='" + profile_pic + '\'' +
                ", device_name='" + device_name + '\'' +
                ", message_id=" + message_id +
                ", macAddress='" + macAddress + '\'' +
                ", last_message='" + last_message + '\'' +
                '}';
    }
}
