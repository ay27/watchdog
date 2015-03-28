package bitman.ay27.watchdog.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.widget.RandomCharKeyboard;
import bitman.ay27.watchdog.widget.RandomNumericKeyboard;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.main_toolbar)
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.random_keyboard_char);

        addContentView(new RandomNumericKeyboard(this),
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        ButterKnife.inject(this);
//        setSupportActionBar(toolbar);

    }
}
