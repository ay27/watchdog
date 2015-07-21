package bitman.ay27.watchdog.ui.new_activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import bitman.ay27.watchdog.PrefUtils;
import bitman.ay27.watchdog.R;
import bitman.s117.libwatchcat.WatchCat_Controller;
import bitman.s117.libwatchcat.WatchCat_Controller_Impl;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.kyleduo.switchbutton.SwitchButton;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-18.
 */
public class FlashLockrActivity extends ActionBarActivity implements View.OnClickListener {

    @InjectView(R.id.flash_lockr_switch)
    SwitchButton switchButton;
    @InjectView(R.id.flash_lockr_status)
    TextView flashLockrStatus;
    @InjectView(R.id.flash_lockr_toolbar)
    Toolbar flashLockrToolbar;
    @InjectView(R.id.flash_lockr_panel)
    RelativeLayout flashLockrPanel;


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

    private boolean isCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flash_lockr);
        ButterKnife.inject(this);

        flashLockrToolbar.setTitle(R.string.flash_lockr);
        flashLockrToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(flashLockrToolbar);

        isCheck = PrefUtils.isBootloaderEnable();
        switchButton.setChecked(isCheck);

        setEnable(isCheck);

        flashLockrPanel.setOnClickListener(this);
//        switchButton.setOnCheckedChangeListener(lockrCheckedChanged);
    }

    @Override
    public void onClick(View v) {

        isCheck = !isCheck;

        boolean result;
        if (isCheck) {
            result = enableFlashLock();
        } else {
            result = disableFlashLock();
        }
        if (result) {
            PrefUtils.setBootLoaderEnable(isCheck);
            setEnable(isCheck);
            switchButton.performClick();

        } else {
            switchButton.setChecked(!isCheck);
        }
    }

    private void setEnable(boolean value) {
        flashLockrStatus.setText(value ? R.string.flash_lockr_open : R.string.flash_lockr_close);
        flashLockrStatus.setTextColor(getResources().getColor(value ? R.color.green_1 : R.color.red_1));
    }
}
