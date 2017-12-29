package com.syshuman.kadir.haircolor3.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.syshuman.kadir.haircolor3.view.activities.BoardingActivity;
import com.syshuman.kadir.haircolor3.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LoginFragment extends Fragment {

    @BindView(R.id.login_btn) Button btnLogin;
    @BindView(R.id.register_btn) Button btnRegister;
    @BindView(R.id.forgot_btn) Button btnForgot;

    @BindView(R.id.account_email_input) EditText uname;
    @BindView(R.id.password_input) EditText upass;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        ButterKnife.bind(this, view);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BoardingActivity) getActivity()).login(uname.getText().toString(), upass.getText().toString());
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterFragment registerFragment = new RegisterFragment();
                ((BoardingActivity) getActivity()).addFragment(registerFragment, "RegisterFragment");
            }
        });


        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotFragment forgotFragment = new ForgotFragment();
                ((BoardingActivity) getActivity()).addFragment(forgotFragment, "ForgotFragment");
            }
        });

        return view;
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
