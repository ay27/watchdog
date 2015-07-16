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
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class DogWatchService extends Service {
    private final static String TAG = DogWatchService.class.getSimpleName();
    private boolean binded = false;
    private RSSIdeamon mRSSId;

    public class LocalBinder extends Binder {
        public DogWatchService getService() {
            return DogWatchService.this;
        }
    }

    public final static int CHARA_INVALID                   = -1;   // Represent an invalid characteristic
    public final static int CHARA_BATT_RAMAIN               = 0x0;  // R uint8
    public final static int CHARA_TIME_UTC                  = 0x1;  // RW uint32
    public final static int CHARA_RF_CALIBRATE              = 0x2;  // RW int8
    public final static int CHARA_RF_TXLEVEL                = 0x3;  // RW uint8
    public final static int CHARA_VIBRATE_TRIGGER           = 0x4;  //  W uint8
    public final static int CHARA_DISCONNECT_ALARM_SWITCH   = 0x5;  //  W uint8 //TODO: NOT USE NOW

    public static final int VIBRATE_STOP = 0x0;
    public static final int VIBRATE_NFC = 0x1;
    public static final int VIBRATE_OUT_OF_RANGE = 0x2;
    public static final int VIBRATE_FINDING = 0x3;

    public final static String ACTION_REP_GATT_CONNECTED = "org.bitman.bluetooth.le.ACTION_REP_GATT_CONNECTED";
    public final static String ACTION_REP_GATT_CONNECTED_FAIL = "org.bitman.bluetooth.le.ACTION_REP_GATT_CONNECTED_FAIL";
    public final static String ACTION_REP_GATT_DISCONNECTED = "org.bitman.bluetooth.le.ACTION_REP_GATT_DISCONNECTED";
    public final static String ACTION_REP_GATT_DISCONNECTED_FAIL = "org.bitman.bluetooth.le.ACTION_REP_GATT_DISCONNECTED_FAIL";
    public final static String ACTION_REP_RSSI_OUT_OF_RANGE = "org.bitman.bluetooth.le.ACTION_REP_RSSI_OUT_OF_RANGE";
    public final static String ACTION_REP_RSSI_RETURN_RANGE = "org.bitman.bluetooth.le.ACTION_REP_RSSI_RETURN_RANGE";

    public final static String ACTION_REP_DATA_NOFIFY = "org.bitman.bluetooth.le.ACTION_REP_DATA_NOFIFY"; // TODO: NOT USE NOW

    public final static String EXTRA_REP_FAIL_INFO = "org.bitman.bluetooth.le.EXTRA_REP_FAIL_INFO";
    public final static String EXTRA_REP_DATA_CHAR_UUID = "org.bitman.bluetooth.le.EXTRA_REP_DATA_CHAR_UUID";
    public final static String EXTRA_REP_DATA_RAW = "org.bitman.bluetooth.le.EXTRA_REP_DATA_RAW";


    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

//    public final static String ACTION_SERVICE_INITIALIZE_FAIL = "org.bitman.bluetooth.le.ACTION_SERVICE_INITIALIZE_FAIL";
//    public final static String ACTION_SERVICE_INITIALIZED = "org.bitman.bluetooth.le.ACTION_SERVICE_INITIALIZED";
//    public final static String ACTION_SERVICE_DESTORYED = "org.bitman.bluetooth.le.ACTION_SERVICE_DESTORYED";


    private final static ServiceInfo mWatchServices = new ServiceInfo();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private DogWatchCallback mAppNotifyCallback;

    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;
    private boolean isAutoConnectionOn = false;

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                if (gatt.getDevice().getBondState() != BluetoothDevice.BOND_BONDED) {

                    final String eInfo = "Connected to a unbonded device, disconnect.";
                    Log.w(TAG, eInfo);
                    if (mAppNotifyCallback != null)
                        mAppNotifyCallback.onConnectedFail(eInfo);
                    broadcastFail(ACTION_REP_GATT_CONNECTED_FAIL, eInfo);

                    disconnect();
                    return;
                }

                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                stopRSSId();
                if (mAppNotifyCallback != null)
                    mAppNotifyCallback.onDisconnected();
                broadcastUpdate(ACTION_REP_GATT_DISCONNECTED);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (!isDogWatch(gatt)) {
                    final String eInfo = "This device is not a DogWatch, disconnect.";
                    Log.w(TAG, eInfo);
                    if (mAppNotifyCallback != null)
                        mAppNotifyCallback.onConnectedFail(eInfo);
                    broadcastFail(ACTION_REP_GATT_CONNECTED_FAIL, eInfo);
                }

                startRSSId();

                ServiceInfo.ResloveResult srv_rf_cabli = mWatchServices.resloveName(CHARA_RF_CALIBRATE);
                readCharacteristic(gatt.getService(srv_rf_cabli.serviceUUID).getCharacteristic(srv_rf_cabli.characterUUID)); // Update Calibrate Power

                mConnectionState = STATE_CONNECTED;
                if (mAppNotifyCallback != null)
                    mAppNotifyCallback.onConnected(gatt.getDevice().getAddress(), isAutoConnectionOn);
                broadcastUpdate(ACTION_REP_GATT_CONNECTED);
            } else {
                final String eInfo = "Discovering services fail.";
                Log.w(TAG, eInfo);
                if (mAppNotifyCallback != null)
                    mAppNotifyCallback.onConnectedFail(eInfo);
                broadcastFail(ACTION_REP_GATT_CONNECTED_FAIL, eInfo);

                disconnect();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            int charaName = mWatchServices.resloveUUID(characteristic.getUuid());
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // get operate finished, pack the data and notify
                if (charaName == CHARA_RF_CALIBRATE) {
                    mCalibrateTxPower = characteristic.getValue()[0];
                }

                if (mAppNotifyCallback != null)
                    mAppNotifyCallback.onGetFinish(charaName, characteristic.getUuid(), characteristic.getValue());
            } else {
                final String eInfo = "Get fail, name: " + charaName + ", UUID:" + characteristic.getUuid() + ", status == " + status;
                Log.w(TAG, eInfo);
                if (mAppNotifyCallback != null)
                    mAppNotifyCallback.onGetFail(eInfo);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            // post operate finished, pack the data and notify
            int charaName = mWatchServices.resloveUUID(characteristic.getUuid());
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (mAppNotifyCallback != null)
                    mAppNotifyCallback.onPostFinish(charaName, characteristic.getUuid(), characteristic.getValue());
            } else {
                final String eInfo = "Post fail, name: " + charaName + ", UUID:" + characteristic.getUuid() + ", status == " + status;
                Log.w(TAG, eInfo);
                if (mAppNotifyCallback != null)
                    mAppNotifyCallback.onPostFail(eInfo);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            //TODO: not used now
            broadcastUpdate(ACTION_REP_DATA_NOFIFY, characteristic);
        }

        @Override
        public void onReadRemoteRssi(android.bluetooth.BluetoothGatt gatt, int rssi, int status) {
            updateAvgRssi(rssi);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mRSSId = new RSSIdeamon();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        close();
        mRSSId.terminate();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        synchronized (this) {
            if (!binded) {
                binded = true;
                return mBinder;
            } else {
                return null;
            }
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        // close();
        binded = false;
        mAppNotifyCallback = null;
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize(DogWatchCallback callback) {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mAppNotifyCallback == null) {
            mAppNotifyCallback = callback;
        }

        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                //broadcastFail(ACTION_SERVICE_INITIALIZE_FAIL, "Unable to initialize BluetoothManager.");
                return false;

            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            //broadcastFail(ACTION_SERVICE_INITIALIZE_FAIL, "Unable to initialize BluetoothManager.");
            return false;
        }

        //broadcastUpdate(ACTION_SERVICE_INITIALIZED);
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
    public boolean connect(final String address, boolean autocon) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            if (mAppNotifyCallback != null)
                mAppNotifyCallback.onConnectedFail("BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null
                && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {

            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                // Trying to use an existing mBluetoothGatt for connection success.
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                Log.w(TAG, "Trying to use an existing mBluetoothGatt for connection.");
                if (mAppNotifyCallback != null)
                    mAppNotifyCallback.onConnectedFail("Trying to use an existing mBluetoothGatt for connection fail.");
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
            Log.w(TAG, "Trying connect to a unbonded device, refused.");
            if (mAppNotifyCallback != null)
                mAppNotifyCallback.onConnectedFail("Trying connect to a unbonded device, refused.");
        }

        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            if (mAppNotifyCallback != null)
                mAppNotifyCallback.onConnectedFail("Device not found.  Unable to connect.");
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
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            final String eInfo = "BluetoothAdapter not initialized";
            Log.w(TAG, eInfo);
            if (mAppNotifyCallback != null)
                mAppNotifyCallback.onDisconnectFail(eInfo);
            broadcastFail(ACTION_REP_GATT_DISCONNECTED_FAIL, eInfo);
            return;
        }
        stopRSSId();
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

    private void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.writeCharacteristic(characteristic);
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


    public boolean post(int name, byte[] val) {
        // TODO: process push request there
//        Log.i(TAG, "byte[] = "+val[0]+" "+ val[1]+" "+val[2]+" "+val[3]);
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            final String eInfo = "BluetoothAdapter not initialized";
            Log.w(TAG, eInfo);
            if (mAppNotifyCallback != null)
                mAppNotifyCallback.onPostFail(eInfo);
            return false;
        }
        ServiceInfo.ResloveResult resloveResult = mWatchServices.resloveName(name);
        BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(resloveResult.serviceUUID).getCharacteristic(resloveResult.characterUUID);
        characteristic.setValue(val);
        writeCharacteristic(characteristic);
        return true;
    }

    public boolean get(int name) {
        // TODO: process get request there
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            final String eInfo = "BluetoothAdapter not initialized";
            Log.w(TAG, eInfo);
            if (mAppNotifyCallback != null)
                mAppNotifyCallback.onGetFail(eInfo);
            return false;
        }

        ServiceInfo.ResloveResult resloveResult = mWatchServices.resloveName(name);
        BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(resloveResult.serviceUUID).getCharacteristic(resloveResult.characterUUID);
        readCharacteristic(characteristic);
        return true;
    }

    /**
     * get connection info, if haven't connection, return null
     *
     * @return Device Address if connected, null if not connected
     */
    public String getConnectedAddress() {
        if (mConnectionState == STATE_CONNECTED)
            return mBluetoothDeviceAddress;
        else
            return null;
    }

    public int getConnectionState() {
        return mConnectionState;
    }

    public boolean isAutoConnectionOn() {
        return isAutoConnectionOn;
    }

    private double mDistThreahold = -1;
    private final static int RSSID_AVG_COUNT = 5;
    private final static int RSSID_AVG_UPDATE_INTERVAL = 500;
    private int cycBuffPtr = 0;
    private int[] cycBuff = new int[RSSID_AVG_COUNT];
    private boolean isOutOfRange;
    private int mCalibrateTxPower = -50;

    private final Runnable mRSSIdRunnable = new Runnable() {
        @Override
        public void run() {
            mBluetoothGatt.readRemoteRssi();
            mRSSId.controlHandler.postDelayed(this, RSSID_AVG_UPDATE_INTERVAL);
        }
    };

    /**
     * set the out of range distance
     *
     * @param threshold threshold distance, -1 for disable, >0 for enable notify
     */
    public void setOutOfRange(double threshold) {
        if (threshold > 0 || threshold == -1)
            mDistThreahold = threshold;
    }

    public void startRSSId() {
        cycBuffPtr = 0;
        isOutOfRange = false;
        for (int i = 0; i < cycBuff.length; ++i) {
            cycBuff[i] = 0;
        }
        mRSSId.controlHandler.postDelayed(mRSSIdRunnable, RSSID_AVG_UPDATE_INTERVAL);
    }

    public void stopRSSId() {
        mRSSId.controlHandler.removeCallbacks(mRSSIdRunnable);
    }

    public double getCurrentRSSI() {
        return cycBuff[cycBuffPtr];
    }

    public double getAvgRSSI() {
        double rssi = 0;
        for (int i : cycBuff) {
            rssi = rssi + i;
        }
        return rssi / RSSID_AVG_COUNT;
    }

    public double calcAccuracy() {
        return calculateAccuracy(mCalibrateTxPower, getAvgRSSI());
    }

    private static double calculateAccuracy(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return accuracy;
        }
    }


    private void updateAvgRssi(int newRssi) {
        cycBuff[cycBuffPtr] = newRssi;
        cycBuffPtr = (++cycBuffPtr) % RSSID_AVG_COUNT;

        if (mDistThreahold != -1) {
            double nowAccuracy = calcAccuracy();
            if (nowAccuracy > mDistThreahold) { // not in range
                if (!isOutOfRange) {
                    if (mAppNotifyCallback != null)
                        mAppNotifyCallback.onWatchOutOfRange(nowAccuracy);
                    broadcastUpdate(ACTION_REP_RSSI_OUT_OF_RANGE);
                    isOutOfRange = true;
                }
            } else { // in range
                if (isOutOfRange) {
                    if (mAppNotifyCallback != null)
                        mAppNotifyCallback.onWatchReturnRange(nowAccuracy);
                    broadcastUpdate(ACTION_REP_RSSI_RETURN_RANGE);
                    isOutOfRange = false;
                }
            }

        }
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

    private boolean isDogWatch(BluetoothGatt gatt) {
        if (mBluetoothGatt == null)
            return false;

        for (ServiceInfo.ServiceRecord serviceItem : mWatchServices.services) {
            final BluetoothGattService service = gatt.getService(serviceItem.service);
            if (service == null)
                return false;
            for (UUID charaItem : serviceItem.characteristics) {
                final BluetoothGattCharacteristic chara = service.getCharacteristic(charaItem);
                if (chara == null)
                    return false;
            }
        }
        return true;
    }
}
