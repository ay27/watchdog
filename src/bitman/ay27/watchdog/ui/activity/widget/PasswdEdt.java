package bitman.ay27.watchdog.ui.activity.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-22.
 */
public class PasswdEdt extends LinearLayout {
    public PasswdEdt(Context context) {
        super(context);
    }

    public PasswdEdt(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PasswdEdt(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPasswdLength(int length) {

    }

    public void setDisturbNum(boolean value) {

    }

    public static interface PasswdFinishedCallback {
    }
}
