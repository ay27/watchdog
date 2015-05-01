package bitman.ay27.watchdog.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;
import bitman.ay27.watchdog.db.DbManager;
import bitman.ay27.watchdog.db.model.WatchCat;
import bitman.ay27.watchdog.ui.activity.MainActivity;
import bitman.s117.libwatchcat.WatchCat_Controller;
import bitman.s117.libwatchcat.WatchCat_Controller_Impl;

import java.io.IOException;
import java.util.List;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/3/27.
 */
public class OnBootReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences pref = context.getSharedPreferences(MainActivity.PREF_NAME, Context.MODE_PRIVATE);
        boolean lock = pref.getBoolean(MainActivity.KEY_BOOT_LOADER_LOCK, false);

        WatchCat_Controller wc_ctl = new WatchCat_Controller_Impl();
        if (lock) {
            try {
                wc_ctl.loadFsProtector();
                wc_ctl.enableBootloaderWriteProtect();
                wc_ctl.lockFlashLock();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int sd_status = pref.getInt(MainActivity.KEY_SD_STATUS, 0);
        if (sd_status == 2 && wc_ctl.isSDCardExist()) {
            wc_ctl.loadBCPT();
            wc_ctl.enableEncryption(pref.getString(MainActivity.KEY_SD_PASSWD,""), pref.getInt(MainActivity.KEY_ENCRYPT_TYPE, 0));
        }
        if (!wc_ctl.isSDCardExist()) {
            pref.edit().putInt(MainActivity.KEY_SD_STATUS, 0).apply();
        }

        Intent newIntent = new Intent(context, DaemonService.class);
//        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(newIntent);
    }
}
