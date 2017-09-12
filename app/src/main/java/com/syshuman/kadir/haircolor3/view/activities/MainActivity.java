package com.syshuman.kadir.haircolor3.view.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.syshuman.kadir.haircolor3.dagger.components.DaggerHC3Component;
import com.syshuman.kadir.haircolor3.dagger.components.HC3Component;
import com.syshuman.kadir.haircolor3.dagger.modules.ContextModule;
import com.syshuman.kadir.haircolor3.eventbus.MessageEvents;
import com.syshuman.kadir.haircolor3.R;
import com.syshuman.kadir.haircolor3.model.BluetoothLeService;
import com.syshuman.kadir.haircolor3.model.RestServer;
import com.syshuman.kadir.haircolor3.view.fragments.ReadFragment;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ReadFragment.OnFragmentInteractionListener {

    String ble_status="No connection";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    public final static String EXTRA_DATA   = "com.syshuman.kadir.haircolor3.model.extra.EXTRA_DATA";

    private Context context;
    private String LOG_TAG="Adafruit";

    @BindView(R.id.txtZone1) TextView txtZone1;
    @BindView(R.id.txtZone2) TextView txtZone2;
    @BindView(R.id.txtZone3) TextView txtZone3;
    @BindView(R.id.txtTarget) TextView txtTarget;

    @BindView(R.id.btnZone1) Button btnZone1;
    @BindView(R.id.btnZone2) Button btnZone2;
    @BindView(R.id.btnZone3) Button btnZone3;
    @BindView(R.id.btnTarget) Button btnTarget;

    @BindView(R.id.lZone1) LinearLayout lZone1;
    @BindView(R.id.lZone2) LinearLayout lZone2;
    @BindView(R.id.lZone3) LinearLayout lZone3;
    @BindView(R.id.lTarget) LinearLayout lTarget;

    @BindView(R.id.lblZone1) TextView lblZone1;
    @BindView(R.id.lblZone2) TextView lblZone2;
    @BindView(R.id.lblZone3) TextView lblZone3;
    @BindView(R.id.lblTarget) TextView lblTarget;

    @BindView(R.id.txtRecipe) TextView txtRecipe;
    @BindView(R.id.btnGetRecipe) ImageButton btnGetRecipe;

    @BindView(R.id.btnBLE) FloatingActionButton btnBLE;
    @BindView(R.id.spCompany) Spinner spCompanies;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.imgBattery) ImageButton imgBattery;
    @BindView(R.id.txtBattery) TextView txtBattery;
    private BluetoothLeService bluetoothLeService;
    private String deviceAddress = "DD:68:7B:5D:B0:9B";

    CharSequence categories[] = new CharSequence[] {"Natural", "Colored", "Pigments"};
    AlertDialog.Builder builder;

    private Boolean silent = true;

    private RestServer restServer;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private MediaPlayer firstSound, lastSound;
    private int zone = 1;

    HC3Component component;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        this.context = getApplicationContext();
        component = DaggerHC3Component
                .builder()
                .contextModule(new ContextModule(context))
                .build();


        getPermissions();

        NavAndDraw();

        btnZone1.setOnClickListener(onZone1Click);
        btnZone2.setOnClickListener(onZone2Click);
        btnZone3.setOnClickListener(onZone3Click);
        btnTarget.setOnClickListener(onTargetClick);

        lZone1.setOnClickListener(onlZone1Click);
        lZone2.setOnClickListener(onlZone2Click);
        lZone3.setOnClickListener(onlZone3Click);
        lTarget.setOnClickListener(onlTargetClick);

        btnBLE.setOnClickListener(onBLEListener);
        btnGetRecipe.setOnClickListener(onGetRecipeListener);

        restServer = component.getRestServer();

        getInitialData();

        builder = new AlertDialog.Builder(this);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE);

    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if(!bluetoothLeService.initialize()) {
                Log.d(LOG_TAG, "Unable to Initialize");

            }
            bluetoothLeService.connect(deviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(LOG_TAG, "Service disconnected");
        }
    };

    private BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.d(LOG_TAG, "GATT CONNECTED");
                setButtonStatus(1);
                bluetoothLeService.send("9");
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


    public void getInitialData() {
        spCompanies = (Spinner) findViewById(R.id.spCompany);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.companies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCompanies.setAdapter(adapter);
        firstSound = MediaPlayer.create(context, R.raw.beep07);
        lastSound = MediaPlayer.create(context, R.raw.beep04);

        getPreferences();
    }

    public void getPreferences() {
        sharedPreferences = component.getSharedPreferences();
        editor = sharedPreferences.edit();
        lblZone1.setText(sharedPreferences.getString("Zone1", "Natural"));
        lblZone2.setText(sharedPreferences.getString("Zone2", "Colored"));
        lblZone3.setText(sharedPreferences.getString("Zone3", "Colored"));
        lblTarget.setText(sharedPreferences.getString("Target", "Colored"));
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
            Log.d(LOG_TAG, "Sent 1 To device ");
            if(!silent) firstSound.start();
            bluetoothLeService.send("1"); // Tell Arduino to read
        }
    };

    View.OnClickListener onlZone1Click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            builder.setTitle("Category for Zone1");
            builder.setItems(categories, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    lblZone1.setText(categories[which]);
                    editor.putString("Zone1", (categories[which]).toString());
                    editor.commit();
                    // the user clicked on colors[which]
                }
            });
            builder.show();

        }
    };

    View.OnClickListener onZone2Click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!silent)  firstSound.start();
            bluetoothLeService.send("2"); // Tell Arduino to read
            Log.d(LOG_TAG, "Sent 2");
        }
    };

    View.OnClickListener onlZone2Click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            builder.setTitle("Category for Zone2");
            builder.setItems(categories, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    lblZone2.setText(categories[which]);
                    editor.putString("Zone2", lblZone2.getText().toString());
                    editor.commit();
                }
            });
            builder.show();

        }
    };

    View.OnClickListener onZone3Click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!silent) firstSound.start();
            bluetoothLeService.send("3"); // Tell Arduino to read
        }
    };


    View.OnClickListener onlZone3Click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            builder.setTitle("Category for Zone3");
            builder.setItems(categories, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    lblZone3.setText(categories[which]);
                    editor.putString("Zone3", lblZone3.getText().toString());
                    editor.commit();
                }
            });
            builder.show();

        }
    };

    View.OnClickListener onTargetClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!silent)  firstSound.start();
            bluetoothLeService.send("4"); // Tell Arduino to read
        }
    };

    View.OnClickListener onlTargetClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            builder.setTitle("Category for Target");
            builder.setItems(categories, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    lblTarget.setText(categories[which]);
                    editor.putString("Target", lblTarget.getText().toString());
                    editor.commit();
                }
            });
            builder.show();
        }
    };

    View.OnClickListener onGetRecipeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!silent) firstSound.start();
            restServer.getRecipe(txtZone1.getText().toString(), txtZone2.getText().toString(), txtZone3.getText().toString(), txtTarget.getText().toString());
        }
    };

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
                btnBLE.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorGrey)));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
        if (bluetoothLeService != null) {
            boolean result = bluetoothLeService.connect(deviceAddress);
            Log.d(LOG_TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        bluetoothLeService = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(gattUpdateReceiver);
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
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        setButtonStatus(0);
    }

    public void decode( String str) {
        Log.d(LOG_TAG, str);

        String cod = str.substring(str.indexOf("cmd") + 3, str.indexOf("pow"));
        switch (cod) {
            case "49":
            case "50":
            case "51":
            case "52":
                decodeColor(str);
                break;
            case "53":
                break;
            case "57":
                setBatteryLevel(str);
                break;
            default:
        }
    }


     public void decodeColor(String str){
         String r_r = str.substring(str.indexOf("r_r") + 3, str.indexOf("r_g"));
         String r_g = str.substring(str.indexOf("r_g") + 3, str.indexOf("r_b"));
         String r_b = str.substring(str.indexOf("r_b") + 3, str.indexOf("r_c"));
         String r_c = str.substring(str.indexOf("r_c") + 3, str.indexOf("g_r"));

         String g_r = str.substring(str.indexOf("g_r") + 3, str.indexOf("g_g"));
         String g_g = str.substring(str.indexOf("g_g") + 3, str.indexOf("g_b"));
         String g_b = str.substring(str.indexOf("g_b") + 3, str.indexOf("g_c"));
         String g_c = str.substring(str.indexOf("g_c") + 3, str.indexOf("b_r"));

         String b_r = str.substring(str.indexOf("b_r") + 3, str.indexOf("b_g"));
         String b_g = str.substring(str.indexOf("b_g") + 3, str.indexOf("b_b"));
         String b_b = str.substring(str.indexOf("b_b") + 3, str.indexOf("b_c"));
         String b_c = str.substring(str.indexOf("b_c") + 3, str.indexOf("a_r"));

         String a_r = str.substring(str.indexOf("a_r") + 3, str.indexOf("a_g"));
         String a_g = str.substring(str.indexOf("a_g") + 3, str.indexOf("a_b"));
         String a_b = str.substring(str.indexOf("a_b") + 3, str.indexOf("a_c"));
         String a_c = str.substring(str.indexOf("a_c") + 3, str.indexOf("cmd"));

         String cmd = str.substring(str.indexOf("cmd") + 3, str.indexOf("pow"));
         String power = str.substring(str.indexOf("pow") + 3, str.indexOf("|"));
         int pow = Integer.valueOf(power); // 400
         pow = 100*(pow-340) / (420-340);
         txtBattery.setText(String.valueOf(pow)+"%");
         if(pow<50)
             imgBattery.setBackgroundColor(Color.RED);
         else
             imgBattery.setBackgroundColor(Color.BLUE);

         String company = spCompanies.getSelectedItem().toString();
         if(!silent) lastSound.start();
         String catalog = "Natural";

         restServer = new RestServer(context);

         restServer.getColor3(company, catalog, cmd, power,
                 r_r, r_g, r_b, r_c,
                 g_r, g_g, g_b, g_c,
                 b_r, b_g, b_b, b_c,
                 a_r, a_g, a_b, a_c);


         Log.d(LOG_TAG, "Step data here");
     }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull  int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(LOG_TAG, "coarse location permission granted");
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
                    Log.d(LOG_TAG, "Battery Level");
                } else if (id == R.id.btn_reset) {
                    Log.d(LOG_TAG, "Reset Device");
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {

                            //uart.send("8");
                        }
                    });
                } else if (id == R.id.nav_slideshow) {
                    Intent intent = new Intent(context, SpeechToTextActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_manage) {
                    Log.d(LOG_TAG, "Nav Manage");
                } else if (id == R.id.nav_share) {
                    Log.d(LOG_TAG, "Nav Share");
                } else if (id == R.id.nav_send) {
                    Log.d(LOG_TAG, "Nav Send");
                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void setBatteryLevel(String str) {
        String power = str.substring(str.indexOf("pow") + 3, str.indexOf("|"));
        int pow = Integer.valueOf(power); // 400
        pow = 100 * (pow - 340) / (430 - 340);
        txtBattery.setText(String.valueOf(pow)+"%");
        Log.d(LOG_TAG, "setBatteryLevel"+power);
        if(pow<50)
            imgBattery.setBackgroundColor(Color.RED);
        else
            imgBattery.setBackgroundColor(Color.BLUE);
    }

    @Override
    public void onFragmentInteraction(String color1, String delta1, String color2, String delta2) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetColor(MessageEvents.onGetColor event) {
        Toast.makeText(context, event.color + event.zone, Toast.LENGTH_LONG).show();
        if(zone==1) txtZone1.setText("Zone11");
        if(zone==2) txtZone2.setText("Zone22");
        if(zone==3) txtZone3.setText("Zone33");
        if(zone==4) txtTarget.setText("Target");

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetRecipe(MessageEvents.onGetRecipe event) {
        Toast.makeText(context, event.recipe, Toast.LENGTH_LONG).show();
        txtRecipe.setText(event.recipe);

    }



}
