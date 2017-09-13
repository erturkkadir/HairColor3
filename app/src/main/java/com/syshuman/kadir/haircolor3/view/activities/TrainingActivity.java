package com.syshuman.kadir.haircolor3.view.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import com.syshuman.kadir.haircolor3.R;
import com.syshuman.kadir.haircolor3.eventbus.MessageEvents;
import com.syshuman.kadir.haircolor3.model.BluetoothLeService;
import com.syshuman.kadir.haircolor3.model.MySVM;
import com.syshuman.kadir.haircolor3.model.RestServer;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TrainingActivity extends AppCompatActivity  {

    private MediaPlayer firstSound, lastSound;
    private String ble_status="No connection";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    public final static String EXTRA_DATA = "com.syshuman.kadir.haircolor3.model.extra.EXTRA_DATA";

    private Context context;
    private boolean silent=true;
    private String LOG_TAG="Service";
    RestServer restServer;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.spTCompany) Spinner spTCompany;
    @BindView(R.id.spTCategory) Spinner spTCategory;
    @BindView(R.id.spTSeries) Spinner spTSeries;
    @BindView(R.id.spTColor) Spinner spTColor;
    @BindView(R.id.btnTrain) ImageButton btnTrain;
    @BindView(R.id.btnBLE) FloatingActionButton btnBLE;
    @BindView(R.id.btnClearData) ImageButton btnClearData;
    @BindView(R.id.btnModel) ImageButton btnModel;

    private AlertDialog.Builder builder;
    private BluetoothLeService bluetoothLeService;
    private String deviceAddress = "DD:68:7B:5D:B0:9B";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_trainig);
        this.context = getApplicationContext();

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        getData();

        btnTrain.setOnClickListener(btnTrainOnClick);

        btnBLE.setOnClickListener(onBLEListener);

        btnClearData.setOnClickListener(onClearDataOnclick);

        btnModel.setOnClickListener(onModelClick);

        restServer = new RestServer(context);

        builder = new AlertDialog.Builder(this);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE);

    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            /*
            if(!bluetoothLeService.initialize()) {
                Log.d(LOG_TAG, "Unable to Initialize");
            }
            Log.d(LOG_TAG, "Initialized");
            bluetoothLeService.connect(deviceAddress);
            */

            if(!bluetoothLeService.isConnected) {
                bluetoothLeService.connect(deviceAddress);
                Log.d(LOG_TAG, "not connected");
            } else {
                setButtonStatus(1);
                Log.d(LOG_TAG, "connected");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            setButtonStatus(1);
        }
    };

    private BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                setButtonStatus(1);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {

                setButtonStatus(0);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                setButtonStatus(2);
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String data = intent.getStringExtra(EXTRA_DATA);
                Log.d(LOG_TAG, "Data " + data);
                decode(data);
                setButtonStatus(1);
            }
        }
    };

    public void setButtonStatus(int status) {

        switch(status) {
            case 0 : // Disconnected
                btnBLE.setImageResource(R.drawable.bt_active);
                btnBLE.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorGrey)));
                break;
            case 1 : // Connected
                btnBLE.setImageResource(R.drawable.bt_active);
                btnBLE.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue)));
                break;
            case 2 : // Discovered
                btnBLE.setImageResource(R.drawable.bt_active);
                btnBLE.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue)));break;
            case 3: // Data available
                btnBLE.setImageResource(R.drawable.bt_active);
                btnBLE.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue)));
                break;
            default:
                btnBLE.setImageResource(R.drawable.bt_active);
                btnBLE.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue)));
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }


    View.OnClickListener onClearDataOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            builder.setTitle("Confirm");
            builder.setMessage("Are you sure to delete training data");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    restServer.clearTrainData();
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    };


    View.OnClickListener onModelClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            restServer.getTrainedData();

        }
    };

    View.OnClickListener btnTrainOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!silent) firstSound.start();
            bluetoothLeService.send("1"); // Tell Arduino to read
        }
    };

    View.OnClickListener onBLEListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Snackbar.make(view, ble_status, Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    };

    public void getData() {

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.companies, R.layout.simple_spinner);
        adapter.setDropDownViewResource(R.layout.simple_spinner);
        spTCompany.setAdapter(adapter);

        firstSound = MediaPlayer.create(getApplicationContext(), R.raw.beep07);
        lastSound = MediaPlayer.create(getApplicationContext(), R.raw.beep04);


        spTCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String company = adapterView.getItemAtPosition(i).toString();
                restServer.getCategory(context, company, spTCategory);
                ((TextView) view).setTextColor(Color.BLACK);
                setListenerForCategory();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setListenerForCategory() {

        spTCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String company = spTCompany.getSelectedItem().toString();
                String category = spTCategory.getSelectedItem().toString();
                restServer.getSeries(context, company, category, spTSeries);
                ((TextView) view).setTextColor(Color.BLACK);
                setListenerForSeries();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setListenerForSeries() {
        spTSeries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String company = spTCompany.getSelectedItem().toString();
                String catalog = spTCategory.getSelectedItem().toString();
                String series = spTSeries.getSelectedItem().toString();
                restServer.getColorList(context, company, catalog, series, spTColor);
                ((TextView) view).setTextColor(Color.BLACK);
                setListenerForColor();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setListenerForColor() {
        spTColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) view).setTextColor(Color.BLACK);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
        if (bluetoothLeService != null) {
            if(!bluetoothLeService.initialized)
                if(!bluetoothLeService.isConnected)
                    bluetoothLeService.connect(deviceAddress);

        }*/
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
        if(!EventBus.getDefault().isRegistered(this))   EventBus.getDefault().register(this);

    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(EventBus.getDefault().isRegistered(this))   EventBus.getDefault().unregister(this);
    }

    public void decode(final String str) {
        String cmd = str.substring(str.indexOf("cmd")+3, str.indexOf("pow"));
        if(cmd.equals("57")) {
            setButtonStatus(1);
            return;
        }
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

        String pow = str.substring(str.indexOf("pow")+3, str.indexOf("|"));

        String company  = spTCompany.getSelectedItem().toString();
        String category = spTCategory.getSelectedItem().toString();
        String series = spTSeries.getSelectedItem().toString();
        String color = spTColor.getSelectedItem().toString();

        if(!silent) firstSound.stop();
        if(!silent) lastSound.start();

        RestServer restServer = new RestServer(context);
        restServer.train3(context,
                        r_r, r_g, r_b, r_c,
                        g_r, g_g, g_b, g_c,
                        b_r, b_g, b_b, b_c,
                        a_r, a_g, a_b, a_c,
                        pow, company, category, series, color);

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTrainedData(MessageEvents.onTrainedData event) {

        JSONArray jsonArray = event.jsonArray;
        int length = jsonArray.length();

        double[][] xtrain = new double[length][17];
        double[][] ytrain = new double[length][1];

        try {
            for(int i=0; i<length;i++) {
                JSONObject inner = new JSONObject(jsonArray.get(i).toString());
                xtrain[i][0]  = inner.getInt("r_r")*1.0;
                xtrain[i][1]  = inner.getInt("r_g")*1.0;
                xtrain[i][2]  = inner.getInt("r_b")*1.0;
                xtrain[i][3]  = inner.getInt("r_c")*1.0;
                xtrain[i][4]  = inner.getInt("g_r")*1.0;
                xtrain[i][5]  = inner.getInt("g_g")*1.0;
                xtrain[i][6]  = inner.getInt("g_b")*1.0;
                xtrain[i][7]  = inner.getInt("g_c")*1.0;
                xtrain[i][8]  = inner.getInt("b_r")*1.0;
                xtrain[i][9]  = inner.getInt("b_g")*1.0;
                xtrain[i][10] = inner.getInt("b_b")*1.0;
                xtrain[i][11] = inner.getInt("b_c")*1.0;
                xtrain[i][12] = inner.getInt("a_r")*1.0;
                xtrain[i][13] = inner.getInt("a_g")*1.0;
                xtrain[i][14] = inner.getInt("a_b")*1.0;
                xtrain[i][15] = inner.getInt("a_c")*1.0;
                xtrain[i][16] = inner.getInt("pow")*1.0;

                ytrain[i][0]  = inner.getInt("cn_id")*1.0;

            }
        } catch (JSONException e) {

        }

        MySVM mysvm = new MySVM(context);
        mysvm.svmTrain(xtrain, ytrain);


    }

}
