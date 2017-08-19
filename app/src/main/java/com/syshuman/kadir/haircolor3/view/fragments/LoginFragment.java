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


public class LoginFragment extends Fragment {

    EditText uname = null;
    EditText upass = null;
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

        uname = (EditText) view.findViewById(R.id.account_email_input);
        upass = (EditText) view.findViewById(R.id.password_input);

        Button btnLogin = (Button) view.findViewById(R.id.login_btn);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BoardingActivity) getActivity()).login(uname.getText().toString(), upass.getText().toString());
            }
        });

        Button btnRegister = (Button) view.findViewById(R.id.register_btn);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterFragment registerFragment = new RegisterFragment();
                ((BoardingActivity) getActivity()).replaceFragment(registerFragment, "RegisterFragment");

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
