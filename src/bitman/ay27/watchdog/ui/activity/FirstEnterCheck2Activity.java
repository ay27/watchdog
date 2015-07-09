package bitman.ay27.watchdog.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.db.DbManager;
import bitman.ay27.watchdog.db.model.KeyguardStatus;
import bitman.ay27.watchdog.widget.keyboard.KeyboardCallback;
import bitman.ay27.watchdog.widget.keyboard.KeyboardUtil;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import java.util.List;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/6/25.
 */
public class FirstEnterCheck2Activity extends Activity {

    private static final String TAG = "FirstEnterCheck2";
    @InjectView(R.id.keyguard_keyboard_input)
    EditText inputEdt;
    @InjectView(R.id.keyboard_view)
    KeyboardView keyboardView;
    @InjectView(R.id.keyguard_app_lock)
    TextView keyguardAppLock;

    private KeyguardStatus status;
    private KeyboardCallback finishCallback = new KeyboardCallback() {
        @Override
        public void onInputFinished(String passwd) {
            if (passwd.equals(status.passwd)) {
                Intent intent = new Intent(FirstEnterCheck2Activity.this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.keyguard_keyboard);
        ButterKnife.inject(this);


        keyguardAppLock.setVisibility(View.VISIBLE);

        List list = DbManager.getInstance().query(KeyguardStatus.class);
        if (list == null || list.size() == 0) {
            Log.e(TAG, "can not read keyguard status from DB");
            return;
        }
        status = (KeyguardStatus) list.get(0);
        if (status == null) {
            Toast.makeText(this, "keyguard status error", Toast.LENGTH_SHORT).show();
            finish();
        }

        setupKeyboard();

//        TaskUtils.executeAsyncTask(new NfcScanner(this));
    }

    @OnClick(R.id.keyguard_change_btn)
    public void changeMode(View view) {
        Intent intent = new Intent(this, FirstEnterCheckActivity.class);
        intent.putExtra("Status", status);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    private void setupKeyboard() {
        inputEdt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int old = inputEdt.getInputType();
                    inputEdt.setInputType(InputType.TYPE_NULL);
//                    edt.setFocusable(true);
                    new KeyboardUtil(FirstEnterCheck2Activity.this, keyboardView, inputEdt, finishCallback).showKeyboard();
                    inputEdt.setInputType(old);
                    inputEdt.setSelection(inputEdt.getText().length());
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    inputEdt.requestFocus();
                }
                return true;
            }
        });
        new KeyboardUtil(this, keyboardView, inputEdt, finishCallback).showKeyboard();
    }

}
