package bitman.ay27.watchdog;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import bitman.ay27.watchdog.service.DaemonService;
import bitman.ay27.watchdog.utils.Common;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/3/27.
 */
public class WatchdogApplication extends Application {
    private static WatchdogApplication instance = null;
    private SharedPreferences mPrefs;

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Intent intent = new Intent(this, DaemonService.class);
        startService(intent);

//        IntentFilter filter = new IntentFilter(Common.INTENT_TAG_DISCOVERED);
//        this.registerReceiver(new NfcFoundReceiver(), filter);
//        filter = new IntentFilter(Common.ACTION_TAG_LOST);
//        this.registerReceiver(new NfcLostReceiver(), filter);

        mPrefs = getSharedPreferences(Common.PREFS, Context.MODE_WORLD_READABLE);
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
}
