package bitman.ay27.watchdog.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.UserHandle;
import de.robv.android.xposed.XposedHelpers;

public class Common {
	public static final String MOD_PACKAGE_NAME = "bitman.ay27.watchdog";

	public static final String SETTINGS_UPDATED_INTENT = MOD_PACKAGE_NAME + ".SETTINGS_UPDATED";

	public static final String PREFS = "NFCModSettings";
	@Deprecated
	public static final String PREF_LOCKED = "On_Locked";
	public static final String PREF_TAGLOST = "TagLostDetecting";
	@Deprecated
	public static final String PREF_PRESENCE_CHECK_TIMEOUT_OLD = "presence_check_timeout";
	public static final String PREF_PRESENCE_CHECK_TIMEOUT = "presence_check_time_out";
	public static final String PREF_DEBUG_MODE = "debug_mode";
	public static final String PREF_PLAY_TAG_LOST_SOUND = "should_play_tag_lost_sound";
	public static final String PREF_NFC_KEYS = "authorized_nfc_tag_uuids";
	public static final String PREF_NFC_KEYS_NAMES = "authorized_nfc_tag_friendly_names";
	public static final String PREF_SOUNDS_TO_PLAY = "nfc_sounds_to_play";
	public static final String PREF_ENABLE_NFC_WHEN = "enable_nfc_for_lock_state";

	/* -- */
	public static final String PACKAGE_NFC = "com.android.nfc";

	// Intent sent when a tag is lost
	public static final String ACTION_TAG_LOST = "android.nfc.action.TAG_LOST";

	/* Helper intents of Tasker */
	public static final String ACTION_TAG_CHANGED = "android.nfc.action.TAG_CHANGED";

	// Intent used internally in this module to unlock the device.
	public static final String INTENT_UNLOCK_DEVICE = MOD_PACKAGE_NAME + ".UNLOCK_DEVICE";

	// The intent above is replaced by the one below if the above is used with adb.
	public static final String INTENT_UNLOCK_INTERCEPTED = MOD_PACKAGE_NAME + ".UNLOCK_ATTEMPT_INTERCEPTED";

	/* The following can be accessed in apps like Tasker.
	 * 
	 * In Tasker, you can simply use the variable %tag_uuid or %tag_present
	 */

	/* Used by ACTION_TAG_LOST, String extra */
	public static final String EXTRA_ID_STRING = "tag_uuid";

	/* Used by ACTION_TAG_CHANGED, Boolean extra */
	public static final String EXTRA_TAG_PRESENT = "tag_present";
	public static final String ACTION_UNMOUNT = "bitman.ay27.watchdog.unmount";
	public static final String ACTION_UNMOUNT_SUCCESS = "bitman.ay27.watchdog.unmount_success";
	public static final String ACTION_MOUNT = "bitman.ay27.watchdog.mount";
	public static final String ACTION_MOUNT_SUCCESS = "bitman.ay27.watchdog.mount_success";
	public static final String ACTION_KILL_KEYGUARD = "bitman.ay27.watchdog.kill_keyguard";
	public static final String ACTION_FORMAT = "bitman.ay27.watchdog.format";
	public static final String ACTION_FORMAT_SUCCESS = "bitman.ay27.watchdog.format_success";
	public static final String ACTION_DISABLE_USB = "bitman.ay27.watchdog.disable_usb";
	public static final String ACTION_DISABLE_USB_SUCCESS = "bitman.ay27.watchdog.disable_usb_success";
	public static final String ACTION_ENABLE_USB = "bitman.ay27.watchdog.enable_usb";
	public static final String ACTION_ENABLE_USB_SUCCESS = "bitman.ay27.watchdog.enable_usb_success";

	// Converting byte[] to hex string, used to convert NFC UUID to String
	public static String byteArrayToHexString(byte [] inarray) {
		int i, j, in;
		String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
		String out= "";

		for (j = 0 ; j < inarray.length ; ++j) {
			in = (int) inarray[j] & 0xff;
			i = (in >> 4) & 0x0f;
			out += hex[i];
			i = in & 0x0f;
			out += hex[i];
		}
		return out;
	}

	/* Helper method, on API 17 this method uses sendBroadcastAsUser to prevent
	 * system warnings in logcat.
	 */
	public static void sendBroadcast(Context context, Intent intent) {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
			context.sendBroadcastAsUser(intent, 
					(UserHandle) XposedHelpers.getStaticObjectField(UserHandle.class, "CURRENT"));
		} else {
			context.sendBroadcast(intent);
		}
	}

	public static void sendTagChangedBroadcast(Context context, byte[] uid, boolean tagPresent) {
		String uidString = byteArrayToHexString(uid);

		Intent intent = new Intent(ACTION_TAG_CHANGED);
		intent.putExtra(EXTRA_ID_STRING, uidString);
		intent.putExtra(NfcAdapter.EXTRA_ID, uid);
		intent.putExtra(EXTRA_TAG_PRESENT, tagPresent);

		sendBroadcast(context, intent);
	}
}
