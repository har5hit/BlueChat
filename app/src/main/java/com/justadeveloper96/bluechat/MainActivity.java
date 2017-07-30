package com.justadeveloper96.bluechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import helpers.RealmManager;
import helpers.bluetooth.CleanUpService;
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

        list=new ArrayList<>();
        cAdapter=new ContactsAdapter(this,list,this);

        recyclerView= (RecyclerView) findViewById(R.id.recycler_view);

        Log.d(TAG, "setUpList: cadapter");

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
    }

   /* private void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
        transaction.replace(R.id.fl_container,fragment );
        transaction.addToBackStack(null);

// Commit the transaction
        transaction.commit();
    }*/

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        startActivity(new Intent(getContext(),ChatActivity.class)
                .putExtra(Constants.MAC_ADDRESS,list.get(position).macAddress)
                .putExtra(Constants.NAME,list.get(position).name)
        );
    }
}
