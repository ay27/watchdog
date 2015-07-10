//package bitman.ay27.watchdog;
//
//import android.content.*;
//import android.os.Bundle;
//import bitman.ay27.watchdog.utils.Common;
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
//* Created by ay27 on 15-7-9.
//*/
//public class HookExternalStorageFormatter implements IXposedHookZygoteInit, IXposedHookLoadPackage {
//
//    private static final String FORMAT_ONLY = "com.android.internal.os.storage.FORMAT_ONLY";
//    private static final ComponentName COMPONENT_NAME = new ComponentName("android", "com.android.settings.MediaFormat");
//    private Context mContext;
//
//    @Override
//    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
//        try {
//            Class<?> MediaFormat = findClass("com.android.internal.os.storage.ExternalStorageFormatter", lpparam.classLoader);
//            findAndHookMethod(MediaFormat, "onCreate", new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    super.afterHookedMethod(param);
//                    XposedBridge.log("hook ExternalStorageFormatter onCreate");
//
//                    mContext = (Context) param.thisObject;
//
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
//                }
//            });
//        } catch (Exception e) {
//            XposedBridge.log(e.toString());
//        }
//    }
//
//    @Override
//    public void initZygote(StartupParam startupParam) throws Throwable {
//    }
//}
