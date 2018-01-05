package com.syshuman.kadir.haircolor3.view.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.syshuman.kadir.haircolor3.R;
import com.syshuman.kadir.haircolor3.model.RestServer;
import com.syshuman.kadir.haircolor3.utils.Config;
import com.syshuman.kadir.haircolor3.utils.NotificationUtils;
import com.syshuman.kadir.haircolor3.utils.PermissionUtils;
import com.syshuman.kadir.haircolor3.view.fragments.CameraFragment;
import com.syshuman.kadir.haircolor3.view.fragments.CustomerFragment;
import com.syshuman.kadir.haircolor3.view.fragments.StylistFragment;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity  {

    private static final String LOG_TAG = "MainActivity";

    private static final int PERMISSION_ALL = 123;
    private static final int REQUEST_COARSE_LOCATION =  0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 2;
    private AlertDialog dialog;
    private Context context;
    private BottomSheetBehavior sheetBehavior;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private PermissionUtils permissionsUtils;

    private FragmentTransaction fragmentTransaction;

    RestServer restServer;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.bottom_sheet) LinearLayout layoutBottomSheet;
    @BindView(R.id.custName) TextView custName;
    @BindView(R.id.lastVisit) TextView lastVisit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        NavAndDraw();

        this.context = getApplicationContext();

        dialog = new AlertDialog.Builder(this).create();

        getPermissions();

        setBottomSheet();

        registerFirebaseReceiver();

        restServer = new RestServer(context);

    }


    private void registerFirebaseReceiver() {

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                }
            }
        };
    }

    private void setBottomSheet() {

        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {

                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {

                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:

                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        custName.setText("Select Customer");
        lastVisit.setText("");
        custName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e("", "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId)) {
            //txtRegId.setText("Firebase Reg Id: " + regId);
        } else {
            //txtRegId.setText("Firebase Reg Id is not received yet!");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_training) {
            Intent intent = new Intent(context, TrainingActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_settings) {
            Intent intent = new Intent(context, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_about) {
            Intent intent = new Intent(context, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,  new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void NavAndDraw() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_open, R.string.navigation_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {

                    case R.id.nav_customer :
                        CustomerFragment customerFragment = new CustomerFragment();
                        customerFragment.setContext(context, restServer);
                        addFragment(customerFragment, "CustomerFragment");
                        break;

                    case R.id.nav_stylist :
                        StylistFragment stylistFragment = new StylistFragment();
                        addFragment(stylistFragment, "StylistFragment");
                        dispatchTakePictureIntent();
                        break;

                    case R.id.nav_widget :
                        //Intent intent = new Intent(context, SpeechToTextActivity.class);
                        //startActivity(intent);

                        CameraFragment cameraFragment = new CameraFragment();
                        cameraFragment.setContext(context);
                        addFragment(cameraFragment, "CameraFragment");
                        break;

                    case R.id.nav_myaccount:
                        Toast.makeText(context,"MyAccount",Toast.LENGTH_LONG).show();
                        break;
                    case R.id.nav_signout:
                        Toast.makeText(context,"SignOut",Toast.LENGTH_LONG).show();
                        sign_out();
                        break;
                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }


    public void setTitle(String title) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);
    }

    private void sign_out() {
        SharedPreferences prefs = this.getSharedPreferences("com.syshuman.kadir.socks", this.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("um_no");
        editor.apply();
        Intent intent = new Intent(context, BoardingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    public void getPermissions() {

        permissionsUtils = new PermissionUtils(this);
        Map<String, Integer> permissions = new HashMap<>();
        permissions.put(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_COARSE_LOCATION);
        permissions.put(Manifest.permission.CAMERA, REQUEST_IMAGE_CAPTURE);
        permissions.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_EXTERNAL_STORAGE);

        permissionsUtils.getPermissions(permissions);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull  int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL:
                if (grantResults.length > 0) {
                    if(grantResults[0] != PackageManager.PERMISSION_GRANTED) permissionsUtils.deniedLocation();
                    if(grantResults[1] != PackageManager.PERMISSION_GRANTED) permissionsUtils.deniedCamera();
                    if(grantResults[2] != PackageManager.PERMISSION_GRANTED) permissionsUtils.deniedStorage();
                }
                break;
        }
    }

    public void replaceFragment(Fragment fragment, String TAG) {

        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment, TAG);
        fragmentTransaction.commit();
    }

    public void addFragment(Fragment fragment, String TAG) {
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_container, fragment, TAG).addToBackStack(null);
        fragmentTransaction.commit();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            saveImage(imageBitmap, imageBitmap);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    private void saveImage(Bitmap haystack, Bitmap needle) {

        int match_method = Imgproc.TM_CCOEFF;


        /* Save Image to server */
        Mat mHaystack = new Mat();
        Utils.bitmapToMat(haystack, mHaystack);

        Mat mNeedle = new Mat();
        Utils.bitmapToMat(needle, mNeedle);

        int result_cols = needle.getWidth();
        int result_rows = needle.getHeight();
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

        // Do the Matching and Normalize
        Imgproc.matchTemplate(mHaystack, mNeedle, result, match_method);
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        // / Localizing the best match with minMaxLoc
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

        Point matchLoc = mmr.maxLoc;

        Toast.makeText(context, " saved ", Toast.LENGTH_SHORT).show();
    }

    public void fetchCustomerData() {
        restServer.getCustomers(context);
    }
}
