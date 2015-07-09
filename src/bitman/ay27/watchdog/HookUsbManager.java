//package bitman.ay27.watchdog;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.hardware.usb.UsbAccessory;
//import android.hardware.usb.UsbDevice;
//import bitman.ay27.watchdog.utils.Common;
//import de.robv.android.xposed.*;
//import de.robv.android.xposed.callbacks.XC_LoadPackage;
//
//import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
//import static de.robv.android.xposed.XposedHelpers.findClass;
//
///**
// * Created by ay27 on 15-7-10.
// */
//public class HookUsbManager implements IXposedHookZygoteInit, IXposedHookLoadPackage {
//    public static final String USB_FUNCTION_MTP = "mtp";
//    public static final String USB_FUNCTION_PTP = "ptp";
//    private Context mContext;
//
//    @Override
//    public void initZygote(StartupParam startupParam) throws Throwable {
//
//    }
//
//    @Override
//    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
//        try {
//            Class UsbManager = findClass("android.hardware.usb.UsbManager", lpparam.classLoader);
//            XposedBridge.hookAllConstructors(UsbManager, new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
//                    super.afterHookedMethod(param);
//                    mContext = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
//
//                    IntentFilter filter = new IntentFilter(Common.ACTION_DISABLE_USB);
//                    filter.addAction(Common.ACTION_ENABLE_USB);
//
//                    mContext.registerReceiver(new BroadcastReceiver() {
//                        @Override
//                        public void onReceive(Context context, Intent intent) {
//                            XposedBridge.log("receive usb ctl");
//                            boolean flag = intent.getAction().equals(Common.ACTION_DISABLE_USB);
//                            XposedHelpers.callMethod(param.thisObject, "setCurrentFunction", USB_FUNCTION_MTP, flag);
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            XposedHelpers.callMethod(param.thisObject, "setCurrentFunction", USB_FUNCTION_PTP, flag);
//                            XposedBridge.log("setCurrentFunction success");
//                        }
//                    }, filter);
//                }
//            });
//
//
//
//            findAndHookMethod(UsbManager, "openDevice", UsbDevice.class, new XC_MethodHook() {
//                @Override
//                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                    super.beforeHookedMethod(param);
//                    param.setResult(null);
//                    XposedBridge.log("hook openDevice, return null");
//                }
//            });
//
//            findAndHookMethod(UsbManager, "isFunctionEnabled", String.class, new XC_MethodHook() {
//                @Override
//                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                    super.beforeHookedMethod(param);
//                    param.setResult(false);
//                    XposedBridge.log("hook isFunctionEnabled, false");
//                }
//            });
//
//            findAndHookMethod(UsbManager, "openAccessory", UsbAccessory.class, new XC_MethodHook() {
//                @Override
//                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                    super.beforeHookedMethod(param);
//                    param.setResult(false);
//                    XposedBridge.log("hook open accessory, false");
//                }
//            });
//
//
//        } catch (Exception e) {
//            XposedBridge.log(e.toString());
//        }
//    }
//}
