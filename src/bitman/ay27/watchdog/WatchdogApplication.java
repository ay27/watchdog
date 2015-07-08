package bitman.ay27.watchdog;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;
import bitman.ay27.watchdog.service.DaemonService;
import bitman.ay27.watchdog.service.NfcFoundReceiver;
import bitman.ay27.watchdog.utils.Common;
import com.tencent.android.tpush.XGNotifaction;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushNotifactionCallback;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/3/27.
 */
public class WatchdogApplication extends Application {
    private static WatchdogApplication instance = null;
    private SharedPreferences mPrefs;

    public static String DeviceId;

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
            editor.commit();

            sendBroadcast(new Intent(Common.SETTINGS_UPDATED_INTENT));
        }

        IntentFilter filter = new IntentFilter(Common.ACTION_TAG_CHANGED);
        registerReceiver(new NfcFoundReceiver(), filter);
//        filter = new IntentFilter(Common.ACTION_TAG_LOST);
//        registerReceiver(new NfcLostReceiver(), filter);


        // 在主进程设置信鸽相关的内容
//        if (isMainProcess()) {
            XGPushManager.registerPush(this);
//        }

    }


    private boolean isMainProcess() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

}
