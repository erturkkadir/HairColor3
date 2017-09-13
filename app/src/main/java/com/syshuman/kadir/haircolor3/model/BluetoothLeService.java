package com.syshuman.kadir.haircolor3.model;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import static android.bluetooth.BluetoothProfile.STATE_CONNECTING;

public class BluetoothLeService extends Service {

    private final static String TAG = "Service";

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    public boolean isConnected = false;

    private BluetoothGattCharacteristic tx;
    private BluetoothGattCharacteristic rx;

    public static String ACTION_GATT_CONNECTED              = "com.syshuman.kadir.haircolor3.model.extra.ACTION_GATT_CONNECTED";
    public static String ACTION_GATT_DISCONNECTED           = "com.syshuman.kadir.haircolor3.model.extra.ACTION_GATT_DISCONNECTED";
    public static String ACTION_GATT_SERVICES_DISCOVERED    = "com.syshuman.kadir.haircolor3.model.extra.ACTION_GATT_SERVICES_DISCOVERED";
    public static String ACTION_DATA_AVAILABLE              = "com.syshuman.kadir.haircolor3.model.extra.ACTION_DATA_AVAILABLE";
    public static String EXTRA_DATA                         = "com.syshuman.kadir.haircolor3.model.extra.EXTRA_DATA";

    public UUID UART_UUID                                   = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    public UUID TX_UUID                                     = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    public UUID RX_UUID                                     = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
    public UUID CLIENT_UUID                                 = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB");

    private String readStr ="";

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        close();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    private void connectFailure(String str) {
        Log.d(TAG, str);
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    gatt.discoverServices();
                    broadcastUpdate(ACTION_GATT_CONNECTED);
                    Log.d(TAG, "Connected from "+ status + " to" + newState);
                    isConnected = true;
                } else {
                    connectFailure("Connected but no Gatt found");
                    isConnected = false;
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                isConnected = false;
                rx = null;
                tx = null;
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
                Log.d(TAG, "Connection Status Changed from "+ status + " to" + newState);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

            if(status != BluetoothGatt.GATT_SUCCESS) {
                connectFailure("Gatt Failure");
                Log.d(TAG, "GATT Failure "+ status);
                return;
            }
            tx = gatt.getService(UART_UUID).getCharacteristic(TX_UUID);
            rx = gatt.getService(UART_UUID).getCharacteristic(RX_UUID);

            Log.d(TAG, "tx discovered "+ tx.toString());
            Log.d(TAG, "rx discovered "+ rx.toString());

            if (!gatt.setCharacteristicNotification(rx, true)) {
                connectFailure("SET Characteristic  FAILED for rx: ");
                return;
            }

            BluetoothGattDescriptor desc = rx.getDescriptor(CLIENT_UUID);
            if (desc == null) {
                connectFailure("RX not exist" );
                return;
            }
            Log.d(TAG, "desc" + desc.getCharacteristic().toString());

            desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

            if (!gatt.writeDescriptor(desc)) {
                connectFailure("Client Desc could not be written" );
                return;
            }

            broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            Log.d(TAG, "Service Discovered");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,  BluetoothGattCharacteristic characteristic) {

            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }
    };

    private void send(byte[] data) {
        if (tx == null || data == null || data.length == 0) return;
        tx.setValue(data);
        mBluetoothGatt.writeCharacteristic(tx);
    }

    public void send(String data) {
        if (data != null && !data.isEmpty()) {
            send(data.getBytes(Charset.forName("UTF-8")));
        } else {
            Log.d(TAG, "out of the send 1");
        }
    }

    private void broadcastUpdate(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(String action,  BluetoothGattCharacteristic characteristic) {
        Intent intent = new Intent(action);
        String msg = characteristic.getStringValue(0);

        Log.d(TAG, msg);
        if (msg.indexOf('|') > 0) {
            readStr = readStr + msg;
            intent.putExtra(EXTRA_DATA, readStr);
            sendBroadcast(intent);
            readStr = "";
        } else {
            readStr = readStr + msg;
        }
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    public boolean initialize() {

        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.d(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        Log.d(TAG, "initialized BluetoothManager.");

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        Log.d(TAG, "BluetoothManager obtained.");

        return true;
    }

    public boolean connect(String address) {

        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
            if (mBluetoothGatt.connect()) {
            } else {
                return false;
            }
        }

        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.d(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        Log.d(TAG, "Device found.");

        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        mBluetoothDeviceAddress = address;
        //mConnectionState = STATE_CONNECTING;

        return true;
    }

    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    public void close() {
        if (mBluetoothGatt == null) return;
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }
}