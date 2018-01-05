package com.syshuman.kadir.haircolor3.view.fragments;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.syshuman.kadir.haircolor3.R;


public class StylistFragment extends Fragment {

    public StylistFragment() {
        // Required empty public constructor
    }
 public static StylistFragment newInstance(String param1, String param2) {
        StylistFragment fragment = new StylistFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sytlist, container, false);
        return view;
    }

}
