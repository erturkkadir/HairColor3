package com.syshuman.kadir.haircolor3.view.activities;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.syshuman.kadir.haircolor3.model.BluetoothLeUart;
import com.syshuman.kadir.haircolor3.R;
import com.syshuman.kadir.haircolor3.view.fragments.ReadFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BluetoothLeUart.Callback, ReadFragment.OnFragmentInteractionListener {

    String deviceID, messages, readStr, ble_status="No connection";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private BluetoothLeUart uart;
    private Context context;
    private FloatingActionButton btnBLE;
    private Toolbar toolbar;
    private Button btnZone1, btnZone2, btnZone3, btnTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.context = getApplicationContext();

        getPermissions();

        NavAndDraw();

        btnZone1 = (Button) findViewById(R.id.btnZone1);
        btnZone2 = (Button) findViewById(R.id.btnZone2);
        btnZone3 = (Button) findViewById(R.id.btnZone3);
        btnTarget = (Button) findViewById(R.id.btnTarget);
        btnZone1.setOnClickListener(onZone1Click);
        btnZone2.setOnClickListener(onZone2Click);
        btnZone3.setOnClickListener(onZone3Click);
        btnTarget.setOnClickListener(onTargetClick);

        btnBLE = (FloatingActionButton) findViewById(R.id.btnBLE);
        btnBLE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, ble_status, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                uart = new BluetoothLeUart(context);
            }
        });

    }

    View.OnClickListener onZone1Click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ReadFragment readFragment = new ReadFragment().newInstance("Zone1");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.content_main, readFragment, "ReadFragment");
            transaction.commit();
            Log.d("HC", "zone1");

            }
    };
    View.OnClickListener onZone2Click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };
    View.OnClickListener onZone3Click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };
    View.OnClickListener onTargetClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void disableBLE() {
        ble_status = "Disable BLE";
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnBLE.setImageResource(R.drawable.bt_active);
                btnBLE.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorGrey)));
                Log.i("BLE", "DisabledBLE");
            }
        });

    }

    public void enableBLE() {
        ble_status = "BLE Enabled";
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnBLE.setImageResource(R.drawable.bt_active);
                btnBLE.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue)));
                Log.i("BLE", "enabled BLE");
            }
        });

    }

    private void writeLine(final CharSequence text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messages += text;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        uart.registerCallback(this);
        uart.connectFirstAvailable();
    }

    @Override
    protected void onStop() {
        super.onStop();
        uart.unregisterCallback(this);
        uart.disconnect();
        disableBLE();
    }

    @Override
    public void onConnected(BluetoothLeUart uart) {
        Log.i("BLE", "onConnected" + uart.toString());
        ble_status = "Connected ";
        enableBLE();
    }

    @Override
    public void onConnectFailed(BluetoothLeUart uart) {
        ble_status = "Connection Failed, " + uart.toString();
        disableBLE();
    }

    @Override
    public void onDisconnected(BluetoothLeUart uart) {
        ble_status = "Device Disconnected";
        disableBLE();
    }

    @Override
    public void onReceive(BluetoothLeUart uart, BluetoothGattCharacteristic rx) {
        ble_status = "Data Streaming";
        String msg = "" + rx.getStringValue(0);
        if (msg.indexOf('|') > 0) {
            readStr = readStr + msg;
            decode(readStr);
            readStr = "";
        } else {
            readStr = readStr + msg;
        }
    }

    @Override
    public void onDeviceFound(BluetoothDevice device) {
        ble_status = "Device Found";
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnBLE.setImageResource(R.drawable.bt_passive);
                btnBLE.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.yellow)));
            }
        });
    }

    @Override
    public void onDeviceInfoAvailable() {
        ble_status = "BLE Disabled";
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnBLE.setImageResource(R.drawable.bt_passive);
                btnBLE.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorGrey)));
                Log.i("BLE", "disableLE");
            }
        });
        ble_status = "Device Info Available" + uart.toString();
    }

    public void decode(final String str) {
        Log.d("Data", str);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //String step = str.substring(str.indexOf('l') + 1, str.indexOf('|'));
                Log.d("Debug", "Step data here");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull  int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG", "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
            }
        }
    }

    public void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE is not supported", Toast.LENGTH_SHORT).show();
            finish();
        }
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
                if (id == R.id.nav_camera) {
                    Log.d("Debug", "Nav Camera");
                } else if (id == R.id.nav_gallery) {
                    Log.d("Debug", "Nav Gallery");
                } else if (id == R.id.nav_slideshow) {
                    Log.d("Debug", "Nav Slideshow");
                } else if (id == R.id.nav_manage) {
                    Log.d("Debug", "Nav Manage");
                } else if (id == R.id.nav_share) {
                    Log.d("Debug", "Nav Share");
                } else if (id == R.id.nav_send) {
                    Log.d("Debug", "Nav Send");
                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public void onFragmentInteraction(String color1, String delta1, String color2, String delta2) {

    }
}
