package com.justadeveloper96.bluechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import helpers.MyApplication;
import helpers.RealmManager;
import helpers.bluetooth.CleanUpService;
import io.realm.Realm;
import model.Message;
import model.MessageEvent;
import model.User;

import static helpers.Utils.getContext;

/**
 * Created by Harshith on 20/7/17.
 */


public class MainActivity extends AppCompatActivity implements ItemClickListener, View.OnClickListener {


    private RecyclerView recyclerView;
    private ContactsAdapter cAdapter;

    public List<User> list;
    FloatingActionButton fab;

    private LinearLayout ll_empty;

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {


        startService(new Intent(this, CleanUpService.class));

        getSupportActionBar().setTitle("Chats");
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        ll_empty= (LinearLayout) findViewById(R.id.ll_empty);

        recyclerView= (RecyclerView) findViewById(R.id.recycler_view);

        Log.d(TAG, "setUpList: cadapter");

        list=new ArrayList<>();
        list.addAll(RealmManager.getAllStoredContacts().findAll());

        cAdapter=new ContactsAdapter(this,list,this);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
        list.addAll(RealmManager.getAllStoredContacts().findAll());
        if (list.size()>0)
        {
            ll_empty.setVisibility(View.GONE);
        }else
        {
            ll_empty.setVisibility(View.VISIBLE);
        }

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
        }
        /*else if(id==R.id.action_share)
        {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "BlueChat");
            String sAux = "\nCheck this cool Bluetooth chat app\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=com.justadeveloper96.bluechat \n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "Share on"));
        }else if(id==R.id.action_like)
        {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.justadeveloper96.bluechat"));
            startActivity(intent);
        }*/
        return true;
    }

    @Override
    public void onItemClick(int position) {
        startActivity(new Intent(getContext(),ChatActivity.class)
                .putExtra(Constants.MAC_ADDRESS,list.get(position).macAddress)
                .putExtra(Constants.NAME,list.get(position).name)
        );
    }

    @Override
    public void onItemLongClick(final int position) {
        final PopupMenu popup = new PopupMenu(this, recyclerView.getChildAt(position));
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.user_actions, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId()==R.id.action_delete)
                {
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            final int m_id=list.get(position).message_id;
                            final String mac=list.get(position).macAddress;
                            list.remove(position);
                            cAdapter.notifyItemRemoved(position);
                            try {
                                MyApplication.closeBluetoothService(mac);
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }

                            RealmManager.getRealm().executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.where(User.class).equalTo("macAddress",mac).findFirst().deleteFromRealm();
                                    realm.where(Message.class).equalTo("id", m_id).findAll().deleteAllFromRealm();
                                }
                            });
                        }
                    });

                }
                return true;
            }
        });
        popup.show();
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(MainActivity.this,SearchActivity.class));
    }

}
