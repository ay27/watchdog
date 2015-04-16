package bitman.ay27.watchdog.ui.activity;

import android.graphics.Color;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.widget.keyboard.KeyboardUtil;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/7.
 */
public class SetPasswdActivity extends ActionBarActivity {

    @InjectView(R.id.set_passwd_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.set_passwd_new_passwd)
    EditText newPasswdEdt;
    @InjectView(R.id.set_passwd_old_passwd)
    EditText oldPasswdEdt;
    @InjectView(R.id.set_passwd_confirm_passwd)
    EditText confirmEdt;
    @InjectView(R.id.set_passwd_ok_btn)
    Button okBtn;
    @InjectView(R.id.keyboard_view)
    KeyboardView keyboardView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_passwd);
        ButterKnife.inject(this);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

//        newPasswdEdt.setInputType(InputType.TYPE_NULL);
        newPasswdEdt.setOnTouchListener(generateListener(newPasswdEdt));
//        oldPasswdEdt.setInputType(InputType.TYPE_NULL);
        oldPasswdEdt.setOnTouchListener(generateListener(oldPasswdEdt));
//        confirmEdt.setInputType(InputType.TYPE_NULL);
        confirmEdt.setOnTouchListener(generateListener(confirmEdt));
    }

    private View.OnTouchListener generateListener(final EditText edt) {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int old = edt.getInputType();
                    edt.setInputType(InputType.TYPE_NULL);
//                    edt.setFocusable(true);
                    new KeyboardUtil(SetPasswdActivity.this, keyboardView, edt, null).showKeyboard();
                    edt.setInputType(old);
                    edt.setSelection(edt.getText().length());
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    edt.requestFocus();
                }
                return true;
            }
        };
    }
}
