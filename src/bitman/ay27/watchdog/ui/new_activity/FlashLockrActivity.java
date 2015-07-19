package bitman.ay27.watchdog.ui.new_activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;
import bitman.ay27.watchdog.PrefUtils;
import bitman.ay27.watchdog.R;
import bitman.s117.libwatchcat.WatchCat_Controller;
import bitman.s117.libwatchcat.WatchCat_Controller_Impl;
import com.kyleduo.switchbutton.SwitchButton;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-18.
 */
public class FlashLockrActivity extends Activity {

    private boolean reEnter = false;
    private SwitchButton switchButton;

    private CompoundButton.OnCheckedChangeListener lockrCheckedChanged = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (reEnter) {
                reEnter = false;
                return;
            }
            PrefUtils.setBootLoaderEnable(isChecked);

            boolean result;
            if (isChecked) {
                result = enableFlashLock();
            } else {
                result = disableFlashLock();
            }
            if (result) {
                PrefUtils.setBootLoaderEnable(isChecked);
            } else {
                reEnter = true;
                switchButton.setChecked(!isChecked);
            }
        }
    };

    private boolean disableFlashLock() {
        WatchCat_Controller wc_ctl = new WatchCat_Controller_Impl();
        try {
            wc_ctl.disableBootloaderWriteProtect();
            wc_ctl.unlockFlashLock();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (wc_ctl.queryBootloaderWriteProtect() || wc_ctl.queryFlashLockEnable()) {
            Toast.makeText(FlashLockrActivity.this, R.string.bootloader_unlock_failed, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Toast.makeText(FlashLockrActivity.this, R.string.bootloader_unlock_success, Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private boolean enableFlashLock() {
        WatchCat_Controller wc_ctl = new WatchCat_Controller_Impl();
        try {
            wc_ctl.lockFlashLock();
            wc_ctl.loadFsProtector();
            wc_ctl.enableBootloaderWriteProtect();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(FlashLockrActivity.this, R.string.lock_failed, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (wc_ctl.queryBootloaderWriteProtect() && wc_ctl.queryFlashLockEnable()) {
            Toast.makeText(FlashLockrActivity.this, R.string.lock_success, Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(FlashLockrActivity.this, R.string.lock_failed, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
