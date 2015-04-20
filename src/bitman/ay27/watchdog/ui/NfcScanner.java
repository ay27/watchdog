package bitman.ay27.watchdog.ui;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.util.Log;
import bitman.ay27.watchdog.db.DbManager;
import bitman.ay27.watchdog.db.model.NfcCard;
import bitman.ay27.watchdog.service.NfcLostReceiver;
import bitman.ay27.watchdog.utils.Utils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/19.
 */
public class NfcScanner extends AsyncTask {

    private static final String TAG = "NfcScanner";
    public static int READER_FLAGS =
            NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
    private Activity activity;
    private boolean scannerStatus = false;
    private NfcAdapter adapter;
    private List<NfcCard> cards;
    private NfcAdapter.ReaderCallback readerCallback = new NfcAdapter.ReaderCallback() {
        @Override
        public void onTagDiscovered(Tag tag) {
            String cardId = Utils.ByteArrayToHexString(tag.getId());
            Log.i(TAG, "found nfc " + cardId);

            for (NfcCard card : cards) {
                if (card.code.equals(cardId)) {
                    return;
                }
            }

            Intent intent = new Intent();
            intent.setAction(NfcLostReceiver.ACTION_NFC_LOST);
            activity.sendBroadcast(intent);
        }
    };

    public NfcScanner(Activity activity) {
        this.activity = activity;

        cards = DbManager.getInstance().query(NfcCard.class);
        if (cards == null || cards.size() == 0) {
            return;
        }
        adapter = NfcAdapter.getDefaultAdapter(activity);
    }

    private TimerTask generateTask() {
        return new TimerTask() {
            @Override
            public void run() {
                if (scannerStatus) {
                adapter.disableReaderMode(activity);
                    scannerStatus = false;
                } else {
                    scannerStatus = true;
                    adapter.enableReaderMode(activity, readerCallback, READER_FLAGS, null);
                }
            }
        };
    }

    @Override
    protected Object doInBackground(Object[] params) {
//        Timer timer = new Timer();
//        timer.schedule(generateTask(), 0);
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        adapter.enableReaderMode(activity, readerCallback, READER_FLAGS, null);

    }
}
