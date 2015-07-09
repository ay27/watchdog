package bitman.ay27.watchdog.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import bitman.ay27.watchdog.db.DbManager;
import bitman.ay27.watchdog.db.model.ServiceStatus;
import bitman.ay27.watchdog.net.NetManager;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ay27 on 15-7-8.
 */
public class HeartbeatService extends Service {
    private TimerTask heartbeatTask = new TimerTask() {
        @Override
        public void run() {
            NetManager.heartbeat();
        }
    };
    private Timer timer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        timer = new Timer();

        // send a heartbeat every one minute
        timer.schedule(heartbeatTask, 60000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();


        List<ServiceStatus> tmp = DbManager.getInstance().query(ServiceStatus.class);
        if (tmp == null || tmp.size() == 0) {
            return;
        }
        for (ServiceStatus status : tmp) {
            if (status.serviceClassName.equals(getClass().getName())) {
                if (status.autoOpen) {
                    startService(new Intent(this, HeartbeatService.class));
                }
                break;
            }
        }
    }
}