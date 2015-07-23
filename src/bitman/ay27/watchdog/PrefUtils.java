package bitman.ay27.watchdog;

import android.content.Context;
import android.content.SharedPreferences;
import bitman.ay27.watchdog.db.model.NfcCard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ay27 on 15-7-10.
 */
public class PrefUtils {

    public static final String KEY_BOOT_LOADER_LOCK = "boot_loader_lock";
    public static final String KEY_KEYGUARD = "keyguard";
    public static final String PREF_NAME = "main_preference";
    public static final String KEY_USB = "usb_debug";
    public static final String KEY_SD_STATE = "sd_state";
    public static final String KEY_SD_PASSWD = "sd_passwd";
    public static final String KEY_ENCRYPT_TYPE = "sd_encrypt_type";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_USER_PASSWORD = "password";

    public static final String KEY_USER_ID = "userId";
    public static final String KEY_NFC_CARDS = "nfc_cards";
    public static final String KEY_DEVICE_NAME = "deviceName";
    public static final String KEY_BLE_DEVICE_ADDR = "ble_address";
    public static final String KEY_BLE_DIST = "ble_dist";
    public static final String KEY_SIM_SERIAL_NUMBER = "sim_serial_number";
    public static final String KEY_PHONE_SAFETY = "phone_safety";
    public static final String KEY_AUTO_CLOSE_USB = "auto_close_usb";
    public static final String KEY_DISTURB_PASSWD = "disturb_passwd";

    private static Context context = WatchdogApplication.getContext();
    private static SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

    private PrefUtils() {
    }

    public static void registerListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        pref.registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        pref.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public static String getUserId() {
        return pref.getString(KEY_USER_ID, "");
    }

    public static void setUserId(String userId) {
        pref.edit().putString(KEY_USER_ID, userId).apply();
    }

    public static void setBootLoaderEnable(boolean enable) {
        pref.edit().putBoolean(KEY_BOOT_LOADER_LOCK, enable).apply();
    }

    public static boolean isBootloaderEnable() {
        return pref.getBoolean(KEY_BOOT_LOADER_LOCK, false);
    }

    public static boolean isKeyguardEnable() {
        return pref.getBoolean(KEY_KEYGUARD, false);
    }

    public static void setKeyguardEnable(boolean enable) {
        pref.edit().putBoolean(KEY_KEYGUARD, enable).apply();
    }

    public static boolean isUsbEnable() {
        return pref.getBoolean(KEY_USB, true);
    }

    public static void setUsbEnable(boolean enable) {
        pref.edit().putBoolean(KEY_USB, enable).apply();
    }

    public static int getSdState() {
        return pref.getInt(KEY_SD_STATE, 0);
    }

    public static void setSdState(int state) {
        pref.edit().putInt(KEY_SD_STATE, state).apply();
    }

    public static String getSdPasswd() {
        return pref.getString(KEY_SD_PASSWD, "");
    }

    public static void setSdPasswd(String passwd) {
        pref.edit().putString(KEY_SD_PASSWD, passwd).apply();
    }

    public static int getSdEncryptType() {
        return pref.getInt(KEY_ENCRYPT_TYPE, 0);
    }

    public static void setSdEncryptType(int type) {
        pref.edit().putInt(KEY_ENCRYPT_TYPE, type).apply();
    }

    public static String getUserName() {
        return pref.getString(KEY_USERNAME, "");
    }

    public static void setUserName(String username) {
        pref.edit().putString(KEY_USERNAME, username).apply();
    }

    public static String getUserPasswd() {
        return pref.getString(KEY_USER_PASSWORD, "");
    }

    public static void setUserPasswd(String password) {
        pref.edit().putString(KEY_USER_PASSWORD, password).apply();
    }


    public static void addNfcCard(NfcCard card) {
        Set<String> strs = pref.getStringSet(KEY_NFC_CARDS, new HashSet<String>());
        for (String str : strs) {
            String[] ss = str.split(":");
            if (ss[1].equals(card.code)) {
                strs.remove(str);
                break;
            }
        }
        strs.add(card.name + ":" + card.code);
        pref.edit().putStringSet(KEY_NFC_CARDS, strs).apply();
    }

    public static void rmNfcCard(NfcCard card) {
        Set<String> strs = pref.getStringSet(KEY_NFC_CARDS, new HashSet<String>());
        for (String str : strs) {
            String[] ss = str.split(":");
            if (ss[1].equals(card.code)) {
                strs.remove(str);
                break;
            }
        }
        pref.edit().putStringSet(KEY_NFC_CARDS, strs).apply();
    }

    public static List<NfcCard> getNfcCards() {
        Set<String> strs = pref.getStringSet(KEY_NFC_CARDS, new HashSet<String>());
        List<NfcCard> cards = new ArrayList<NfcCard>();
        for (String card : strs) {
            String[] ss = card.split(":");
            cards.add(new NfcCard(ss[0], ss[1]));
        }
        return cards;
    }

    public static void setNfcCards(Set<String> cards) {
        pref.edit().putStringSet(KEY_NFC_CARDS, cards).apply();
    }

    public static String getDeviceName() {
        return pref.getString(KEY_DEVICE_NAME, "");
    }

    public static void setDeviceName(String deviceName) {
        pref.edit().putString(KEY_DEVICE_NAME, deviceName).apply();
    }

    public static String getBLEAddr() {
        return pref.getString(KEY_BLE_DEVICE_ADDR, "");
    }

    public static void setBLEAddr(String addr) {
        pref.edit().putString(KEY_BLE_DEVICE_ADDR, addr).apply();
    }

    public static int getBleDist() {
        return pref.getInt(KEY_BLE_DIST, 0);
    }

    public static void setBleDist(int value) {
        pref.edit().putInt(KEY_BLE_DIST, value).apply();
    }

    public static String getSimSerialNumber() {
        return pref.getString(KEY_SIM_SERIAL_NUMBER, "");
    }

    public static void setSimSerialNumber(String serialNumber) {
        pref.edit().putString(KEY_SIM_SERIAL_NUMBER, serialNumber).apply();
    }

    public static void setPhoneSafety(boolean isSafety) {
        pref.edit().putBoolean(KEY_PHONE_SAFETY, isSafety).apply();
    }

    public static boolean isPhoneSafe() {
        return pref.getBoolean(KEY_PHONE_SAFETY, true);
    }

    public static void setAutoCloseUsb(boolean checked) {
        pref.edit().putBoolean(KEY_AUTO_CLOSE_USB, checked).apply();
    }

    public static boolean isAutoCloseUsb() {
        return pref.getBoolean(KEY_AUTO_CLOSE_USB, false);
    }

    public static void setDisturbPasswd(boolean checked) {
        pref.edit().putBoolean(KEY_DISTURB_PASSWD, checked).apply();
    }

    public static boolean isDisturbPasswd() {
        return pref.getBoolean(KEY_DISTURB_PASSWD, false);
    }
}
