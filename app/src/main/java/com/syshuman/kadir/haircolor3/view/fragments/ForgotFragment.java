package com.syshuman.kadir.haircolor3.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.syshuman.kadir.haircolor3.R;
import com.syshuman.kadir.haircolor3.view.activities.BoardingActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ForgotFragment extends Fragment {

    @BindView(R.id.email_forgot) EditText etEmail;
    @BindView(R.id.send_forgot)  Button btnForgot;


    public ForgotFragment() {
        // Required empty public constructor
    }

    public static ForgotFragment newInstance(String param1, String param2) {
        ForgotFragment fragment = new ForgotFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_forgot, container, false);

        ButterKnife.bind(this, view);
        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInstruction(etEmail.getText().toString());
            }
        });

        return view;
    }


    private void sendInstruction(String email) {
        ((BoardingActivity) getActivity()).forgot(email);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
