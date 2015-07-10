package bitman.ay27.watchdog.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import bitman.ay27.watchdog.WatchdogApplication;
import bitman.ay27.watchdog.ui.activity.MainActivity;
import bitman.ay27.watchdog.utils.UpgradeSystemPermission;

/**
 * Created by ay27 on 15-7-10.
 */
public class UsbStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("usb state", "receive message");
        if (intent.getAction().equals("android.hardware.usb.action.USB_STATE")) {
            boolean connected = intent.getExtras().getBoolean("connected");
            Log.i("usb state", "connected? " + connected);
            SharedPreferences pref = WatchdogApplication.getContext().getSharedPreferences(MainActivity.PREF_NAME, Context.MODE_PRIVATE);
            if (connected && !pref.getBoolean(MainActivity.KEY_USB, true)) {
                UpgradeSystemPermission.runCmd("echo 0 > /sys/devices/virtual/android_usb/android0/enable");
                Log.i("usb state", "close connect");
            }
        }
    }
}
