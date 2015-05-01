package bitman.ay27.watchdog.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.*;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.service.KeyguardService;
import bitman.ay27.watchdog.service.ServiceManager;
import bitman.ay27.watchdog.utils.Common;
import bitman.ay27.watchdog.utils.UpgradeSystemPermission;
import bitman.s117.libwatchcat.WatchCat_Controller;
import bitman.s117.libwatchcat.WatchCat_Controller_Impl;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import com.kyleduo.switchbutton.SwitchButton;

public class MainActivity extends ActionBarActivity {

    public static final String TAG = "MainActivity";
    public static final String KEY_BOOT_LOADER_LOCK = "boot_loader_lock";
    public static final String KEY_SD_ENCRYPT = "sd_encrypt";
    public static final String KEY_KEYGUARD = "keyguard";
    public static final String PREF_NAME = "main_preference";
    public static final String KEY_USB = "usb_debug";
    public static final String KEY_SD_STATUS = "sd_status";
    public static final String KEY_SD_PASSWD = "sd_passwd";
    public static final String KEY_ENCRYPT_TYPE = "sd_encrypt_type";

    @InjectView(R.id.main_toolbar)
    Toolbar toolbar;
    //    @InjectView(R.id.main_boot_loader_summer)
//    TextView bootLoaderSummer;
    @InjectView(R.id.main_boot_loader_lock_switch)
    SwitchButton bootLoaderSwitch;
    //    @InjectView(R.id.main_sd_encrypt_summer)
//    TextView sdEncryptSummer;
//    @InjectView(R.id.main_sd_encrypt_switch)
//    SwitchButton sdEncryptSwitch;
    //    @InjectView(R.id.main_keyguard_summer)
//    TextView keyguardSummer;

    @InjectView(R.id.main_sd_title)
    TextView sdTitle;
    @InjectView(R.id.main_sd_summer)
    TextView sdSummer;

    @InjectView(R.id.main_keyguard_switch)
    SwitchButton keyguardSwitch;
    //    @InjectView(R.id.main_usb_summer)
//    TextView usbSummer;
    @InjectView(R.id.main_usb_switch)
    SwitchButton usbSwitch;

    @InjectView(R.id.main_sd_format_panel)
    View formatPanel;
    @InjectView(R.id.main_sd_format_title)
    TextView formatTitle;
    @InjectView(R.id.main_sd_format_summer)
    TextView formatSummer;

    private SharedPreferences pref;
    private boolean usbReEnter = false;
    private SharedPreferences.OnSharedPreferenceChangeListener sdStatusChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(KEY_SD_STATUS)) {
                int status = sharedPreferences.getInt(KEY_SD_STATUS, 0);
                switch (status) {
                    case 0:     // nothing
                        sdTitle.setText(R.string.sd_no_sd_card);
                        sdSummer.setText(R.string.sd_no_sd_card_summer);
                        enableFormatPanel(false);
                        break;
                    case 1:     // found sd card
                        sdTitle.setText(R.string.sd_wait_to_enable);
                        sdSummer.setText(R.string.sd_wait_to_enable_summer);
                        enableFormatPanel(false);
                        break;
                    case 2:
                        sdTitle.setText(R.string.sd_can_be_remove);
                        sdSummer.setText(R.string.sd_can_be_remove_summer);
                        enableFormatPanel(true);
                        break;
                }
            }
        }
    };
    private ProgressDialog pd;
    private BroadcastReceiver unmountSuccessReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            WatchCat_Controller wc_ctl = new WatchCat_Controller_Impl();
            try {
                wc_ctl.disableEncryption();
                wc_ctl.unloadBCPT();
                if (wc_ctl.isBcptLoaded() || wc_ctl.queryEncryption()) {
                    Toast.makeText(MainActivity.this, R.string.remove_sd_failed, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, R.string.remove_sd_success, Toast.LENGTH_SHORT).show();
                    pref.edit().putInt(KEY_SD_STATUS, 0).apply();
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                Toast.makeText(MainActivity.this, R.string.remove_sd_failed, Toast.LENGTH_SHORT).show();
            }
            if (pd != null && pd.isShowing())
                pd.dismiss();

            unregisterReceiver(unmountSuccessReceiver);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        ButterKnife.inject(this);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        boolean result = UpgradeSystemPermission.upgradeRootPermission();
        if (!result) {
            usbSwitch.setEnabled(false);
        }

        init();

        pref.registerOnSharedPreferenceChangeListener(sdStatusChangeListener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pref.unregisterOnSharedPreferenceChangeListener(sdStatusChangeListener);
    }

    private void init() {
        bootLoaderSwitch.setChecked(pref.getBoolean(KEY_BOOT_LOADER_LOCK, false));
        keyguardSwitch.setChecked(pref.getBoolean(KEY_KEYGUARD, false));
        usbSwitch.setChecked(pref.getBoolean(KEY_USB, false));

        try {
            int currentStatus = Settings.Global.getInt(this.getContentResolver(), Settings.Global.ADB_ENABLED);
            usbSwitch.setChecked(currentStatus == 1);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }

        int status = pref.getInt(KEY_SD_STATUS, 0);
        switch (status) {
            case 0:     // nothing
                sdTitle.setText(R.string.sd_no_sd_card);
                sdSummer.setText(R.string.sd_no_sd_card_summer);
                break;
            case 1:     // found sd card
                sdTitle.setText(R.string.sd_wait_to_enable);
                sdSummer.setText(R.string.sd_wait_to_enable_summer);
                break;
            case 2:
                sdTitle.setText(R.string.sd_can_be_remove);
                sdSummer.setText(R.string.sd_can_be_remove_summer);
                break;
        }

    }

    @OnCheckedChanged(R.id.main_usb_switch)
    public void usbCheckChanged(CompoundButton buttonView, boolean isChecked) {

        if (usbReEnter) {
            usbReEnter = false;
            return;
        }

        UpgradeSystemPermission.grantSystemPermission(UpgradeSystemPermission.PERMISSION_WRITE_SECURE_SETTINGS);

        boolean result = Settings.Global.putInt(this.getContentResolver(), Settings.Global.ADB_ENABLED, isChecked ? 1 : 0);
        if (!result) {
            usbReEnter = true;
            Toast.makeText(this, R.string.change_usb_error, Toast.LENGTH_SHORT).show();
            usbSwitch.setChecked(!isChecked);
        }
    }

    @OnClick(R.id.main_login_panel)
    public void loginClick(View view) {
        new LoginDialog(this).show();
    }

    @OnClick(R.id.main_sd_panel)
    public void sdPanelClick(View view) {
        int status = pref.getInt(KEY_SD_STATUS, 0);
        final WatchCat_Controller wc_ctl = new WatchCat_Controller_Impl();
        switch (status) {
            case 0:     // nothing
                break;
            case 1:     // found sd card

                if (!checkSDCardExist()) {
                    return;
                }

                new InputSdPasswdDialog(this, new InputSdPasswdDialog.InputFinishedCallback() {
                    @Override
                    public void finished(String passwd, int mode) {

                        if (!checkSDCardExist()) {
                            return;
                        }

                        try {
                            wc_ctl.loadBCPT();
                            wc_ctl.enableEncryption(passwd, mode);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                            Toast.makeText(MainActivity.this, R.string.bcpt_enable_failed, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (wc_ctl.isBcptLoaded() && wc_ctl.queryEncryption()) {
                            Toast.makeText(MainActivity.this, R.string.bcpt_enable_success, Toast.LENGTH_SHORT).show();

                            enableFormatPanel(true);

                            pref.edit().putInt(KEY_SD_STATUS, 2).apply();
                            pref.edit().putString(KEY_SD_PASSWD, passwd).apply();
                            pref.edit().putInt(KEY_ENCRYPT_TYPE, mode).apply();
                        } else {
                            Toast.makeText(MainActivity.this, R.string.bcpt_enable_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();
                break;
            case 2:
                if (!checkSDCardExist()) {
                    return;
                }

                sendBroadcast(new Intent(Common.ACTION_UNMOUNT));
                pd = new ProgressDialog(MainActivity.this);
                pd.setMessage(getResources().getString(R.string.rejecting_sd_card));
                pd.setProgress(0);
                pd.show();
                registerReceiver(unmountSuccessReceiver, new IntentFilter(Common.ACTION_UNMOUNT_SUCCESS));
                break;
        }
    }

    private boolean checkSDCardExist() {
        WatchCat_Controller wc_ctl = new WatchCat_Controller_Impl();
        if (!wc_ctl.isSDCardExist()) {
            Toast.makeText(MainActivity.this, R.string.sd_no_sd_card, Toast.LENGTH_SHORT).show();
            try {
                wc_ctl.disableEncryption();
                wc_ctl.unloadBCPT();
                pref.edit().putInt(KEY_SD_STATUS, 0).apply();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            return false;
        }
        return true;
    }

    private void enableFormatPanel(boolean value) {
        formatPanel.setClickable(value);
        formatTitle.setEnabled(value);
        formatSummer.setEnabled(value);
    }

    @OnClick(R.id.main_sd_format_panel)
    public void formatPanelClick(View view) {
        WatchCat_Controller wc_ctl = new WatchCat_Controller_Impl();
        try {
            wc_ctl.formatEncryptionDisk();
            Toast.makeText(this, R.string.format_success, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            Toast.makeText(this, R.string.format_failed, Toast.LENGTH_SHORT).show();
        }
    }


    @OnCheckedChanged(R.id.main_boot_loader_lock_switch)
    public void bootLoaderCheckChanged(CompoundButton buttonView, boolean isChecked) {
        pref.edit().putBoolean(KEY_BOOT_LOADER_LOCK, isChecked).apply();

        WatchCat_Controller wc_ctl = new WatchCat_Controller_Impl();

        if (isChecked) {
            try {
                wc_ctl.lockFlashLock();
                wc_ctl.loadFsProtector();
                wc_ctl.enableBootloaderWriteProtect();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "lock failed", Toast.LENGTH_SHORT).show();
            }
            if (wc_ctl.queryBootloaderWriteProtect() && wc_ctl.queryFlashLockEnable()) {
                Toast.makeText(this, R.string.lock_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.lock_failed, Toast.LENGTH_SHORT).show();
            }
        } else {
            try {
                wc_ctl.disableBootloaderWriteProtect();
                wc_ctl.unlockFlashLock();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (wc_ctl.queryBootloaderWriteProtect() || wc_ctl.queryFlashLockEnable()) {
                Toast.makeText(this, R.string.bootloader_unlock_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.bootloader_unlock_success, Toast.LENGTH_SHORT).show();
            }
        }
//        if (isChecked) {
//            bootLoaderSummer.setText(R.string.boot_loader_summer_true);
//        } else {
//            bootLoaderSummer.setText(R.string.boot_loader_summer_false);
//        }
    }

//    @OnCheckedChanged(R.id.main_sd_encrypt_switch)
//    public void sdEncryptCheckChanged(CompoundButton buttonView, boolean isChecked) {
//        pref.edit().putBoolean(KEY_SD_ENCRYPT, isChecked).apply();
////        if (isChecked) {
////            sdEncryptSummer.setText(R.string.sd_encrypt_summer_true);
////        } else {
////            sdEncryptSummer.setText(R.string.sd_encrypt_summer_false);
////        }
//    }

    @OnCheckedChanged(R.id.main_keyguard_switch)
    public void keyguardCheckChanged(CompoundButton buttonView, boolean isChecked) {
        pref.edit().putBoolean(KEY_KEYGUARD, isChecked).apply();
        ServiceManager manager = ServiceManager.getInstance();
        if (isChecked) {
            manager.addService(KeyguardService.class);
//            keyguardSummer.setText(R.string.keyguard_enable);
        } else {
            manager.removeService(KeyguardService.class);
//            keyguardSummer.setText(R.string.keyguard_disable);
        }
    }

    @OnClick(R.id.main_draw_panel)
    public void drawPanelClick(View view) {
        Intent intent = new Intent(this, SetDrawPasswdActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.main_sec_passwd_panel)
    public void setPasswdPanelClick(View view) {
        Intent intent = new Intent(this, SetPasswdActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.main_bind_nfc_panel)
    public void nfcPanelClick(View view) {
        Intent intent = new Intent(this, BindNfcActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.main_about_panel)
    public void aboutClick(View view) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.about)
                .setMessage(R.string.about_content)
                .setPositiveButton(R.string.ok, null)
                .create()
                .show();
    }
}
