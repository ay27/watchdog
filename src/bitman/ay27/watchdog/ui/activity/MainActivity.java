package bitman.ay27.watchdog.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.service.KeyguardService;
import bitman.ay27.watchdog.service.ServiceManager;
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

    @InjectView(R.id.main_toolbar)
    Toolbar toolbar;
    //    @InjectView(R.id.main_boot_loader_summer)
//    TextView bootLoaderSummer;
    @InjectView(R.id.main_boot_loader_lock_switch)
    SwitchButton bootLoaderSwitch;
    //    @InjectView(R.id.main_sd_encrypt_summer)
//    TextView sdEncryptSummer;
    @InjectView(R.id.main_sd_encrypt_switch)
    SwitchButton sdEncryptSwitch;
    //    @InjectView(R.id.main_keyguard_summer)
//    TextView keyguardSummer;
    @InjectView(R.id.main_keyguard_switch)
    SwitchButton keyguardSwitch;
    //    @InjectView(R.id.main_usb_summer)
//    TextView usbSummer;
    @InjectView(R.id.main_usb_switch)
    SwitchButton usbSwitch;

    private SharedPreferences pref;
    private boolean usbReEnter = false;
    private WatchCat_Controller wc_ctl;

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

        wc_ctl = new WatchCat_Controller_Impl();
    }

    private void init() {
        bootLoaderSwitch.setChecked(pref.getBoolean(KEY_BOOT_LOADER_LOCK, false));
        sdEncryptSwitch.setChecked(pref.getBoolean(KEY_SD_ENCRYPT, false));
        keyguardSwitch.setChecked(pref.getBoolean(KEY_KEYGUARD, false));
        usbSwitch.setChecked(pref.getBoolean(KEY_USB, false));

        try {
            int currentStatus = Settings.Global.getInt(this.getContentResolver(), Settings.Global.ADB_ENABLED);
            usbSwitch.setChecked(currentStatus == 1);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }

    }

    @OnCheckedChanged(R.id.main_usb_switch)
    public void usbCheckChanged(CompoundButton buttonView, boolean isChecked) {

//        if (isChecked) {
//            usbSummer.setText(R.string.usb_enable);
//        } else {
//            usbSummer.setText(R.string.usb_disable);
//        }

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

    @OnCheckedChanged(R.id.main_boot_loader_lock_switch)
    public void bootLoaderCheckChanged(CompoundButton buttonView, boolean isChecked) {
        pref.edit().putBoolean(KEY_BOOT_LOADER_LOCK, isChecked).apply();

        if (isChecked) {
            wc_ctl.loadFsProtector();
            wc_ctl.enableBootloaderWriteProtect();
        }
//        if (isChecked) {
//            bootLoaderSummer.setText(R.string.boot_loader_summer_true);
//        } else {
//            bootLoaderSummer.setText(R.string.boot_loader_summer_false);
//        }
    }

    @OnCheckedChanged(R.id.main_sd_encrypt_switch)
    public void sdEncryptCheckChanged(CompoundButton buttonView, boolean isChecked) {
        pref.edit().putBoolean(KEY_SD_ENCRYPT, isChecked).apply();
//        if (isChecked) {
//            sdEncryptSummer.setText(R.string.sd_encrypt_summer_true);
//        } else {
//            sdEncryptSummer.setText(R.string.sd_encrypt_summer_false);
//        }
    }

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
