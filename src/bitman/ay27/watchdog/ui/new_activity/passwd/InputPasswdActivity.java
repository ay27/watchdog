package bitman.ay27.watchdog.ui.new_activity.passwd;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.widget.TintCheckBox;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.*;
import bitman.ay27.watchdog.PrefUtils;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.db.DbManager;
import bitman.ay27.watchdog.db.model.KeyguardStatus;
import bitman.ay27.watchdog.widget.keyboard.KeyboardUtil;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-22.
 */
public class InputPasswdActivity extends ActionBarActivity {

    @InjectView(R.id.set_passwd_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.set_passwd_new_passwd)
    EditText newPasswdEdt;
    @InjectView(R.id.set_passwd_confirm_passwd)
    EditText confirmEdt;
    @InjectView(R.id.new_passwd_error)
    TextView newPasswdError;
    @InjectView(R.id.set_passwd_open_disturb)
    TintCheckBox setPasswdOpenDisturb;
    @InjectView(R.id.set_passwd_what_is_disturb)
    ImageView setPasswdWhatIsDisturb;
    @InjectView(R.id.set_passwd_ok_btn)
    Button setPasswdOkBtn;

    private KeyguardStatus status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_passwd);
        ButterKnife.inject(this);

        toolbar.setTitle(R.string.input_passwd);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        status = (KeyguardStatus) DbManager.getInstance().query(KeyguardStatus.class).get(0);

//        newPasswdEdt.setOnTouchListener(generateListener(newPasswdEdt));
//        confirmEdt.setOnTouchListener(generateListener(confirmEdt));
    }

    @OnClick(R.id.set_passwd_what_is_disturb)
    public void whatClick(View view) {
        new WhatIsDisturbDialog(this).show();
    }


    @OnClick(R.id.set_passwd_ok_btn)
    public void okClick(View view) {
        if (!newPasswdEdt.getText().toString().equals(confirmEdt.getText().toString())) {
            newPasswdError.setVisibility(View.VISIBLE);
            return;
        }

        PrefUtils.setDisturbPasswd(setPasswdOpenDisturb.isChecked());

        status.passwd = newPasswdEdt.getText().toString();
        DbManager manager = DbManager.getInstance();
        manager.update(KeyguardStatus.class, status);
        Toast.makeText(this, R.string.change_passwd_ok, Toast.LENGTH_SHORT).show();
        finish();
    }


//    private View.OnTouchListener generateListener(final EditText edt) {
//        return new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    int old = edt.getInputType();
//                    edt.setInputType(InputType.TYPE_NULL);
////                    edt.setFocusable(true);
//                    new KeyboardUtil(InputPasswdActivity.this, keyboardView, edt, null).showKeyboard();
//                    edt.setInputType(old);
//                    edt.setSelection(edt.getText().length());
//                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    edt.requestFocus();
//                }
//                return true;
//            }
//        };
//    }

    private class WhatIsDisturbDialog extends Dialog {

        public WhatIsDisturbDialog(Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.what_is_disturb_dialog);
            this.findViewById(R.id.what_is_disturb_ok_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancel();
                }
            });
        }
    }

}
