package model;

import com.justadeveloper96.bluechat.Constants;

/**
 * Created by harshith on 28/7/17.
 */

public class ChatStatusEvent {

    public int status;

    public ChatStatusEvent(@Constants.ChatStatusConstants int status) {
        this.status = status;
    }
}
