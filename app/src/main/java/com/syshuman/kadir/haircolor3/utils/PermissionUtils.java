package com.syshuman.kadir.haircolor3.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static android.support.v4.app.ActivityCompat.requestPermissions;


public class PermissionUtils {

    private static final int PERMISSION_ALL = 123;
    private AlertDialog dialog;

    private Activity activity;

    public PermissionUtils(Activity activity) {
        this.activity = activity;
        dialog = new AlertDialog.Builder(activity).create();
    }

    public void getPermissions(Map<String, Integer> permission) {

        int i = 0;
        String[] list = new String[permission.size()];
        for(String key : permission.keySet()) {
            if (activity.checkSelfPermission(key) != PackageManager.PERMISSION_GRANTED) {
                list[i++] = key;
            }
        }

        if(i>0) {
            requestPermissions(activity, Arrays.copyOfRange(list, 0, i), PERMISSION_ALL);
        }


        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(activity, "BLE is not supported", Toast.LENGTH_SHORT).show();
            dialog.setTitle("Functionality limited");
            dialog.setMessage("Since location access has not been granted, this app will not be able to discover MapHairColor device in the background.");
            //dialog.setPositiveButton(android.R.string.ok, null);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                }
            });
            dialog.show();
        }
    }


    public void deniedLocation() {
        dialog.setTitle("Functionality limited");
        dialog.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void deniedStorage() {
        dialog.setTitle("Functionality limited");
        dialog.setMessage("Since Storage access has not been granted, this app will not be able to use storage feature");
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void deniedCamera() {
        dialog.setTitle("Functionality limited");
        dialog.setMessage("Since Camera access has not been granted, this app will not be able to use camera feature");
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }



}
