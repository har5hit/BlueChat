package com.justadeveloper96.bluechat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.transitionseverywhere.Slide;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;

import helpers.Utils;
import helpers.bluetooth.BlueHelper;


/**
 * Created by Harshith on 20/7/17.
 */

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private EditText name;
    private ImageButton edit;
    private Button save;
    private LinearLayout container_save;
    private String current_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();

        setListeners();
    }

    private void setListeners() {
        edit.setOnClickListener(this);
        name.addTextChangedListener(this);
        save.setOnClickListener(this);
    }

    private void init() {
        name= (EditText) findViewById(R.id.ed_name);
        edit= (ImageButton) findViewById(R.id.btn_edit);
        save= (Button) findViewById(R.id.btn_save);
        container_save = (LinearLayout) findViewById(R.id.ll_save);
        current_name= BlueHelper.getBluetoothAdapter().getName();
        name.setText(current_name);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_edit:
                editData();
                break;

            case R.id.btn_save:
                saveData();
                break;
        }
    }

    private void editData() {
        Utils.log("edit data");
        name.setEnabled(true);
       editMode(true);
    }

    private void editMode(boolean show)
    {
        edit.setVisibility(show?View.VISIBLE:View.GONE);
        TransitionManager.beginDelayedTransition(container_save, new TransitionSet()
                .addTransition(show?(new Slide(Gravity.LEFT)):(new Slide(Gravity.RIGHT))));
        save.setVisibility(show?View.INVISIBLE:View.VISIBLE);
    }

    private void saveData() {
        Utils.log("save data");
        current_name= Utils.getText(name);
        name.setEnabled(false);
        editMode(false);
        BlueHelper.getBluetoothAdapter().setName(current_name);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (current_name.equals(s.toString()) || s.length()==0)
        {
            save.setEnabled(false);
        }else {
            save.setEnabled(true);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
