package bitman.ay27.watchdog.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import bitman.ay27.watchdog.PrefUtils;
import bitman.ay27.watchdog.net.NetManager;
import bitman.ay27.watchdog.ui.KeyguardManager;
import bitman.ay27.watchdog.utils.SuperUserAccess;
import bitman.ay27.watchdog.watchlink.DogWatchService;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-13.
 */
public class DogWatchReceiver extends BroadcastReceiver {
    private static final String TAG = "DogWatchReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(DogWatchService.ACTION_REP_GATT_CONNECTED)) {

        } else if (action.equals(DogWatchService.ACTION_REP_GATT_CONNECTED_FAIL)) {

        } else if (action.equals(DogWatchService.ACTION_REP_GATT_DISCONNECTED)) {

        } else if (action.equals(DogWatchService.ACTION_REP_GATT_DISCONNECTED_FAIL)) {

        } else if (action.equals(DogWatchService.ACTION_REP_RSSI_OUT_OF_RANGE)) {
            Log.i(TAG, "out of range");
            PrefUtils.setPhoneSafety(false);
            new KeyguardManager(context).launchKeyguard();
            SuperUserAccess.runCmd("echo 0 > /sys/devices/virtual/android_usb/android0/enable");
            // TODO open internet
            // TODO close flash locker
            NetManager.state("danger");
        } else if (action.equals(DogWatchService.ACTION_REP_RSSI_RETURN_RANGE)) {
            Log.i(TAG, "return range");
            SuperUserAccess.runCmd("echo 1 > /sys/devices/virtual/android_usb/android0/enable");
            NetManager.state("save");
        }
    }
}
