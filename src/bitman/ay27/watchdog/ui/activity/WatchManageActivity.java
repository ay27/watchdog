package bitman.ay27.watchdog.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import bitman.ay27.watchdog.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watch_manager);
        ButterKnife.inject(this);

        bindWatchPanel.setVisibility(View.VISIBLE);
        currentPanel.setVisibility(View.GONE);
        setSettingEnable(false);

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

    }

    public void findWatchClick(View view) {
    }

    public void safeDistClick(View view) {
    }

    public void correctDistClick(View view) {
    }

    public void correctTimeClick(View view) {
    }

    public void detailClick(View view) {
    }

    public void unbindClick(View view) {
    }
}
