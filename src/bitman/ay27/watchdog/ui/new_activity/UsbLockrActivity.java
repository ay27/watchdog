package bitman.ay27.watchdog.ui.new_activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.widget.TintCheckBox;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import bitman.ay27.watchdog.PrefUtils;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.utils.Common;
import bitman.ay27.watchdog.utils.SuperUserAccess;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.kyleduo.switchbutton.SwitchButton;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-18.
 */
public class UsbLockrActivity extends ActionBarActivity {

    @InjectView(R.id.usb_lockr_toolbar)
    Toolbar usbLockrToolbar;
    @InjectView(R.id.usb_lockr_status)
    TextView usbLockrStatus;
    @InjectView(R.id.usb_lockr_switch)
    SwitchCompat switchButton;
    @InjectView(R.id.usb_lockr_panel)
    RelativeLayout usbLockrPanel;
    @InjectView(R.id.usb_lockr_check)
    TintCheckBox usbLockrCheck;
    @InjectView(R.id.usb_lockr_auto_close_panel)
    RelativeLayout usbLockrAutoClosePanel;
    private boolean isCheck = false;

    private View.OnClickListener lockPanelClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            isCheck = !isCheck;

            boolean result;
            if (!isCheck) {
                // disable usb
                result = SuperUserAccess.disableUsb();
                if (result) {
                    Toast.makeText(UsbLockrActivity.this, R.string.usb_disable, Toast.LENGTH_SHORT).show();
                }
            } else {
                // enable usb
                result = SuperUserAccess.enableUsb();
                if (result) {
                    // make the default function is MTP
                    sendBroadcast(new Intent(Common.ACTION_CHOOSE_MTP));
                    Toast.makeText(UsbLockrActivity.this, R.string.usb_enable, Toast.LENGTH_SHORT).show();
                }
            }

            if (!result) {
                Toast.makeText(UsbLockrActivity.this, R.string.usb_failed, Toast.LENGTH_SHORT).show();
                switchButton.setChecked(!isCheck);
            } else {
                PrefUtils.setUsbEnable(isCheck);

                usbLockrStatus.setText(isCheck ? R.string.usb_lockr_open : R.string.usb_lockr_close);
                usbLockrStatus.setTextColor(getResources().getColor(isCheck ? R.color.green_1 : R.color.red_1));
                switchButton.performClick();

            }
        }
    };
    private View.OnClickListener autoCloseClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            usbLockrCheck.performClick();
            PrefUtils.setAutoCloseUsb(usbLockrCheck.isChecked());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usb_lockr);
        ButterKnife.inject(this);

        usbLockrToolbar.setTitle(R.string.usb_lockr);
        usbLockrToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(usbLockrToolbar);

        isCheck = PrefUtils.isUsbEnable();
        switchButton.setChecked(isCheck);
        usbLockrCheck.setChecked(PrefUtils.isAutoCloseUsb());

        usbLockrStatus.setText(isCheck ? R.string.usb_lockr_open : R.string.usb_lockr_close);
        usbLockrStatus.setTextColor(getResources().getColor(isCheck ? R.color.green_1 : R.color.red_1));

        usbLockrPanel.setOnClickListener(lockPanelClick);
        usbLockrAutoClosePanel.setOnClickListener(autoCloseClick);
    }


}
