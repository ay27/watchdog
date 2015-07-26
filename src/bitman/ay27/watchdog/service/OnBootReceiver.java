package bitman.ay27.watchdog.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import bitman.ay27.watchdog.PrefUtils;
import bitman.ay27.watchdog.ui.new_activity.lock.KeyguardManager;
import bitman.s117.libwatchcat.WatchCat_Controller;
import bitman.s117.libwatchcat.WatchCat_Controller_Impl;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/3/27.
 */
public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean lock = PrefUtils.isBootloaderEnable();

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

        int sd_status = PrefUtils.getSdState();
        if (sd_status == 2 && wc_ctl.isSDCardExist()) {
            wc_ctl.loadBCPT();
            wc_ctl.enableEncryption(PrefUtils.getSdPasswd(), PrefUtils.getSdEncryptType());
        }
        if (!wc_ctl.isSDCardExist()) {
            PrefUtils.setSdState(0);
        }

        Intent newIntent = new Intent(context, DaemonService.class);
//        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(newIntent);

        KeyguardManager manager = new KeyguardManager(context);
        manager.launchKeyguard();
    }
}
