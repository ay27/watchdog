package bitman.ay27.watchdog.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.*;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.storage.IMountService;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.net.NetManager;
import bitman.ay27.watchdog.service.HeartbeatService;
import bitman.ay27.watchdog.service.KeyguardService;
import bitman.ay27.watchdog.service.ServiceManager;
import bitman.ay27.watchdog.utils.Common;
import bitman.s117.libwatchcat.WatchCat_Controller;
import bitman.s117.libwatchcat.WatchCat_Controller_Impl;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import com.kyleduo.switchbutton.SwitchButton;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MainActivity extends ActionBarActivity {

    public static final String TAG = "MainActivity";
    public static final String KEY_BOOT_LOADER_LOCK = "boot_loader_lock";
    public static final String KEY_SD_ENCRYPT = "sd_encrypt";
    public static final String KEY_KEYGUARD = "keyguard";
    public static final String PREF_NAME = "main_preference";
    public static final String KEY_USB = "usb_debug";
    public static final String KEY_SD_STATE = "sd_state";
    public static final String KEY_SD_PASSWD = "sd_passwd";
    public static final String KEY_ENCRYPT_TYPE = "sd_encrypt_type";
    private static final int CHECK = 0x1;

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

    @InjectView(R.id.main_user_summer)
    TextView userSummer;

//    @InjectView(R.id.main_sd_format_panel)
//    View formatPanel;
//    @InjectView(R.id.main_sd_format_title)
//    TextView formatTitle;
//    @InjectView(R.id.main_sd_format_summer)
//    TextView formatSummer;

    private SharedPreferences pref;
    //    private SdService sdThread;
    private boolean usbReEnter = false;
    private SharedPreferences.OnSharedPreferenceChangeListener sdStatusChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(KEY_SD_STATE)) {
                int status = sharedPreferences.getInt(KEY_SD_STATE, 0);
                switch (status) {
                    case 0:     // nothing
                        sdTitle.setText(R.string.sd_no_sd_card);
                        sdSummer.setText(R.string.sd_no_sd_card_summer);
//                        enableFormatPanel(false);
                        break;
                    case 1:     // found sd card
                        sdTitle.setText(R.string.sd_wait_to_enable);
                        sdSummer.setText(R.string.sd_wait_to_enable_summer);
//                        enableFormatPanel(false);
                        break;
                    case 2:
                        sdTitle.setText(R.string.sd_can_be_remove);
                        sdSummer.setText(R.string.sd_can_be_remove_summer);
//                        enableFormatPanel(true);
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
                    pref.edit().putInt(KEY_SD_STATE, 0).apply();
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                Toast.makeText(MainActivity.this, R.string.remove_sd_failed, Toast.LENGTH_SHORT).show();
            }
            if (pd != null && pd.isShowing())
                pd.dismiss();

            unregisterReceiver(unmountSuccessReceiver);

            if (checkSDCardExist()) {
                pref.edit().putInt(KEY_SD_STATE, 1).apply();
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

    private static IMountService getIMountService(Context context) {
        StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        Class<?> c = StorageManager.class;
        try {
            Field mMountService = c.getField("mMountService");
            mMountService.setAccessible(true);
            return (IMountService) mMountService.get(mStorageManager);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

//    private Intent sdServiceIntent;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHECK && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, R.string.check_error, Toast.LENGTH_LONG).show();
            finish();
        }
    }

//    private ServiceConnection conn = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            sdThread = ((SdService.MBinder)service).getService();
//            sdThread.load();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//
//        }
//    };

    @Override
    protected void onResume() {
        super.onResume();
        WatchCat_Controller wc_ctl = new WatchCat_Controller_Impl();
        if (!wc_ctl.isSDCardExist()) {
            pref.edit().putInt(KEY_SD_STATE, 0).apply();
        } else {
            if (wc_ctl.isBcptLoaded()) {
                if (wc_ctl.queryEncryption()) {
                    pref.edit().putInt(KEY_SD_STATE, 2).apply();
                } else {
                    pref.edit().putInt(KEY_SD_STATE, 1).apply();
                }
            } else {
                pref.edit().putInt(KEY_SD_STATE, 1).apply();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        ButterKnife.inject(this);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        pref.registerOnSharedPreferenceChangeListener(sdStatusChangeListener);


//        sdServiceIntent = new Intent(this, SdService.class);
//        startService();
//        bindService(sdServiceIntent, conn, Context.BIND_AUTO_CREATE);

//        sdThread = new SdThread(this, pref);
//        sdThread.start();
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pref.unregisterOnSharedPreferenceChangeListener(sdStatusChangeListener);
//        unbindService(conn);
//        stopService(sdServiceIntent);
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

        int status = pref.getInt(KEY_SD_STATE, 0);
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

        final String username = pref.getString("username", null);
        if (username != null) {
            NetManager.signIn(username, pref.getString("password", null), new NetManager.NetCallback() {
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

    @OnClick(R.id.main_login_panel)
    public void loginClick(View view) {

        new LoginDialog(this, new LoginDialog.Callback() {
            @Override
            public void onSuccess(String uid, String username, String password) {
                userSummer.setText(getString(R.string.sign_in_success) + ": " + username);
                pref.edit().putString("username", username).apply();
                pref.edit().putString("password", password).apply();
                pref.edit().putString("userId", uid).apply();

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

    @OnCheckedChanged(R.id.main_usb_switch)
    public void usbCheckChanged(CompoundButton buttonView, boolean isChecked) {


//        if (usbReEnter) {
//            usbReEnter = false;
//            return;
//        }
//        boolean result = UpgradeSystemPermission.upgradeRootPermission();
//        if (!result) {
//            usbSwitch.setEnabled(false);
//            usbReEnter = true;
//            return;
//        }
//
//        try {
//            int currentStatus = Settings.Global.getInt(this.getContentResolver(), Settings.Global.ADB_ENABLED);
//            if (currentStatus == 1 && isChecked) {
//                return;
//            }
//            if (currentStatus != 1 && !isChecked) {
//                return;
//            }
//        } catch (Settings.SettingNotFoundException e) {
//            e.printStackTrace();
//            Log.e(TAG, e.toString());
//        }
//
//
//        UpgradeSystemPermission.grantSystemPermission(UpgradeSystemPermission.PERMISSION_WRITE_SECURE_SETTINGS);
//        UpgradeSystemPermission.grantSystemPermission(UpgradeSystemPermission.PERMISSION_MOUNT_UNMOUNT_FS);
//
//        result = Settings.Global.putInt(this.getContentResolver(), Settings.Global.ADB_ENABLED, isChecked ? 1 : 0);
//        if (!result) {
//            usbReEnter = true;
//            Toast.makeText(this, R.string.change_usb_error, Toast.LENGTH_SHORT).show();
//            usbSwitch.setChecked(!isChecked);
//        }

//        try {
//            Class<?> storageManager = Class.forName("android.os.storage.StorageManager");
//            Method disableUsbMassStorage = storageManager.getMethod("disableUsbMassStorage");
//            disableUsbMassStorage.setAccessible(true);
//            Object o = getSystemService("storage");
//            disableUsbMassStorage.invoke(o);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }

//        IMountService service = getIMountService(this);
//        try {
//            service.setUsbMassStorageEnabled(isChecked);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }

//        sendBroadcast(new Intent(Common.ACTION_DISABLE_USB));

//        Intent intent = new Intent(Common.ACTION_FORMAT);
//        intent.putExtra("path", Environment.getExternalStorageDirectory());
//        sendBroadcast(intent);
    }

    @OnClick(R.id.main_sd_panel)
    public void sdPanelClick(View view) {
        int status = pref.getInt(KEY_SD_STATE, 0);
//        switch (status) {
//            case 0:
//                sdThread.load();
//                break;
//            case 1:
//                new InputSdPasswdDialog(this, new InputSdPasswdDialog.InputFinishedCallback() {
//                    @Override
//                    public void finished(String passwd, int mode) {
//                        sdThread.enable(passwd, mode);
//                    }
//                }).show();
//                break;
//            case 2:
//                sdThread.disable();
//                sdThread.unload();
//                break;
//        }
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
                            mountDevice();
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                            Toast.makeText(MainActivity.this, R.string.bcpt_enable_failed, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (wc_ctl.isBcptLoaded() && wc_ctl.queryEncryption()) {
                            Toast.makeText(MainActivity.this, R.string.bcpt_enable_success, Toast.LENGTH_SHORT).show();

//                            enableFormatPanel(true);

                            pref.edit().putInt(KEY_SD_STATE, 2).apply();
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

    private void mountDevice() {
        registerReceiver(mountSuccessReceiver, new IntentFilter(Common.ACTION_MOUNT_SUCCESS));
        sendBroadcast(new Intent(Common.ACTION_MOUNT));
    }

    private boolean checkSDCardExist() {
        WatchCat_Controller wc_ctl = new WatchCat_Controller_Impl();
        if (!wc_ctl.isSDCardExist()) {
            Toast.makeText(MainActivity.this, R.string.sd_no_sd_card, Toast.LENGTH_SHORT).show();
            try {
                wc_ctl.disableEncryption();
                wc_ctl.unloadBCPT();
                pref.edit().putInt(KEY_SD_STATE, 0).apply();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            return false;
        }
        return true;
    }

//    private void enableFormatPanel(boolean value) {
//        formatPanel.setEnabled(value);
//        formatPanel.setClickable(value);
//        formatTitle.setEnabled(value);
//        formatTitle.setClickable(value);
//        formatSummer.setEnabled(value);
//        formatSummer.setClickable(value);
//    }

    //    @OnClick(R.id.main_sd_format_panel)
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

    @OnClick(R.id.main_bind_watch_panel)
    public void watchClick(View view) {
        Intent intent = new Intent(this, BindWatchActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.main_bind_nfc_panel)
    public void nfcClick(View view) {
        Intent intent = new Intent(this, BindNfcActivity.class);
        startActivity(intent);
    }

}
