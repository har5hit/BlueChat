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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import helpers.PermissionHelper;
import helpers.Utils;
import helpers.bluetooth.BlueHelper;

import static helpers.Utils.getContext;

public class SearchActivity extends BlueActivity implements Runnable, ItemClickListener, PermissionHelper.PermissionsListener {
    //private ContactsListFragment fragment;
    private Button scan;
    private RecyclerView recyclerView;

    private SearchAdapter sAdapter;
    private List<BluetoothDevice> list;

    private PermissionHelper permissionHelper;

    private static final String TAG = "SearchActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        scan= (Button) findViewById(R.id.btn_scan);
        recyclerView= (RecyclerView) findViewById(R.id.recycler_view);
        list=new ArrayList<>();

        sAdapter=new SearchAdapter(list,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(sAdapter);

        //fragment=ContactsListFragment.newInstance(Constants.FIND_NEW);

        Log.d(TAG, "onCreate: fragment created");


        permissionHelper=new PermissionHelper(this).setListener(this);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        //setFragment(fragment);

        Log.d(TAG, "onCreate: fragment set");


        registerReceiver(mReceiver, filter);

//        searchForPairedDevices();



    }




    private void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
        transaction.replace(R.id.fl_container,fragment );
        transaction.addToBackStack(null);
// Commit the transaction
        transaction.commit();

        Log.d(TAG, "setFragment: fragment transaction done");
    }

    public void searchForPairedDevices() {

        //RealmManager.deleteAll(RealmManager.getAllPairedDevices());
        Set<BluetoothDevice> devices= BlueHelper.getAllPairedDevices();
        list.addAll(devices);
        /*for(BluetoothDevice device:devices)
        {

            Log.d(TAG, "searchForPairedDevices: data added in list");
            //fragment.getList().add(new User(device.getName(),device.getAddress()));
            //    RealmManager.saveData(new User(device.getName(),device.getAddress()));
            list.add(device);
        }*/
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
                list.add(device);
                sAdapter.notifyItemInserted(list.size()-1);

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
        /*fragment.getList().clear();
        searchForPairedDevices();
        BlueHelper.startDiscovery();
        scan.setEnabled(false);
        unlockScan();*/

        permissionHelper.requestPermission(
                new String[]{
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.BLUETOOTH},200);

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
                .putExtra(Constants.MAC_ADDRESS,list.get(position).getAddress())
                .putExtra(Constants.NAME,list.get(position).getName())
        );
        finish();
    }

    @Override
    public void onPermissionGranted(int request_code) {
        Utils.log("permission granted");
        list.clear();
        searchForPairedDevices();
        BlueHelper.startDiscovery();
        scan.setEnabled(false);
        unlockScan();

    }

    @Override
    public void onPermissionRejectedManyTimes(@NonNull List<String> rejectedPerms, int request_code) {

    }
}
