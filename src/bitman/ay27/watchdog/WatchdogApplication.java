package bitman.ay27.watchdog;

import android.app.Application;
import android.content.*;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import bitman.ay27.watchdog.service.DaemonService;
import bitman.ay27.watchdog.service.ServiceManager;
import bitman.ay27.watchdog.ui.activity.WatchManageActivity;
import bitman.ay27.watchdog.utils.Common;
import bitman.ay27.watchdog.watchlink.DefaultDogWatchCallback;
import bitman.ay27.watchdog.watchlink.DogWatchService;
import com.tencent.android.tpush.XGPushManager;

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

        // 在主进程设置信鸽相关的内容
        XGPushManager.registerPush(this, DeviceId);

        ServiceManager.getInstance().addService(DogWatchService.class);
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
