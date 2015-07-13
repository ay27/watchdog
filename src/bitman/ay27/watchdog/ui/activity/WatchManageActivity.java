package bitman.ay27.watchdog.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import bitman.ay27.watchdog.PrefUtils;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.ui.activity.widget.ChooseDistDialog;
import bitman.ay27.watchdog.ui.activity.widget.ScanBleDialog;
import bitman.ay27.watchdog.watchlink.DefaultDogWatchCallback;
import bitman.ay27.watchdog.watchlink.DogWatchCallback;
import bitman.ay27.watchdog.watchlink.DogWatchService;
import butterknife.ButterKnife;
import butterknife.InjectView;

import java.util.*;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/7.
 */
public class WatchManageActivity extends Activity {


    private static final String TAG = "WatchManagerActivity";
    @InjectView(R.id.watch_manager_bind_btn)
    Button bindBtn;
    @InjectView(R.id.watch_manager_new_panel)
    LinearLayout bindWatchPanel;
    @InjectView(R.id.watch_manager_current_distance)
    TextView currentDistanceTxv;
    @InjectView(R.id.watch_manager_current_panel)
    LinearLayout currentPanel;
    @InjectView(R.id.watch_manager_find_watch)
    LinearLayout findWatchPanel;
    @InjectView(R.id.watch_manager_set_safe_distance)
    LinearLayout setSafeDistancePanel;
    @InjectView(R.id.watch_manager_correct_distance)
    LinearLayout correctDistancePanel;
    @InjectView(R.id.watch_manager_correct_time)
    LinearLayout correctTimePanel;
    @InjectView(R.id.watch_manager_detail)
    LinearLayout detailPanel;
    @InjectView(R.id.watch_manager_dis_bind)
    LinearLayout unBindPanel;
    @InjectView(R.id.watch_manager_setting_panel)
    LinearLayout settingPanel;
    @InjectView(R.id.watch_manager_find_watch_txv)
    TextView findWatchTxv;
    @InjectView(R.id.watch_manager_find_watch_summer)
    TextView findWatchSummer;
    @InjectView(R.id.watch_manager_safe_dist_txv)
    TextView safeDistTxv;
    @InjectView(R.id.watch_manager_safe_dist_summer)
    TextView safeDistSummer;
    @InjectView(R.id.watch_manager_correct_dist_txv)
    TextView correctDistTxv;
    @InjectView(R.id.watch_manager_correct_dist_summer)
    TextView correctDistSummer;
    @InjectView(R.id.watch_manager_correct_time_txv)
    TextView correctTimeTxv;
    @InjectView(R.id.watch_manager_correct_time_summer)
    TextView correctTimeSummer;
    @InjectView(R.id.watch_manager_detail_txv)
    TextView detailTxv;
    @InjectView(R.id.watch_manager_detail_summer)
    TextView detailSummer;
    @InjectView(R.id.watch_manager_unbind_txv)
    TextView unbindTxv;
    private ProgressDialog pd;
    private DogWatchService dogWatchService;

    private Runnable correctDistRunnable = new Runnable() {
        @Override
        public void run() {

            double rssi = dogWatchService.getAvgRSSI();
            dogWatchService.post(DogWatchService.CHARA_RF_CALIBRATE, new byte[]{(byte) rssi});

            if (pd.isShowing()) {
                pd.cancel();
            }
        }
    };

    private DialogInterface.OnClickListener unbindWatchListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dogWatchService.disconnect();
        }
    };

    private DogWatchCallback dogWatchCallback = new DefaultDogWatchCallback() {
        @Override
        public void onPostFinish(final int name, UUID characUUID, final byte[] remoteVal) {
            super.onPostFinish(name, characUUID, remoteVal);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(WatchManageActivity.this, "on post: " + name + " " + Arrays.toString(remoteVal), Toast.LENGTH_SHORT).show();
                    if (name == DogWatchService.CHARA_TIME_YEAR && pd.isShowing()) {
                        pd.cancel();
                    }
                }
            }, 20);
        }

        @Override
        public void onPostFail(final String reason) {
            super.onPostFail(reason);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(WatchManageActivity.this, reason, Toast.LENGTH_SHORT).show();
                }
            }, 20);
        }

        @Override
        public void onGetFinish(final int name, UUID characUUID, final byte[] remoteVal) {
            super.onGetFinish(name, characUUID, remoteVal);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(WatchManageActivity.this, "on get: " + name + " " + Arrays.toString(remoteVal), Toast.LENGTH_SHORT).show();
                }
            }, 20);
        }

        @Override
        public void onGetFail(final String reason) {
            super.onGetFail(reason);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(WatchManageActivity.this, reason, Toast.LENGTH_SHORT).show();
                }
            }, 20);
        }

        @Override
        public void onDisconnectFail(final String reason) {
            super.onDisconnectFail(reason);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(WatchManageActivity.this, "disconnect failed "+reason, Toast.LENGTH_SHORT).show();
                }
            }, 20);
        }

        @Override
        public void onDisconnected() {
            super.onDisconnected();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bindWatchPanel.setVisibility(View.VISIBLE);
                    currentPanel.setVisibility(View.GONE);
                    setSettingEnable(false);
                    timer.cancel();

                    PrefUtils.setBLEAddr("");

                    Toast.makeText(WatchManageActivity.this, "disconnect", Toast.LENGTH_SHORT).show();
                }
            }, 20);
        }

        @Override
        public void onConnectedFail(final String reason) {
            super.onConnectedFail(reason);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(WatchManageActivity.this, reason, Toast.LENGTH_SHORT).show();
                }
            }, 20);
        }

        @Override
        public void onConnected(final String address, boolean isAutoConnEnable) {
            super.onConnected(address, isAutoConnEnable);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bindWatchPanel.setVisibility(View.GONE);
                    currentPanel.setVisibility(View.VISIBLE);
                    setSettingEnable(true);
                    PrefUtils.setBLEAddr(address);
                    setUpDistView();
                }
            }, 20);
        }
    };

    private Timer timer;
    private void setUpDistView() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentDistanceTxv.setText("" + String.format("%02f", dogWatchService.calcAccuracy()) + getString(R.string.distance_unit));
                    }
                });
            }
        };
        timer = new Timer();
        timer.schedule(task, 0, 2000);
    }

    private Handler handler = new Handler();

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service == null) {
                Toast.makeText(WatchManageActivity.this, "unbind service first", Toast.LENGTH_SHORT).show();
                return;
            }
            dogWatchService = ((DogWatchService.LocalBinder) service).getService();
            dogWatchService.initialize(dogWatchCallback);
            if (dogWatchService.getConnectionState() != DogWatchService.STATE_DISCONNECTED) {
                return;
            }
            if (!PrefUtils.getBLEAddr().isEmpty()) {
                dogWatchService.connect(PrefUtils.getBLEAddr(), true);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "service disconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watch_manager);
        ButterKnife.inject(this);

        String addr = PrefUtils.getBLEAddr();
        if (addr.isEmpty()) {
            bindWatchPanel.setVisibility(View.VISIBLE);
            currentPanel.setVisibility(View.GONE);
            setSettingEnable(false);
        } else {
            bindWatchPanel.setVisibility(View.GONE);
            currentPanel.setVisibility(View.VISIBLE);
            setSettingEnable(true);
        }

        bindService(new Intent(this, DogWatchService.class), conn, Context.BIND_AUTO_CREATE);
//        setSettingEnable(false);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        if (timer!=null) {
            timer.cancel();
        }
    }

    private void setSettingEnable(boolean flag) {
        setEnable(findWatchPanel, flag);
        setEnable(findWatchTxv, flag);
        setEnable(findWatchSummer, flag);

        setEnable(setSafeDistancePanel, flag);
        setEnable(safeDistTxv, flag);
        setEnable(safeDistSummer, flag);

        setEnable(correctDistancePanel, flag);
        setEnable(correctDistTxv, flag);
        setEnable(correctDistSummer, flag);

        setEnable(correctTimePanel, flag);
        setEnable(correctTimeTxv, flag);
        setEnable(correctTimeSummer, flag);

        setEnable(detailPanel, flag);
        setEnable(detailTxv, flag);
        setEnable(detailSummer, flag);

        setEnable(unBindPanel, flag);
        setEnable(unbindTxv, flag);

        setEnable(settingPanel, flag);
    }

    private void setEnable(View view, boolean flag) {
        view.setEnabled(flag);
        view.setClickable(flag);
    }

    public void bindWatchClick(View view) {
        new ScanBleDialog(this, new ScanBleDialog.ScanBtDeviceCallback() {
            @Override
            public void onResult(BluetoothDevice device) {
                if (device != null) {
                    if (dogWatchService == null) {
                        return;
                    }
                    dogWatchService.connect(device.getAddress(), true);
                }
            }
        }).show();
    }

    public void findWatchClick(View view) {
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.finding_watch));
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dogWatchService.post(DogWatchService.CHARA_VIBRATE_TRIGGER, new byte[]{DogWatchService.VIBRATE_STOP});
            }
        });
        pd.show();
        dogWatchService.post(DogWatchService.CHARA_VIBRATE_TRIGGER, new byte[]{DogWatchService.VIBRATE_FINDING});
    }

    public void safeDistClick(View view) {
        new ChooseDistDialog(this, PrefUtils.getBleDist(), new ChooseDistDialog.DistCallback() {
            @Override
            public void onFinished(int progress) {
                Toast.makeText(WatchManageActivity.this, "" + progress, Toast.LENGTH_LONG).show();
                PrefUtils.setBleDist(progress);
            }
        }).show();
    }

    public void correctDistClick(View view) {
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.wait_to_correct_dist));

        View dialogView = getLayoutInflater().inflate(R.layout.correct_dist_dialog, null);
        final AlertDialog correctDistDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.correct_dist_dialog_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (correctDistDialog.isShowing()) {
                    correctDistDialog.cancel();
                }
                pd.show();
                Handler handler = new Handler();
                handler.postDelayed(correctDistRunnable, 3000);
            }
        });

        correctDistDialog.show();

    }

    public void correctTimeClick(View view) {
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.wait_to_correct_time));
        pd.show();

        Calendar calendar = Calendar.getInstance();
        dogWatchService.post(DogWatchService.CHARA_TIME_SEC, new byte[]{(byte) calendar.get(Calendar.SECOND)});
        dogWatchService.post(DogWatchService.CHARA_TIME_MIN, new byte[]{(byte) calendar.get(Calendar.MINUTE)});
        dogWatchService.post(DogWatchService.CHARA_TIME_HOUR, new byte[]{(byte) calendar.get(Calendar.HOUR)});
        dogWatchService.post(DogWatchService.CHARA_TIME_DAY, new byte[]{(byte) calendar.get(Calendar.DAY_OF_MONTH)});
        dogWatchService.post(DogWatchService.CHARA_TIME_MONTH, new byte[]{(byte) calendar.get(Calendar.MONTH)});
        int year = calendar.get(Calendar.YEAR);
        dogWatchService.post(DogWatchService.CHARA_TIME_YEAR, new byte[]{(byte) (year & 0xff), (byte) (year & 0xff00)});
    }

    public void detailClick(View view) {
        startActivity(new Intent(this, WatchDetailActivity.class));
    }

    public void unbindClick(View view) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.ask_unbind_watch)
                .setPositiveButton(R.string.ok, unbindWatchListener)
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }
}
