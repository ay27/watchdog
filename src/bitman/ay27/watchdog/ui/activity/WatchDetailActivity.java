package bitman.ay27.watchdog.ui.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;
import bitman.ay27.watchdog.PrefUtils;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.service.DogWatchServiceManager;
import bitman.ay27.watchdog.watchlink.DefaultDogWatchCallback;
import bitman.ay27.watchdog.watchlink.DogWatchCallback;
import bitman.ay27.watchdog.watchlink.DogWatchService;
import butterknife.ButterKnife;
import butterknife.InjectView;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

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

    private BluetoothDevice device;
    private DogWatchServiceManager manager = DogWatchServiceManager.getInstance();
    private DogWatchCallback initialCallback = new DefaultDogWatchCallback() {
        @Override
        public void onGetFinish(int name, UUID characUUID, byte[] remoteVal) {
            super.onGetFinish(name, characUUID, remoteVal);

            switch (name) {
                case DogWatchService.CHARA_TIME_UTC:
                    long timeFrom2000 = remoteVal[0] + remoteVal[1] << 8 + remoteVal[2] << 16 + remoteVal[3] << 24;
                    long timeFrom1970 = timeFrom2000 * 1000 + (new Date(100, 0, 1, 0, 0, 0).getTime());
                    Date date = new Date(timeFrom1970);
                    watchDetailTime.setText(String.format("%d年%d月%d日 %d:%d:%d",
                            date.getYear(), date.getMonth(), date.getDay(),
                            date.getTime(), date.getMinutes(), date.getSeconds()));
                    break;
                case DogWatchService.CHARA_RF_TXLEVEL:
                    watchDetailBroadcastPower.setText("" + remoteVal[0]);
                    break;
                case DogWatchService.CHARA_BATT_RAMAIN:
                    watchDetailBattery.setText(String.format("%.01f%%", remoteVal[0]));
                    break;
            }
        }
    };

    private DogWatchServiceManager.BindCallback bindCallback = new DogWatchServiceManager.BindCallback() {
        @Override
        public void onBindSuccess(DogWatchService service) {

            service.initialize(initialCallback);
            if (service.getConnectionState() != DogWatchService.STATE_DISCONNECTED) {
                setUpView(service);
                return;
            }
            if (!PrefUtils.getBLEAddr().isEmpty()) {
                service.connect(PrefUtils.getBLEAddr(), true);
            }

            setUpView(service);
        }

        @Override
        public void onBindFailed() {
            Toast.makeText(WatchDetailActivity.this, R.string.bt_device_lost, Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onDisconnected() {
            Toast.makeText(WatchDetailActivity.this, R.string.bt_device_lost, Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    private void setUpView(final DogWatchService service) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateView(service);
                    }
                });
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 0, 2000);
    }

    private void updateView(DogWatchService service) {
        // device detail

        watchDetailName.setText(device.getName());
        watchDetailMacAddr.setText(device.getAddress());

        int type = device.getType();
        String typeName = "";
        switch (type) {
            case BluetoothDevice.DEVICE_TYPE_UNKNOWN:
                typeName = getString(R.string.device_type_unknown);
                break;
            case BluetoothDevice.DEVICE_TYPE_LE:
                typeName = getString(R.string.device_type_le);
                break;
            case BluetoothDevice.DEVICE_TYPE_CLASSIC:
                typeName = getString(R.string.device_type_classic);
                break;
            case BluetoothDevice.DEVICE_TYPE_DUAL:
                typeName = getString(R.string.device_type_daul);
                break;
        }
        watchDetailDeviceType.setText(typeName);

        watchDetailUuid.setText(device.getUuids()[0].toString());


        // service state
        service.get(DogWatchService.CHARA_TIME_UTC);
        service.get(DogWatchService.CHARA_RF_TXLEVEL);
        service.get(DogWatchService.CHARA_BATT_RAMAIN);
        watchDetailRssi.setText(String.format("%.01fdB", service.getAvgRSSI()));
        watchDetailDistance.setText(String.format("%.02f米", service.calcAccuracy()));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watch_detail);
        ButterKnife.inject(this);

        toolbar.setTitle(R.string.watch_detail);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);


        String addr = PrefUtils.getBLEAddr();
        if (addr.isEmpty()) {
            Toast.makeText(this, R.string.bt_device_lost, Toast.LENGTH_SHORT).show();
            finish();
            return;
        } else {
            device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(addr);
        }

        if (device == null) {
            Toast.makeText(this, R.string.bt_device_lost, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        manager.bind(this, bindCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.unbind(this, bindCallback);
    }
}
