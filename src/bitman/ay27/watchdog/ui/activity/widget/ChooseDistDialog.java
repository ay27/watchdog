package bitman.ay27.watchdog.ui.activity.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import bitman.ay27.watchdog.R;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-13.
 */
public class ChooseDistDialog extends Dialog implements View.OnClickListener {

    private final int defaultValue;
    private DistCallback cb;
    private FiveSegmentSeekBar seekBar;
    private Button okBtn;

    public ChooseDistDialog(Context context, int defaultValue, DistCallback cb) {
        super(context);
        this.cb = cb;
        this.defaultValue = defaultValue;
    }

    @Override
    public void onClick(View v) {
        cb.onFinished(seekBar.getProgress());
        cancel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_dist_dialog);

        seekBar = (FiveSegmentSeekBar) findViewById(R.id.choose_dist_dialog_seekbar);
        okBtn = (Button) findViewById(R.id.choose_dist_dialog_ok);

        okBtn.setOnClickListener(this);
        seekBar.setProgress(defaultValue);
    }

    public interface DistCallback {
        public void onFinished(int progress);
    }
}
