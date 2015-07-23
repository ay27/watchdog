package bitman.ay27.watchdog.ui.new_activity;

import android.app.ProgressDialog;
import android.content.*;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import bitman.ay27.watchdog.PrefUtils;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.ui.activity.widget.InputSdPasswdDialog;
import bitman.ay27.watchdog.utils.Common;
import bitman.s117.libwatchcat.WatchCat_Controller;
import bitman.s117.libwatchcat.WatchCat_Controller_Impl;
import butterknife.ButterKnife;
import butterknife.InjectView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-18.
 */
public class SdEncryptorActivity extends ActionBarActivity {

    private static final String TAG = "SdEncryptorActivity";

    @InjectView(R.id.sd_encryptor_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.sd_encryptor_sd_status)
    TextView sdEncryptorSdStatus;
    @InjectView(R.id.sd_encryptor_action_panel)
    LinearLayout sdEncryptorActionPanel;
    @InjectView(R.id.sd_encryptor_format_panel)
    LinearLayout sdEncryptorFormatPanel;
    @InjectView(R.id.sd_encryptor_action_title)
    TextView sdEncryptorActionTitle;
    @InjectView(R.id.sd_encryptor_action_summer)
    TextView sdEncryptorActionSummer;
    @InjectView(R.id.sd_encryptor_format_title)
    TextView sdEncryptorFormatTitle;
    @InjectView(R.id.sd_encryptor_format_summer)
    TextView sdEncryptorFormatSummer;

    private WatchCat_Controller wctl = new WatchCat_Controller_Impl();

    private SharedPreferences.OnSharedPreferenceChangeListener sdStatusChangedListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(PrefUtils.KEY_SD_STATE)) {
                updateSdStatus();
            }
        }
    };
    private Timer timer;
    private boolean sdStatus = wctl.isSDCardExist();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sd_encryptor);
        ButterKnife.inject(this);

        toolbar.setTitle(R.string.sd_lockr);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

//        PrefUtils.registerListener(sdStatusChangedListener);

        updateSdStatus();

        startScanSdExistTask();

    }

    private void startScanSdExistTask() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (sdStatus != wctl.isSDCardExist()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateSdStatus();
                        }
                    });
                    sdStatus = !sdStatus;
                }
            }
        }, 0, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        PrefUtils.unregisterListener(sdStatusChangedListener);
        if (timer != null) {
            timer.cancel();
        }
    }


    public void sdActionClick(View view) {
        int state = PrefUtils.getSdState();
        updateSdStatus();

        if (state == 0) {
            noSdCard();
        } else if (state == 1) {
            showInputSdPasswdDialog();
        } else if (state == 2) {
            removeSd();
        } else {
            updateSdStatus();
        }
    }

    public void sdFormatClick(View view) {
        try {
            /**
             * reEnable the encryption
             * 1. send UNMOUNT broadcast, unmount the sd card
             * 2. format sd card
             * 3. re-mount
             * 4. receive the MOUNT broadcast, it means all the process finish
             */

//            wctl.formatEncryptionDisk();


            final ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage(getString(R.string.sd_formating));
            pd.setCancelable(false);
            pd.show();

            final BroadcastReceiver mountSuccessReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    unregisterReceiver(this);
                    if (pd.isShowing())
                        pd.dismiss();

                    updateSdStatus();

                    Toast.makeText(SdEncryptorActivity.this, R.string.format_success, Toast.LENGTH_SHORT).show();
                }
            };

            BroadcastReceiver unmountSuccessReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    try {
                        wctl.formatEncryptionDisk();
                        registerReceiver(mountSuccessReceiver, new IntentFilter(Common.ACTION_MOUNT_SUCCESS));
                        sendBroadcast(new Intent(Common.ACTION_MOUNT));
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                        Toast.makeText(SdEncryptorActivity.this, R.string.format_failed, Toast.LENGTH_SHORT).show();
                        if (pd.isShowing()) {
                            pd.cancel();
                        }
                        return;
                    }
                    unregisterReceiver(this);
                }
            };
            registerReceiver(unmountSuccessReceiver, new IntentFilter(Common.ACTION_UNMOUNT_SUCCESS));
            sendBroadcast(new Intent(Common.ACTION_UNMOUNT));

        } catch (Exception e) {
            Log.e(TAG, e.toString());
            Toast.makeText(this, R.string.format_failed, Toast.LENGTH_SHORT).show();
        }
    }


    private void removeSd() {
        if (!wctl.isSDCardExist()) {
            noSdCard();
            return;
        }

        /**
         * 1. send unmount broadcast, will be deal in hook/HookMountService
         * 2. when the HookMountServie process finish, will send a ACTION_UNMOUNT_SUCCESS broadcast, here, we can unload the BCPT
         */

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getResources().getString(R.string.rejecting_sd_card));
        pd.setProgress(0);
        pd.show();

        BroadcastReceiver unmountSuccessReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    wctl.disableEncryption();
                    wctl.unloadBCPT();
                    if (wctl.isBcptLoaded() || wctl.queryEncryption()) {
                        Toast.makeText(SdEncryptorActivity.this, R.string.remove_sd_failed, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SdEncryptorActivity.this, R.string.remove_sd_success, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                    Toast.makeText(SdEncryptorActivity.this, R.string.remove_sd_failed, Toast.LENGTH_SHORT).show();
                }
                if (pd.isShowing())
                    pd.dismiss();

                updateSdStatus();

                unregisterReceiver(this);
            }
        };
        registerReceiver(unmountSuccessReceiver, new IntentFilter(Common.ACTION_UNMOUNT_SUCCESS));
        sendBroadcast(new Intent(Common.ACTION_UNMOUNT));
    }

    private void showInputSdPasswdDialog() {
        new InputSdPasswdDialog(this, new InputSdPasswdDialog.InputFinishedCallback() {
            @Override
            public void finished(String passwd, int mode) {

                if (!wctl.isSDCardExist()) {
                    noSdCard();
                    return;
                }

                try {

                    BroadcastReceiver mountSuccessReceiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            updateSdStatus();
                            unregisterReceiver(this);
                        }
                    };

                    /**
                     * 1. load driver
                     * 2. enable encryption
                     * 3. send the MOUNT action, this action will be deal with the hoot/HookMountService
                     * 4. check if the action is success
                     */
                    wctl.loadBCPT();
                    wctl.enableEncryption(passwd, mode);
                    registerReceiver(mountSuccessReceiver, new IntentFilter(Common.ACTION_MOUNT_SUCCESS));
                    sendBroadcast(new Intent(Common.ACTION_MOUNT));
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                    Toast.makeText(SdEncryptorActivity.this, R.string.bcpt_enable_failed, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (wctl.isBcptLoaded() && wctl.queryEncryption()) {
                    PrefUtils.setSdPasswd(passwd);
                    PrefUtils.setSdEncryptType(mode);

                    Toast.makeText(SdEncryptorActivity.this, R.string.bcpt_enable_success, Toast.LENGTH_SHORT).show();
                    updateSdStatus();

                } else {
                    Toast.makeText(SdEncryptorActivity.this, R.string.bcpt_enable_failed, Toast.LENGTH_SHORT).show();
                }
            }
        }).show();
    }

    private void noSdCard() {
        Toast.makeText(this, R.string.sd_no_sd_card, Toast.LENGTH_SHORT).show();
        PrefUtils.setSdState(0);
//        finish();
    }


    /**
     * XYZ:
     * X:SdCardExist
     * Y:DriverExist
     * Z:EncryptionExist
     */
    private void updateSdStatus() {
        int statusCode = 0;

        statusCode += wctl.queryEncryption() ? 1 : 0;
        statusCode += wctl.isBcptLoaded() ? 10 : 0;
        statusCode += wctl.isSDCardExist() ? 100 : 0;

        Log.i(TAG, "status code : " + statusCode);

        switch (statusCode) {
            case 0:
                noSdCard();
                sdEncryptorSdStatus.setText(R.string.sd_no_sd_card);
                sdEncryptorSdStatus.setTextColor(getResources().getColor(R.color.red_1));
                setActionPanel(false);
                setFormatPanel(false);
                PrefUtils.setSdState(1);
                break;
            case 1:
                planC();
                break;
            case 10:
                planB();
                break;
            case 11:
                planA();
                break;
            case 100:
            case 110:
                setActionPanel(true);
                setFormatPanel(false);
                sdEncryptorSdStatus.setText(R.string.sd_wait_to_enable);
                sdEncryptorSdStatus.setTextColor(getResources().getColor(R.color.red_1));
                sdEncryptorActionTitle.setText(R.string.sd_action_load);
                sdEncryptorActionSummer.setText(R.string.sd_action_load_summer);
                PrefUtils.setSdState(1);
                break;
            case 101:
                planC();
                break;
            case 111:
                setActionPanel(true);
                setFormatPanel(true);
                sdEncryptorSdStatus.setText(R.string.sd_can_be_remove);
                sdEncryptorSdStatus.setTextColor(getResources().getColor(R.color.green_1));
                sdEncryptorActionTitle.setText(R.string.sd_action_remove);
                sdEncryptorActionSummer.setText(R.string.sd_action_remove_summer);
                PrefUtils.setSdState(2);
                break;
        }
    }

    private void setActionPanel(boolean value) {
        sdEncryptorActionPanel.setEnabled(value);
        sdEncryptorActionPanel.setClickable(value);
        sdEncryptorActionTitle.setEnabled(value);
        sdEncryptorActionSummer.setEnabled(value);
    }

    private void setFormatPanel(boolean value) {
        sdEncryptorFormatPanel.setEnabled(value);
        sdEncryptorFormatPanel.setClickable(value);
        sdEncryptorFormatTitle.setEnabled(value);
        sdEncryptorFormatSummer.setEnabled(value);
    }

    private void planA() {
//        wctl.umountSDCardCmd();
        BroadcastReceiver unmountReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                wctl.disableEncryption();
                wctl.unloadBCPT();
                updateSdStatus();
                unregisterReceiver(this);
            }
        };
        registerReceiver(unmountReceiver, new IntentFilter(Common.ACTION_UNMOUNT_SUCCESS));
        sendBroadcast(new Intent(Common.ACTION_UNMOUNT));
    }

    private void planB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                wctl.unloadBCPT();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateSdStatus();
                    }
                });
            }
        }).start();
    }

    private void planC() {
        wctl.loadBCPT();
        planA();
    }

}
