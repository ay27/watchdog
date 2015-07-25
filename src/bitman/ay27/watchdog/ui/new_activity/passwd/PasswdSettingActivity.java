package bitman.ay27.watchdog.ui.new_activity.passwd;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import bitman.ay27.watchdog.R;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-21.
 */
public class PasswdSettingActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passwd_setting);
        Toolbar toolbar = (Toolbar)findViewById(R.id.passwd_setting_toolbar);
        toolbar.setTitle(R.string.set_passwd);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }

    public void startSetPasswdClick(View view) {
        Intent intent = new Intent(this, ChooseBgImgActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
}
