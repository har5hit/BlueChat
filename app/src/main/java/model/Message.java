package model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Harshith on 26/7/17.
 */

public class Message extends RealmObject {
    public Message(String message, String  user_mac, long timestamp,int id) {
        this.message = message;
        this.user_mac = user_mac;
        this.timestamp = timestamp;
        this.id=id;
    }

    public Message() {
    }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", user_mac='" + user_mac + '\'' +
                ", id=" + id +
                ", timestamp=" + timestamp +
                '}';
    }

    public String message;

    public String user_mac;

    public int id;

    @PrimaryKey
    public long timestamp;
}
