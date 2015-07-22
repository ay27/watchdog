package bitman.ay27.watchdog.ui.new_activity.passwd;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import bitman.ay27.watchdog.R;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-22.
 */
public class DynamicKeyActivity extends ActionBarActivity {

    @InjectView(R.id.passwd_set_key_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passwd_set_key);
        ButterKnife.inject(this);

        toolbar.setTitle(R.string.dynamic_key);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }

    public void startSetPasswdClick(View view) {
        Intent intent = new Intent(this, InputPasswdActivity.class);
        startActivity(intent);
    }
}
