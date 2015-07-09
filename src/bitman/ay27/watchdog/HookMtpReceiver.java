//package bitman.ay27.watchdog;
//
//import android.content.Context;
//import android.content.Intent;
//import android.hardware.usb.UsbDeviceConnection;
//import android.hardware.usb.UsbManager;
//import android.os.Bundle;
//import com.baidu.location.LocationClient;
//import de.robv.android.xposed.IXposedHookLoadPackage;
//import de.robv.android.xposed.IXposedHookZygoteInit;
//import de.robv.android.xposed.XC_MethodHook;
//import de.robv.android.xposed.XposedBridge;
//import de.robv.android.xposed.callbacks.XC_LoadPackage;
//
//import java.util.concurrent.CountDownLatch;
//
//import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
//import static de.robv.android.xposed.XposedHelpers.findClass;
//
///**
// * Created by ay27 on 15-7-9.
// */
//public class HookMtpReceiver implements IXposedHookZygoteInit, IXposedHookLoadPackage {
//
//    public static final String USB_CONFIGURED = "configured";
//    public static final String USB_FUNCTION_MTP = "mtp";
//    public static final String USB_FUNCTION_PTP = "ptp";
//    @Override
//    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
//        try {
//            Class<?> MtpDevice = findClass("com.android.providers.media.MtpReceiver", lpparam.classLoader);
//            findAndHookMethod(MtpDevice, "handleUsbState", Context.class, Intent.class, new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    Intent intent = (Intent) param.args[1];
//                    Bundle extras = intent.getExtras();
//                    boolean connected = extras.getBoolean(USB_CONFIGURED);
//                    boolean mtpEnabled = extras.getBoolean(USB_FUNCTION_MTP);
//                    boolean ptpEnabled = extras.getBoolean(USB_FUNCTION_PTP);
//                    XposedBridge.log("handle usb state "+"connected: "+connected+" mtpEnabled: "+mtpEnabled+" ptpEnabled: "+ptpEnabled);
//                }
//            });
//
//        } catch (Exception e) {
//            XposedBridge.log(e.toString());
//        }
//    }
//
//    @Override
//    public void initZygote(StartupParam startupParam) throws Throwable {
//
//    }
//}
