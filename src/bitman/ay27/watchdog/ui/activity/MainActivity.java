package bitman.ay27.watchdog.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.service.KeyguardService;
import bitman.ay27.watchdog.ui.DrawingCanvas;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    @InjectView(R.id.main_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.main_login)
    View loginPanel;
    @InjectView(R.id.main_set_watch)
    View setWatchPanel;
    @InjectView(R.id.main_set_passwd)
    View setPasswdPanel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ButterKnife.inject(this);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        loginPanel.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        if (v.getId() == loginPanel.getId()) {
            intent.setClass(this, LoginActivity.class);
        }
        else if (v.getId() == setWatchPanel.getId()) {
            intent.setClass(this, SetWatchActivity.class);
        }
        else if (v.getId() == setPasswdPanel.getId()) {
            intent.setClass(this, SetPasswdActivity.class);
        }
        else {
            return;
        }

        startActivity(intent);
    }
}
