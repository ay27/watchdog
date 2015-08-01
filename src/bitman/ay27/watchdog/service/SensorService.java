package bitman.ay27.watchdog.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;
import bitman.ay27.watchdog.PrefUtils;
import bitman.ay27.watchdog.WatchdogApplication;
import bitman.ay27.watchdog.watchlink.DogWatchService;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-8-1.
 */
public class SensorService extends Service {
    private final float THRESHOLD = (float) 1.5;
    private float oldX, oldY, oldZ;
    private SensorManager manager;
    private boolean isAlarm = false;
    private Timer timer;
    private SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0], y = event.values[1], z = event.values[2];
            if (Math.abs(x - oldX) > THRESHOLD || Math.abs(y - oldY) > THRESHOLD || Math.abs(z - oldZ) > THRESHOLD) {
                if (isAlarm) {
                    isAlarm = false;
                    sendNfcVibrate();
//                    Toast.makeText(getBaseContext(), "warning", Toast.LENGTH_SHORT).show();
                } else if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
            } else {
                if (timer == null) {
                    timer = new Timer();
                    timer.schedule(generateTask(), PrefUtils.getMoveAlarmTime());
                }
            }

            oldX = x;
            oldY = y;
            oldZ = z;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
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

    private TimerTask generateTask() {
        return new TimerTask() {
            @Override
            public void run() {
                isAlarm = true;
            }
        };
    }

    private void sendNfcVibrate() {
        WatchdogApplication.getContext().bindService(new Intent(WatchdogApplication.getContext(), DogWatchService.class), conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        manager.registerListener(sensorListener, sensor, 100000);

//        timer = new Timer();
//        timer.schedule(task, 0, 1000);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        manager.unregisterListener(sensorListener);
    }
}
