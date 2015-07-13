package bitman.ay27.watchdog.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import bitman.ay27.watchdog.PrefUtils;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.ui.activity.widget.ChooseDistDialog;
import bitman.ay27.watchdog.ui.activity.widget.ScanBleDialog;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/7.
 */
public class WatchManageActivity extends Activity {


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
    private Runnable correctDistRunnable = new Runnable() {
        @Override
        public void run() {

            // TODO get the average rssi and push to watch

            if (pd.isShowing()) {
                pd.cancel();
            }
        }
    };
    private DialogInterface.OnClickListener unbindWatchListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // TODO unbind watch
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watch_manager);
        ButterKnife.inject(this);

        bindWatchPanel.setVisibility(View.VISIBLE);
        currentPanel.setVisibility(View.GONE);
//        setSettingEnable(false);

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
                    Toast.makeText(WatchManageActivity.this, device.getAddress(), Toast.LENGTH_LONG).show();
                    // TODO connect to device
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
                // TODO send stop vibrate
            }
        });
        // TODO push long vibrate, then when pd cancel, push stop vibrate
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
        // TODO push time to watch, then dismiss pd
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
