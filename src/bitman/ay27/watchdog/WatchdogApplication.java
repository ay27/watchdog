package bitman.ay27.watchdog;

import android.app.Application;
import android.content.*;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;
import bitman.ay27.watchdog.service.DaemonService;
import bitman.ay27.watchdog.watchlink.DogWatchServiceManager;
import bitman.ay27.watchdog.service.ServiceManager;
import bitman.ay27.watchdog.utils.Common;
import bitman.ay27.watchdog.watchlink.DefaultDogWatchCallback;
import bitman.ay27.watchdog.watchlink.DogWatchService;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.service.XGPushService;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/3/27.
 */
public class WatchdogApplication extends Application {
    private static final String TAG = "WatchdogAppliation";
    public static String DeviceId;
    private static WatchdogApplication instance = null;
    private SharedPreferences mPrefs;

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        DeviceId = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        Intent intent = new Intent(this, DaemonService.class);
        startService(intent);

        setNfcModulePref();

        openNetwork();

        // 在主进程设置信鸽相关的内容
        startService(new Intent(this, XGPushService.class));
        XGPushManager.registerPush(this, DeviceId);

        ServiceManager.getInstance().addService(DogWatchService.class);

        tryConnectWatch();
    }

    private void openNetwork() {
        if (!PrefUtils.isAutoOpenNetwork()) {
            return;
        }

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
//        sendBroadcast(new Intent(Common.ACTION_OPEN_DATA_CONNECT));
    }


    private void tryConnectWatch() {
        String addr = PrefUtils.getBLEAddr();
        if (addr.isEmpty()) {
            return;
        }
        Log.i(TAG, "bind service");
        final DogWatchServiceManager manager = DogWatchServiceManager.getInstance();
        manager.bind(this, new DogWatchServiceManager.BindCallback() {
            @Override
            public void onBindSuccess(DogWatchService service) {
                boolean result = service.initialize(new DefaultDogWatchCallback());
                if (!result) {
                    Log.i(TAG, "initial DogWatchService failed");
                    manager.unbind(getContext(), this);
                    return;
                }
                service.connect(PrefUtils.getBLEAddr(), true);
                manager.unbind(getContext(), this);
            }

            @Override
            public void onBindFailed() {
                manager.unbind(getContext(), this);
            }

            @Override
            public void onDisconnected() {
                manager.unbind(getContext(), this);
            }
        });
    }

    private void setNfcModulePref() {
        mPrefs = getSharedPreferences(Common.PREFS, Context.MODE_WORLD_READABLE);
        if (mPrefs.getString(Common.PREF_ENABLE_NFC_WHEN, null) == null) {
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(Common.PREF_ENABLE_NFC_WHEN, "screen_off");
            editor.putBoolean(Common.PREF_DEBUG_MODE, true);
            editor.putStringSet(Common.PREF_SOUNDS_TO_PLAY, new HashSet<String>(Arrays.asList(
                    getResources().getStringArray(R.array.pref_sounds_to_play_values))));
            editor.putBoolean(Common.PREF_TAGLOST, true);
            editor.putString(Common.PREF_PRESENCE_CHECK_TIMEOUT, "2000");
            editor.putStringSet(Common.PREF_NFC_KEYS_NAMES, new HashSet<String>(Arrays.asList(getResources().getStringArray(R.array.card_name))));
            editor.putStringSet(Common.PREF_NFC_KEYS, new HashSet<String>(Arrays.asList(getResources().getStringArray(R.array.card_uuid))));
            editor.apply();

            sendBroadcast(new Intent(Common.SETTINGS_UPDATED_INTENT));
        }
    }


}
