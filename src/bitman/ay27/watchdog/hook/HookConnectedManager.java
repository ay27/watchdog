package bitman.ay27.watchdog.hook;

import android.app.AndroidAppHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import bitman.ay27.watchdog.WatchdogApplication;
import bitman.ay27.watchdog.utils.Common;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-25.
 */
public class HookConnectedManager implements IXposedHookZygoteInit, IXposedHookLoadPackage {

    private Context mContext;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        try {
            Class<?> ConMgr = findClass("android.net.ConnectivityManager", lpparam.classLoader);

            XposedBridge.hookAllConstructors(ConMgr, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    XposedBridge.log("hook ConnectivityManager");
                    AndroidAppHelper.currentApplication().registerReceiver(new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            XposedBridge.log("set mobile data enabled");
                            XposedHelpers.callMethod(param.thisObject, "setMobileDataEnabled", true);
                            XposedBridge.log("set mobile data enabled finish");
                        }
                    }, new IntentFilter(Common.ACTION_OPEN_DATA_CONNECT));
                }
            });
        } catch (Exception e) {
            XposedBridge.log(e.toString() + "\n" + e.getMessage());
        }
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
    }
}
