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

    private static final String KEY_BOOT_LOADER_LOCK = "boot_loader_lock";
    private static final String KEY_KEYGUARD = "keyguard";
    private static final String PREF_NAME = "main_preference";
    private static final String KEY_USB = "usb_debug";
    private static final String KEY_SD_STATE = "sd_state";
    private static final String KEY_SD_PASSWD = "sd_passwd";
    private static final String KEY_ENCRYPT_TYPE = "sd_encrypt_type";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_PASSWORD = "password";

    private static final String KEY_USER_ID = "userId";
    private static final String KEY_NFC_CARDS = "nfc_cards";

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
        return pref.getBoolean(KEY_USB, false);
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
}
