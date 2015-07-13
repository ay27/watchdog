package bitman.ay27.watchdog.watchlink;

import android.content.IntentFilter;

/**
 * Created by Spartan on 2015/7/11.
 */
public class DogWatchServiceUtils {

    public static IntentFilter makeResponseIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DogWatchService.ACTION_REP_GATT_CONNECTED );
        intentFilter.addAction(DogWatchService.ACTION_REP_GATT_CONNECTED_FAIL );
        intentFilter.addAction(DogWatchService.ACTION_REP_GATT_DISCONNECTED );
        intentFilter.addAction(DogWatchService.ACTION_REP_GATT_DISCONNECTED_FAIL );
        intentFilter.addAction(DogWatchService.ACTION_REP_RSSI_OUT_OF_RANGE );
        intentFilter.addAction(DogWatchService.ACTION_REP_RSSI_RETURN_RANGE );
        return intentFilter;
    }
}
