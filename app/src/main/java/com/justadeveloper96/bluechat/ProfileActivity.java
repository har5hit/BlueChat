package com.justadeveloper96.bluechat;

import android.app.Activity;
import android.database.DatabaseUtils;
import android.databinding.DataBindingUtil;
import android.support.v4.database.DatabaseUtilsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.justadeveloper96.bluechat.databinding.ActivityProfileBinding;

import helpers.SharedPrefs;
import helpers.Utils;

public class ProfileActivity extends BlueActivity {


    private ActivityProfileBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_profile);
        binding.tvUserName.setText(SharedPrefs.getPrefs().getString(SharedPrefs.USER_NAME));
    }

    public void saveProfile(View v)
    {
        SharedPrefs.getPrefs().save(SharedPrefs.USER_NAME, Utils.getText(binding.tvUserName));
        finish();
    }
}
