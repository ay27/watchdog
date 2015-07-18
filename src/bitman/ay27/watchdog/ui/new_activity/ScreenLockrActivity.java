package bitman.ay27.watchdog.ui.new_activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;
import bitman.ay27.watchdog.PrefUtils;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.service.KeyguardService;
import bitman.ay27.watchdog.service.ServiceManager;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-18.
 */
public class ScreenLockrActivity extends Activity {


    private CompoundButton.OnCheckedChangeListener screenLockrListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            PrefUtils.setKeyguardEnable(isChecked);
            ServiceManager manager = ServiceManager.getInstance();
            if (isChecked) {
                manager.addService(KeyguardService.class);
                Toast.makeText(ScreenLockrActivity.this, R.string.screen_lock_enable, Toast.LENGTH_SHORT).show();
            } else {
                manager.removeService(KeyguardService.class);
                Toast.makeText(ScreenLockrActivity.this, R.string.screen_lock_disable, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
