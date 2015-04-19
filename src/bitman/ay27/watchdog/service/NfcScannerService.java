package bitman.ay27.watchdog.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/19.
 */
public class NfcScannerService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
