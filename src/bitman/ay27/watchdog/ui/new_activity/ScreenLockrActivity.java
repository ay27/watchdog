package bitman.ay27.watchdog.ui.new_activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import bitman.ay27.watchdog.PrefUtils;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.db.DbManager;
import bitman.ay27.watchdog.db.model.KeyguardStatus;
import bitman.ay27.watchdog.service.KeyguardService;
import bitman.ay27.watchdog.service.ServiceManager;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.List;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-18.
 */
public class ScreenLockrActivity extends ActionBarActivity implements View.OnClickListener {


    private static final String TAG = "ScreenLockrActivity";
    private static final int REQ_SET_UP_PASSWD = 0x1;
    @InjectView(R.id.screen_lockr_status)
    TextView screenLockrStatus;
    @InjectView(R.id.screen_lockr_switch)
    SwitchButton screenLockrSwitch;
    @InjectView(R.id.screen_lockr_panel)
    RelativeLayout screenLockrPanel;
    @InjectView(R.id.screen_lockr_toolbar)
    Toolbar toolbar;

    private boolean isCheck = false;


    private ServiceManager manager = ServiceManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_lockr);
        ButterKnife.inject(this);

        toolbar.setTitle(R.string.screen_lockr);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        isCheck = PrefUtils.isKeyguardEnable();

        screenLockrPanel.setOnClickListener(this);
        setStatus(isCheck);

        screenLockrSwitch.setChecked(isCheck);

    }

    private void setStatus(boolean isCheck) {
        screenLockrStatus.setTextColor(getResources().getColor(isCheck ? R.color.green_1 : R.color.red_1));
        screenLockrStatus.setText(isCheck ? R.string.screen_lockr_open : R.string.screen_lockr_close);
    }

    @Override
    public void onClick(View v) {
        isCheck = !isCheck;

        if (isCheck) {
            List list = DbManager.getInstance().query(KeyguardStatus.class);
            if (list == null || list.size() == 0) {
                Log.e(TAG, "can not read keyguard status from DB");
                start2SetUpPasswd();
                return;
            }
            KeyguardStatus status = (KeyguardStatus) list.get(0);
            if (status == null) {
                Toast.makeText(this, "keyguard status error", Toast.LENGTH_SHORT).show();
                start2SetUpPasswd();
                return;
            }

            manager.addService(KeyguardService.class);
            Toast.makeText(ScreenLockrActivity.this, R.string.screen_lock_enable, Toast.LENGTH_SHORT).show();
        } else {
            manager.removeService(KeyguardService.class);
            Toast.makeText(ScreenLockrActivity.this, R.string.screen_lock_disable, Toast.LENGTH_SHORT).show();
        }
        screenLockrSwitch.performClick();
        PrefUtils.setKeyguardEnable(isCheck);
        setStatus(isCheck);
    }

    private void start2SetUpPasswd() {
        Intent intent = new Intent(this, PasswdSettingActivity.class);
        startActivityForResult(intent, REQ_SET_UP_PASSWD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SET_UP_PASSWD) {
            boolean result = resultCode == RESULT_OK;
            PrefUtils.setKeyguardEnable(result);
            screenLockrSwitch.setChecked(result);
            setStatus(result);
            isCheck = result;
            manager.addService(KeyguardService.class);
            Toast.makeText(ScreenLockrActivity.this, R.string.screen_lock_enable, Toast.LENGTH_SHORT).show();
        }
    }
}
