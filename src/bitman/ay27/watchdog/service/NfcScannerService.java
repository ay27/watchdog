//package bitman.ay27.watchdog.service;
//
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Intent;
//import android.nfc.NfcAdapter;
//import android.nfc.Tag;
//import android.os.IBinder;
//import android.util.Log;
//import bitman.ay27.watchdog.db.DbManager;
//import bitman.ay27.watchdog.db.model.NfcCard;
//import bitman.ay27.watchdog.db.model.ServiceStatus;
//import bitman.ay27.watchdog.utils.Utils;
//
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
///**
// * Proudly to user Intellij IDEA.
// * Created by ay27 on 15/4/19.
// */
//public class NfcScannerService extends Service {
//
//    private static final String TAG = "NfcScannerService";
//    public static int READER_FLAGS =
//            NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
//
//    private boolean scannerStatus = false;
//    private NfcAdapter adapter;
//    private List<NfcCard> cards;
//
//    private NfcAdapter.ReaderCallback readerCallback = new NfcAdapter.ReaderCallback() {
//        @Override
//        public void onTagDiscovered(Tag tag) {
//            String cardId = Utils.ByteArrayToHexString(tag.getId());
//            Log.i(TAG, "found nfc "+ cardId);
//
//            for (NfcCard card : cards) {
//                if (card.code.equals(cardId)) {
//                    return;
//                }
//            }
//
//            Intent intent = new Intent();
//            intent.setAction(NfcLostReceiver.ACTION_NFC_LOST);
//            sendBroadcast(intent);
//        }
//    };
//
//    private TimerTask timeTask = new TimerTask() {
//        @Override
//        public void run() {
//            if (scannerStatus) {
//                adapter.disableReaderMode(null);
//                scannerStatus = false;
//            } else {
//                scannerStatus = true;
//                adapter.enableReaderMode(null, readerCallback, READER_FLAGS, null);
//            }
//        }
//    };
//    private Timer timer;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        cards = DbManager.getInstance().query(NfcCard.class);
//        if (cards == null || cards.size() == 0) {
//            return;
//        }
//        adapter = NfcAdapter.getDefaultAdapter(this);
//        startTimer();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        List<ServiceStatus> tmp = DbManager.getInstance().query(ServiceStatus.class);
//        if (tmp == null || tmp.size() == 0) {
//            return;
//        }
//        for (ServiceStatus status : tmp) {
//            if (status.serviceClassName.equals(getClass().getName())) {
//                if (status.autoOpen) {
//                    startService(new Intent(this, NfcScannerService.class));
//                }
//                break;
//            }
//        }
//
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    private void startTimer() {
//        timer = new Timer();
//        timer.schedule(timeTask, 0, 2000L);
//    }
//}
