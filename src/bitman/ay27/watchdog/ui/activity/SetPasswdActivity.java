//package bitman.ay27.watchdog.ui.activity;
//
//import android.graphics.Color;
//import android.inputmethodservice.KeyboardView;
//import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
//import android.support.v7.widget.Toolbar;
//import android.text.InputType;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//import bitman.ay27.watchdog.R;
//import bitman.ay27.watchdog.db.DbManager;
//import bitman.ay27.watchdog.db.model.KeyguardStatus;
//import bitman.ay27.watchdog.widget.keyboard.KeyboardUtil;
//import butterknife.ButterKnife;
//import butterknife.InjectView;
//import butterknife.OnClick;
//
//import java.util.List;
//
///**
//* Proudly to user Intellij IDEA.
//* Created by ay27 on 15/4/7.
//*/
//public class SetPasswdActivity extends ActionBarActivity {
//
//    @InjectView(R.id.set_passwd_toolbar)
//    Toolbar toolbar;
//    @InjectView(R.id.set_passwd_new_passwd)
//    EditText newPasswdEdt;
//    @InjectView(R.id.set_passwd_old_passwd)
//    EditText oldPasswdEdt;
//    @InjectView(R.id.set_passwd_confirm_passwd)
//    EditText confirmEdt;
//    @InjectView(R.id.set_passwd_ok_btn)
//    Button okBtn;
//    @InjectView(R.id.keyboard_view)
//    KeyboardView keyboardView;
//
//    @InjectView(R.id.old_passwd_error)
//    TextView oldPasswdError;
//    @InjectView(R.id.new_passwd_error)
//    TextView newPasswdError;
//
//    private KeyguardStatus status;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.set_passwd);
//        ButterKnife.inject(this);
//        toolbar.setTitleTextColor(Color.WHITE);
//        setSupportActionBar(toolbar);
//
//        readStatusFromDB();
//
//        if (status == null || status.passwd == null || status.passwd.length() == 0) {
//            oldPasswdEdt.setVisibility(View.GONE);
//        }
//
////        newPasswdEdt.setInputType(InputType.TYPE_NULL);
//        newPasswdEdt.setOnTouchListener(generateListener(newPasswdEdt));
////        oldPasswdEdt.setInputType(InputType.TYPE_NULL);
//        oldPasswdEdt.setOnTouchListener(generateListener(oldPasswdEdt));
////        confirmEdt.setInputType(InputType.TYPE_NULL);
//        confirmEdt.setOnTouchListener(generateListener(confirmEdt));
//    }
//
//    private void readStatusFromDB() {
//        List tmp = DbManager.getInstance().query(KeyguardStatus.class);
//        if (tmp == null || tmp.size() == 0) {
//            status = null;
//        } else {
//            status = (KeyguardStatus) tmp.get(0);
//        }
//    }
//
//    @OnClick(R.id.set_passwd_ok_btn)
//    public void okClick(View view) {
//        if (status!=null && status.passwd!=null && !status.passwd.equals(oldPasswdEdt.getText().toString())) {
//            oldPasswdError.setVisibility(View.VISIBLE);
//            return;
//        }
//        else if (!newPasswdEdt.getText().toString().equals(confirmEdt.getText().toString())) {
//            newPasswdError.setVisibility(View.VISIBLE);
//            return;
//        }
//        if (status == null) {
//            status = new KeyguardStatus();
//        }
//        status.passwd = newPasswdEdt.getText().toString();
//        DbManager manager = DbManager.getInstance();
//        manager.update(KeyguardStatus.class, status);
//        Toast.makeText(this, R.string.change_passwd_ok, Toast.LENGTH_SHORT).show();
//    }
//
//    private View.OnTouchListener generateListener(final EditText edt) {
//        return new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    int old = edt.getInputType();
//                    edt.setInputType(InputType.TYPE_NULL);
////                    edt.setFocusable(true);
//                    new KeyboardUtil(SetPasswdActivity.this, keyboardView, edt, null).showKeyboard();
//                    edt.setInputType(old);
//                    edt.setSelection(edt.getText().length());
//                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    edt.requestFocus();
//                }
//                return true;
//            }
//        };
//    }
//}
