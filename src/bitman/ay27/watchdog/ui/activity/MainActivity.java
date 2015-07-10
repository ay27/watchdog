package bitman.ay27.watchdog.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.*;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import bitman.ay27.watchdog.PrefUtils;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.net.NetManager;
import bitman.ay27.watchdog.service.HeartbeatService;
import bitman.ay27.watchdog.service.KeyguardService;
import bitman.ay27.watchdog.service.ServiceManager;
import bitman.ay27.watchdog.utils.Common;
import bitman.ay27.watchdog.utils.UpgradeSystemPermission;
import bitman.s117.libwatchcat.WatchCat_Controller;
import bitman.s117.libwatchcat.WatchCat_Controller_Impl;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.kyleduo.switchbutton.SwitchButton;

public class MainActivity extends ActionBarActivity {

    public static final String TAG = "MainActivity";
    private static final int CHECK = 0x1;

    @InjectView(R.id.main_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.main_user_summer)
    TextView userSummer;
    @InjectView(R.id.main_sd_title)
    TextView sdTitle;
    @InjectView(R.id.main_sd_summer)
    TextView sdSummer;
    @InjectView(R.id.main_boot_loader_lock_switch)
    SwitchButton bootLoaderSwitch;
    @InjectView(R.id.main_keyguard_switch)
    SwitchButton keyguardSwitch;
    @InjectView(R.id.main_usb_switch)
    SwitchButton usbSwitch;
    @InjectView(R.id.main_sd_format_panel)
    View formatPanel;
    @InjectView(R.id.main_sd_format_title)
    TextView formatTitle;
    @InjectView(R.id.main_sd_format_summer)
    TextView formatSummer;

    private SharedPreferences.OnSharedPreferenceChangeListener sdStatusChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            setSdPanel();
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
                    PrefUtils.setSdState(0);
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                Toast.makeText(MainActivity.this, R.string.remove_sd_failed, Toast.LENGTH_SHORT).show();
            }
            if (pd != null && pd.isShowing())
                pd.dismiss();

            unregisterReceiver(unmountSuccessReceiver);

            if (checkSDCardExist()) {
                PrefUtils.setSdState(1);
            }
        }
    };

    private BroadcastReceiver mountSuccessReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(MainActivity.this, "mount ok", Toast.LENGTH_SHORT).show();
            unregisterReceiver(mountSuccessReceiver);
        }
    };


    private CompoundButton.OnCheckedChangeListener bootLoaderCheckChanged = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            PrefUtils.setBootLoaderEnable(isChecked);

            WatchCat_Controller wc_ctl = new WatchCat_Controller_Impl();

            if (isChecked) {
                try {
                    wc_ctl.lockFlashLock();
                    wc_ctl.loadFsProtector();
                    wc_ctl.enableBootloaderWriteProtect();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "lock failed", Toast.LENGTH_SHORT).show();
                }
                if (wc_ctl.queryBootloaderWriteProtect() && wc_ctl.queryFlashLockEnable()) {
                    Toast.makeText(MainActivity.this, R.string.lock_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, R.string.lock_failed, Toast.LENGTH_SHORT).show();
                }
            } else {
                try {
                    wc_ctl.disableBootloaderWriteProtect();
                    wc_ctl.unlockFlashLock();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (wc_ctl.queryBootloaderWriteProtect() || wc_ctl.queryFlashLockEnable()) {
                    Toast.makeText(MainActivity.this, R.string.bootloader_unlock_failed, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, R.string.bootloader_unlock_success, Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    private CompoundButton.OnCheckedChangeListener keyguardCheckChanged = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            PrefUtils.setKeyguardEnable(isChecked);
            ServiceManager manager = ServiceManager.getInstance();
            if (isChecked) {
                manager.addService(KeyguardService.class);
//            keyguardSummer.setText(R.string.keyguard_enable);
            } else {
                manager.removeService(KeyguardService.class);
//            keyguardSummer.setText(R.string.keyguard_disable);
            }
        }
    };
    private CompoundButton.OnCheckedChangeListener usbCheckChanged = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            PrefUtils.setUsbEnable(isChecked);
            if (!isChecked) {
                UpgradeSystemPermission.runCmd("echo 0 > /sys/devices/virtual/android_usb/android0/enable");
                return;
            }
            UpgradeSystemPermission.runCmd("echo 1 > /sys/devices/virtual/android_usb/android0/enable");

            // make the default function is MTP
            sendBroadcast(new Intent(Common.ACTION_CHOOSE_MTP));
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        ButterKnife.inject(this);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);


        PrefUtils.registerListener(sdStatusChangeListener);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        WatchCat_Controller wc_ctl = new WatchCat_Controller_Impl();
        if (!wc_ctl.isSDCardExist()) {
            PrefUtils.setSdState(0);
            enableFormatPanel(false);
        } else {
            if (wc_ctl.isBcptLoaded()) {
                if (wc_ctl.queryEncryption()) {
                    PrefUtils.setSdState(2);
                    enableFormatPanel(true);
                } else {
                    PrefUtils.setSdState(1);
                    enableFormatPanel(false);
                }
            } else {
                PrefUtils.setSdState(1);
                enableFormatPanel(false);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHECK && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, R.string.check_error, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PrefUtils.unregisterListener(sdStatusChangeListener);
    }

    private void init() {
        bootLoaderSwitch.setChecked(PrefUtils.isBootloaderEnable());
        keyguardSwitch.setChecked(PrefUtils.isKeyguardEnable());
        usbSwitch.setChecked(PrefUtils.isUsbEnable());

        setSdPanel();

        bootLoaderSwitch.setOnCheckedChangeListener(bootLoaderCheckChanged);
        keyguardSwitch.setOnCheckedChangeListener(keyguardCheckChanged);
        usbSwitch.setOnCheckedChangeListener(usbCheckChanged);

        tryLogin();

    }

    private void setSdPanel() {
        int state = PrefUtils.getSdState();
        switch (state) {
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

    @OnClick(R.id.main_login_panel)
    void loginClick(View view) {

        new LoginDialog(this, new LoginDialog.Callback() {
            @Override
            public void onSuccess(String uid, String username, String password) {
                userSummer.setText(getString(R.string.sign_in_success) + ": " + username);
                PrefUtils.setUserName(username);
                PrefUtils.setUserPasswd(password);
                PrefUtils.setUserId(uid);

                NetManager.online();
                ServiceManager manager = ServiceManager.getInstance();
                manager.addService(HeartbeatService.class);

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.bind_device)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.bind, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NetManager.bind(new NetManager.NetCallback() {
                                    @Override
                                    public void onSuccess(int code, String recv) {
                                        Toast.makeText(MainActivity.this, R.string.bind_success, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onError(int code, String recv, Throwable throwable) {
                                        Toast.makeText(MainActivity.this, R.string.bind_failed, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .create()
                        .show();
            }

            @Override
            public void onFailed() {
                userSummer.setText(R.string.sign_in_failed);
            }
        }).show();
    }


    @OnClick(R.id.main_sd_panel)
    void sdPanelClick(View view) {
        int state = PrefUtils.getSdState();
        final WatchCat_Controller wc_ctl = new WatchCat_Controller_Impl();
        switch (state) {
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
                            PrefUtils.setSdPasswd(passwd);
                            PrefUtils.setSdEncryptType(mode);
                            registerReceiver(mountSuccessReceiver, new IntentFilter(Common.ACTION_MOUNT_SUCCESS));
                            sendBroadcast(new Intent(Common.ACTION_MOUNT));
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                            Toast.makeText(MainActivity.this, R.string.bcpt_enable_failed, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (wc_ctl.isBcptLoaded() && wc_ctl.queryEncryption()) {
                            Toast.makeText(MainActivity.this, R.string.bcpt_enable_success, Toast.LENGTH_SHORT).show();

                            enableFormatPanel(true);

                            PrefUtils.setSdState(2);
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

    @OnClick(R.id.main_sd_format_panel)
    void formatClick(View view) {
        WatchCat_Controller wc_ctl = new WatchCat_Controller_Impl();
        try {
            wc_ctl.formatEncryptionDisk();
            Toast.makeText(this, R.string.format_success, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            Toast.makeText(
                    this, R.string.format_failed, Toast.LENGTH_SHORT).show();
        }
    }


    @OnClick(R.id.main_draw_panel)
    void drawPanelClick(View view) {
        Intent intent = new Intent(this, SetDrawPasswdActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.main_sec_passwd_panel)
    void setPasswdPanelClick(View view) {
        Intent intent = new Intent(this, SetPasswdActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.main_bind_watch_panel)
    void watchClick(View view) {
        Intent intent = new Intent(this, BindWatchActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.main_bind_nfc_panel)
    void nfcClick(View view) {
        Intent intent = new Intent(this, BindNfcActivity.class);
        startActivity(intent);
    }


    private void tryLogin() {
        final String username = PrefUtils.getUserName();
        if (!username.isEmpty()) {
            NetManager.signIn(username, PrefUtils.getUserPasswd(), new NetManager.NetCallback() {
                @Override
                public void onSuccess(int code, String recv) {
                    userSummer.setText(getString(R.string.sign_in_success) + ": " + username);

                    NetManager.online();
                    ServiceManager manager = ServiceManager.getInstance();
                    manager.addService(HeartbeatService.class);
                }

                @Override
                public void onError(int code, String recv, Throwable throwable) {
                    userSummer.setText(R.string.sign_in_failed);
                }
            });
        }
    }

    private void enableFormatPanel(boolean value) {
        formatPanel.setEnabled(value);
        formatPanel.setClickable(value);
        formatTitle.setEnabled(value);
        formatTitle.setClickable(value);
        formatSummer.setEnabled(value);
        formatSummer.setClickable(value);
    }

    private boolean checkSDCardExist() {
        WatchCat_Controller wc_ctl = new WatchCat_Controller_Impl();
        if (!wc_ctl.isSDCardExist()) {
            Toast.makeText(MainActivity.this, R.string.sd_no_sd_card, Toast.LENGTH_SHORT).show();
            try {
                wc_ctl.disableEncryption();
                wc_ctl.unloadBCPT();
                PrefUtils.setSdState(0);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            return false;
        }
        return true;
    }

}
