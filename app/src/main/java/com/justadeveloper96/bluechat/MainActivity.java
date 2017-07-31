package com.justadeveloper96.bluechat;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import helpers.RealmManager;
import helpers.bluetooth.CleanUpService;
import model.MessageEvent;
import model.User;

import static helpers.Utils.getContext;

/**
 * Created by Harshith on 20/7/17.
 */


public class MainActivity extends BlueActivity implements ItemClickListener {


    private RecyclerView recyclerView;
    private ContactsAdapter cAdapter;

    public List<User> list;

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {

        startService(new Intent(this, CleanUpService.class));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SearchActivity.class));
            }
        });


        recyclerView= (RecyclerView) findViewById(R.id.recycler_view);

        Log.d(TAG, "setUpList: cadapter");

        list=new ArrayList<>();
        list.addAll(RealmManager.getAllStoredContacts().findAll());

        cAdapter=new ContactsAdapter(this,list,this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
        list.addAll(RealmManager.getAllStoredContacts().findAll());
        cAdapter.notifyDataSetChanged();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void storeMessage(MessageEvent me) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).macAddress.equals(me.macAddress_other))
            {
                cAdapter.notifyItemChanged(i);
            }
        }
    }

    private void openProfile() {
        startActivity(new Intent(this,ProfileActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            openProfile();
        }else if(id==R.id.action_share)
        {
            final PackageManager pm = getPackageManager();
//get a list of installed apps.
            PackageInfo packages = null;
            try {
                packages = pm.getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                File file = new File(packages.applicationInfo.sourceDir);
                sharingIntent.setType("text/plain");
                sharingIntent.setPackage("com.android.bluetooth");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                startActivity(Intent.createChooser(sharingIntent, "Share App"));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        }
        return true;
    }

    @Override
    public void onItemClick(int position) {
        startActivity(new Intent(getContext(),ChatActivity.class)
                .putExtra(Constants.MAC_ADDRESS,list.get(position).macAddress)
                .putExtra(Constants.NAME,list.get(position).name)
        );
    }
}
