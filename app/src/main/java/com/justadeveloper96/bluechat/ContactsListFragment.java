package com.justadeveloper96.bluechat;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import helpers.BlueHelper;
import helpers.RealmHelper;
import helpers.RealmManager;
import io.realm.RealmQuery;
import model.User;

/**
 * A placeholder fragment containing a simple view.
 */
public class ContactsListFragment extends Fragment {

    private static final String TAG = "ContactsListFragment";

    private RecyclerView recyclerView;
    private ContactsAdapter cAdapter;
    private int type;

    public List<User> list;

    public List<User> getList() {
        return list;
    }

    public ContactsAdapter getAdapter() {
        return cAdapter;
    }

    public ContactsListFragment() {
    }

    public static ContactsListFragment newInstance(int type) {

        Bundle args = new Bundle();
        ContactsListFragment fragment = new ContactsListFragment();
        args.putInt("type",type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        list=new ArrayList<>();
        cAdapter=new ContactsAdapter(getActivity(),list);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated: ");
        setUpList(view);

    }

    private void setUpList(View view) {
        recyclerView= (RecyclerView) view.findViewById(R.id.recycler_view);
        type= getArguments().getInt("type");


        RealmQuery<User> query;
        if (type==Constants.FIND_NEW)
        {
            query= RealmHelper.getRealm(getContext()).where(User.class).equalTo("message_id",0);
            ((SearchActivity)getActivity()).searchForPairedDevices();

        }else {
            query= RealmHelper.getRealm(getContext()).where(User.class).notEqualTo("message_id",0);
        }

        Log.d(TAG, "setUpList: cadapter");

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cAdapter);

    }



}
