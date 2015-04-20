package bitman.ay27.watchdog.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import bitman.ay27.watchdog.utils.Common;

/**
* Proudly to user Intellij IDEA.
* Created by ay27 on 15/4/19.
*/
public class NfcFoundReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent.getAction().equals("android.nfc.action.TAG_DISCOVERED")) {
            System.out.println("found nfc");
        }
    }

}
