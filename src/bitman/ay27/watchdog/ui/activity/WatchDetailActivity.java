package bitman.ay27.watchdog.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import bitman.ay27.watchdog.R;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-11.
 */
public class WatchDetailActivity extends ActionBarActivity {

    @InjectView(R.id.watch_detail_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.watch_detail_name)
    TextView watchDetailName;
    @InjectView(R.id.watch_detail_mac_addr)
    TextView watchDetailMacAddr;
    @InjectView(R.id.watch_detail_device_type)
    TextView watchDetailDeviceType;
    @InjectView(R.id.watch_detail_uuid)
    TextView watchDetailUuid;
    @InjectView(R.id.watch_detail_time)
    TextView watchDetailTime;
    @InjectView(R.id.watch_detail_broadcast_power)
    TextView watchDetailBroadcastPower;
    @InjectView(R.id.watch_detail_battery)
    TextView watchDetailBattery;
    @InjectView(R.id.watch_detail_rssi)
    TextView watchDetailRssi;
    @InjectView(R.id.watch_detail_distance)
    TextView watchDetailDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watch_detail);
        ButterKnife.inject(this);
        toolbar.setTitle(R.string.watch_detail);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }
}
