package bitman.ay27.watchdog.ui.activity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.ui.LoginDialog;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.main_toolbar)
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        ButterKnife.inject(this);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }

    @OnClick(R.id.main_login_panel)
    public void loginClick(View view) {
        new LoginDialog(this).show();
    }

    @OnClick(R.id.main_about_panel)
    public void aboutClick(View view) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.about)
                .setMessage(R.string.about_content)
                .setPositiveButton(R.string.ok, null)
                .create()
                .show();
    }
}
