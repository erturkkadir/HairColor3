package com.syshuman.kadir.haircolor3.view.activities;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
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
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.syshuman.kadir.haircolor3.model.BluetoothLeUart;
import com.syshuman.kadir.haircolor3.R;
import com.syshuman.kadir.haircolor3.model.RestServer;
import com.syshuman.kadir.haircolor3.view.fragments.ReadFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.syshuman.kadir.haircolor3.R.id.btnBLE;

public class MainActivity extends AppCompatActivity implements BluetoothLeUart.Callback, ReadFragment.OnFragmentInteractionListener {

    String messages, readStr, ble_status="No connection";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private BluetoothLeUart uart;
    private Context context;

    @BindView(R.id.txtZone1) TextView txtZone1;
    @BindView(R.id.txtZone2) TextView txtZone2;
    @BindView(R.id.txtZone3) TextView txtZone3;
    @BindView(R.id.txtTarget) TextView txtTarget;

    @BindView(R.id.btnZone1) Button btnZone1;
    @BindView(R.id.btnZone2) Button btnZone2;
    @BindView(R.id.btnZone3) Button btnZone3;
    @BindView(R.id.btnTarget) Button btnTarget;

    @BindView(R.id.btnBLE) FloatingActionButton btnBLE;
    @BindView(R.id.spCompany) Spinner spCompanies;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private MediaPlayer firstSound, lastSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        this.context = getApplicationContext();

        getPermissions();

        NavAndDraw();

        btnZone1.setOnClickListener(onZone1Click);
        btnZone2.setOnClickListener(onZone2Click);
        btnZone3.setOnClickListener(onZone3Click);
        btnTarget.setOnClickListener(onTargetClick);
        btnBLE.setOnClickListener(onBLEListener);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                uart = new BluetoothLeUart(context);
            }
        });

        getCompanies();

    }

    public void getCompanies() {spCompanies = (Spinner) findViewById(R.id.spCompany);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.companies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCompanies.setAdapter(adapter);

        //messages.setText("Started..!!!!!!!.\n");
       // messages.setMovementMethod(new ScrollingMovementMethod());
        firstSound = MediaPlayer.create(getApplicationContext(), R.raw.beep07);
        lastSound = MediaPlayer.create(getApplicationContext(), R.raw.beep04);

    }

    View.OnClickListener onBLEListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Snackbar.make(view, ble_status, Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    };

    View.OnClickListener onZone1Click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            firstSound.start();
            uart.send("1"); // Tell Arduino to read
            }
    };

    View.OnClickListener onZone2Click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            firstSound.start();
            uart.send("2"); // Tell Arduino to read
        }
    };

    View.OnClickListener onZone3Click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            firstSound.start();
            uart.send("3"); // Tell Arduino to read
        }
    };

    View.OnClickListener onTargetClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            firstSound.start();
            uart.send("4"); // Tell Arduino to read
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
        writeLine("\nScanning for device... ");
        uart.registerCallback(this);
        uart.connectFirstAvailable();
    }

    @Override
    protected void onStop() {
        super.onStop();
        uart.unregisterCallback(this);
        uart.disconnect();
        disableBLE();
        writeLine("\nStopped..");
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
                String red_red = str.substring(str.indexOf("r_r") + 1, str.indexOf("r_g"));
                String red_gre = str.substring(str.indexOf("r_g") + 1, str.indexOf("r_b"));
                String red_blu = str.substring(str.indexOf("r_b") + 1, str.indexOf("r_c"));
                String red_cle = str.substring(str.indexOf("r_c") + 1, str.indexOf("g_r"));

                String gre_red = str.substring(str.indexOf("g_r") + 1, str.indexOf("g_g"));
                String gre_gre = str.substring(str.indexOf("g_g") + 1, str.indexOf("g_b"));
                String gre_blu = str.substring(str.indexOf("g_b") + 1, str.indexOf("g_c"));
                String gre_cle = str.substring(str.indexOf("g_c") + 1, str.indexOf("b_r"));

                String blu_red = str.substring(str.indexOf("b_r") + 1, str.indexOf("b_g"));
                String blu_gre = str.substring(str.indexOf("b_g") + 1, str.indexOf("b_b"));
                String blu_blu = str.substring(str.indexOf("b_b") + 1, str.indexOf("b_c"));
                String blu_cle = str.substring(str.indexOf("b_c") + 1, str.indexOf("e_r"));

                String company = spCompanies.getSelectedItem().toString();
                lastSound.start();

                RestServer restServer = new RestServer(context);

                restServer.getColor(txtZone1, company, "Natural",
                        red_red, red_gre, red_blu, red_cle,
                        gre_red, gre_gre, gre_blu, gre_cle,
                        blu_red, blu_gre, blu_blu, blu_cle);


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
