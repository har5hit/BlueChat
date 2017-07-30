package com.justadeveloper96.bluechat;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import helpers.PermissionHelper;
import helpers.Utils;
import helpers.bluetooth.BlueHelper;

import static helpers.Utils.getContext;

/**
 * Created by Harshith on 27/7/17.
 */

public class SearchActivity extends BlueActivity implements Runnable, ItemClickListener, PermissionHelper.PermissionsListener {
    //private ContactsListFragment fragment;
    private Button scan;
    private RecyclerView recyclerView;

    private SearchAdapter sAdapter;
    private List<BluetoothDevice> devices;
    private List<Boolean> states;

    private PermissionHelper permissionHelper;

    private static final String TAG = "SearchActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        scan= (Button) findViewById(R.id.btn_scan);
        recyclerView= (RecyclerView) findViewById(R.id.recycler_view);
        devices =new LinkedList<>();
        states =new LinkedList<>();

        sAdapter=new SearchAdapter(devices,states,this,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(sAdapter);

        permissionHelper=new PermissionHelper(this).setListener(this);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        registerReceiver(mReceiver, filter);

        startScanner();

        permissionHelper.requestPermission(
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},200);
    }


    public void searchForPairedDevices() {

        Set<BluetoothDevice> devices= BlueHelper.getAllPairedDevices();
        this.devices.addAll(devices);
        for (int i = 0; i < devices.size(); i++) {
            this.states.add(false);
        }
        sAdapter.notifyDataSetChanged();
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // RealmManager.saveData(new User(device.getName(),device.getAddress()));
               /* fragment.getList().add(new User(device.getName(),device.getAddress()));
                fragment.getAdapter().notifyItemInserted(fragment.getList().size());*/

               if (!devices.contains(device)) {
                   devices.add(device);
                   states.add(true);
                   sAdapter.notifyItemInserted(devices.size() - 1);
               }else {
                   int index=devices.indexOf(device);
                   states.set(index,true);
                   sAdapter.notifyItemChanged(index);

               }

                Utils.log("found devices",device.getName()+" / "+device.getAddress());
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        permissionHelper.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    public void scanNew(View v)
    {
        permissionHelper.requestPermission(
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},200);
    }


    public void unlockScan()
    {
        new Handler().postDelayed(this,14000);
    }

    @Override
    public void run() {
        scan.setEnabled(true);
    }

    @Override
    public void onItemClick(int position) {
        //  RealmManager.getRealm().where(BluetoothDeviceWrapper.class).findAll().deleteAllFromRealm();

        BlueHelper.getBluetoothAdapter().cancelDiscovery();

        startActivity(new Intent(this,ChatActivity.class)
                .putExtra(Constants.MAC_ADDRESS, devices.get(position).getAddress())
                .putExtra(Constants.NAME, devices.get(position).getName())
        );
        finish();
    }

    @Override
    public void onPermissionGranted(int request_code) {
        Utils.log("permission granted");
        startScanner();
    }

    private void startScanner() {
        devices.clear();
        states.clear();
        searchForPairedDevices();
        BlueHelper.setDiscoverable(this);
        BlueHelper.startDiscovery();
        scan.setEnabled(false);
        unlockScan();
    }

    @Override
    public void onPermissionRejectedManyTimes(@NonNull List<String> rejectedPerms, int request_code) {
            finish();
    }
}
