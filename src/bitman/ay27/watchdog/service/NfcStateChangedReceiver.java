package bitman.ay27.watchdog.service;

import android.content.*;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import bitman.ay27.watchdog.PrefUtils;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.WatchdogApplication;
import bitman.ay27.watchdog.db.model.NfcCard;
import bitman.ay27.watchdog.utils.Common;
import bitman.ay27.watchdog.watchlink.DogWatchService;

import java.util.List;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/19.
 */
public class NfcStateChangedReceiver extends BroadcastReceiver {

    private static final String TAG = "NfcFoundReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Common.ACTION_TAG_CHANGED)) {

            String uid = intent.getStringExtra(Common.EXTRA_ID_STRING);
            boolean present = intent.getBooleanExtra(Common.EXTRA_TAG_PRESENT, false);

            Log.i(TAG, "nfc change");
            Log.i(TAG, "uid string " + uid);
            Log.i(TAG, "tag present " + present);

            List<NfcCard> cards = PrefUtils.getNfcCards();
            if (cards != null && cards.size() != 0) {
                for (NfcCard card1 : cards) {
                    if (uid.equals(card1.code)) {
                        if (present) {
                            MediaPlayer.create(context, R.raw.end).start();
                        } else {
                            MediaPlayer.create(context, R.raw.tag_lost).start();
                            sendNfcVibrate();
                        }
//                        SharedPreferences mPrefs = context.getSharedPreferences(Common.PREFS, Context.MODE_WORLD_READABLE);
//                        mPrefs.edit().putStringSet(Common.PREF_SOUNDS_TO_PLAY,
//                                new HashSet<String>(Arrays.asList(context.getResources().getStringArray(R.array.pref_sounds_to_play_values)))).commit();
//                        Intent i = new Intent(Common.SETTINGS_UPDATED_INTENT);
//                        context.sendBroadcast(i);
//                        return;
                    }
                }
            }
//            SharedPreferences mPrefs = context.getSharedPreferences(Common.PREFS, Context.MODE_WORLD_READABLE);
//            mPrefs.edit().putStringSet(Common.PREF_SOUNDS_TO_PLAY,
//                    new HashSet<String>()).commit();
//            Intent i = new Intent(Common.SETTINGS_UPDATED_INTENT);
//            context.sendBroadcast(i);
        }
    }


    private DogWatchService dogWatchService;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            dogWatchService = ((DogWatchService.LocalBinder)service).getService();
            if (dogWatchService == null || dogWatchService.getConnectionState()!=DogWatchService.STATE_CONNECTED) {
                return;
            }
            dogWatchService.post(DogWatchService.CHARA_VIBRATE_TRIGGER, new byte[]{DogWatchService.VIBRATE_NFC});
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void sendNfcVibrate() {
        WatchdogApplication.getContext().bindService(new Intent(WatchdogApplication.getContext(), DogWatchService.class), conn, Context.BIND_AUTO_CREATE);
    }

}
