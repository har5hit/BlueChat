package com.justadeveloper96.bluechat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import helpers.bluetooth.BlueHelper;

/**
 * Created by Harshith on 20/7/17.
 */

public class BlueActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //BlueHelper.init(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==BlueHelper.REQUEST_ENABLE_BT && resultCode==RESULT_CANCELED)
        {
            finish();
            return;
        }

        if (requestCode==BlueHelper.REQUEST_ENABLE_BT && resultCode==RESULT_OK)
        {
            BlueHelper.setDiscoverable(this);
        }
    }
}
