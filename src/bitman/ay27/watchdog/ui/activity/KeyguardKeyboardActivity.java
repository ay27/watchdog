package bitman.ay27.watchdog.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.db.model.KeyguardStatus;
import bitman.ay27.watchdog.utils.Common;
import bitman.ay27.watchdog.widget.PasswdEdt;
import bitman.ay27.watchdog.widget.keyboard.KeyboardCallback;
import bitman.ay27.watchdog.widget.keyboard.KeyboardUtil;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;

import java.util.Random;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/15.
 */
public class KeyguardKeyboardActivity extends Activity {
    @InjectView(R.id.keyguard_app_lock)
    TextView keyguardAppLock;
    @InjectView(R.id.keyguard_change_btn)
    Button keyguardChangeBtn;
    @InjectView(R.id.passwd_edt)
    PasswdEdt passwdEdt;

    @InjectView(R.id.key_btn_cancel)
    Button keyBtnCancel;
    @InjectView(R.id.key_btn_back)
    Button keyBtnBack;

    @InjectViews({R.id.key_btn_0, R.id.key_btn_1, R.id.key_btn_2, R.id.key_btn_3, R.id.key_btn_4, R.id.key_btn_5, R.id.key_btn_6,
            R.id.key_btn_7, R.id.key_btn_8, R.id.key_btn_9})
    Button[] btns;

//    @InjectView(R.id.keyguard_keyboard_input)
//    EditText inputEdt;
//    @InjectView(R.id.keyboard_view)
//    KeyboardView keyboardView;

    private KeyguardStatus status;
    private WindowManager wm;
    private View view;
    private boolean staticViewAdded = false;


    private BroadcastReceiver killKeyguardReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
    private PasswdEdt.PasswdFinishedCallback kbCallback = new PasswdEdt.PasswdFinishedCallback() {
        @Override
        public void onEditFinished(boolean disturbCorrect, String passwd) {
            if (!disturbCorrect) {
                return;
            }
            if (passwd.equals(status.passwd)) {
                finish();
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

        registerReceiver(killKeyguardReceiver, new IntentFilter(Common.ACTION_KILL_KEYGUARD));
    }

    @OnClick(R.id.keyguard_change_btn)
    public void changeMode(View view) {
        Intent intent = new Intent(this, KeyguardImgActivity.class);
        intent.putExtra("Status", status);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        if (staticViewAdded) {
            wm.removeViewImmediate(view);
        }

        unregisterReceiver(killKeyguardReceiver);
        passwdEdt.unregisterCallback();

        super.onDestroy();
    }

    private void setupKeyboard() {
        passwdEdt.setDisturbNum(true);
        passwdEdt.setPasswdLength(status.passwd.length());
        passwdEdt.registerFinishedCallback(kbCallback);

        for (Button button : btns) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    passwdEdt.edit(((Button) v).getText());
                }
            });
        }

        keyBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwdEdt.cancelEdit();
            }
        });

        keyBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwdEdt.backEdit();
            }
        });

        randomKeyboard();
        addStaticView(view);
        staticViewAdded = true;
    }

    private void randomKeyboard() {
        int[] ints = getRandomSequence(10);
        for (int i = 0; i < 10; i++) {
            btns[i].setText(""+ints[i]);
        }
    }

    private static int[] getRandomSequence(int total) {
        int[] sequence = new int[total];
        int[] output = new int[total];

        for (int i = 0; i < total; i++) {
            sequence[i] = i;
        }

        Random random = new Random();
        int end = total - 1;

        for (int i = 0; i < total; i++) {
            int num = random.nextInt(end + 1);
            output[i] = sequence[num];
            sequence[num] = sequence[end];
            end--;
        }

        return output;
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
