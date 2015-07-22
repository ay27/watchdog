package bitman.ay27.watchdog.ui.new_activity;

import android.app.ProgressDialog;
import android.content.*;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sd_encryptor);
        ButterKnife.inject(this);

        toolbar.setTitle(R.string.sd_lockr);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        PrefUtils.registerListener(sdStatusChangedListener);

        updateSdStatus();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PrefUtils.unregisterListener(sdStatusChangedListener);
    }


    public void sdActionClick(View view) {
        int state = PrefUtils.getSdState();
        updateSdStatus();

        if (state == 0) {
            exitWithNoSdCard();
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
             * 1. call format function
             * 2. send UNMOUNT broadcast, unmount the sd card
             * 3. re-mount and enable the encryption
             * 4. receive the MOUNT broadcast, it means all the process finish
             */

            wctl.formatEncryptionDisk();


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
                        wctl.disableEncryption();
                        wctl.unloadBCPT();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                wctl.loadBCPT();
                                wctl.enableEncryption(PrefUtils.getSdPasswd(), PrefUtils.getSdEncryptType());
                                registerReceiver(mountSuccessReceiver, new IntentFilter(Common.ACTION_MOUNT_SUCCESS));
                                sendBroadcast(new Intent(Common.ACTION_MOUNT));
                            }
                        }, 3000);
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
            return;
        }
    }


    private void removeSd() {
        if (!wctl.isSDCardExist()) {
            exitWithNoSdCard();
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
                    exitWithNoSdCard();
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

    private void exitWithNoSdCard() {
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

        switch (statusCode) {
            case 000:
                Toast.makeText(this, R.string.sd_no_sd_card, Toast.LENGTH_SHORT).show();
                PrefUtils.setSdState(0);
                finish();
                break;
            case 001:
                planC();
                break;
            case 010:
                planB();
                break;
            case 011:
                planA();
                break;
            case 100:
            case 110:
                sdEncryptorSdStatus.setText(R.string.sd_wait_to_enable);
                sdEncryptorSdStatus.setTextColor(getResources().getColor(R.color.red_1));
                sdEncryptorActionTitle.setText(R.string.sd_action_load);
                sdEncryptorActionSummer.setText(R.string.sd_action_load_summer);
                setFormatPanel(false);
                PrefUtils.setSdState(1);
                break;
            case 101:
                planC();
                break;
            case 111:
                sdEncryptorSdStatus.setText(R.string.sd_can_be_remove);
                sdEncryptorSdStatus.setTextColor(getResources().getColor(R.color.green_1));
                sdEncryptorActionTitle.setText(R.string.sd_action_remove);
                sdEncryptorActionSummer.setText(R.string.sd_action_remove_summer);
                setFormatPanel(true);
                PrefUtils.setSdState(2);
                break;
        }
    }

    private void setFormatPanel(boolean value) {
        sdEncryptorFormatPanel.setEnabled(value);
        sdEncryptorFormatPanel.setClickable(value);
        sdEncryptorFormatTitle.setEnabled(value);
        sdEncryptorFormatSummer.setEnabled(value);
    }

    private void planA() {
        wctl.umountSDCardCmd();
        wctl.disableEncryption();
        wctl.unloadBCPT();
        updateSdStatus();
    }

    private void planB() {
        wctl.unloadBCPT();
        updateSdStatus();
    }

    private void planC() {
        wctl.loadBCPT();
        planA();
    }

}
