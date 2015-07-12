/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package bitman.ay27.watchdog.watchlink;

import android.app.Service;
import android.bluetooth.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;
    private String currentAddress = null;
    private boolean isAutoConnectionOn = false;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_REQ_GATT_CONNECT = "org.bitman.bluetooth.le.ACTION_REQ_GATT_CONNECT";
    public final static String ACTION_REQ_GATT_DISCONNECT = "org.bitman.bluetooth.le.ACTION_REQ_GATT_DISCONNECT";
    public final static String ACTION_REQ_DATA_PUSH = "org.bitman.bluetooth.le.ACTION_REQ_DATA_PUSH";
    public final static String ACTION_REQ_DATA_GET = "org.bitman.bluetooth.le.ACTION_REQ_DATA_GET";
    public final static String ACTION_REQ_STOP_SERVICE = "org.bitman.bluetooth.le.ACTION_REQ_STOP_SERVICE";

    public final static String ACTION_REQ_RSSI_TASK_START = "org.bitman.bluetooth.le.ACTION_REQ_RSSI_TASK_START";
    public final static String ACTION_REQ_RSSI_TASK_STOP = "org.bitman.bluetooth.le.ACTION_REQ_RSSI_TASK_STOP";

    public final static String EXTRA_REQ_PARAM = "org.bitman.bluetooth.le.EXTRA_REQ_PARAM";

    public final static String ACTION_REP_GATT_CONNECTED = "org.bitman.bluetooth.le.ACTION_REP_GATT_CONNECTED";
    public final static String ACTION_REP_GATT_CONNECTED_FAIL = "org.bitman.bluetooth.le.ACTION_REP_GATT_CONNECTED_FAIL";
    public final static String ACTION_REP_GATT_DISCONNECTED = "org.bitman.bluetooth.le.ACTION_REP_GATT_DISCONNECTED";
    public final static String ACTION_REP_GATT_DISCONNECTED_FAIL = "org.bitman.bluetooth.le.ACTION_REP_GATT_DISCONNECTED_FAIL";
    public final static String ACTION_REP_GATT_SERVICES_DISCOVERED = "org.bitman.bluetooth.le.ACTION_REP_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_REP_RSSI_TASK_STARTED = "org.bitman.bluetooth.le.ACTION_REP_RSSI_TASK_STARTED";
    public final static String ACTION_REP_RSSI_TASK_STOPED = "org.bitman.bluetooth.le.ACTION_REP_RSSI_TASK_STOPED";
    public final static String ACTION_REP_RSSI_OUT_OF_RANGE = "org.bitman.bluetooth.le.ACTION_REP_RSSI_OUT_OF_RANGE";

    public final static String ACTION_REP_DATA_PUSH_FINISH = "org.bitman.bluetooth.le.ACTION_REP_DATA_PUSH_FINISH";
    public final static String ACTION_REP_DATA_PUSH_FAIL = "org.bitman.bluetooth.le.ACTION_REP_DATA_PUSH_FAIL";
    public final static String ACTION_REP_DATA_GET_FINISH = "org.bitman.bluetooth.le.ACTION_REP_DATA_GET_FINISH";
    public final static String ACTION_REP_DATA_GET_FAIL = "org.bitman.bluetooth.le.ACTION_REP_DATA_GET_FAIL";
    public final static String ACTION_REP_DATA_NOFIFY = "org.bitman.bluetooth.le.ACTION_REP_DATA_NOFIFY";

    public final static String EXTRA_REP_FAIL_INFO = "org.bitman.bluetooth.le.EXTRA_REP_FAIL_INFO";
    public final static String EXTRA_REP_DATA_CHAR = "org.bitman.bluetooth.le.EXTRA_REP_DATA_CHAR";
    public final static String EXTRA_REP_DATA_CHAR_UUID = "org.bitman.bluetooth.le.EXTRA_REP_DATA_CHAR_UUID";
    public final static String EXTRA_REP_DATA_LEN = "org.bitman.bluetooth.le.EXTRA_REP_DATA_LEN";
    public final static String EXTRA_REP_DATA_RAW = "org.bitman.bluetooth.le.EXTRA_REP_DATA_RAW";


    public final static String ACTION_SERVICE_INITIALIZE_FAIL = "org.bitman.bluetooth.le.ACTION_SERVICE_INITIALIZE_FAIL";
    public final static String ACTION_SERVICE_INITIALIZED = "org.bitman.bluetooth.le.ACTION_SERVICE_INITIALIZED";
    public final static String ACTION_SERVICE_DESTORYED = "org.bitman.bluetooth.le.ACTION_SERVICE_DESTORYED";

    // Use for receive incoming connection request
    private final BroadcastReceiver mRequestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_REQ_GATT_CONNECT)) {
                // handle connect request there
                final BluetoothLeServiceParamConnect paramConnect = intent.getParcelableExtra(EXTRA_REQ_PARAM);
                if (paramConnect == null) {
                    Log.e(TAG, "Request has no parameter.");
                    broadcastFail(ACTION_REP_GATT_CONNECTED_FAIL, "Request has no parameter.");
                    return;
                }
                Log.d(TAG, "Connect request received, address: " + paramConnect.address + "AutoConnect: " + paramConnect.autoConnect);
                connect(paramConnect.address, paramConnect.autoConnect == 1);
            } else if (action.equals(ACTION_REQ_GATT_DISCONNECT)) {
                // handle disconnect request there
                final BluetoothLeServiceParamDisconnect paramDisconnect = intent.getParcelableExtra(EXTRA_REQ_PARAM);
//                if(paramDisconnect == null){
//                    Log.e(TAG, "Request has no parameter.");
//                    broadcastFail(ACTION_REP_GATT_DISCONNECTED_FAIL, "Request has no parameter.");
//                    return;
//                }
                Log.d(TAG, "Disconnect request received");
                disconnect();
            } else if (action.equals(ACTION_REQ_DATA_PUSH)) {
                // handle push request there
                final BluetoothLeServiceParamPush paramPush = intent.getParcelableExtra(EXTRA_REQ_PARAM);
                if (paramPush == null) {
                    Log.e(TAG, "Request has no parameter.");
                    broadcastFail(ACTION_REP_DATA_PUSH_FAIL, "Request has no parameter.");
                    return;
                }
                processPushRequest(paramPush);
            } else if (action.equals(ACTION_REQ_DATA_GET)) {
                // handle get request there
                final BluetoothLeServiceParamGet paramGet = intent.getParcelableExtra(EXTRA_REQ_PARAM);
                if (paramGet == null) {
                    Log.e(TAG, "Request has no parameter.");
                    broadcastFail(ACTION_REP_DATA_GET_FAIL, "Request has no parameter.");
                    return;
                }
                processGetRequest(paramGet);
            } else if (action.equals(ACTION_REQ_STOP_SERVICE)) {
                Log.d(TAG, "Service stop request received.");
                BluetoothLeService.super.stopSelf();
            }
        }
    };

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(ACTION_REP_GATT_CONNECTED);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(ACTION_REP_GATT_DISCONNECTED);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_REP_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //TODO: get operate finished, pack the data and send broadcast
                broadcastUpdate(ACTION_REP_DATA_GET_FINISH, characteristic);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //TODO: push operate finished, check the push result and send broadcast
            broadcastUpdate(ACTION_REP_DATA_PUSH_FINISH, characteristic);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            //TODO: not used
            broadcastUpdate(ACTION_REP_DATA_NOFIFY, characteristic);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
        registerReceiver(mRequestReceiver, makeRequestIntentFilter());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        close();
        broadcastUpdate(ACTION_SERVICE_DESTORYED);
        unregisterReceiver(mRequestReceiver);

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        // close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                broadcastFail(ACTION_SERVICE_INITIALIZE_FAIL, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            broadcastFail(ACTION_SERVICE_INITIALIZE_FAIL, "Unable to initialize BluetoothManager.");
            return false;
        }

        broadcastUpdate(ACTION_SERVICE_INITIALIZED);
        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @param autocon Whether to directly connect to the remote device (false) or to automatically
     *                connect as soon as the remote device becomes available (true)
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    private boolean connect(final String address, boolean autocon) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            broadcastFail(ACTION_REP_GATT_CONNECTED_FAIL, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null
                && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {

            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                // Trying to use an existing mBluetoothGatt for connection success.
                broadcastUpdate(ACTION_REP_GATT_CONNECTED);
                return true;
            } else {
                Log.w(TAG, "Trying to use an existing mBluetoothGatt for connection.");
                broadcastFail(ACTION_REP_GATT_CONNECTED_FAIL, "Trying to use an existing mBluetoothGatt for connection fail.");
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            broadcastFail(ACTION_REP_GATT_CONNECTED_FAIL, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        isAutoConnectionOn = autocon;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    private void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            broadcastFail(ACTION_REP_GATT_DISCONNECTED_FAIL, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    private void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    private void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    private void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
    }


    private void processPushRequest(BluetoothLeServiceParamPush param) {
        // TODO: process push request there
    }

    private void processGetRequest(BluetoothLeServiceParamGet param) {
        // TODO: process get request there
    }

    /**
     * get connection info, if haven't connection, return null
     *
     * @return Device Address if connected, null if not connected
     */
    public String getConnectedAddress() {
        return currentAddress;
    }

    public boolean isAutoConnectionOn() {
        return isAutoConnectionOn;
    }

    public boolean isRSSITaskRunning() {
        return false;
    }

    public void setOutOfRange(double threshold) {

    }

    public double getCurrentRSSI() {
        return 0;
    }

    public double getAvgRSSI() {
        return 0;
    }

    public double calcAccuracy() {
        return 0;
    }

    private void updateAvgRssi(double newRssi) {

    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }

    private void broadcastFail(final String action, final String info) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_REP_FAIL_INFO, info);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_REP_DATA_CHAR_UUID, characteristic.getUuid().toString());

        // Always try to add the RAW value
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            intent.putExtra(EXTRA_REP_DATA_RAW, data);
        }

        sendBroadcast(intent);
    }

    private static IntentFilter makeRequestIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_REQ_GATT_CONNECT);
        intentFilter.addAction(BluetoothLeService.ACTION_REQ_GATT_DISCONNECT);
        intentFilter.addAction(BluetoothLeService.ACTION_REQ_DATA_PUSH);
        intentFilter.addAction(BluetoothLeService.ACTION_REQ_DATA_GET);
        intentFilter.addAction(BluetoothLeService.ACTION_REQ_STOP_SERVICE);
        intentFilter.addAction(BluetoothLeService.ACTION_REQ_RSSI_TASK_START);
        intentFilter.addAction(BluetoothLeService.ACTION_REQ_RSSI_TASK_STOP);
        return intentFilter;
    }
}