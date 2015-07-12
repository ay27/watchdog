package bitman.ay27.watchdog.ui.activity.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-13.
 */
public class FiveSegmentSeekBar extends SeekBar implements SeekBar.OnSeekBarChangeListener {
    private int newProgress = 0, lastProgress = 0;

    public FiveSegmentSeekBar(Context context) {
        super(context);
    }

    public FiveSegmentSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FiveSegmentSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        setMax(100);
        setProgress(0);
        setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (progress > newProgress + 100 || progress < newProgress - 100)

        {
            newProgress = lastProgress;
            seekBar.setProgress(lastProgress);
            return;
        }
        newProgress = progress;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        newProgress = seekBar.getProgress();
        if (newProgress < 12) {
            lastProgress = 0;
            newProgress = 0;
            seekBar.setProgress(0);
        } else if (newProgress > 12 && newProgress < 37) {
            lastProgress = 25;
            newProgress = 25;
            seekBar.setProgress(25);
        } else if (newProgress > 37 && newProgress < 62) {
            lastProgress = 50;
            newProgress = 50;
            seekBar.setProgress(50);

        } else if (newProgress > 62 && newProgress < 87) {
            lastProgress = 75;
            newProgress = 75;
            seekBar.setProgress(75);
        } else {
            lastProgress = 100;
            newProgress = 100;
            seekBar.setProgress(100);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        newProgress = seekBar.getProgress();
        if (newProgress < 12) {
            lastProgress = 0;
            newProgress = 0;
            seekBar.setProgress(0);
        } else if (newProgress > 12 && newProgress < 37) {
            lastProgress = 25;
            newProgress = 25;
            seekBar.setProgress(25);
        } else if (newProgress > 37 && newProgress < 62) {
            lastProgress = 50;
            newProgress = 50;
            seekBar.setProgress(50);

        } else if (newProgress > 62 && newProgress < 87) {
            lastProgress = 75;
            newProgress = 75;
            seekBar.setProgress(75);
        } else {
            lastProgress = 100;
            newProgress = 100;
            seekBar.setProgress(100);
        }
    }
}
