package bitman.ay27.watchdog.ui.new_activity.lock;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import bitman.ay27.watchdog.PrefUtils;
import bitman.ay27.watchdog.db.DbManager;
import bitman.ay27.watchdog.db.model.KeyguardStatus;

import java.util.List;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/15.
 */
public class KeyguardManager {

    private static final String TAG = "KeyguardManager";
    private KeyguardStatus status;
    private Context context;

    public KeyguardManager(Context context) {
        this.context = context;
        List list = DbManager.getInstance().query(KeyguardStatus.class);
        if (list == null || list.size() == 0) {
            Log.e(TAG, "can not read keyguard status from DB");
            return;
        }
        status = (KeyguardStatus) list.get(0);
    }

    public void launchKeyguard() {
        if (status == null) {
            Log.e(TAG, "without status, can not launch keyguard");
            return;
        }

        if (!PrefUtils.isPhoneSafe()) {
            Intent intent = new Intent(context, LockAlarmActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
            return;
        }

        if (status.unlockType == KeyguardStatus.PasswdType.keyboard) {
            Intent intent = new Intent(context, KeyguardKeyboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(context, KeyguardImgActivity.class);
            intent.putExtra("Status", status);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            context.startActivity(intent);
        }
    }
}
