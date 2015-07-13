package bitman.ay27.watchdog.ui.activity.widget;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import bitman.ay27.watchdog.R;

import java.util.ArrayList;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/6/3.
 */
public class ScanBleDialog extends Dialog {

    ListView oldDeviceList, newDeviceList;
    ProgressBar pb;
    private BluetoothAdapter mBtAdapter;
    private BluetoothDeviceAdapter oldDeviceAdapter, newDeviceAdapter;
    private ScanBtDeviceCallback callback;

    private AdapterView.OnItemClickListener deviceItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (parent.getId() == newDeviceList.getId()) {
                callback.onResult(newDeviceAdapter.getItem(position));
            } else if (parent.getId() == oldDeviceList.getId()) {
                callback.onResult(oldDeviceAdapter.getItem(position));
            }
            cancel();
        }
    };

    private Handler leHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (foundDevice== null ){
                return;
            }
            if (foundDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                if (newDeviceAdapter.devices.contains(foundDevice)) {
                    return;
                }
                newDeviceAdapter.addDevice(foundDevice);
            }
        }
    };

    private BluetoothDevice foundDevice;
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            foundDevice = device;
            leHandler.obtainMessage(0).sendToTarget();
        }
    };


    public ScanBleDialog(Context context, ScanBtDeviceCallback callback) {
        super(context);
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.scan_bluetooth_device_dialog);
        findViews();
        checkBluetoothDevice();
        setupOldList();
        setupNewList();
    }

    private void checkBluetoothDevice() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBtAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            getContext().startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        mBtAdapter.stopLeScan(leScanCallback);
    }

    private void setupNewList() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        newDeviceAdapter = new BluetoothDeviceAdapter(null);
        newDeviceList.setAdapter(newDeviceAdapter);
        newDeviceList.setOnItemClickListener(deviceItemClickListener);

        doDiscovery();
    }

    private void doDiscovery() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBtAdapter.stopLeScan(leScanCallback);
            }
        }, 10000);
        mBtAdapter.startLeScan(leScanCallback);
    }

    private void setupOldList() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        oldDeviceAdapter = new BluetoothDeviceAdapter(new ArrayList<BluetoothDevice>(mBtAdapter.getBondedDevices()));
        oldDeviceList.setAdapter(oldDeviceAdapter);
        oldDeviceList.setOnItemClickListener(deviceItemClickListener);
    }

    private void findViews() {
        oldDeviceList = (ListView) findViewById(R.id.scan_bt_old_list);
        newDeviceList = (ListView) findViewById(R.id.scan_bt_new_list);
        pb = (ProgressBar) findViewById(R.id.scan_bt_progressbar);
        pb.setVisibility(View.VISIBLE);
    }

    public static interface ScanBtDeviceCallback {
        public void onResult(BluetoothDevice device);
    }

    private class BluetoothDeviceAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> devices;

        public BluetoothDeviceAdapter(ArrayList<BluetoothDevice> devices) {
            this.devices = devices;
            if (devices == null) {
                this.devices = new ArrayList<BluetoothDevice>();
            }
        }

        public void addDevice(BluetoothDevice device) {
            this.devices.add(device);
            notifyDataSetChanged();
        }


        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public BluetoothDevice getItem(int position) {
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bluetooth_device_item, null);
            TextView deviceName = (TextView) convertView.findViewById(R.id.bluetooth_device_item_txv);
            TextView deviceAddr = (TextView) convertView.findViewById(R.id.bluetooth_device_address_txv);
            BluetoothDevice device = devices.get(position);
            if (device != null) {
                deviceName.setText(device.getName());
                deviceAddr.setText(device.getAddress());
            }
            return convertView;
        }
    }
}
