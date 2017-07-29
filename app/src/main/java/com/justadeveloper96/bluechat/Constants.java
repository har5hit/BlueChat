package com.justadeveloper96.bluechat;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.util.UUID;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by sankalp on 20/7/17.
 */

public class Constants {


    public static final int FIND_NEW = 851;
    public static final int FIND_STORED = 310;

    public static final UUID uuid=UUID.fromString("8df45a3f-31dc-4322-8dd6-42247842756a");
    public static final String app_name="BlueChat";
    public static final String POSITION="position";
    public static final String MAC_ADDRESS="mac_address";
    public static final String NAME="name";


    public static final int STATUS_CONNECTING=0;
    public static final int STATUS_CONNECTED=1;
    public static final int STATUS_CONNECTING_FAILED=2;
    public static final int STATUS_LISTENING=3;
    public static final int STATUS_LISTENING_FAILED=4;
    public static final int STATUS_DISCONNECTED=5;
    public static final int STATUS_TYPING=6;

    public static final String TYPING=".,^&4";

    @Retention(SOURCE)
    @IntDef({STATUS_CONNECTING,STATUS_CONNECTED,STATUS_CONNECTING_FAILED,STATUS_LISTENING,STATUS_LISTENING_FAILED,STATUS_DISCONNECTED,STATUS_TYPING})
    public @interface ChatStatusConstants{}

    public static String[] ERROR_MSG={"CONNECTING...","CONNECTED","CONNECTION FAILED","LISTENING","LISTENING FAILED","DISCONNECTED","Typing..."};

}
