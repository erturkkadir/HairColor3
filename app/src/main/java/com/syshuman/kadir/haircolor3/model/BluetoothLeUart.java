package com.syshuman.kadir.haircolor3.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.util.Log;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


public class BluetoothLeUart extends BluetoothGattCallback implements BluetoothAdapter.LeScanCallback { // UUIDs for UART service and associated characteristics.

    private static String LOG_TAG = "Adafruit";
    // UUIDs for UART service and associated characteristics.
    private static UUID UART_UUID      = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    private static UUID TX_UUID        = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    private static UUID RX_UUID        = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");

    // UUID for the UART BTLE client characteristic which is necessary for notifications.
    private static UUID CLIENT_UUID    = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB");

    // UUIDs for the Device Information service and associated characeristics.
    private static UUID DIS_UUID       = UUID.fromString("000001530-1212-EFDE-1523-785FEABCD123");
    private static UUID DIS_MANUF_UUID = UUID.fromString("00002A29-0000-1000-8000-00805F9B34FB");
    private static UUID DIS_MODEL_UUID = UUID.fromString("00002A24-0000-1000-8000-00805F9B34FB");
    private static UUID DIS_HWREV_UUID = UUID.fromString("00002A26-0000-1000-8000-00805F9B34FB");
    private static UUID DIS_SWREV_UUID = UUID.fromString("00002A28-0000-1000-8000-00805F9B34FB");

    // Internal UART state.
    private Context context;
    private WeakHashMap<Callback, Object> callbacks;
    private BluetoothAdapter adapter;
    private BluetoothGatt gatt;
    private BluetoothGattCharacteristic tx;
    private BluetoothGattCharacteristic rx;
    private boolean connectFirst;
    private boolean writeInProgress; // Flag to indicate a write is currently in progress

    // Device Information state.W
    private BluetoothGattCharacteristic disManuf;
    private BluetoothGattCharacteristic disModel;
    private BluetoothGattCharacteristic disHWRev;
    private BluetoothGattCharacteristic disSWRev;
    private boolean disAvailable;
    private Timer timer;

    // Queues for characteristic read (synchronous)
    private Queue<BluetoothGattCharacteristic> readQueue;

    // Interface for a BluetoothLeUart client to be notified of UART actions.
    public interface Callback {
        void onConnected(BluetoothLeUart uart);
        void onConnectFailed(BluetoothLeUart uart);
        void onDisconnected(BluetoothLeUart uart);
        void onReceive(BluetoothLeUart uart, BluetoothGattCharacteristic rx);
        void onDeviceFound(BluetoothDevice device);
        void onDeviceInfoAvailable();
    }

    public BluetoothLeUart(Context context) {
        super();
        this.context = context;
        this.callbacks = new WeakHashMap<>();
        this.adapter = BluetoothAdapter.getDefaultAdapter();
        this.gatt = null;
        this.tx = null;
        this.rx = null;
        this.disManuf = null;
        this.disModel = null;
        this.disHWRev = null;
        this.disSWRev = null;
        this.disAvailable = false;
        this.connectFirst = true;
        this.writeInProgress = false;
        this.readQueue = new ConcurrentLinkedQueue<>();
        this.timer = new Timer();
        Log.d(LOG_TAG, "Initialize BLE");
    }

    // Return instance of BluetoothGatt.

    public BluetoothGatt getGatt() {
        Log.d(LOG_TAG, gatt.toString());
        return gatt;
    }

    // Return true if connected to UART device, false otherwise.

    public boolean isConnected() {
        Log.d(LOG_TAG, "isConnected");
        return (tx != null && rx != null);
    }


    public String getDeviceInfo() {
        Log.d(LOG_TAG, "getDevice");
        if (tx == null ) {
            // Do nothing if there is no connection.
            Log.d(LOG_TAG, "null TX");
            return "";
        }
        String str1 = "Manufacturer : " + disManuf.getStringValue(0) + "\n";
        String str2 = "Model        : " + disModel.getStringValue(0) + "\n";
        String str3 = "Firmware     : " + disSWRev.getStringValue(0) + "\n";
        return str1+str2+str3;
    }


    private boolean deviceInfoAvailable() {
        Log.d(LOG_TAG, "deviceInfoAv");
        return disAvailable;
    }


    // Send data to connected UART device.
    private void send(byte[] data) {
        Log.d(LOG_TAG, "send init");
        if (tx == null || data == null || data.length == 0) {
            // Do nothing if there is no connection or message to send.
            return;
        }
        // Update TX characteristic value.  Note the setValue overload that takes a byte array must be used.
        tx.setValue(data);
        writeInProgress = true; // Set the write in progress flag
        Log.d(LOG_TAG, "Before writeCharacteristic");
        gatt.writeCharacteristic(tx);
        Log.d(LOG_TAG, "before while");
/*
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                writeInProgress = false;
                Log.d("Debug", "Timeout happened...");
            }
        }, 5000, 5000);

*/

        while (writeInProgress) {
            // Wait for the flag to clear in onCharacteristicWrite
        }

        Log.d(LOG_TAG, "After while");
    }

    // Send data to connected UART device.
    public void send(String data) {
        if (data != null && !data.isEmpty()) {
            Log.d(LOG_TAG, "in the send 1");
            send(data.getBytes(Charset.forName("UTF-8")));
        } else {
            Log.d(LOG_TAG, "out of the send 1");
        }
    }

    // Register the specified callback to receive UART callbacks.
    public void registerCallback(Callback callback) {
        callbacks.put(callback, null);
    }

    // Unregister the specified callback.
    public void unregisterCallback(Callback callback) {
        callbacks.remove(callback);
    }

    // Disconnect to a device if currently connected.
    public void disconnect() {
        if (gatt != null) {
            gatt.disconnect();
        }
        gatt = null;
        tx = null;
        rx = null;
        Log.d(LOG_TAG, "Disconnect");
    }

    // Stop any in progress UART device scan.
    private void stopScan() {
        if (adapter != null) {
            adapter.stopLeScan(this);
        }
        Log.d(LOG_TAG, "Stop Scan");
    }

    // Start scanning for BLE UART devices.  Registered callback's onDeviceFound method will be called
    // when devices are found during scanning.
    private void startScan() {
        if (adapter != null) {
            adapter.startLeScan(this);
        }
        Log.d(LOG_TAG, "start Scan");
    }

    // Connect to the first available UART device.
    public void connectFirstAvailable() {

        disconnect();
        stopScan();  // Start scan and connect to first available device.
        connectFirst = true;
        startScan();
    }

    // Handlers for BluetoothGatt and LeScan events.
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        if (newState == BluetoothGatt.STATE_CONNECTED) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Connected to device, start discovering services.
                if (!gatt.discoverServices()) {
                    // Error starting service discovery.
                    connectFailure();
                }
            }
            else {
                // Error connecting to device.
                connectFailure();
            }
        }
        else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
            // Disconnected, notify callbacks of disconnection.
            rx = null;
            tx = null;
            notifyOnDisconnected(this);
        }
        Log.d(LOG_TAG, "ConnectionStateChanged");
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        // Notify connection failure if service discovery failed.
        if (status == BluetoothGatt.GATT_FAILURE) {
            connectFailure();
            return;
        }

        // Save reference to each UART characteristic.
        tx = gatt.getService(UART_UUID).getCharacteristic(TX_UUID);
        Log.d(LOG_TAG, "TX : " + tx.toString());
        rx = gatt.getService(UART_UUID).getCharacteristic(RX_UUID);
        Log.d(LOG_TAG, "TX : " + rx.toString());

        // Save reference to each DIS characteristic.

        disManuf = gatt.getService(DIS_UUID).getCharacteristic(DIS_MANUF_UUID);
        disModel = gatt.getService(DIS_UUID).getCharacteristic(DIS_MODEL_UUID);
        disHWRev = gatt.getService(DIS_UUID).getCharacteristic(DIS_HWREV_UUID);
        disSWRev = gatt.getService(DIS_UUID).getCharacteristic(DIS_SWREV_UUID);


        // Add device information characteristics to the read queue
        // These need to be queued because we have to wait for the response to the first
        // read request before a second one can be processed (which makes you wonder why they
        // implemented this with async logic to begin with???)
        try
        {
            readQueue.offer(disManuf);
            readQueue.offer(disModel);
            readQueue.offer(disHWRev);
            readQueue.offer(disSWRev);
        }catch (Exception e) {
            Log.d(LOG_TAG, "asdsad");
        }


        // Request a dummy read to get the device information queue going
        try {
            gatt.readCharacteristic(disManuf);
        } catch (Exception e) {
            Log.d(LOG_TAG,"unable to read Characteristic");
        }

        // Setup notifications on RX characteristic changes (i.e. data received).
        // First call setCharacteristicNotification to enable notification.
        if (!gatt.setCharacteristicNotification(rx, true)) {
            // Stop if the characteristic notification setup failed.
            Log.d(LOG_TAG, "GATT FAILED : ");
            connectFailure();
            return;
        }
        // Next update the RX characteristic's client descriptor to enable notifications.
        BluetoothGattDescriptor desc = rx.getDescriptor(CLIENT_UUID);
        if (desc == null) {
            // Stop if the RX characteristic has no client descriptor.
            Log.d(LOG_TAG, "CLIENT UUID not exist" );
            connectFailure();
            return;
        }
        desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        if (!gatt.writeDescriptor(desc)) {
            // Stop if the client descriptor could not be written.
            Log.d(LOG_TAG, "Client Desc could not be written" );
            connectFailure();
            return;
        }
        // Notify of connection completion.
        notifyOnConnected(this);
        Log.d(LOG_TAG, "All well");
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        notifyOnReceive(this, characteristic);
        Log.d(LOG_TAG, "onCharacteristicChanged");
    }

    @Override
    public void onCharacteristicRead (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);

        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.d(LOG_TAG, "Char Step 1 : " + characteristic.getStringValue(0));
            // Check if there is anything left in the queue
            BluetoothGattCharacteristic nextRequest = readQueue.poll();
            if(nextRequest != null){
                // Send a read request for the next item in the queue
                gatt.readCharacteristic(nextRequest);
            }
            else {
                // We've reached the end of the queue
                disAvailable = true;
                notifyOnDeviceInfoAvailable();
            }
        }
        else {
            Log.d(LOG_TAG, "Failed reading characteristic " + characteristic.getUuid().toString());
        }
        Log.d(LOG_TAG, "onCharacteristicRead  : " + characteristic.getStringValue(0));
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.d(LOG_TAG, "Chr write: " );
        super.onCharacteristicWrite(gatt, characteristic, status);

        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.d(LOG_TAG,"Characteristic write successful");

        }
        writeInProgress = false;
        Log.d(LOG_TAG, "onCharacteristicWrite");

    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        // Stop if the device doesn't have the UART service.
        if (!parseUUIDs(scanRecord).contains(UART_UUID)) {
            return;
        }
        // Notify registered callbacks of found device.
        notifyOnDeviceFound(device);
        // Connect to first found device if required.
        if (connectFirst) {
            // Stop scanning for devices.
            stopScan();
            // Prevent connections to future found devices.
            connectFirst = false;
            // Connect to device.
            gatt = device.connectGatt(context, true, this);
        }
        Log.d(LOG_TAG, "onLeScan");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Private functions to simplify the notification of all callbacks of a certain event.
    private void notifyOnConnected(BluetoothLeUart uart) {
        for (Callback cb : callbacks.keySet()) {
            if (cb != null) {
                cb.onConnected(uart);
            }
        }
        Log.d(LOG_TAG, "onNotifyOnConnected");
    }

    private void notifyOnConnectFailed(BluetoothLeUart uart) {
        for (Callback cb : callbacks.keySet()) {
            if (cb != null) {
                cb.onConnectFailed(uart);
            }
        }
        Log.d(LOG_TAG, "NotifyOnConnectionFailed");
    }

    private void notifyOnDisconnected(BluetoothLeUart uart) {
        for (Callback cb : callbacks.keySet()) {
            if (cb != null) {
                cb.onDisconnected(uart);
            }
        }
        Log.d(LOG_TAG, "NotifyOnDisconnected");
    }

    private void notifyOnReceive(BluetoothLeUart uart, BluetoothGattCharacteristic rx) {
        for (Callback cb : callbacks.keySet()) {
            if (cb != null ) {
                cb.onReceive(uart, rx);
            }
        }
        Log.d(LOG_TAG, "NotifyOnReceived");
    }

    private void notifyOnDeviceFound(BluetoothDevice device) {
        for (Callback cb : callbacks.keySet()) {
            if (cb != null) {
                cb.onDeviceFound(device);
            }
        }
        Log.d(LOG_TAG, "notifyOnDeviceFound");
    }

    private void notifyOnDeviceInfoAvailable() {
        for (Callback cb : callbacks.keySet()) {
            if (cb != null) {
                cb.onDeviceInfoAvailable();
            }
        }
        Log.d(LOG_TAG, "notifyOnDeviceInfoAvailable");
    }

    // Notify callbacks of connection failure, and reset connection state.
    private void connectFailure() {
        rx = null;
        tx = null;
        notifyOnConnectFailed(this);
        Log.d(LOG_TAG, "connectFailure");
    }

    // Filtering by custom UUID is broken in Android 4.3 and 4.4, see:
    //   http://stackoverflow.com/questions/18019161/startlescan-with-128-bit-uuids-doesnt-work-on-native-android-ble-implementation?noredirect=1#comment27879874_18019161
    // This is a workaround function from the SO thread to manually parse advertisement data.
    private List<UUID> parseUUIDs(final byte[] advertisedData) {
        List<UUID> uuids = new ArrayList<>();

        int offset = 0;
        while (offset < (advertisedData.length - 2)) {
            int len = advertisedData[offset++];
            if (len == 0)
                break;

            int type = advertisedData[offset++];
            switch (type) {
                case 0x02: // Partial list of 16-bit UUIDs
                case 0x03: // Complete list of 16-bit UUIDs
                    while (len > 1) {
                        int uuid16 = advertisedData[offset++];
                        uuid16 += (advertisedData[offset++] << 8);
                        len -= 2;
                        uuids.add(UUID.fromString(String.format("%08x-0000-1000-8000-00805f9b34fb", uuid16)));
                    }
                    break;
                case 0x06:// Partial list of 128-bit UUIDs
                case 0x07:// Complete list of 128-bit UUIDs
                    // Loop through the advertised 128-bit UUID's.
                    while (len >= 16) {
                        try {
                            // Wrap the advertised bits and order them.
                            ByteBuffer buffer = ByteBuffer.wrap(advertisedData, offset++, 16).order(ByteOrder.LITTLE_ENDIAN);
                            long mostSignificantBit = buffer.getLong();
                            long leastSignificantBit = buffer.getLong();
                            uuids.add(new UUID(leastSignificantBit,
                                    mostSignificantBit));
                        } catch (IndexOutOfBoundsException e) {
                            // Defensive programming.
                            Log.d(LOG_TAG, "Here : "+e.toString());
                            //continue;
                        } finally {
                            // Move the offset to read the next uuid.
                            offset += 15;
                            len -= 16;
                        }
                    }
                    break;
                default:
                    offset += (len - 1);
                    break;
            }
        }
        return uuids;
    }
}