package bitman.ay27.watchdog.ui.new_activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import bitman.ay27.watchdog.PrefUtils;
import bitman.ay27.watchdog.R;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-18.
 */
public class AppSettingActivity extends ActionBarActivity {

    @InjectView(R.id.app_setting_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.app_setting_app_lockr_switch)
    SwitchCompat appLockrSwitch;
    @InjectView(R.id.app_setting_app_lockr_panel)
    RelativeLayout appLockrPanel;
    @InjectView(R.id.app_setting_auto_upload_gps_switch)
    SwitchCompat autoUploadGpsSwitch;
    @InjectView(R.id.app_setting_auto_upload_gps_panel)
    RelativeLayout autoUploadGpsPanel;
    @InjectView(R.id.app_setting_auto_open_network_switch)
    SwitchCompat autoOpenNetworkSwitch;
    @InjectView(R.id.app_setting_auto_open_network_panel)
    RelativeLayout autoOpenNetworkPanel;
    @InjectView(R.id.app_setting_disturb_passwd_switch)
    SwitchCompat disturbPasswdSwitch;
    @InjectView(R.id.app_setting_disturb_passwd_panel)
    RelativeLayout disturbPasswdPanel;
    @InjectView(R.id.app_setting_sim_switch)
    SwitchCompat simSwitch;
    @InjectView(R.id.app_setting_sim_panel)
    RelativeLayout simPanel;
    @InjectView(R.id.app_setting_smart_nfc_switch)
    SwitchCompat smartNfcSwitch;
    @InjectView(R.id.app_setting_smart_nfc_panel)
    RelativeLayout smartNfcPanel;


    private boolean simLockr;
    private boolean appLockrEnabled;
    private boolean autoUploadGpsEnabled;
    private boolean autoOpenNetwork;
    private boolean disturbPasswd;
    private boolean smartNfc;

    private View.OnClickListener appLockrClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            appLockrEnabled = !appLockrEnabled;
            PrefUtils.setAppLockrEnable(appLockrEnabled);
            appLockrSwitch.performClick();
        }
    };
    private View.OnClickListener autoUploadGpsClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            autoUploadGpsEnabled = !autoUploadGpsEnabled;
            PrefUtils.setAutoUploadGps(autoUploadGpsEnabled);
            autoUploadGpsSwitch.performClick();
        }
    };
    private View.OnClickListener autoOpenNetworkClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            autoOpenNetwork = !autoOpenNetwork;
            PrefUtils.setAutoOpenNetwork(autoOpenNetwork);
            autoOpenNetworkSwitch.performClick();
        }
    };
    private View.OnClickListener disturbPasswdClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            disturbPasswd = !disturbPasswd;
            PrefUtils.setDisturbPasswd(disturbPasswd);
            disturbPasswdSwitch.performClick();
        }
    };
    private View.OnClickListener simClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            simLockr = !simLockr;
            PrefUtils.setSimLockr(simLockr);
            simSwitch.performClick();
        }
    };
    private View.OnClickListener smartNfcClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            smartNfc = !smartNfc;
            PrefUtils.setCheckWatchDist(smartNfc);
            smartNfcSwitch.performClick();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_settings);
        ButterKnife.inject(this);


        toolbar.setTitle(R.string.app_setting);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        appLockrEnabled = PrefUtils.isAppLockrEnable();
        appLockrSwitch.setChecked(appLockrEnabled);
        appLockrPanel.setOnClickListener(appLockrClick);


        autoUploadGpsEnabled = PrefUtils.isGpsAutoUpload();
        autoUploadGpsSwitch.setChecked(autoUploadGpsEnabled);
        autoUploadGpsPanel.setOnClickListener(autoUploadGpsClick);

        autoOpenNetwork = PrefUtils.isAutoOpenNetwork();
        autoOpenNetworkSwitch.setChecked(autoOpenNetwork);
        autoOpenNetworkPanel.setOnClickListener(autoOpenNetworkClick);

        disturbPasswd = PrefUtils.isDisturbPasswd();
        disturbPasswdSwitch.setChecked(disturbPasswd);
        disturbPasswdPanel.setOnClickListener(disturbPasswdClick);

        simLockr = PrefUtils.isSimLock();
        simSwitch.setChecked(simLockr);
        simPanel.setOnClickListener(simClick);

        smartNfc = PrefUtils.isCheckWatchDist();
        smartNfcSwitch.setChecked(smartNfc);
        smartNfcPanel.setOnClickListener(smartNfcClick);

    }
}
