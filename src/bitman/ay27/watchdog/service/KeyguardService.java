package bitman.ay27.watchdog.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import bitman.ay27.watchdog.db.DbManager;
import bitman.ay27.watchdog.db.model.ServiceStatus;
import bitman.ay27.watchdog.ui.new_activity.lock.KeyguardManager;

import java.util.List;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/5.
 */
public class KeyguardService extends Service {

    private BroadcastReceiver screenOnReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // now, do nothing
//            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(context.KEYGUARD_SERVICE);
//            KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("");
//            keyguardLock.disableKeyguard();//解锁系统锁屏
        }
    };
    private BroadcastReceiver screenOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new KeyguardManager(context).launchKeyguard();
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter screenOff = new IntentFilter("android.intent.action.SCREEN_OFF");
        this.registerReceiver(screenOffReceiver, screenOff);

        IntentFilter screenOn = new IntentFilter("android.intent.action.SCREEN_ON");
        this.registerReceiver(screenOnReceiver, screenOn);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(screenOnReceiver);
        unregisterReceiver(screenOffReceiver);

        List<ServiceStatus> tmp = DbManager.getInstance().query(ServiceStatus.class);
        if (tmp == null || tmp.size() == 0) {
            return;
        }
        for (ServiceStatus status : tmp) {
            if (status.serviceClassName.equals(getClass().getName())) {
                if (status.autoOpen) {
                    startService(new Intent(this, KeyguardService.class));
                }
                break;
            }
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
