//package bitman.ay27.watchdog.service;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//import bitman.ay27.watchdog.utils.Common;
//
///**
// * Proudly to user Intellij IDEA.
// * Created by ay27 on 15/4/19.
// */
//public class NfcLostReceiver extends BroadcastReceiver {
//
//    // 与Manifest保持一致
//    public static final String ACTION_NFC_LOST = "bitman.ay27.watchdog.nfc_lost";
//    private static final String TAG = "NfcLostReceiver";
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        if (intent.getAction().equals(Common.ACTION_TAG_LOST)) {
//            Log.i(TAG, "nfc lost");
//        }
//    }
//}
