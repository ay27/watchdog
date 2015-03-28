package bitman.ay27.watchdog.ui;

import android.content.Context;
import android.view.View;
import bitman.ay27.watchdog.WatchdogApplication;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/3/27.
 */
public class WindowManager {

    private static Context appContext = WatchdogApplication.getContext();
    private static android.view.WindowManager wm;

    public void addWindow(View view, android.view.WindowManager.LayoutParams params) {
        if (wm == null)
            wm = (android.view.WindowManager) appContext.getSystemService("window");
        wm.addView(view, params);
    }

    public void rmWindow(View view) {
        if (wm == null)
            wm = (android.view.WindowManager) appContext.getSystemService("window");
        wm.removeViewImmediate(view);
    }
}
