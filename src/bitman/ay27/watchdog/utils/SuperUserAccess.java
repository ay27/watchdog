package bitman.ay27.watchdog.utils;

import android.util.Log;
import bitman.ay27.watchdog.WatchdogApplication;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/1/24.
 */
public class SuperUserAccess {

    public static final String PERMISSION_WRITE_SECURE_SETTINGS = "android.permission.WRITE_SECURE_SETTINGS";
    public static final String PACKAGE_NAME = WatchdogApplication.getContext().getPackageName();
    public static final String PERMISSION_MOUNT_UNMOUNT_FS = "android.permission.MOUNT_UNMOUNT_FILESYSTEMS";

    private SuperUserAccess() {
    }

    public static boolean upgradeRootPermission() {
        return runCmd("chmod 777 " + PACKAGE_NAME);
//        Process process = null;
//        DataOutputStream os = null;
//        try {
//            String cmd="chmod 777 " + PACKAGE_NAME;
//            runCmd(cmd);
////            String cmd2 = "pm grant bitman.ay27.blockade  android.permission.WRITE_SECURE_SETTINGS";
//            process = Runtime.getRuntime().exec("su"); //切换到root帐号
//            os = new DataOutputStream(process.getOutputStream());
//            os.writeBytes(cmd1 + "\n");
////            os.writeBytes(cmd2 + "\n");
//            os.writeBytes("exit\n");
//            os.flush();
//            process.waitFor();
//        } catch (Exception e) {
//            return false;
//        } finally {
//            try {
//                if (os != null) {
//                    os.close();
//                }
//                process.destroy();
//            } catch (Exception e) {
//            }
//        }
//        return true;
    }

    public static boolean grantSystemPermission(String permission) {
        return runCmd("pm grant " + PACKAGE_NAME + " " + permission);
    }

    public static boolean runCmd(String cmd) {
        Process process = null;
        DataOutputStream dos = null;
        InputStream is = null, eis = null;
        int exitValue = 0;
        try {
            process = Runtime.getRuntime().exec("su");

            is = process.getInputStream();
            eis = process.getErrorStream();

            dos = new DataOutputStream(process.getOutputStream());
            dos.writeBytes(cmd + "\n exit\n");
            dos.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            byte[] bytes = new byte[1024];
            if (is != null) {
                try {
                    is.read(bytes);
                    String str = new String(bytes);
                    Log.i("is", str);
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (eis != null) {
                try {
                    eis.read(bytes);
                    String str = new String(bytes);
                    Log.i("eis", str);
                    eis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (process != null) {
                exitValue = process.exitValue();
                process.destroy();
            }
        }
        return exitValue == 0;
    }

    public static boolean disableUsb() {
        return runCmd("echo 0 > /sys/devices/virtual/android_usb/android0/enable");
    }

    public static boolean enableUsb() {
        return runCmd("echo 1 > /sys/devices/virtual/android_usb/android0/enable");
    }

    public static boolean isUsbEnable() {
        Process process = null;
        DataOutputStream dos = null;
        InputStream is = null;
        int exitValue = 0;
        try {
            process = Runtime.getRuntime().exec("su");

            is = process.getInputStream();

            dos = new DataOutputStream(process.getOutputStream());
            dos.writeBytes("cat /sys/devices/virtual/android_usb/android0/enable" + "\n exit\n");
            dos.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            byte[] bytes = new byte[10];
            try {
                if (dos != null) {
                    dos.close();
                }

                if (is != null) {
                    is.read(bytes);
                    is.close();
                }
                return bytes[0] == 49;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
