package bitman.ay27.watchdog.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import bitman.ay27.watchdog.WatchdogApplication;
import bitman.ay27.watchdog.db.DbManager;
import bitman.ay27.watchdog.db.model.ServiceStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/3/27.
 */
public class ServiceManager {
    private static final String TAG = "ServiceManager";
    private static ServiceManager instance;
    private Context context = WatchdogApplication.getContext();
    private ArrayList<Pack> packs;

    private ServiceManager() {
        packs = new ArrayList<Pack>();
        readFromDB();
    }

    public static ServiceManager getInstance() {
        if (instance == null) {
            instance = new ServiceManager();
        }
        return instance;
    }

    private void readFromDB() {
        DbManager manager = DbManager.getInstance();
        List<ServiceStatus> statuses = manager.query(ServiceStatus.class);

        ClassLoader loader = ClassLoader.getSystemClassLoader();

        for (ServiceStatus status : statuses) {
            try {
                packs.add(new Pack(loader.loadClass(status.serviceClassName), status));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                Log.e(TAG, e.toString());
            }
        }
    }

    private void updateDB() {
        DbManager manager = DbManager.getInstance();
        List status = manager.query(ServiceStatus.class);
        manager.bulkDelete(ServiceStatus.class, status);
        manager.bulkInsert(ServiceStatus.class, getAllStatus());
    }

    private List getAllStatus() {
        ArrayList<ServiceStatus> statuses = new ArrayList<ServiceStatus>();
        for (Pack pack : packs) {
            statuses.add(pack.status);
        }
        return statuses;
    }

    public void addService(Class serviceCls) {
        Pack pack;
        packs.add(pack = new Pack(serviceCls, new ServiceStatus(serviceCls.getPackage().getName(), serviceCls.getName(), true)));
        updateDB();

        context.startService(new Intent(context, pack.service));
    }

    public void removeService(Class serviceCls) {
        for (Pack pack : packs) {
            if (pack.service.getName().equals(serviceCls.getName())) {
                context.stopService(new Intent(context, pack.service));
                packs.remove(pack);
                updateDB();
                break;
            }
        }
    }


    public void startAll() {
        for (Pack pack : packs) {
            if (pack.status.autoOpen) {
                context.startService(new Intent(context, pack.service));
            }
        }
    }

    public void stopAll() {
        for (Pack pack : packs) {
            context.stopService(new Intent(context, pack.service));
        }
    }

    public void destroy() {
        updateDB();
    }

    private class Pack {
        ServiceStatus status;
        Class service;

        public Pack(Class service, ServiceStatus status) {
            this.service = service;
            this.status = status;
        }
    }
}
