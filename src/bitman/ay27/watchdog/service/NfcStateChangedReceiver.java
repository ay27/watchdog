package bitman.ay27.watchdog.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
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
        String uid = intent.getStringExtra(Common.EXTRA_ID_STRING);
        boolean present = intent.getBooleanExtra(Common.EXTRA_TAG_PRESENT, false);

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
                }
            }
        }
    }

    private void sendNfcVibrate() {
        final DogWatchServiceManager manager = DogWatchServiceManager.getInstance();
        final Context context = WatchdogApplication.getContext();
        manager.bind(context, new DogWatchServiceManager.BindCallback() {
            @Override
            public void onBindSuccess(DogWatchService service) {
                service.post(DogWatchService.CHARA_VIBRATE_TRIGGER, new byte[]{DogWatchService.VIBRATE_NFC});
                manager.unbind(context, this);
            }

            @Override
            public void onBindFailed() {
                manager.unbind(context, this);
            }

            @Override
            public void onDisconnected() {
                manager.unbind(context, this);
            }
        });
    }

}
