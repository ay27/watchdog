package bitman.ay27.watchdog.watchlink;

import android.content.IntentFilter;

/**
 * Created by Spartan on 2015/7/11.
 */
public class BluetoothLeServiceUtils {

    public static IntentFilter makeResponseIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_REP_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_REP_GATT_CONNECTED_FAIL);
        intentFilter.addAction(BluetoothLeService.ACTION_REP_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_REP_GATT_DISCONNECTED_FAIL);
        intentFilter.addAction(BluetoothLeService.ACTION_REP_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_REP_RSSI_TASK_STARTED);
        intentFilter.addAction(BluetoothLeService.ACTION_REP_RSSI_TASK_STOPED);
        intentFilter.addAction(BluetoothLeService.ACTION_REP_RSSI_OUT_OF_RANGE);
        intentFilter.addAction(BluetoothLeService.ACTION_REP_DATA_PUSH_FINISH);
        intentFilter.addAction(BluetoothLeService.ACTION_REP_DATA_PUSH_FAIL);
        intentFilter.addAction(BluetoothLeService.ACTION_REP_DATA_GET_FINISH);
        intentFilter.addAction(BluetoothLeService.ACTION_REP_DATA_GET_FAIL);
        intentFilter.addAction(BluetoothLeService.ACTION_REP_DATA_NOFIFY);
        intentFilter.addAction(BluetoothLeService.ACTION_SERVICE_INITIALIZE_FAIL);
        intentFilter.addAction(BluetoothLeService.ACTION_SERVICE_INITIALIZED);
        intentFilter.addAction(BluetoothLeService.ACTION_SERVICE_DESTORYED);
        return intentFilter;
    }
}
