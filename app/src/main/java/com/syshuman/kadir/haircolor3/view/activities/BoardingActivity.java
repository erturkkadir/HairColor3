package com.syshuman.kadir.haircolor3.view.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.syshuman.kadir.haircolor3.eventbus.BoardingEvents;
import com.syshuman.kadir.haircolor3.model.HairColorUser;
import com.syshuman.kadir.haircolor3.R;
import com.syshuman.kadir.haircolor3.model.RestServer;
import com.syshuman.kadir.haircolor3.view.fragments.LoginFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class BoardingActivity extends AppCompatActivity {

    RestServer restServer;
    Context context;
    String devId;

    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_boarding);
        this.context = getBaseContext();

        restServer = new RestServer(context);

        this.devId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        SharedPreferences prefs = this.getSharedPreferences("com.syshuman.kadir.socks", this.MODE_PRIVATE);
        Integer um_no = prefs.getInt("um_no", 0);
        if(um_no!=0) {
            HairColorUser hcuser = new HairColorUser();
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
        } else {
            LoginFragment loginFragment = new LoginFragment();
            replaceFragment(loginFragment, "LoginFragment");
        }
    }

    public void replaceFragment(Fragment fragment, String TAG) {

        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container, fragment, TAG).addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void login( String uName, String uPass) {

        restServer.login( uName, uPass);
    }

    public void register( String fName, String lName, String uPass) {

        restServer.register(context, fName, uPass, fName, lName, devId, 8);
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



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BoardingEvents.onLoginSuccess event) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BoardingEvents.onLoginFailed error) {
        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BoardingEvents.onRegisterSuccess event) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        Toast.makeText(context, event.toString(), Toast.LENGTH_LONG).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BoardingEvents.onRegistrationFailed error) {
        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
    }
}
