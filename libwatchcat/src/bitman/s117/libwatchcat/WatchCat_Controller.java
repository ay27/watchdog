package bitman.s117.libwatchcat;

import java.io.IOException;

/**
 * Created by Spartan on 2015/4/1.
 */
public interface WatchCat_Controller {
    //Watch Fs Protector Part
    public boolean isFsProtectorLoaded();

    public void loadFsProtector();

    public void unloadFsProtector();

    public boolean queryBootloaderWriteProtect();

    public void enableBootloaderWriteProtect();

    public void disableBootloaderWriteProtect();

    //Watch BCPT Part
    public boolean isBcptLoaded();

    public void loadBCPT();

    public void unloadBCPT();

    public boolean queryEncryption();

    // AES - mode=0, BLOWFISL - mode=1
    public void enableEncryption(String cipher, int mode);

    public void disableEncryption();

    public void formatEncryptionDisk();

    public boolean isSDCardExist();


    //Watch Flash Lock Part
    public boolean isFlashLockExist();

    public boolean queryFlashLockEnable();

    public void lockFlashLock() throws IOException;

    public void unlockFlashLock() throws IOException;
}
