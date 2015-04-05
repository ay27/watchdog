package bitman.ay27.watchdog.ui.activity;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.widget.keyboard.KeyboardCallback;
import bitman.ay27.watchdog.widget.keyboard.RandomCharKeyboard;
import bitman.ay27.watchdog.widget.keyboard.RandomNumericKeyboard;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/5.
 */
public class KeyguardActivity extends Activity {

    @InjectView(R.id.keyguard_char)
    RandomCharKeyboard charKeyboard;
    @InjectView(R.id.keyguard_numeric)
    RandomNumericKeyboard numericKeyboard;
    private WindowManager wm;
    private KeyboardCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final View view = getLayoutInflater().inflate(R.layout.keyguard_keyboard, null);
        ButterKnife.inject(this, view);
        numericKeyboard.setVisibility(View.INVISIBLE);

        charKeyboard.registerPasswdListener(callback = new KeyboardCallback() {
            @Override
            public void onInputFinished(String passwd) {
                if (passwd.equals("abcd")) {
                    Toast.makeText(KeyguardActivity.this, "ok", Toast.LENGTH_SHORT).show();
                    removeStaticView(view);
                    finish();
                }
            }
        });

        addStaticView(view);
    }

    private void addStaticView(View view) {
        wm = (WindowManager) getApplicationContext().getSystemService("window");
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

        /**
         *以下都是WindowManager.LayoutParams的相关属性
         * 具体用途请参考SDK文档
         */
        final int PARAMS = WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD // | WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;

        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;   //这里是关键，你也可以试试2003
        wmParams.format = PixelFormat.OPAQUE;
        /**
         *这里的flags也很关键
         *代码实际是wmParams.flags |= FLAG_NOT_FOCUSABLE;
         *40的由来是wmParams的默认属性（32）+ FLAG_NOT_FOCUSABLE（8）
         */
        wmParams.flags = PARAMS;
        wmParams.width = wmParams.MATCH_PARENT;
        wmParams.height = wmParams.MATCH_PARENT;
        wm.addView(view, wmParams);  //创建View
    }

    private void removeStaticView(View view) {
        wm.removeViewImmediate(view);
    }
}
