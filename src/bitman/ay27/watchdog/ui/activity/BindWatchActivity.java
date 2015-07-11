package bitman.ay27.watchdog.ui.activity;

import android.app.Activity;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
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
public class BindWatchActivity extends Activity {


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watch_manager);
        ButterKnife.inject(this);

    }
}
