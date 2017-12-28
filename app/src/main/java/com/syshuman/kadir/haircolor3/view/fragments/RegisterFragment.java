package com.syshuman.kadir.haircolor3.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.syshuman.kadir.haircolor3.R;
import com.syshuman.kadir.haircolor3.view.activities.BoardingActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RegisterFragment extends Fragment {

    @BindView(R.id.reg_register) Button btnRegister;
    @BindView(R.id.reg_fname) EditText fName;
    @BindView(R.id.reg_lname) EditText lName;
    @BindView(R.id.reg_email) EditText email;
    @BindView(R.id.reg_pass1) EditText pass1;
    @BindView(R.id.reg_pass2) EditText pass2;
    @BindView(R.id.reg_eula) CheckBox eula;


    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance(String param1, String param2) {
       return new RegisterFragment();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private boolean chkForm() {
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkForm()) {
                    ((BoardingActivity) getActivity()).register(fName.getText().toString(), lName.getText().toString(),  pass1.getText().toString());
                }
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
