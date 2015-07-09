//package bitman.ay27.watchdog;
//
//import android.hardware.usb.UsbDeviceConnection;
//import de.robv.android.xposed.IXposedHookLoadPackage;
//import de.robv.android.xposed.IXposedHookZygoteInit;
//import de.robv.android.xposed.XC_MethodHook;
//import de.robv.android.xposed.XposedBridge;
//import de.robv.android.xposed.callbacks.XC_LoadPackage;
//
//import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
//import static de.robv.android.xposed.XposedHelpers.findClass;
//
///**
// * Created by ay27 on 15-7-9.
// */
//public class HookMtpDevice implements IXposedHookZygoteInit, IXposedHookLoadPackage {
//    @Override
//    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
//
//        try {
//            Class<?> MtpDevice = findClass("android.mtp.MtpDevice", lpparam.classLoader);
//            findAndHookMethod(MtpDevice, "open", UsbDeviceConnection.class, new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    XposedBridge.log("open mtp " + param.args[0]);
//                }
//            });
//
//            findAndHookMethod(MtpDevice, "close", new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    XposedBridge.log("close mtp");
//                }
//            });
//
////            XposedBridge.hookAllConstructors(MtpDevice, new XC_MethodHook() {
////                @Override
////                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
////                    mContext = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
////                    IntentFilter filter = new IntentFilter(Common.ACTION_UNMOUNT);
////                    mContext.registerReceiver(new BroadcastReceiver() {
////                        @Override
////                        public void onReceive(Context context, Intent intent) {
////                            XposedBridge.log("receive unmount");
////                            XposedHelpers.callMethod(param.thisObject, "unmountVolume", "/storage/usbdisk", true, false);
////                            XposedBridge.log("after call method");
////                            try {
////                                Thread.sleep(5000);
////                            } catch (InterruptedException e) {
////                                e.printStackTrace();
////                            }
////                            mContext.sendBroadcast(new Intent(Common.ACTION_UNMOUNT_SUCCESS));
////                        }
////                    }, filter);
////                }
////            });
//        } catch (Exception e) {
//            e.printStackTrace();
//            XposedBridge.log("mtp device error : " + e.toString());
//        }
//    }
//
//    @Override
//    public void initZygote(StartupParam startupParam) throws Throwable {
//
//    }
//}
