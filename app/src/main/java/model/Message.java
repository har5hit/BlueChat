package model;

import io.realm.RealmObject;

/**
 * Created by harshith on 26/7/17.
 */

public class Message extends RealmObject {
    public Message(String message, int user_id, long timestamp) {
        this.message = message;
        this.user_id = user_id;
        this.timestamp = timestamp;
    }

    public Message() {
    }

    public String message;

    public int user_id;

    public long timestamp;
}
