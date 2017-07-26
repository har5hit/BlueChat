package com.justadeveloper96.bluechat;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.UUID;

import helpers.SharedPrefs;
import helpers.Utils;

public class ProfileActivity extends BlueActivity {



    private EditText tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        tvUserName= (EditText) findViewById(R.id.tv_user_name);
        tvUserName.setText(SharedPrefs.getPrefs().getString(SharedPrefs.USER_NAME));
    }

    public void saveProfile(View v)
    {
        if (Utils.getText(tvUserName).isEmpty()) {
            SharedPrefs.getPrefs().save(SharedPrefs.USER_NAME, Utils.getText(tvUserName));
        }

        if (SharedPrefs.getPrefs().getString(SharedPrefs.UUID).isEmpty())
        {
            SharedPrefs.getPrefs().save(SharedPrefs.UUID, UUID.randomUUID().toString());
        }
        finish();
    }
}
