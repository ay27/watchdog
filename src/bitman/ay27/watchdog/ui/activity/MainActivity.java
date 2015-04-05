package bitman.ay27.watchdog.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.service.KeyguardService;
import bitman.ay27.watchdog.ui.DrawingCanvas;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.main_toolbar)
    Toolbar toolbar;

    Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ButterKnife.inject(this);
        setSupportActionBar(toolbar);

        startService(intent = new Intent(this, KeyguardService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intent);
    }
}
