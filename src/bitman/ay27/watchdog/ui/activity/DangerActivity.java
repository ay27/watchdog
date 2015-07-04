package bitman.ay27.watchdog.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import bitman.ay27.watchdog.R;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/5/2.
 */
public class DangerActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.laptop_danger);

        Toolbar bar = (Toolbar)findViewById(R.id.laptop_toolbar);
        bar.setTitle("失窃模式");
        bar.setTitleTextColor(Color.WHITE);
        bar.setBackgroundColor(getResources().getColor(R.color.red_1));
        setSupportActionBar(bar);
    }
}
