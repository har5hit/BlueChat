package com.justadeveloper96.bluechat;

import android.content.Intent;
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

import helpers.RealmManager;
import model.User;

/**
 * A placeholder fragment containing a simple view.
 */
public class ContactsListFragment extends Fragment implements ItemClickListener {

    private static final String TAG = "ContactsListFragment";

    private RecyclerView recyclerView;
    private ContactsAdapter cAdapter;
    private int type;

    public List<User> list;

    public List<User> getList() {
        return list;
    }

    public ContactsListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        list=new ArrayList<>();
        cAdapter=new ContactsAdapter(getActivity(),list,this);

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
      //  type= getArguments().getInt("type");

        list.addAll(RealmManager.getAllStoredContacts().findAll());
        Log.d(TAG, "setUpList: cadapter");

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cAdapter);
    }


    @Override
    public void onItemClick(int position) {
        startActivity(new Intent(getContext(),ChatActivity.class)
                .putExtra(Constants.MAC_ADDRESS,list.get(position).macAddress)
                .putExtra(Constants.NAME,list.get(position).name)
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        list.clear();
        list.addAll(RealmManager.getAllStoredContacts().findAll());
        cAdapter.notifyDataSetChanged();
    }
}
