package model;

/**
 * Created by Harshith on 24/7/17.
 */

public class MessageEvent {

    public android.os.Message message;

    public String user_name;

    public String macAddress;

    public String macAddress_other;


    public MessageEvent(android.os.Message message, String user_name, String macAddress, String macAddress_other) {
        this.message = message;
        this.user_name = user_name;
        this.macAddress = macAddress;
        this.macAddress_other=macAddress_other;
    }

    @Override
    public String toString() {
        return "MessageEvent{" +
                "message=" + message +
                ", user_name='" + user_name + '\'' +
                ", macAddress='" + macAddress + '\'' +
                '}';
    }
}
