package bitman.ay27.watchdog.ui;

import android.content.Context;
import android.content.Intent;
import bitman.ay27.watchdog.db.DbManager;
import bitman.ay27.watchdog.db.model.KeyguardStatus;
import bitman.ay27.watchdog.ui.activity.KeyguardImgActivity;
import bitman.ay27.watchdog.ui.activity.KeyguardKeyboardActivity;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/15.
 */
public class KeyguardManager {

    private KeyguardStatus status;
    private Context context;

    public KeyguardManager(Context context) {
        this.context = context;
        status = (KeyguardStatus) DbManager.getInstance().query(KeyguardStatus.class).get(0);
    }

    public void launchKeyguard() {
        if (status == null || status.unlockType == KeyguardStatus.PasswdType.keyboard) {
            Intent intent = new Intent(context, KeyguardKeyboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(context, KeyguardImgActivity.class);
            intent.putExtra("Status", status);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
