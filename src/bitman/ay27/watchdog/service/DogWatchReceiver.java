package bitman.ay27.watchdog.service;

import android.content.*;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import bitman.ay27.watchdog.PrefUtils;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.WatchdogApplication;
import bitman.ay27.watchdog.net.NetManager;
import bitman.ay27.watchdog.ui.KeyguardManager;
import bitman.ay27.watchdog.utils.Common;
import bitman.ay27.watchdog.watchlink.DogWatchService;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-13.
 */
public class DogWatchReceiver extends BroadcastReceiver {
    private static final String TAG = "DogWatchReceiver";

    static MediaPlayer player;
    private DogWatchService dogWatchService;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            dogWatchService = ((DogWatchService.LocalBinder) service).getService();
            WatchdogApplication.getContext().unbindService(conn);
            if (dogWatchService == null || dogWatchService.getConnectionState() != DogWatchService.STATE_CONNECTED) {
                return;
            }
            dogWatchService.post(DogWatchService.CHARA_VIBRATE_TRIGGER, new byte[]{DogWatchService.VIBRATE_OUT_OF_RANGE});
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    private ServiceConnection conn1 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            dogWatchService = ((DogWatchService.LocalBinder) service).getService();
            WatchdogApplication.getContext().unbindService(conn1);
            if (dogWatchService == null || dogWatchService.getConnectionState() != DogWatchService.STATE_CONNECTED) {
                return;
            }
            dogWatchService.post(DogWatchService.CHARA_VIBRATE_TRIGGER, new byte[]{DogWatchService.VIBRATE_STOP});
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(DogWatchService.ACTION_REP_GATT_CONNECTED)) {

        } else if (action.equals(DogWatchService.ACTION_REP_GATT_CONNECTED_FAIL)) {

        } else if (action.equals(DogWatchService.ACTION_REP_GATT_DISCONNECTED)) {

        } else if (action.equals(DogWatchService.ACTION_REP_GATT_DISCONNECTED_FAIL)) {

        } else if (action.equals(DogWatchService.ACTION_REP_RSSI_OUT_OF_RANGE)) {
            Log.i(TAG, "out of range");
            PrefUtils.setPhoneSafety(false);
            new KeyguardManager(context).launchKeyguard();
//            SuperUserAccess.runCmd("echo 0 > /sys/devices/virtual/android_usb/android0/enable");
            // TODO open internet
            // TODO close flash locker
            NetManager.state("danger");
            player = MediaPlayer.create(context, R.raw.tag_lost);
            player.setLooping(true);
            player.start();

            sendOutOfRangeVibrate();

        } else if (action.equals(DogWatchService.ACTION_REP_RSSI_RETURN_RANGE)) {
            Log.i(TAG, "return range");
//            SuperUserAccess.runCmd("echo 1 > /sys/devices/virtual/android_usb/android0/enable");
            NetManager.state("save");
            PrefUtils.setPhoneSafety(true);
            if (player.isPlaying()) {
                player.stop();
            }

            sendReturnRange();

            WatchdogApplication.getContext().sendBroadcast(new Intent(Common.ACTION_KILL_KEYGUARD));
        }
    }

    private void sendReturnRange() {
        WatchdogApplication.getContext().bindService(new Intent(WatchdogApplication.getContext(), DogWatchService.class), conn1, Context.BIND_AUTO_CREATE);
    }

    private void sendOutOfRangeVibrate() {
        WatchdogApplication.getContext().bindService(new Intent(WatchdogApplication.getContext(), DogWatchService.class), conn, Context.BIND_AUTO_CREATE);
    }
}