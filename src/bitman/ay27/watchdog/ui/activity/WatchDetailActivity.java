//package bitman.ay27.watchdog.ui.activity;
//
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.View;
//import android.widget.TextView;
//import android.widget.Toast;
//import bitman.ay27.watchdog.PrefUtils;
//import bitman.ay27.watchdog.R;
//import bitman.ay27.watchdog.watchlink.DefaultDogWatchCallback;
//import bitman.ay27.watchdog.watchlink.DogWatchCallback;
//import bitman.ay27.watchdog.watchlink.DogWatchService;
//import bitman.ay27.watchdog.watchlink.DogWatchServiceManager;
//import butterknife.ButterKnife;
//import butterknife.InjectView;
//
//import java.util.Calendar;
//import java.util.Date;
//import java.util.UUID;
//
///**
// * Proudly to use Intellij IDEA.
// * Created by ay27 on 15-7-11.
// */
//public class WatchDetailActivity extends ActionBarActivity {
//
//    private static final String TAG = "WatchDetailActivity";
//    @InjectView(R.id.watch_detail_toolbar)
//    Toolbar toolbar;
//    @InjectView(R.id.watch_detail_name)
//    TextView watchDetailName;
//    @InjectView(R.id.watch_detail_mac_addr)
//    TextView watchDetailMacAddr;
//    @InjectView(R.id.watch_detail_device_type)
//    TextView watchDetailDeviceType;
//    @InjectView(R.id.watch_detail_time)
//    TextView watchDetailTime;
//    @InjectView(R.id.watch_detail_broadcast_power)
//    TextView watchDetailBroadcastPower;
//    @InjectView(R.id.watch_detail_battery)
//    TextView watchDetailBattery;
//    @InjectView(R.id.watch_detail_rssi)
//    TextView watchDetailRssi;
//    @InjectView(R.id.watch_detail_distance)
//    TextView watchDetailDistance;
//
//    private BluetoothDevice device;
//    private DogWatchService dogWatchService;
//    private DogWatchServiceManager manager = DogWatchServiceManager.getInstance();
//
//    private DogWatchCallback initialCallback = new DefaultDogWatchCallback() {
//        @Override
//        public void onGetFinish(final int name, final UUID characUUID, final byte[] remoteVal) {
//            super.onGetFinish(name, characUUID, remoteVal);
//
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.i(TAG, "on get finish " + name);
//
//                    switch (name) {
//                        case DogWatchService.CHARA_TIME_UTC:
//                            long timeFrom2000 = fuck(remoteVal[0], 0) | fuck(remoteVal[1], 8) | fuck(remoteVal[2], 16) | fuck(remoteVal[3], 24);
//                            long timeFrom1970 = timeFrom2000 * 1000 + (new Date(100, 0, 1, 0, 0, 0).getTime());
//                            Calendar calendar = Calendar.getInstance();
//                            calendar.setTimeInMillis(timeFrom1970);
//                            watchDetailTime.setText(String.format("%d年%d月%d日 %d:%d",
//                                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
//                                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
//
//                            dogWatchService.get(DogWatchService.CHARA_RF_TXLEVEL);
//                            break;
//                        case DogWatchService.CHARA_RF_TXLEVEL:
//                            watchDetailBroadcastPower.setText("" + remoteVal[0]);
//                            dogWatchService.get(DogWatchService.CHARA_BATT_RAMAIN);
//                            break;
//                        case DogWatchService.CHARA_BATT_RAMAIN:
//                            if (remoteVal[0] == 0) {
//                                watchDetailBattery.setText(R.string.batterying);
//                            } else {
//                                watchDetailBattery.setText(String.format("%d%%", (int) remoteVal[0]));
//                            }
//                            break;
//                    }
//                }
//            });
//
//        }
//    };
//
//    private long fuck(byte b, long shift) {
//        return (((long) b) & 0xff) << shift;
//    }
//
//    private DogWatchServiceManager.BindCallback bindCallback = new DogWatchServiceManager.BindCallback() {
//        @Override
//        public void onBindSuccess(DogWatchService service) {
//
//            if (service.getConnectionState() != DogWatchService.STATE_CONNECTED) {
//                Toast.makeText(WatchDetailActivity.this, R.string.bt_device_lost, Toast.LENGTH_SHORT).show();
//                finish();
//                return;
//            }
//
//            dogWatchService = service;
//            service.initialize(initialCallback);
//
//            updateView();
//        }
//
//        @Override
//        public void onBindFailed() {
//            Toast.makeText(WatchDetailActivity.this, R.string.bt_device_lost, Toast.LENGTH_SHORT).show();
//            finish();
//        }
//
//        @Override
//        public void onDisconnected() {
//            Toast.makeText(WatchDetailActivity.this, R.string.bt_device_lost, Toast.LENGTH_SHORT).show();
//            finish();
//        }
//    };
//
//    private void updateView() {
//        // device detail
//
//        watchDetailName.setText(device.getName());
//        watchDetailMacAddr.setText(device.getAddress());
//
//        int type = device.getType();
//        String typeName = "";
//        switch (type) {
//            case BluetoothDevice.DEVICE_TYPE_UNKNOWN:
//                typeName = getString(R.string.device_type_unknown);
//                break;
//            case BluetoothDevice.DEVICE_TYPE_LE:
//                typeName = getString(R.string.device_type_le);
//                break;
//            case BluetoothDevice.DEVICE_TYPE_CLASSIC:
//                typeName = getString(R.string.device_type_classic);
//                break;
//            case BluetoothDevice.DEVICE_TYPE_DUAL:
//                typeName = getString(R.string.device_type_daul);
//                break;
//        }
//        watchDetailDeviceType.setText(typeName);
//
//        if (dogWatchService == null) {
//            return;
//        }
//
//        // service state
//        dogWatchService.get(DogWatchService.CHARA_TIME_UTC);
//        watchDetailRssi.setText(String.format("%.01fdB", dogWatchService.getAvgRSSI()));
//        watchDetailDistance.setText(String.format("%.02f米", dogWatchService.calcAccuracy()));
//
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.watch_detail);
//        ButterKnife.inject(this);
//
//        toolbar.setTitle(R.string.watch_detail);
//        toolbar.setTitleTextColor(Color.WHITE);
//        setSupportActionBar(toolbar);
//
//
//        String addr = PrefUtils.getBLEAddr();
//        if (addr.isEmpty()) {
//            Toast.makeText(this, R.string.bt_device_lost, Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        } else {
//            device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(addr);
//        }
//
//        if (device == null) {
//            Toast.makeText(this, R.string.bt_device_lost, Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        manager.bind(this, bindCallback);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        manager.unbind(this, bindCallback);
//    }
//
//    public void refreshClick(View view) {
//        updateView();
//    }
//}
