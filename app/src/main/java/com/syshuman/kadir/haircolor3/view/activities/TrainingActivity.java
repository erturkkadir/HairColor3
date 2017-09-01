package com.syshuman.kadir.haircolor3.view.activities;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import com.syshuman.kadir.haircolor3.R;
import com.syshuman.kadir.haircolor3.model.BluetoothLeUart;
import com.syshuman.kadir.haircolor3.model.RestServer;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TrainingActivity extends AppCompatActivity implements BluetoothLeUart.Callback {

    private BluetoothLeUart uart;
    private MediaPlayer firstSound, lastSound;
    private String readStr = "", ble_status="No connection";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private Context context;
    private boolean silent=true;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.spTCompany) Spinner spTCompany;
    @BindView(R.id.spTCatalog) Spinner spTCatalog;
    @BindView(R.id.spTColor) Spinner spTColor;
    @BindView(R.id.btnTrain) ImageButton btnTrain;
    @BindView(R.id.btnBLE) FloatingActionButton btnBLE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_trainig);
        this.context = getBaseContext();

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        getData();

        btnTrain.setOnClickListener(btnTrainOnClick);

        btnBLE.setOnClickListener(onBLEListener);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                uart = new BluetoothLeUart(context);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }


    View.OnClickListener btnTrainOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!silent) firstSound.start();
            uart.send("1");
        }
    };

    View.OnClickListener onBLEListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Snackbar.make(view, ble_status, Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    };

    private void writeLine(final CharSequence text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
              //  messages.append(text);
            }
        });
    }

    public void disableBLE(String str) {
        ble_status = "Disable BLE";
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnBLE.setImageResource(R.drawable.bt_active);
                btnBLE.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorGrey)));
                Log.i("BLE", "DisabledBLE");
            }
        });
        btnTrain.setClickable(false);
        writeLine(str);

    }

    public void enableBLE(String str) {
        ble_status = "BLE Enabled";
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnBLE.setImageResource(R.drawable.bt_active);
                btnBLE.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue)));
                Log.i("BLE", "enabled BLE");
            }
        });
        btnTrain.setClickable(true);
        writeLine(str);
    }

    public void getData() {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.companies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTCompany.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.catalog, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTCatalog.setAdapter(adapter1);
        //adapter.clear();

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.color, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTColor.setAdapter(adapter2);

        firstSound = MediaPlayer.create(getApplicationContext(), R.raw.beep07);
        lastSound = MediaPlayer.create(getApplicationContext(), R.raw.beep04);
    }

    @Override
    protected void onResume() {
        super.onResume();
        writeLine("\nScanning for device... ");
        uart.registerCallback(this);
        uart.connectFirstAvailable();
    }

    // OnStop, called right before the activity loses foreground fo public void onConnection.  Close the BTLE connection.
    @Override
    protected void onStop() {
        super.onStop();
        uart.unregisterCallback(this);
        uart.disconnect();
        disableBLE("\nStopped..");
    }

    @Override
    public void onConnected(BluetoothLeUart uart) {

        enableBLE("\nconnected");
    }

    @Override
    public void onConnectFailed(BluetoothLeUart uart) {
        disableBLE("\nError connecting to device ! ");
    }

    @Override
    public void onDisconnected(BluetoothLeUart uart) {
        disableBLE("\nDisconnected!");
    }

    @Override
    public void onReceive(BluetoothLeUart uart, BluetoothGattCharacteristic rx) {
        String msg = "" + rx.getStringValue(0);
        Log.d("TAG", msg);

        if(msg.indexOf('|')>0) {
            readStr = readStr + msg;
            writeLine("\nReceived : " + readStr + '\r' );
            decode(readStr);
            readStr = "";
        } else {
            readStr = readStr + msg;
        }
    }

    @Override
    public void onDeviceFound(BluetoothDevice device) {
        // Called when a UART device is discovered (after calling startScan).
        Log.d("Test", device.toString());
        writeLine("\nFound device : " + device.toString());
        writeLine("\nWaiting for a connection....");
    }

    @Override
    public void onDeviceInfoAvailable() {
        writeLine(uart.getDeviceInfo());
    }

    public void decode(final String str) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String r_r = str.substring(str.indexOf("r_r")+3, str.indexOf("r_g"));
                String r_g = str.substring(str.indexOf("r_g")+3, str.indexOf("r_b"));
                String r_b = str.substring(str.indexOf("r_b")+3, str.indexOf("r_c"));
                String r_c = str.substring(str.indexOf("r_c")+3, str.indexOf("g_r"));

                String g_r = str.substring(str.indexOf("g_r")+3, str.indexOf("g_g"));
                String g_g = str.substring(str.indexOf("g_g")+3, str.indexOf("g_b"));
                String g_b = str.substring(str.indexOf("g_b")+3, str.indexOf("g_c"));
                String g_c = str.substring(str.indexOf("g_c")+3, str.indexOf("b_r"));

                String b_r = str.substring(str.indexOf("b_r")+3, str.indexOf("b_g"));
                String b_g = str.substring(str.indexOf("b_g")+3, str.indexOf("b_b"));
                String b_b = str.substring(str.indexOf("b_b")+3, str.indexOf("b_c"));
                String b_c = str.substring(str.indexOf("b_c")+3, str.indexOf("a_r"));

                String a_r = str.substring(str.indexOf("a_r")+3, str.indexOf("a_g"));
                String a_g = str.substring(str.indexOf("a_g")+3, str.indexOf("a_b"));
                String a_b = str.substring(str.indexOf("a_b")+3, str.indexOf("a_c"));
                String a_c = str.substring(str.indexOf("a_c")+3, str.indexOf("cmd"));

                String pow = str.substring(str.indexOf("cmd")+3, str.indexOf("pow"));


                String company  = spTCompany.getSelectedItem().toString();
                String catalog = spTCatalog.getSelectedItem().toString();
                String color = spTColor.getSelectedItem().toString();

                if(!silent) firstSound.stop();
                if(!silent) lastSound.start();

                RestServer restServer = new RestServer(context);
                restServer.train(context,
                        r_r, r_g, r_b, r_c,
                        g_r, g_g, g_b, g_c,
                        b_r, b_g, b_b, b_c,
                        a_r, a_g, a_b, a_c,
                        company, catalog, color);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
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
}
