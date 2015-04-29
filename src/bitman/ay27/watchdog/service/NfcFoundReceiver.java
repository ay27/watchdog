package bitman.ay27.watchdog.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.util.Log;
import bitman.ay27.watchdog.utils.Common;

/**
* Proudly to user Intellij IDEA.
* Created by ay27 on 15/4/19.
*/
public class NfcFoundReceiver extends BroadcastReceiver {

    private static final String TAG = "NfcFoundReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent.getAction().equals(Common.ACTION_TAG_CHANGED)) {
            Log.i(TAG, "nfc change");
             Log.i(TAG, "uid string "+intent.getStringExtra(Common.EXTRA_ID_STRING));
            Log.i(TAG, "tag present "+intent.getBooleanExtra(Common.EXTRA_TAG_PRESENT, false));
//            Log.i(TAG, "uid "+NfcAdapter.EXTRA_ID);

//            intent.putExtra(EXTRA_ID_STRING, uidString);
//            intent.putExtra(NfcAdapter.EXTRA_ID, uid);
//            intent.putExtra(EXTRA_TAG_PRESENT, tagPresent);
        }
    }

}
