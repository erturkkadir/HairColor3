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
import android.widget.Filter;
import android.widget.TextView;

import com.syshuman.kadir.haircolor3.R;
import com.syshuman.kadir.haircolor3.adapter.CustomerAdapter;
import com.syshuman.kadir.haircolor3.model.Customer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CustomerFragment extends Fragment {

    @BindView(R.id.rvCustomer) RecyclerView rvCustomer;
    private List<Customer> customerList = new ArrayList<>();
    private CustomerAdapter mAdapter;
    public Context context;

    public CustomerFragment() {
        // Required empty public constructor
    }

    public void setContext(Context context) {
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer, container, false);
        ButterKnife.bind(this, view);

        mAdapter = new CustomerAdapter(customerList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        rvCustomer.setLayoutManager(mLayoutManager);
        rvCustomer.setItemAnimator(new DefaultItemAnimator());
        rvCustomer.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));

        rvCustomer.setAdapter(mAdapter);

        prepareData();
        return view;
    }

    private void prepareData() {

        Customer customer = new Customer("Mad Max: Fury Road", "Action & Adventure", "2015");
        customerList.add(customer);

        customer = new Customer("Mad Max: Fury Road", "Action & Adventure", "2015");
        customerList.add(customer);

        customer = new Customer("Mad Max: Fury Road", "Action & Adventure", "2015");
        customerList.add(customer);

        customer = new Customer("Mad Max: Fury Road", "Action & Adventure", "2015");
        customerList.add(customer);

        customer = new Customer("Mad Max: Fury Road", "Action & Adventure", "2015");
        customerList.add(customer);

        customer = new Customer("Mad Max: Fury Road", "Action & Adventure", "2015");
        customerList.add(customer);

        customer = new Customer("Mad Max: Fury Road", "Action & Adventure", "2015");
        customerList.add(customer);

        customer = new Customer("Mad Max: Fury Road", "Action & Adventure", "2015");
        customerList.add(customer);

        customer = new Customer("Mad Max: Fury Road", "Action & Adventure", "2015");
        customerList.add(customer);

        customer = new Customer("Mad Max: Fury Road", "Action & Adventure", "2015");
        customerList.add(customer);

        customer = new Customer("Mad Max: Fury Road", "Action & Adventure", "2015");
        customerList.add(customer);

        customer = new Customer("Mad Max: Fury Road", "Action & Adventure", "2015");
        customerList.add(customer);

        customer = new Customer("Mad Max: Fury Road", "Action & Adventure", "2015");
        customerList.add(customer);

        customer = new Customer("Mad Max: Fury Road", "Action & Adventure", "2015");
        customerList.add(customer);

        customer = new Customer("Mad Max: Fury Road", "Action & Adventure", "2015");
        customerList.add(customer);

        customer = new Customer("Mad Max: Fury Road", "Action & Adventure", "2015");
        customerList.add(customer);



    }

}
