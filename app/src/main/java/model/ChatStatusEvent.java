package model;

import com.justadeveloper96.bluechat.Constants;

/**
 * Created by Harshith on 28/7/17.
 */

public class ChatStatusEvent {

    @Constants.ChatStatusConstants public int status;
    public String macAddress="";


    public ChatStatusEvent(int status) {
        this.status = status;
    }

    public ChatStatusEvent(@Constants.ChatStatusConstants int status, String macAddress) {
        this.status = status;
        this.macAddress = macAddress;

    }
}
