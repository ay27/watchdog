package bitman.ay27.watchdog.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import bitman.ay27.watchdog.ui.activity.MainActivity;
import bitman.ay27.watchdog.utils.Common;
import bitman.s117.libwatchcat.WatchCat_Controller;
import bitman.s117.libwatchcat.WatchCat_Controller_Impl;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/24.
 */
public class UsbStatusReceiver extends BroadcastReceiver {

    private WatchCat_Controller wc_ctl = new WatchCat_Controller_Impl();
    private SharedPreferences pref;

    @Override
    public void onReceive(Context context, Intent intent) {
        pref = context.getSharedPreferences(MainActivity.PREF_NAME, Context.MODE_PRIVATE);
        if (intent.getAction().equals(Intent.ACTION_MEDIA_CHECKING)) {
            if (pref.getInt(MainActivity.KEY_SD_STATUS, 0) == 0 && wc_ctl.isSDCardExist()) {
                pref.edit().putInt(MainActivity.KEY_SD_STATUS, 1).apply();
                Log.i("UsbStatusReceiver", "load bcpt, status 1");
            }
        }
        else if (intent.getAction().equals(Intent.ACTION_MEDIA_REMOVED)) {
            context.sendBroadcast(new Intent(Common.ACTION_UNMOUNT));
            try {
                wc_ctl.disableEncryption();
                wc_ctl.unloadBCPT();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
