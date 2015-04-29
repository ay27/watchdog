package bitman.ay27.watchdog.ui.activity;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.db.model.KeyguardStatus;
import bitman.ay27.watchdog.widget.keyboard.KeyboardCallback;
import bitman.ay27.watchdog.widget.keyboard.KeyboardUtil;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/15.
 */
public class KeyguardKeyboardActivity extends Activity {

    @InjectView(R.id.keyguard_keyboard_input)
    EditText inputEdt;
    @InjectView(R.id.keyboard_view)
    KeyboardView keyboardView;

    private KeyguardStatus status;
    private WindowManager wm;
    private View view;
    private boolean staticViewAdded = false;
    private KeyboardCallback finishCallback = new KeyboardCallback() {
        @Override
        public void onInputFinished(String passwd) {
            if (passwd.equals(status.passwd)) {
                finish();
            } else {

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = getLayoutInflater().inflate(R.layout.keyguard_keyboard, null);
        ButterKnife.inject(this, view);

        wm = (WindowManager) getApplicationContext().getSystemService("window");

        status = (KeyguardStatus) getIntent().getSerializableExtra("Status");
        if (status == null) {
            Toast.makeText(this, "keyguard status error", Toast.LENGTH_SHORT).show();
            finish();
        }

        setupKeyboard();

//        TaskUtils.executeAsyncTask(new NfcScanner(this));
    }

    @Override
    protected void onDestroy() {
        if (staticViewAdded) {
            wm.removeViewImmediate(view);
        }
        super.onDestroy();
    }

    private void setupKeyboard() {
        inputEdt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int old = inputEdt.getInputType();
                    inputEdt.setInputType(InputType.TYPE_NULL);
//                    edt.setFocusable(true);
                    new KeyboardUtil(KeyguardKeyboardActivity.this, keyboardView, inputEdt, finishCallback).showKeyboard();
                    inputEdt.setInputType(old);
                    inputEdt.setSelection(inputEdt.getText().length());
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    inputEdt.requestFocus();
                }
                return true;
            }
        });
        new KeyboardUtil(this, keyboardView, inputEdt, finishCallback).showKeyboard();
        addStaticView(view);
        staticViewAdded = true;
    }

    private void addStaticView(View view) {
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

        /**
         *以下都是WindowManager.LayoutParams的相关属性
         * 具体用途请参考SDK文档
         */
        final int PARAMS = WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD; // | WindowManager.LayoutParams.FLAG_FULLSCREEN
//                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
//                | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;

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
}
