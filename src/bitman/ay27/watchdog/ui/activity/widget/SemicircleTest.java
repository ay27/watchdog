package bitman.ay27.watchdog.ui.activity.widget;

import android.app.Activity;
import android.os.Bundle;
import bitman.ay27.watchdog.R;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-20.
 */
public class SemicircleTest extends Activity {

    @InjectView(R.id.semicircle)
    Semicircle semicircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.semicircle_test);
        ButterKnife.inject(this);

        boolean[] enables = new boolean[7];
        for (int i = 0; i < 7; i++) {
            enables[i] = true;
        }
        enables[2] = false;
        enables[6] = false;
        semicircle.setEnable(enables);
    }
}
