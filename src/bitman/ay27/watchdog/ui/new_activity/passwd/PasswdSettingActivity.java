package bitman.ay27.watchdog.ui.new_activity.passwd;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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
    }

    public void startSetPasswdClick(View view) {
        Intent intent = new Intent(this, ChooseBgImgActivity.class);
        startActivity(intent);
    }
}
