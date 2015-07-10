//package bitman.ay27.watchdog.service;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.media.MediaPlayer;
//import android.util.Log;
//import bitman.ay27.watchdog.R;
//import bitman.ay27.watchdog.db.DbManager;
//import bitman.ay27.watchdog.db.model.NfcCard;
//import bitman.ay27.watchdog.utils.Common;
//
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.List;
//
///**
//* Proudly to user Intellij IDEA.
//* Created by ay27 on 15/4/19.
//*/
//public class NfcLostReceiver extends BroadcastReceiver {
//
//    private static final String TAG = "NfcLostReceiver";
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        if (intent.getAction().equals(Common.ACTION_TAG_LOST)) {
//            Log.i(TAG, "nfc lost");
//
//            String uid = intent.getStringExtra(Common.EXTRA_ID_STRING);
//
////
//            DbManager manager = DbManager.getInstance();
//            List<NfcCard> cards = manager.query(NfcCard.class);
//            if (cards != null && cards.size() != 0) {
//                for (NfcCard card1 : cards) {
//                    if (uid.equals(card1.code)) {
//
//                        MediaPlayer.create(context, R.raw.tag_lost).start();
//
////                        SharedPreferences mPrefs = context.getSharedPreferences(Common.PREFS, Context.MODE_WORLD_READABLE);
////                        mPrefs.edit().putStringSet(Common.PREF_SOUNDS_TO_PLAY,
////                                new HashSet<String>(Arrays.asList(context.getResources().getStringArray(R.array.pref_sounds_to_play_values)))).commit();
////                        Intent i = new Intent(Common.SETTINGS_UPDATED_INTENT);
////                        context.sendBroadcast(i);
////                        return;
//                    }
//                }
//            }
////            SharedPreferences mPrefs = context.getSharedPreferences(Common.PREFS, Context.MODE_WORLD_READABLE);
////            mPrefs.edit().putStringSet(Common.PREF_SOUNDS_TO_PLAY,
////                    new HashSet<String>()).commit();
////            Intent i = new Intent(Common.SETTINGS_UPDATED_INTENT);
////            context.sendBroadcast(i);
//        }
//    }
//}
