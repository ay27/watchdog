package bitman.ay27.watchdog;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import bitman.ay27.watchdog.service.DaemonService;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/3/27.
 */
public class WatchdogApplication extends Application {
    private static WatchdogApplication instance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Intent intent = new Intent(this, DaemonService.class);
        startService(intent);
    }

    public static Context getContext() {
        return instance;
    }
}
