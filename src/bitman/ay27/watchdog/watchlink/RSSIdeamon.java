package bitman.ay27.watchdog.watchlink;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by Spartan on 2015/7/13.
 */
public class RSSIdeamon implements Runnable {
    public final static int RSSID_STOP = -1;
    public Handler controlHandler;
    public Looper currentLooper;
    Thread workingThread;

    public RSSIdeamon() {
        workingThread = new Thread(this);
        workingThread.start();
    }

    @Override
    public void run() {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }

        controlHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            }
        };

        Looper.loop();
    }

    public void terminate() {
        currentLooper.quit();
    }
}
