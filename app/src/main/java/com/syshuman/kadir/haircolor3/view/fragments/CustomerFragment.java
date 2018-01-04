package com.syshuman.kadir.haircolor3.view.fragments;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.syshuman.kadir.haircolor3.R;
import com.syshuman.kadir.haircolor3.adapter.CustomerAdapter;
import com.syshuman.kadir.haircolor3.model.CustM;
import com.syshuman.kadir.haircolor3.model.RestServer;
import com.syshuman.kadir.haircolor3.view.activities.MainActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CustomerFragment extends Fragment implements CustomerAdapter.CustomerAdapterListener{

    @BindView(R.id.rvCustomer) RecyclerView rvCustomer;

    private List<CustM> custMList = new ArrayList<>();
    private RestServer restServer;

    private CustomerAdapter mAdapter;
    private SearchView searchView;

    private Context context;

    public CustomerFragment() {
        // Required empty public constructor
    }

    public void setContext(Context context, RestServer restServer) {
        this.context = context;
        this.restServer = restServer;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer, container, false);
        ButterKnife.bind(this, view);

        ((MainActivity) getActivity()).setTitle("Customer List");


        custMList = new ArrayList<>();

        mAdapter = new CustomerAdapter(context, custMList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        rvCustomer.setLayoutManager(mLayoutManager);
        rvCustomer.setItemAnimator(new DefaultItemAnimator());
        rvCustomer.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        rvCustomer.setAdapter(mAdapter);

        fetchContacts();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void fetchContacts() {

        restServer.getCustomers(context);

    }

    @Override
    public void onCustomerSelected(CustM custM) {

    }
}
