//package bitman.ay27.watchdog.service;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.media.MediaPlayer;
//import android.util.Log;
//import bitman.ay27.watchdog.PrefUtils;
//import bitman.ay27.watchdog.R;
//import bitman.ay27.watchdog.WatchdogApplication;
//import bitman.ay27.watchdog.db.model.NfcCard;
//import bitman.ay27.watchdog.utils.Common;
//import bitman.ay27.watchdog.watchlink.DogWatchService;
//import bitman.ay27.watchdog.watchlink.DogWatchServiceManager;
//
//import java.util.List;
//
///**
// * Proudly to user Intellij IDEA.
// * Created by ay27 on 15/4/19.
// */
//public class NfcStateChangedReceiver extends BroadcastReceiver {
//
//    private static final String TAG = "NfcFoundReceiver";
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        String uid = intent.getStringExtra(Common.EXTRA_ID_STRING);
//        boolean present = intent.getBooleanExtra(Common.EXTRA_TAG_PRESENT, false);
//
//        Log.i(TAG, "uid string " + uid);
//        Log.i(TAG, "tag present " + present);
//
//        List<NfcCard> cards = PrefUtils.getNfcCards();
//        if (cards != null && cards.size() != 0) {
//            for (NfcCard card1 : cards) {
//                if (uid.equals(card1.code)) {
//                    if (present) {
//                        MediaPlayer.create(context, R.raw.end).start();
//                    } else {
//                        MediaPlayer.create(context, R.raw.tag_lost).start();
//                        sendNfcVibrate();
//                    }
//                }
//            }
//        }
//    }
//
//    private void sendNfcVibrate() {
//        final DogWatchServiceManager manager = DogWatchServiceManager.getInstance();
//        final Context context = WatchdogApplication.getContext();
//        manager.bind(context, new DogWatchServiceManager.BindCallback() {
//            @Override
//            public void onBindSuccess(DogWatchService service) {
//                service.post(DogWatchService.CHARA_VIBRATE_TRIGGER, new byte[]{DogWatchService.VIBRATE_NFC});
//                manager.unbind(context, this);
//            }
//
//            @Override
//            public void onBindFailed() {
//                manager.unbind(context, this);
//            }
//
//            @Override
//            public void onDisconnected() {
//                manager.unbind(context, this);
//            }
//        });
//    }
//
//}

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
    private DogWatchService dogWatchService;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            dogWatchService = ((DogWatchService.LocalBinder) service).getService();
            if (dogWatchService == null || dogWatchService.getConnectionState() != DogWatchService.STATE_CONNECTED) {
                return;
            }

            if (PrefUtils.isCheckWatchDist() && dogWatchService.calcAccuracy() > 2.0) {
                dogWatchService.post(DogWatchService.CHARA_VIBRATE_TRIGGER, new byte[]{DogWatchService.VIBRATE_NFC});
            } else if (!PrefUtils.isCheckWatchDist()) {
                dogWatchService.post(DogWatchService.CHARA_VIBRATE_TRIGGER, new byte[]{DogWatchService.VIBRATE_NFC});
            }

            WatchdogApplication.getContext().unbindService(this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            WatchdogApplication.getContext().unbindService(this);
        }
    };

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

    private void sendNfcVibrate() {
        WatchdogApplication.getContext().bindService(new Intent(WatchdogApplication.getContext(), DogWatchService.class), conn, Context.BIND_AUTO_CREATE);
    }

}