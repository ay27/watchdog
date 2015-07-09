package bitman.ay27.watchdog;

import android.content.*;
import android.os.Environment;
import bitman.ay27.watchdog.utils.Common;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * Created by ay27 on 15-7-8.
 */
public class HookMountService implements IXposedHookZygoteInit, IXposedHookLoadPackage {


    private static final String FORMAT_ONLY = "com.android.internal.os.storage.FORMAT_ONLY";
    private static final ComponentName COMPONENT_NAME = new ComponentName("android", "com.android.settings.MediaFormat");

    private Context mContext;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        /**
         * hook the unmountVolume method, use for the SD encryption
         */
        try {
            Class<?> MountService = findClass("com.android.server.MountService", lpparam.classLoader);
            findAndHookMethod(MountService, "unmountVolume", String.class, boolean.class, boolean.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("unmount volume " + param.args[0] + " " + param.args[1] + " " + param.args[2]);
                }
            });

            XposedBridge.hookAllConstructors(MountService, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    mContext = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
                    IntentFilter filter = new IntentFilter(Common.ACTION_UNMOUNT);
                    mContext.registerReceiver(new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            XposedBridge.log("receive unmount");
                            XposedHelpers.callMethod(param.thisObject, "unmountVolume", "/storage/usbdisk", true, false);
                            XposedBridge.log("after call method");
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mContext.sendBroadcast(new Intent(Common.ACTION_UNMOUNT_SUCCESS));
                        }
                    }, filter);


                    /**
                     * try to use this context to format the sd card
                     */
//                    mContext.registerReceiver(new BroadcastReceiver() {
//                        @Override
//                        public void onReceive(Context context, Intent intent) {
//                            XposedBridge.log("receive format");
//                            Intent intent2Format = new Intent(FORMAT_ONLY);
//                            intent.setComponent(COMPONENT_NAME);
//                            // Transfer the storage volume to the new intent
////                            final StorageVolume storageVolume = getIntent().getParcelableExtra(
////                                    StorageVolume.EXTRA_STORAGE_VOLUME);
////                            intent.putExtra(StorageVolume.EXTRA_STORAGE_VOLUME, storageVolume);
//                            mContext.startService(intent2Format);
//                            XposedBridge.log("format finish");
//                        }
//                    }, new IntentFilter(Common.ACTION_FORMAT));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            XposedBridge.log("unmount volume error : " + e.toString());
        }


        /**
         * hook the mountVolume
         */

        try {
            Class<?> MountService = findClass("com.android.server.MountService", lpparam.classLoader);
            findAndHookMethod(MountService, "mountVolume", String.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("mount volume " + param.args[0]);
                }
            });

            XposedBridge.hookAllConstructors(MountService, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    mContext = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
                    IntentFilter filter = new IntentFilter(Common.ACTION_MOUNT);
                    mContext.registerReceiver(new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            XposedBridge.log("receive mount");
                            XposedHelpers.callMethod(param.thisObject, "mountVolume", "/storage/usbdisk");
                            XposedBridge.log("after call method");
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mContext.sendBroadcast(new Intent(Common.ACTION_MOUNT_SUCCESS));
                        }
                    }, filter);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            XposedBridge.log("mount volume error : " + e.toString());
        }


        /**
         * hook for the formatVolume
         */
//        try {
//            Class<?> MountService = findClass("com.android.server.MountService", lpparam.classLoader);
//            findAndHookMethod(MountService, "formatVolume", String.class, new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    XposedBridge.log("format volume " + param.args[0]);
//                }
//            });
//
//            XposedBridge.hookAllConstructors(MountService, new XC_MethodHook() {
//
//                @Override
//                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
//                    mContext = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
//                    IntentFilter filter = new IntentFilter(Common.ACTION_FORMAT);
//                    mContext.registerReceiver(new BroadcastReceiver() {
//                        @Override
//                        public void onReceive(Context context, Intent intent) {
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            XposedBridge.log("receive format");
//                            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
//                            XposedBridge.log("path " + path);
//                            XposedHelpers.callMethod(param.thisObject, "formatVolume", path);
////                            if (intent.hasExtra("path")) {
//////                                String path = intent.getStringExtra("path");
////                                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
////                                XposedBridge.log("path " + path);
////                                XposedHelpers.callMethod(param.thisObject, "formatVolume", path);
////                            } else
////                                XposedBridge.log("no path");
//
//                            XposedBridge.log("after call method");
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            mContext.sendBroadcast(new Intent(Common.ACTION_FORMAT_SUCCESS));
//                        }
//                    }, filter);
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//            XposedBridge.log("format volume error : " + e.toString());
//        }

    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {

    }
}
