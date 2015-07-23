package bitman.ay27.watchdog.ui.new_activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import bitman.ay27.watchdog.PrefUtils;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.WatchdogApplication;
import bitman.ay27.watchdog.model.SignInRecvForm;
import bitman.ay27.watchdog.net.NetManager;
import bitman.ay27.watchdog.service.HeartbeatService;
import bitman.ay27.watchdog.service.ServiceManager;
import bitman.ay27.watchdog.ui.activity.widget.LoginDialog;
import bitman.ay27.watchdog.ui.activity.widget.Semicircle;
import bitman.ay27.watchdog.ui.new_activity.passwd.PasswdSettingActivity;
import bitman.ay27.watchdog.ui.new_activity.watch.WatchManageActivity;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.gson.Gson;
import org.askerov.dynamicgrid.BaseDynamicGridAdapter;
import org.askerov.dynamicgrid.DynamicGridView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-17.
 */
public class MainActivity extends Activity {

    private static final String TAG = "Main_Activity";
    @InjectView(R.id.main__gridview)
    DynamicGridView gridView;

    @InjectView(R.id.semicircle)
    Semicircle semicircle;

    private List<String> names = Arrays.asList("云端服务", "近身防盗", "DogWatch", "USB锁", "刷机锁", "SD卡加密", "屏幕锁", "密码设置", "应用设置");
    private List<Integer> drawables = Arrays.asList(
            R.drawable.ic_perm_identity_grey600,
            R.drawable.ic_nfc_grey600_48dp,
            R.drawable.ic_watch_black_48dp,

            R.drawable.ic_usb_grey600_48dp,
            R.drawable.shuaji,
            R.drawable.ic_sd_card_grey600_48dp,
            R.drawable.ic_screen_lock_portrait_grey600_48dp,
            R.drawable.ic_vpn_key_black_48dp,
            R.drawable.ic_settings_grey600_48dp);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_);
        ButterKnife.inject(this);

        boolean[] enables = new boolean[7];
        for (int i = 0; i < 7; i++) {
            enables[i] = true;
        }
        enables[2] = false;
        enables[6] = false;
        semicircle.setEnable(enables);

        ArrayList<Item> items = new ArrayList<Item>();
        for (int i = 0; i < 9; i++) {
            items.add(new Item(drawables.get(i), names.get(i), i));
        }

        gridView.setAdapter(new CheeseDynamicAdapter(this, items, 3));

        gridView.setOnDragListener(new DynamicGridView.OnDragListener() {
            @Override
            public void onDragStarted(int position) {
                Log.d(TAG, "drag started at position " + position);
            }

            @Override
            public void onDragPositionsChanged(int oldPosition, int newPosition) {
                Log.d(TAG, String.format("drag item position changed from %d to %d", oldPosition, newPosition));
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                gridView.startEditMode(position);
                return true;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = (Item) gridView.getAdapter().getItem(position);
                Intent intent = new Intent();
//                Arrays.asList("云端服务", "近身防盗", "DogWatch", "USB锁", "刷机锁", "SD卡加密", "屏幕锁", "密码设置", "应用设置");
                switch (item.id) {
                    case 0:
                        login();
                        intent = null;
                        break;
                    case 1:
                        intent.setClass(MainActivity.this, NfcManageActivity.class);
                        break;
                    case 2:
                        intent.setClass(MainActivity.this, WatchManageActivity.class);
                        break;
                    case 3:
                        intent.setClass(MainActivity.this, UsbLockrActivity.class);
                        break;
                    case 4:
                        intent.setClass(MainActivity.this, FlashLockrActivity.class);
                        break;
                    case 5:
                        intent.setClass(MainActivity.this, SdEncryptorActivity.class);
                        break;
                    case 6:
                        intent.setClass(MainActivity.this, ScreenLockrActivity.class);
                        break;
                    case 7:
                        intent.setClass(MainActivity.this, PasswdSettingActivity.class);
                        break;
                    case 8:
                        intent.setClass(MainActivity.this, AppSettingActivity.class);
                        break;
                }
                if (intent != null) {
                    startActivity(intent);
                }
//                Toast.makeText(MainActivity.this, ""+item.id, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void login() {
        new LoginDialog(this, new LoginDialog.Callback() {
            @Override
            public void onSuccess(String content, String username, String password) {

                SignInRecvForm form = null;
                try {

                    Gson gson = new Gson();
                    form = gson.fromJson(content, SignInRecvForm.class);
                    if (form.err != 0) {
                        Toast.makeText(MainActivity.this, R.string.sign_in_failed, Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, R.string.sign_in_failed, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return;
                }

//                loginState = 1;

//                userSummer.setText(getString(R.string.sign_in_success) + ": " + username);
                PrefUtils.setUserName(username);
                PrefUtils.setUserPasswd(password);
                PrefUtils.setUserId(Integer.toString(form.user.uid));

                NetManager.online();
                ServiceManager manager = ServiceManager.getInstance();
                manager.addService(HeartbeatService.class);

                for (SignInRecvForm.Device device : form.user.devices) {
                    if (device.deviceid.equals(WatchdogApplication.DeviceId)) {
//                        loginState = 2;
                        return;
                    }
                }

                bindDevice();
            }

            @Override
            public void onFailed() {
            }
        }).show();
    }

    private void bindDevice() {
        final EditText deviceName = new EditText(MainActivity.this);
        deviceName.setText(Build.BRAND + "-" + Build.MODEL);
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(R.string.bind_device)
                .setView(deviceName)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.bind, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (deviceName.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, R.string.input_device_name, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        PrefUtils.setDeviceName(deviceName.getText().toString());
                        NetManager.bind(new NetManager.NetCallback() {
                            @Override
                            public void onSuccess(int code, String recv) {
                                Toast.makeText(MainActivity.this, R.string.bind_success, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(int code, String recv) {
                                Toast.makeText(MainActivity.this, R.string.bind_failed, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onBackPressed() {
        if (gridView.isEditMode()) {
            gridView.stopEditMode();
        } else {
            super.onBackPressed();
        }
    }

    public static class CheeseDynamicAdapter extends BaseDynamicGridAdapter {
        public CheeseDynamicAdapter(Context context, List<?> items, int columnCount) {
            super(context, items, columnCount);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CheeseViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.main_item, null);
                holder = new CheeseViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (CheeseViewHolder) convertView.getTag();
            }
            holder.build(position);
            return convertView;
        }

        private class CheeseViewHolder {
            private TextView titleText;
            private ImageView image;

            private CheeseViewHolder(View view) {
                titleText = (TextView) view.findViewById(R.id.main_item_txv);
                image = (ImageView) view.findViewById(R.id.main_item_img);
            }

            void build(int position) {
                Item item = (Item) getItem(position);
                titleText.setText(item.name);
                image.setImageResource(item.drawable);
            }
        }
    }

    private class Item {
        String name;
        int drawable;
        int id;

        public Item(int drawable, String name, int id) {
            this.drawable = drawable;
            this.name = name;
            this.id = id;
        }
    }
}
