package bitman.s117.libwatchcat;

import java.io.*;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Spartan on 2015/4/10.
 */
public class WatchCat_Controller_Impl implements WatchCat_Controller {
    public static final String CMD_DISP_CMDLINE = "cat /proc/cmdline";
    public static final String CMD_LSMOD = "lsmod";
    public static final String CMD_INSTALL_FS_PROTECTOR = "modprobe fs_protector";
    public static final String CMD_INSTALL_BCPT = "modprobe bcpt";
    public static final String CMD_UNINSTALL_FS_PROTECTOR = "rmmod fs_protector";
    public static final String CMD_UNINSTALL_BCPT = "rmmod bcpt";

    public static final String CMD_WC_CTL_FSP_TAG_KEY = "__KEY__";
    public static final String CMD_WC_CTL_FSP_TAG_OPC = "__OPC__";
    public static final String CMD_WC_CTL_FSP = "wc_ctl fsp " + CMD_WC_CTL_FSP_TAG_KEY + " " + CMD_WC_CTL_FSP_TAG_OPC;

    public static final String CMD_WC_CTL_CRYPT_TAG_TARGET = "__TARGET__";
    public static final String CMD_WC_CTL_CRYPT_TAG_CPTKEY = "__CPTKEY__";
    public static final String CMD_WC_CTL_CRYPT_TAG_NAME = "__NAME__";
    public static final String CMD_WC_CTL_CRYPT_TAG_CRYPT_ALGR= "__CRYPTALGR__";
    public static final String CMD_WC_CTL_CRYPT_TARGET_PATH_PERFIX = "/dev/block/";
    public static final String CMD_WC_CTL_CRYPT_MAPPED_NAME_PERFIX = "c_";
    public static final String CMD_WC_CTL_CRYPT = "wc_ctl crypt " + CMD_WC_CTL_CRYPT_TAG_TARGET + " " + CMD_WC_CTL_CRYPT_TAG_CPTKEY + " " + CMD_WC_CTL_CRYPT_TAG_NAME + " " + CMD_WC_CTL_CRYPT_TAG_CRYPT_ALGR;
    public static final String CMD_DMSETUP_LS = "dmsetup ls";
    public static final String CMD_DMSETUP_REMOVE_ALL_F = "dmsetup remove_all --force";
    public static final String CMD_MKFS_EXFAT_TAG_TARGET = "__TARGET__";
    public static final String CMD_MKFS_EXFAT = "mkfs.exfat " + CMD_MKFS_EXFAT_TAG_TARGET;

    public static final String CMD_FSTAB_CRYPTSD_SCTIPT = "cryptSD.sh";
    public static final String CMD_FSTAB_NORMALSD_SCTIPT = "normalSD.sh";

    public static final String PATH_MAPPED_DEV_DIR = "/dev/mapper/";
    public static final String PATH_LOCK_KEY = "/sdcard/lock.key";

    protected static byte LOCKFILE_MAGIC[] = new byte[]{-34, -83};

    private static String sys_bootup_cmdline = null;

    //private String mMappedName = null;

    protected static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    protected static byte[] MD5(String s) {
        try {
            byte[] btInput = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            return mdInst.digest();
        } catch (Exception e) {
            return null;
        }
    }

    protected static String FormatAuthMidder() {
        Calendar now = Calendar.getInstance();
        now.setTimeZone(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat prefixFormat = new SimpleDateFormat("dd-yyyy-MM");
        SimpleDateFormat suffixFormat = new SimpleDateFormat("HH-ss-mm");
        prefixFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        suffixFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return prefixFormat.format(now.getTime()) + "-spartan-" + suffixFormat.format(now.getTime());
    }

    protected static String FSP_Keygen() {
        byte key_seq[] = MD5(FormatAuthMidder());
        return bytesToHexString(key_seq).toUpperCase();
    }

    protected static byte[] lockfileRead() {
        File fp = new File(PATH_LOCK_KEY);
        byte datIn[] = new byte[18];
        FileInputStream f_is = null;
        try {
            f_is = new FileInputStream(fp);
            if (18 != f_is.read(datIn, 0, 18))
                datIn = null;
        } catch (FileNotFoundException e) {
            datIn = null;
        } catch (IOException e) {
            datIn = null;
        } finally {
            if (f_is != null)
                try {
                    f_is.close();
                } catch (IOException e) {
                }
        }
        return datIn;
    }

    protected static void lockfileWrite(byte[] digest) throws IOException {
        File fp = new File(PATH_LOCK_KEY);
        fileRemove(fp);
        fp.createNewFile();
        FileOutputStream f_os = new FileOutputStream(fp);

        f_os.write(LOCKFILE_MAGIC);
        f_os.write(digest);
        f_os.flush();
        f_os.close();
    }

    protected static void lockfileRemove() throws IOException {
        File fp = new File(PATH_LOCK_KEY);
        fileRemove(fp);
    }


    protected static void fileRemove(File fp) throws IOException {
        if (fp.exists()) {
            try {
                if (!fp.delete()) {
                    throw new IOException("no write permission to lock file " + PATH_LOCK_KEY);
                }
            } catch (SecurityException ex) {
                throw new IOException("no write permission to lock file " + PATH_LOCK_KEY);
            }
        }
    }

    protected static String getSerialNumber() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            return "0000000000000000";
        }
        return serial;
    }

    protected static String getLinuxSystemCmdline() {
        if (sys_bootup_cmdline == null) {
            CmdLineInvoker invoker = new CmdLineInvoker(CMD_DISP_CMDLINE, false);
            if (invoker.run() != 0) {
                throw new IllegalStateException("Cann't get system boot up cmdline via \"" + CMD_DISP_CMDLINE + "\"");
            }
            sys_bootup_cmdline = invoker.getStdout();
        }
        return sys_bootup_cmdline;
    }

    protected static String fetchEquPair(String content, String key) {
        Pattern equPattern = Pattern.compile("(\\p{Space}|^)+" + key + "=(.*?)(\\p{Space}|$)+");
        Matcher equMatcher = equPattern.matcher(content);
        if (equMatcher.find())
            return equMatcher.group(2);
        else
            return null;
    }

    protected static File[] findFileUnderDir(String dirPath, String filename) {
        File fpDir = new File(dirPath);
        filename = filename.replace('.', '#');
        filename = filename.replaceAll("#", "\\\\.");
        filename = filename.replace('*', '#');
        filename = filename.replaceAll("#", ".*");
        filename = filename.replace('?', '#');
        filename = filename.replaceAll("#", ".?");
        filename = "^" + filename + "$";
        final Pattern filePattern = Pattern.compile(filename);

        if (!fpDir.isDirectory())
            return null;
        return fpDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                Matcher filterMatcher = filePattern.matcher(name);
                return (filterMatcher.find());
            }
        });
    }

    protected static String extractDeviceName(String path) {
        Pattern equPattern = Pattern.compile("\\\\?([a-zA-Z]+?)\\d*?$");
        Matcher equMatcher = equPattern.matcher(path);
        if (equMatcher.find())
            return equMatcher.group(1);
        else
            return null;
    }

    protected static String getFirstCryptedDeviceName(){
        CmdLineInvoker invoker = new CmdLineInvoker(CMD_DMSETUP_LS, true);
        if (invoker.run() != 0)
            throw new IllegalStateException("fail when invoke \"" + CMD_DMSETUP_LS + "\".");
        Pattern equPattern = Pattern.compile("^(\\S+?)\\s+\\(");
        Matcher equMatcher = equPattern.matcher(invoker.getStdout());
        if (equMatcher.find())
            return equMatcher.group(1);
        else
            return null;
    }



    @Override
    public boolean isFsProtectorLoaded() throws IllegalStateException {
        CmdLineInvoker invoker = new CmdLineInvoker(CMD_LSMOD, true);
        if (invoker.run() != 0)
            throw new IllegalStateException("fail when invoke \"" + CMD_LSMOD + "\".");
        return invoker.getStdout().contains("fs_protector");
    }

    @Override
    public void loadFsProtector() throws IllegalStateException {
        CmdLineInvoker invoker = new CmdLineInvoker("", true);

        if (isFsProtectorLoaded()) {
            while (true) {
                String cmd = CMD_WC_CTL_FSP.replace(CMD_WC_CTL_FSP_TAG_OPC, "h").replace(CMD_WC_CTL_FSP_TAG_KEY, FSP_Keygen());
                invoker.setCmd(cmd, true);
                if (invoker.run() != 0)
                    throw new IllegalStateException("fail when invoke \"" + cmd + "\".");
                if (invoker.getStderr().equals(""))
                    break;
                else if (invoker.getStderr().contains("authenticate fail"))
                    continue;
                else if (invoker.getStderr().contains("invalid <operate>"))
                    throw new IllegalStateException("wc_fsp protocol version doesn't match");
                else if (invoker.getStderr().contains("fail to open /dev/wc_fsp, reason: -1"))
                    throw new IllegalStateException("wc_fsp module offline");
                else
                    throw new IllegalStateException("unknow error encounter, detail: " + invoker.getStderr());
            }
        } else {
            invoker.setCmd(CMD_INSTALL_FS_PROTECTOR, true);
            if (invoker.run() != 0)
                throw new IllegalStateException("fail when invoke \"" + CMD_INSTALL_FS_PROTECTOR + "\".");
            if (!invoker.getStderr().equals(""))
                throw new IllegalStateException("modprobe fail, detail: " + invoker.getStderr());
            if (!isFsProtectorLoaded())
                throw new IllegalStateException("unknow error, module install fail, reboot recommended.");
        }

    }

    @Override
    public void unloadFsProtector() {
        CmdLineInvoker invoker;
        if (!isFsProtectorLoaded())
            return;

        invoker = new CmdLineInvoker("", true);
        while (true) {
            String cmd = CMD_WC_CTL_FSP.replace(CMD_WC_CTL_FSP_TAG_OPC, "r").replace(CMD_WC_CTL_FSP_TAG_KEY, FSP_Keygen());
            invoker.setCmd(cmd, true);
            if (invoker.run() != 0)
                throw new IllegalStateException("fail when invoke \"" + cmd + "\".");
            if (invoker.getStderr().equals(""))
                break;
            else if (invoker.getStderr().contains("authenticate fail"))
                continue;
            else if (invoker.getStderr().contains("invalid <operate>"))
                throw new IllegalStateException("wc_fsp protocol version doesn't match");
            else if (invoker.getStderr().contains("fail to open /dev/wc_fsp, reason: -1"))
                throw new IllegalStateException("wc_fsp module offline");
            else
                throw new IllegalStateException("unknow error encounter, detail: " + invoker.getStderr());
        }
        invoker.setCmd(CMD_UNINSTALL_FS_PROTECTOR, true);
        if (invoker.run() != 0)
            throw new IllegalStateException("fail when invoke \"" + CMD_UNINSTALL_FS_PROTECTOR + "\".");
        if (!invoker.getStderr().equals(""))
            throw new IllegalStateException("rmmod fail, detail: " + invoker.getStderr());
    }

    @Override
    public boolean queryBootloaderWriteProtect() {
        if (!isFsProtectorLoaded())
            return false;
        CmdLineInvoker invoker = new CmdLineInvoker("", true);
        while (true) {
            String cmd = CMD_WC_CTL_FSP.replace(CMD_WC_CTL_FSP_TAG_OPC, "n").replace(CMD_WC_CTL_FSP_TAG_KEY, FSP_Keygen());
            invoker.setCmd(cmd, true);
            if (invoker.run() != 0)
                throw new IllegalStateException("fail when invoke \"" + cmd + "\".");
            if (invoker.getStderr().equals(""))
                break;
            else if (invoker.getStderr().contains("authenticate fail"))
                continue;
            else if (invoker.getStderr().contains("invalid <operate>"))
                throw new IllegalStateException("wc_fsp protocol version doesn't match");
            else if (invoker.getStderr().contains("fail to open /dev/wc_fsp, reason: -1"))
                throw new IllegalStateException("wc_fsp module offline");
            else
                throw new IllegalStateException("unknow error encounter, detail: " + invoker.getStderr());
        }
        String fl_enlock = fetchEquPair(invoker.getStdout(), "fl_enlock");
        return fl_enlock.equals("1");
    }

    @Override
    public void enableBootloaderWriteProtect() {
        CmdLineInvoker invoker;
        if (!isFsProtectorLoaded())
            throw new IllegalStateException("Watchcat FsProtector module haven't load");
        invoker = new CmdLineInvoker("", true);
        while (true) {
            String cmd = CMD_WC_CTL_FSP.replace(CMD_WC_CTL_FSP_TAG_OPC, "e").replace(CMD_WC_CTL_FSP_TAG_KEY, FSP_Keygen());
            invoker.setCmd(cmd, true);
            if (invoker.run() != 0)
                throw new IllegalStateException("fail when invoke \"" + cmd + "\".");
            if (invoker.getStderr().equals(""))
                break;
            else if (invoker.getStderr().contains("authenticate fail"))
                continue;
            else if (invoker.getStderr().contains("invalid <operate>"))
                throw new IllegalStateException("wc_fsp protocol version doesn't match");
            else if (invoker.getStderr().contains("fail to open /dev/wc_fsp, reason: -1"))
                throw new IllegalStateException("wc_fsp module offline");
            else
                throw new IllegalStateException("unknow error encounter, detail: " + invoker.getStderr());
        }
    }

    @Override
    public void disableBootloaderWriteProtect() {
        CmdLineInvoker invoker;
        if (!isFsProtectorLoaded())
            throw new IllegalStateException("Watchcat FsProtector module haven't load");
        invoker = new CmdLineInvoker("", true);
        while (true) {
            String cmd = CMD_WC_CTL_FSP.replace(CMD_WC_CTL_FSP_TAG_OPC, "d").replace(CMD_WC_CTL_FSP_TAG_KEY, FSP_Keygen());
            invoker.setCmd(cmd, true);
            if (invoker.run() != 0)
                throw new IllegalStateException("fail when invoke \"" + cmd + "\".");
            if (invoker.getStderr().equals(""))
                break;
            else if (invoker.getStderr().contains("authenticate fail"))
                continue;
            else if (invoker.getStderr().contains("invalid <operate>"))
                throw new IllegalStateException("wc_fsp protocol version doesn't match");
            else if (invoker.getStderr().contains("fail to open /dev/wc_fsp, reason: -1"))
                throw new IllegalStateException("wc_fsp module offline");
            else
                throw new IllegalStateException("unknow error encounter, detail: " + invoker.getStderr());
        }
    }


    @Override
    public boolean isBcptLoaded() throws IllegalStateException {
        CmdLineInvoker invoker = new CmdLineInvoker(CMD_LSMOD, true);
        if (invoker.run() != 0)
            throw new IllegalStateException("fail when invoke \"" + CMD_LSMOD + "\".");
        return invoker.getStdout().contains("bcpt");
    }

    @Override
    public void loadBCPT() {
        if (isBcptLoaded()) {
            return;
        } else {
            CmdLineInvoker invoker = new CmdLineInvoker(CMD_INSTALL_BCPT, true);
            if (invoker.run() != 0)
                throw new IllegalStateException("fail when invoke \"" + CMD_INSTALL_BCPT + "\".");
            if (!invoker.getStderr().equals(""))
                throw new IllegalStateException("modprobe fail, detail: " + invoker.getStderr());
            if (!isBcptLoaded())
                throw new IllegalStateException("unknow error, module install fail, reboot recommended.");
            invoker.setCmd(CMD_FSTAB_CRYPTSD_SCTIPT, true);
            if (invoker.run() != 0)
                throw new IllegalStateException("fail when invoke \"" + CMD_FSTAB_CRYPTSD_SCTIPT + "\".");
            if (!invoker.getStderr().equals(""))
                throw new IllegalStateException("modify fstab fail, detail: " + invoker.getStderr());
        }
    }

    @Override
    public void unloadBCPT() {
        if (!isBcptLoaded()) {
            return;
        } else {
            CmdLineInvoker invoker = new CmdLineInvoker(CMD_UNINSTALL_BCPT, true);
            if (invoker.run() != 0)
                throw new IllegalStateException("fail when invoke \"" + CMD_UNINSTALL_BCPT + "\".");
            if (!invoker.getStderr().equals(""))
                throw new IllegalStateException("rmmod fail, detail: " + invoker.getStderr());
            if (isBcptLoaded())
                throw new IllegalStateException("unknow error, module install fail, reboot recommended.");
            invoker.setCmd(CMD_FSTAB_NORMALSD_SCTIPT, true);
            if (invoker.run() != 0)
                throw new IllegalStateException("fail when invoke \"" + CMD_FSTAB_NORMALSD_SCTIPT + "\".");
            if (!invoker.getStderr().equals(""))
                throw new IllegalStateException("modify fstab fail, detail: " + invoker.getStderr());
        }
    }

    @Override
    public boolean queryEncryption() {
        CmdLineInvoker invoker = new CmdLineInvoker(CMD_DMSETUP_LS, true);
        if (invoker.run() != 0)
            throw new IllegalStateException("fail when invoke \"" + CMD_DMSETUP_LS + "\".");
        return !invoker.getStdout().contains("No devices found");
    }

    // AES - mode=0, BLOWFISL - mode=1
    @Override
    public void enableEncryption(String cipher, int mode){
        CmdLineInvoker invoker;
        if (!isBcptLoaded())
            throw new IllegalStateException("BCPT module haven't load");
        if (queryEncryption())
            disableEncryption();

        invoker = new CmdLineInvoker("", true);

        File[] devList = findFileUnderDir("/dev/block", "sd*");
        String firstDevName;
        if (devList.length == 0) {
            throw new IllegalStateException("no original block device detected");
        } else if (devList.length >= 1) {
            firstDevName = extractDeviceName(devList[0].getName());
            for(int i = 1;i < devList.length;i++){
                if(!firstDevName.equals(devList[i]))
                    throw new IllegalStateException("multiple original block device, you need specify which one");
            }
        } else{
            throw new IllegalStateException("unexpected status happened when fetch device info, detail: devList.length == " + devList.length +
                    ", please feedback the issue to our.");
        }

        String cmd = CMD_WC_CTL_CRYPT.
                replace(CMD_WC_CTL_CRYPT_TAG_TARGET, devList[0].getAbsolutePath()).
                replace(CMD_WC_CTL_CRYPT_TAG_CPTKEY, bytesToHexString(MD5(cipher))).
                replace(CMD_WC_CTL_CRYPT_TAG_NAME, CMD_WC_CTL_CRYPT_MAPPED_NAME_PERFIX + firstDevName);
        if(mode == 0){
            cmd = cmd.replace(CMD_WC_CTL_CRYPT_TAG_CRYPT_ALGR, "aes");
        } else {
            cmd = cmd.replace(CMD_WC_CTL_CRYPT_TAG_CRYPT_ALGR, "blowfish");
        }

        invoker.setCmd(cmd, true);
        if (invoker.run() != 0)
            throw new IllegalStateException("fail when invoke \"" + cmd + "\".");
        if (invoker.getStderr().equals("")) {
            //this.mMappedName = CMD_WC_CTL_CRYPT_MAPPED_NAME_PERFIX + devList[0].getName();
            return;
        } else if (invoker.getStderr().contains("can not open device")) {
            throw new IllegalStateException("cannot open original block device: " + CMD_WC_CTL_CRYPT_TARGET_PATH_PERFIX + devList[0].getAbsolutePath());
        } else {
            throw new IllegalStateException("fail to create encryption mirror , detail: " + invoker.getStderr());
        }
    }

    @Override
    public void disableEncryption() {
        CmdLineInvoker invoker = new CmdLineInvoker(CMD_DMSETUP_REMOVE_ALL_F, true);
        if (invoker.run() != 0)
            throw new IllegalStateException("fail when invoke \"" + CMD_DMSETUP_REMOVE_ALL_F + "\".");
        if (!invoker.getStderr().equals("")) {
            throw new IllegalArgumentException("Unable to clean old mirror, please retry after unmount the mirror, detail: " + invoker.getStderr());
        }
    }

    @Override
    public void formatEncryptionDisk() {
        CmdLineInvoker invoker;
        String cmd = CMD_MKFS_EXFAT.replace(CMD_MKFS_EXFAT_TAG_TARGET, PATH_MAPPED_DEV_DIR + getFirstCryptedDeviceName());
        if(!queryEncryption())
            throw new IllegalStateException("no encryption mirror exist");

        invoker = new CmdLineInvoker(cmd, true);
        if (invoker.run() != 0)
            throw new IllegalStateException("fail when invoke \"" + cmd + "\".");
        if (invoker.getExitno() != 0)
            throw new IllegalStateException("mkfs fail, format undone");
    }

    @Override
    public boolean isSDCardExist(){
        File[] devList = findFileUnderDir("/dev/block", "sd*");
        return devList.length != 0;
    }


    @Override
    public boolean isFlashLockExist() {
        return getLinuxSystemCmdline().contains("flash_locked");
    }

    @Override
    public boolean queryFlashLockEnable() {
        byte nowCtx[] = lockfileRead();
        byte digest[] = MD5(getSerialNumber());
        if (nowCtx == null)
            return true;
        if ((nowCtx[0] != -34) || (nowCtx[1] != -83))
            return true;
        for (int i = 0; i < 16; ++i) {
            if (nowCtx[i + 2] != digest[i])
                return true;
        }
        return false;
    }

    @Override
    public void unlockFlashLock() throws IOException {
        if (!queryFlashLockEnable())
            return;
        lockfileWrite(MD5(getSerialNumber()));
    }

    @Override
    public void lockFlashLock() throws IOException {
        lockfileRemove();
    }
}
