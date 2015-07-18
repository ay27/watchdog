package bitman.ay27.watchdog.ui.new_activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;
import bitman.ay27.watchdog.PrefUtils;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.utils.Common;
import bitman.ay27.watchdog.utils.SuperUserAccess;
import com.kyleduo.switchbutton.SwitchButton;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-18.
 */
public class UsbLockrActivity extends Activity {

    private boolean reEnter = false;
    private SwitchButton switchButton;

    private CompoundButton.OnCheckedChangeListener usbLockrListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (reEnter) {
                reEnter = false;
                return;
            }

            PrefUtils.setUsbEnable(isChecked);
            boolean result;
            if (!isChecked) {
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
                reEnter = true;
                switchButton.setChecked(!isChecked);
            } else {
                PrefUtils.setUsbEnable(isChecked);

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
