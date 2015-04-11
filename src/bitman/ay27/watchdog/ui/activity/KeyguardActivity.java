package bitman.ay27.watchdog.ui.activity;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.db.DbManager;
import bitman.ay27.watchdog.db.model.AngleChain;
import bitman.ay27.watchdog.db.model.KeyguardStatus;
import bitman.ay27.watchdog.processor.AngleChainProcessor;
import bitman.ay27.watchdog.processor.Curve;
import bitman.ay27.watchdog.ui.DrawingCanvas;
import bitman.ay27.watchdog.widget.keyboard.KeyboardCallback;
import bitman.ay27.watchdog.widget.keyboard.RandomCharKeyboard;
import bitman.ay27.watchdog.widget.keyboard.RandomNumericKeyboard;

import java.util.ArrayList;
import java.util.List;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/5.
 */
public class KeyguardActivity extends Activity {

    RandomCharKeyboard charKeyboard;
    RandomNumericKeyboard numericKeyboard;
    DrawingCanvas dCanvas;
    KeyguardStatus status;
    private WindowManager wm;
    private KeyboardCallback callback;

    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List list = DbManager.getInstance().query(KeyguardStatus.class);
        if (list == null || list.size() == 0) {
            Toast.makeText(this, "please setup password", Toast.LENGTH_SHORT).show();
            finish();
        }
        status = (KeyguardStatus) list.get(0);

        switch (status.unlockType) {
            case img:
                useImg();
                break;
            case numeric:
                useNumeric();
                break;
            case character:
                useChar();
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        charKeyboard.unregisterPasswdListener(callback);
    }

    private void useChar() {

        if (status.charPasswd == null || status.charPasswd.length() == 0) {
            Toast.makeText(this, "please setup password", Toast.LENGTH_SHORT).show();
            finish();
        }

        view = getLayoutInflater().inflate(R.layout.keyguard_keyboard, null);
        numericKeyboard = (RandomNumericKeyboard) view.findViewById(R.id.keyguard_numeric);
        charKeyboard = (RandomCharKeyboard) view.findViewById(R.id.keyguard_char);

        numericKeyboard.setVisibility(View.INVISIBLE);

        charKeyboard.registerPasswdListener(callback = new KeyboardCallback() {
            @Override
            public void onInputFinished(String passwd) {
                if (passwd.equals(status.charPasswd)) {
                    Toast.makeText(KeyguardActivity.this, "ok", Toast.LENGTH_SHORT).show();
                    removeStaticView(view);
                    finish();
                }
            }
        });

        addStaticView(view);
    }

    private void useNumeric() {

        if (status.numericPasswd == null || status.numericPasswd.length() == 0) {
            Toast.makeText(this, "please setup password", Toast.LENGTH_SHORT).show();
            finish();
        }

        view = getLayoutInflater().inflate(R.layout.keyguard_keyboard, null);
        numericKeyboard = (RandomNumericKeyboard) view.findViewById(R.id.keyguard_numeric);
        charKeyboard = (RandomCharKeyboard) view.findViewById(R.id.keyguard_char);

        charKeyboard.setVisibility(View.INVISIBLE);
        numericKeyboard.registerPasswdListener(callback = new KeyboardCallback() {
            @Override
            public void onInputFinished(String passwd) {
                if (passwd.equals(status.numericPasswd)) {
                    Toast.makeText(KeyguardActivity.this, "ok", Toast.LENGTH_SHORT).show();
                    removeStaticView(view);
                    finish();
                }
            }
        });

        addStaticView(view);
    }

    private void useImg() {

        if (status.patternAngelChainIds == null || status.patternAngelChainIds.size() == 0) {
            Toast.makeText(this, "please setup password", Toast.LENGTH_SHORT).show();
            finish();
        }

        view = getLayoutInflater().inflate(R.layout.keyguard_img, null);
        dCanvas = (DrawingCanvas) view.findViewById(R.id.keyguard_img);

        final ArrayList<AngleChain> patterns = new ArrayList<AngleChain>();

        final DbManager manager = DbManager.getInstance();
        AngleChain tmp;
        for (Long id : status.patternAngelChainIds) {
            if ((tmp = (AngleChain) manager.query(AngleChain.class, id)) != null) {
                patterns.add(tmp);
            }
        }

        dCanvas.setOnDrawFinishedListener(new DrawingCanvas.DrawingCallback() {
            @Override
            public void onDrawPause(ArrayList<Curve> rawCurves) {
                AngleChainProcessor processor = new AngleChainProcessor(patterns, rawCurves);
                if (processor.compare()) {
                    processor.updatePattern();
                    manager.updateList(AngleChain.class, patterns);
                    removeStaticView(view);
                    finish();
                }
            }

            @Override
            public void onActionDown() {
                // do nothing
            }

            @Override
            public void onActionUp() {
                // do nothing
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

    private void removeStaticView(View view) {
        wm.removeViewImmediate(view);
    }
}
