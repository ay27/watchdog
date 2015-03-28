package bitman.ay27.watchdog.service;

import android.app.Service;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/3/27.
 */
public class ServiceManager {
    private static ServiceManager instance;

    private ServiceManager() {}

    public static ServiceManager getInstance() {
        if (instance == null) {
            instance = new ServiceManager();
        }
        return instance;
    }

    public void addService(Service service) {

    }

    public void startAll() {

    }

    public void stopAll() {

    }

    public void destroy() {

    }
}
