package bitman.ay27.watchdog.service;

import android.content.*;
import android.util.Log;
import bitman.ay27.watchdog.PrefUtils;
import bitman.ay27.watchdog.utils.Common;
import bitman.s117.libwatchcat.WatchCat_Controller;
import bitman.s117.libwatchcat.WatchCat_Controller_Impl;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/24.
 */
public class UsbStatusReceiver extends BroadcastReceiver {

    private WatchCat_Controller wc_ctl = new WatchCat_Controller_Impl();
    private BroadcastReceiver unmountSuccessReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                wc_ctl.disableEncryption();
                wc_ctl.unloadBCPT();
            } catch (Exception e) {
                e.printStackTrace();
            }
            context.unregisterReceiver(unmountSuccessReceiver);
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_MEDIA_CHECKING)) {
            if (PrefUtils.getSdState() == 0 && wc_ctl.isSDCardExist()) {
                PrefUtils.setSdState(1);
                Log.i("UsbStatusReceiver", "load bcpt, status 1");
            }
        } else if (intent.getAction().equals(Intent.ACTION_MEDIA_REMOVED)) {
            context.sendBroadcast(new Intent(Common.ACTION_UNMOUNT));
            context.registerReceiver(unmountSuccessReceiver, new IntentFilter(Common.ACTION_UNMOUNT_SUCCESS));
        }
    }
}
