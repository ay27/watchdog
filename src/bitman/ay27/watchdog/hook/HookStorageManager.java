//package bitman.ay27.watchdog;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import bitman.ay27.watchdog.utils.Common;
//import de.robv.android.xposed.*;
//import de.robv.android.xposed.callbacks.XC_LoadPackage;
//
//import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
//import static de.robv.android.xposed.XposedHelpers.findClass;
//
///**
// * Created by ay27 on 15-7-8.
// */
//public class HookStorageManager implements IXposedHookZygoteInit, IXposedHookLoadPackage {
//
//    private Context mContext;
//    private boolean firstRegister = true;
//
//    @Override
//    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
//
//        /**
//         * hook for the disableUsbMassStorage
//         */
//        try {
//            final Class<?> StorageManager = findClass("android.os.storage.StorageManager", lpparam.classLoader);
//
//            findAndHookMethod(StorageManager, "from", Context.class, new XC_MethodHook() {
//                @Override
//                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                    super.beforeHookedMethod(param);
//                    mContext = (Context) param.args[0];
//                    XposedBridge.log("from invoke");
//                }
//            });
//
//            findAndHookMethod(StorageManager, "disableUsbMassStorage", new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    XposedBridge.log("disableUsbMassStorage");
//                }
//            });
//
//            findAndHookMethod(StorageManager, "enableUsbMassStorage", new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    XposedBridge.log("enableUsbMassStorage");
//                }
//            });
//
//
//            findAndHookMethod(StorageManager, "isUsbMassStorageConnected", new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    super.afterHookedMethod(param);
//                    XposedBridge.log("isUsbMassStorageConnected false");
//                    param.setResult(false);
//                }
//            });
//
//            findAndHookMethod(StorageManager, "isUsbMassStorageEnabled", new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    super.afterHookedMethod(param);
//                    XposedBridge.log("isUsbMassStorageEnabled false");
//                    param.setResult(false);
//                }
//            });
//
//
//            XposedBridge.hookAllConstructors(StorageManager, new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
//                    if (mContext == null) {
//                        return;
//                    }
//                    if (!firstRegister) {
//                        return;
//                    }
//                    firstRegister = false;
//
//                    mContext.registerReceiver(new BroadcastReceiver() {
//                        @Override
//                        public void onReceive(Context context, Intent intent) {
//                            XposedBridge.log("receive disable usb");
//                            XposedHelpers.callMethod(param.thisObject, "disableUsbMassStorage");
//                            XposedBridge.log("after call method");
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            mContext.sendBroadcast(new Intent(Common.ACTION_DISABLE_USB_SUCCESS));
//                        }
//                    }, new IntentFilter(Common.ACTION_DISABLE_USB));
//
//
//                    mContext.registerReceiver(new BroadcastReceiver() {
//                        @Override
//                        public void onReceive(Context context, Intent intent) {
//                            XposedBridge.log("receive enable usb");
//                            XposedHelpers.callMethod(param.thisObject, "enableUsbMassStorage");
//                            XposedBridge.log("after call method");
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            mContext.sendBroadcast(new Intent(Common.ACTION_ENABLE_USB_SUCCESS));
//                        }
//                    }, new IntentFilter(Common.ACTION_ENABLE_USB));
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//            XposedBridge.log("usb error : " + e.toString());
//        }
//
//    }
//
//    @Override
//    public void initZygote(StartupParam startupParam) throws Throwable {
//
//    }
//}
