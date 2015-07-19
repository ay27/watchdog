package bitman.ay27.watchdog.watchlink;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-18.
 */
public class DogWatchServiceManager {

    private static final String TAG = "DogWatchManager";
    private static DogWatchServiceManager instance;
    private final Semaphore bindLockr;
    private final Semaphore connItemCount;
    private final WorkThread workThread;
    private volatile ArrayList<ConnItem> items;


    public DogWatchServiceManager() {
        items = new ArrayList<ConnItem>();
        bindLockr = new Semaphore(1);
        connItemCount = new Semaphore(0);

        workThread = new WorkThread();
        workThread.start();
    }

    public static synchronized DogWatchServiceManager getInstance() {
        if (instance == null) {
            instance = new DogWatchServiceManager();
        }
        return instance;
    }

    public synchronized void bind(final Context context, final BindCallback cb) {
        final ConnItem item = new ConnItem();
        item.callback = cb;
        item.context = context;
        item.bindHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                synchronized (ConnItem.class) {
                    context.bindService(new Intent(context, DogWatchService.class), item.conn = generateConn(context, cb), Context.BIND_AUTO_CREATE);
                    Log.i(TAG, "bind");
                }
            }
        };
        items.add(item);
        connItemCount.release();
//        workThread.notify();
    }

    public synchronized void unbind(final Context context, final BindCallback cb) {
        for (Iterator it = items.iterator(); it.hasNext();) {
            ConnItem item = (ConnItem) it.next();
            if (item.context.equals(context) && item.callback.equals(cb)) {
                synchronized (ConnItem.class) {
                    if (item.conn != null) {
                        context.unbindService(item.conn);
                    }
                }
                it.remove();
//                items.remove(item);

                bindLockr.release();

                Log.i(TAG, "unbind");
            }
        }
//        for (ConnItem item : items) {
//            if (item.context.equals(context) && item.callback.equals(cb)) {
//                synchronized (ConnItem.class) {
//                    if (item.conn != null) {
//                        context.unbindService(item.conn);
//                    }
//                }
//                items.remove(item);
//
//                bindLockr.release();
//
//                Log.i(TAG, "unbind");
//            }
//        }
    }


    private ServiceConnection generateConn(final Context context, final BindCallback cb) {
        return new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                if (service == null) {
                    cb.onBindFailed();
                    return;
                }

                cb.onBindSuccess(((DogWatchService.LocalBinder) service).getService());
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                cb.onDisconnected();
            }
        };
    }

    public static interface BindCallback {

        public abstract void onBindSuccess(DogWatchService service);

        public void onBindFailed();

        public void onDisconnected();
    }

    private static class ConnItem {
        BindCallback callback;
        Context context;
        volatile ServiceConnection conn;
        Handler bindHandler;
    }

    private class WorkThread extends Thread {
        private static final String TAG = "WorkThread";

        @Override
        public void run() {
            super.run();

            while (true) {
                try {

                    Log.i(TAG, "acquire");
                    bindLockr.acquire();

                    Log.i(TAG, "has item");

                    // the bind task only handle ONE-TIME
                    connItemCount.acquire();

                    Log.i(TAG, "handler");
                    ConnItem item = items.get(0);
                    if (item.bindHandler != null) {
                        item.bindHandler.obtainMessage(0).sendToTarget();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
