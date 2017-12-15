package com.syshuman.kadir.haircolor3.view.fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.syshuman.kadir.haircolor3.R;
import com.syshuman.kadir.haircolor3.dagger.components.DaggerHC3Component;
import com.syshuman.kadir.haircolor3.dagger.components.HC3Component;
import com.syshuman.kadir.haircolor3.dagger.modules.ContextModule;
import com.syshuman.kadir.haircolor3.eventbus.MessageEvents;
import com.syshuman.kadir.haircolor3.model.BluetoothLeService;
import com.syshuman.kadir.haircolor3.model.RestServer;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.BIND_AUTO_CREATE;


public class ReadColorFragment extends BaseFragment {

    private Context context;
    String ble_status = "No connection";

    public final static String EXTRA_DATA = "com.syshuman.kadir.haircolor3.model.extra.EXTRA_DATA";
    HC3Component component;


    private String LOG_TAG = "Adafruit";

    @BindView(R.id.txtZone1)  TextView txtZone1;
    @BindView(R.id.txtZone2)  TextView txtZone2;
    @BindView(R.id.txtZone3)  TextView txtZone3;
    @BindView(R.id.txtTarget) TextView txtTarget;

    @BindView(R.id.txtZone1_1)  TextView txtZone1_1;
    @BindView(R.id.txtZone2_1)  TextView txtZone2_1;
    @BindView(R.id.txtZone3_1)  TextView txtZone3_1;
    @BindView(R.id.txtTarget_1) TextView txtTarget_1;

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

    @BindView(R.id.imgBattery) ImageButton imgBattery;
    @BindView(R.id.txtBattery) TextView txtBattery;
    private BluetoothLeService bluetoothLeService;
    String deviceAddress = "DD:68:7B:5D:B0:9B";

    CharSequence categories[] = new CharSequence[]{"Natural", "Colored", "Pigments"};
    AlertDialog.Builder dialog;

    private Boolean silent = true;

    private RestServer restServer;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private MediaPlayer firstSound, lastSound;

    public ReadColorFragment() {
    }

    public static ReadColorFragment newInstance() {
        return new ReadColorFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read_color, container, false);

        ButterKnife.bind(this, view);

        initialize();

        return view;
    }



    private void initialize() {

        component = DaggerHC3Component.builder().contextModule(new ContextModule(context)).build();

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

        dialog = new AlertDialog.Builder(context);

        Intent gattServiceIntent = new Intent(context, BluetoothLeService.class);
        context.bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            bluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!bluetoothLeService.initialized) {
                if (!bluetoothLeService.initialize()) {
                    Log.d(LOG_TAG, "Unable to Initialize");
                    return;
                }
            }

            if (!bluetoothLeService.isConnected) {
                bluetoothLeService.connect(deviceAddress);
                Log.d(LOG_TAG, "connected");
            } else {
                setButtonStatus(1);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(LOG_TAG, "Service disconnected");
            setButtonStatus(0);
        }
    };


    private void setBatteryLevel(String str) {
        int pow = Integer.valueOf(str); // 400
        String power = String.valueOf(100 * (pow - 340) / (430 - 340))+ "%";
        txtBattery.setText(power);
        if(pow<50)
            imgBattery.setBackgroundColor(Color.RED);
        else
            imgBattery.setBackgroundColor(Color.BLUE);
    }

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
                Log.d(LOG_TAG, "GATT DISCCONNECTED");
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

    public void getPreferences() {
        sharedPreferences = component.getSharedPreferences();
        lblZone1.setText(sharedPreferences.getString("Zone1", "Natural"));
        lblZone2.setText(sharedPreferences.getString("Zone2", "Colored"));
        lblZone3.setText(sharedPreferences.getString("Zone3", "Colored"));
        lblTarget.setText(sharedPreferences.getString("Target", "Colored"));
    }

    public void getInitialData() {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.companies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCompanies.setAdapter(adapter);
        firstSound = MediaPlayer.create(context, R.raw.beep07);
        lastSound = MediaPlayer.create(context, R.raw.beep04);

        getPreferences();
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
            dialog.setTitle("Category for Zone1");
            dialog.setItems(categories, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    lblZone1.setText(categories[which]);
                    editor = sharedPreferences.edit();
                    editor.putString("Zone1", (categories[which]).toString());
                    editor.apply();
                    // the user clicked on colors[which]
                }
            });
            dialog.show();
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
            dialog.setTitle("Category for Zone2");
            dialog.setItems(categories, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    lblZone2.setText(categories[which]);
                    editor = sharedPreferences.edit();
                    editor.putString("Zone2", lblZone2.getText().toString());
                    editor.apply();
                }
            });
            dialog.show();

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
            dialog.setTitle("Category for Zone3");
            dialog.setItems(categories, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    lblZone3.setText(categories[which]);
                    editor = sharedPreferences.edit();
                    editor.putString("Zone3", lblZone3.getText().toString());
                    editor.apply();
                }
            });
            dialog.show();

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
            dialog.setTitle("Category for Target");
            dialog.setItems(categories, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    lblTarget.setText(categories[which]);
                    editor = sharedPreferences.edit();
                    editor.putString("Target", lblTarget.getText().toString());
                    editor.apply();
                }
            });
            dialog.show();
        }
    };

    View.OnClickListener onGetRecipeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String str1, str2, str3, str4;

            if(!silent) firstSound.start();
            if(txtZone1.getHint()==null) str1 = ""; else str1 = txtZone1.getHint().toString();
            if(txtZone2.getHint()==null) str2 = ""; else str2 = txtZone2.getHint().toString();
            if(txtZone3.getHint()==null) str3 = ""; else str3 = txtZone3.getHint().toString();
            if(txtTarget.getHint()==null) {
                dialog.setTitle("Target Color is Missing").
                        setMessage("Please Read Target Color.").
                        setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                dialog.show();

            } else {
                str4 = txtTarget.getHint().toString();
                restServer.getRecipe(str1, str2, str3, str4);
            }
        }
    };

    private void setButtonStates(Boolean states) {

        btnZone1.setClickable(states);
        btnZone2.setClickable(states);
        btnZone3.setClickable(states);
        btnTarget.setClickable(states);

        btnZone1.setEnabled(states);
        btnZone2.setEnabled(states);
        btnZone3.setEnabled(states);
        btnTarget.setEnabled(states);

        if (states) {
            btnZone1.setBackground(null);
            btnZone2.setBackground(null);
            btnZone3.setBackground(null);
            btnTarget.setBackground(null);
        } else {
            btnZone1.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGrey));
            btnZone2.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGrey));
            btnZone3.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGrey));
            btnTarget.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGrey));
        }
    }


    public void setButtonStatus(int status) {

        switch(status) {
            case 0 : // Disconnected
                btnBLE.setImageResource(R.drawable.bt_active);
                btnBLE.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorGrey)));
                setButtonStates(false);
                break;
            case 1 : // Connected
                btnBLE.setImageResource(R.drawable.bt_active);
                btnBLE.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue)));
                setButtonStates(true);
                break;
            case 2 : // Discovered
                btnBLE.setImageResource(R.drawable.bt_active);
                btnBLE.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue)));break;
            case 3: // Data available
                btnBLE.setImageResource(R.drawable.bt_active);
                btnBLE.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue)));
                break;
            default:setButtonStates(false);
                btnBLE.setImageResource(R.drawable.bt_active);
                btnBLE.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorGrey)));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        context.registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        context.unregisterReceiver(gattUpdateReceiver);
    }

    @Override
    public void onPause() {
        super.onPause();
        context.unregisterReceiver(gattUpdateReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unbindService(serviceConnection);
        bluetoothLeService = null;
    }

    private static IntentFilter makeGattUpdateIntentFilter() {

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public void decode(String str){
        String cod = str.substring(str.indexOf("cmd") + 3, str.indexOf("pow"));
        if(cod.equals("57")) {
            String bat = str.substring(str.indexOf("pow")+3, str.indexOf("|"));
            setBatteryLevel(bat);
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

        String cmd = str.substring(str.indexOf("cmd")+3, str.indexOf("pow"));
        String pwr = str.substring(str.indexOf("pow")+3, str.indexOf("|"));

        int pow = Integer.valueOf(pwr); // 400
        String power = String.valueOf(100*(pow-340) / (420-340)) + "%";
        txtBattery.setText(power);
        if(pow<50)
            imgBattery.setBackgroundColor(Color.RED);
        else
            imgBattery.setBackgroundColor(Color.BLUE);

        String company = spCompanies.getSelectedItem().toString();
        if(!silent) lastSound.start();

        String zone    = "1";
        String catalog = lblZone1.getText().toString();

        if(cmd.equals("49")) { zone = "1"; catalog = lblZone1.getText().toString(); }
        if(cmd.equals("50")) { zone = "2"; catalog = lblZone2.getText().toString(); }
        if(cmd.equals("51")) { zone = "3"; catalog = lblZone3.getText().toString(); }
        if(cmd.equals("52")) { zone = "4"; catalog = lblTarget.getText().toString(); }

        restServer.getColor3(r_r, r_g, r_b, r_c, g_r, g_g, g_b, g_c, b_r, b_g, b_b, b_c, a_r, a_g, a_b, a_c, company, catalog, zone, pow);

        Log.d(LOG_TAG, "Step data here");
    }





    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvents.onGetColor event) {
        String zone = event.zone;
        String color1="", delta1;
        String color2="", delta2;
        String str1 = "", str2 = "";

        try {
            JSONArray data = event.data;
            JSONObject inner = new JSONObject(data.get(0).toString());

            //company1 = inner.getString("cn_company");
            //category1 = inner.getString("cn_category");
            //series1 = inner.getString("cn_series");
            color1 = inner.getString("cn_color");
            delta1 = inner.getString("delta");
            if(delta1.equals("null")) delta1 = "-";
            //cn_id1 = inner.getString("cn_id");
            str1 = color1 + "("+ delta1+")";

            inner = new JSONObject(data.get(1).toString());
            //company2 = inner.getString("cn_company");
            //category2 = inner.getString("cn_category");
            //series2 = inner.getString("cn_series");
            color2 = inner.getString("cn_color");
            delta2 = inner.getString("delta");
            if(delta2.equals("null")) delta2 = "-";
            //cn_id2 = inner.getString("cn_id");
            str2 = color2 + "("+ delta2+")";


        }catch (JSONException e) {
            e.printStackTrace();

        }

        Toast.makeText(context, color1+color2, Toast.LENGTH_LONG).show();
        switch (zone) {
            case "1" : txtZone1.setText(str1); txtZone1_1.setText(str2); txtZone1.setHint(color1); break;
            case "2" : txtZone2.setText(str1); txtZone2_1.setText(str2); txtZone2.setHint(color1); break;
            case "3" : txtZone3.setText(str1); txtZone3_1.setText(str2); txtZone3.setHint(color1); break;
            case "4" : txtTarget.setText(str1); txtTarget_1.setText(str2); txtTarget.setHint(color1); break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvents.onGetRecipe event) {
        Toast.makeText(context, event.recipe, Toast.LENGTH_LONG).show();
        txtRecipe.setText(event.recipe);

    }

}
