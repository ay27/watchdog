package bitman.ay27.watchdog.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import bitman.ay27.watchdog.PrefUtils;
import bitman.ay27.watchdog.utils.SuperUserAccess;

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
            if (connected && PrefUtils.isAutoCloseUsb() && !PrefUtils.isPhoneSafe()) {
                SuperUserAccess.disableUsb();
                Log.i("usb state", "close connect");
            }
        }
    }
}
