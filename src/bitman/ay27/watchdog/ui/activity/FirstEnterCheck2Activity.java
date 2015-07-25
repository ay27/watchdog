//package bitman.ay27.watchdog.ui.activity;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.InputType;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//import bitman.ay27.watchdog.R;
//import bitman.ay27.watchdog.db.DbManager;
//import bitman.ay27.watchdog.db.model.KeyguardStatus;
//import bitman.ay27.watchdog.widget.PasswdEdt;
//import bitman.ay27.watchdog.widget.keyboard.KeyboardCallback;
//import bitman.ay27.watchdog.widget.keyboard.KeyboardUtil;
//import butterknife.ButterKnife;
//import butterknife.InjectView;
//import butterknife.InjectViews;
//import butterknife.OnClick;
//
//import java.util.List;
//import java.util.Random;
//
///**
// * Proudly to user Intellij IDEA.
// * Created by ay27 on 15/6/25.
// */
//public class FirstEnterCheck2Activity extends Activity {
//
//    private static final String TAG = "FirstEnterCheck2";
//    //    @InjectView(R.id.keyguard_keyboard_input)
////    EditText inputEdt;
////    @InjectView(R.id.keyboard_view)
////    KeyboardView keyboardView;
//    @InjectView(R.id.keyguard_app_lock)
//    TextView keyguardAppLock;
//    @InjectView(R.id.passwd_edt)
//    PasswdEdt passwdEdt;
//    @InjectView(R.id.key_btn_cancel)
//    Button keyBtnCancel;
//    @InjectView(R.id.key_btn_back)
//    Button keyBtnBack;
//
//    @InjectViews({R.id.key_btn_0, R.id.key_btn_1, R.id.key_btn_2, R.id.key_btn_3, R.id.key_btn_4, R.id.key_btn_5, R.id.key_btn_6,
//            R.id.key_btn_7, R.id.key_btn_8, R.id.key_btn_9})
//    Button[] btns;
//
//    private KeyguardStatus status;
//    private PasswdEdt.PasswdFinishedCallback kbCallback = new PasswdEdt.PasswdFinishedCallback() {
//        @Override
//        public void onEditFinished(boolean disturbCorrect, String passwd) {
//            if (!disturbCorrect || !passwd.equals(status.passwd)) {
//                Toast.makeText(FirstEnterCheck2Activity.this, R.string.passwd_error, Toast.LENGTH_LONG).show();
//                return;
//            }
//            Intent intent = new Intent(FirstEnterCheck2Activity.this, MainActivity.class);
////                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            finish();
//
//        }
//    };
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.keyguard_keyboard);
//        ButterKnife.inject(this);
//
//
//        keyguardAppLock.setVisibility(View.VISIBLE);
//
//        List list = DbManager.getInstance().query(KeyguardStatus.class);
//        if (list == null || list.size() == 0) {
//            Log.e(TAG, "can not read keyguard status from DB");
//            return;
//        }
//        status = (KeyguardStatus) list.get(0);
//        if (status == null) {
//            Toast.makeText(this, "keyguard status error", Toast.LENGTH_SHORT).show();
//            finish();
//        }
//
//        setupKeyboard();
//
////        TaskUtils.executeAsyncTask(new NfcScanner(this));
//    }
//
//    @OnClick(R.id.keyguard_change_btn)
//    public void changeMode(View view) {
//        Intent intent = new Intent(this, FirstEnterCheckActivity.class);
//        intent.putExtra("Status", status);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        finish();
//    }
//
//
//    private void setupKeyboard() {
//        passwdEdt.setDisturbNum(true);
//        passwdEdt.setPasswdLength(status.passwd.length());
//        passwdEdt.registerFinishedCallback(kbCallback);
//
//        for (Button button : btns) {
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    passwdEdt.edit(((Button) v).getText());
//                }
//            });
//        }
//
//        keyBtnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                passwdEdt.cancelEdit();
//            }
//        });
//
//        keyBtnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                passwdEdt.backEdit();
//            }
//        });
//
//        randomKeyboard();
//    }
//
//    private void randomKeyboard() {
//        int[] ints = getRandomSequence(10);
//        for (int i = 0; i < 10; i++) {
//            btns[i].setText(""+ints[i]);
//        }
//    }
//
//    private static int[] getRandomSequence(int total) {
//        int[] sequence = new int[total];
//        int[] output = new int[total];
//
//        for (int i = 0; i < total; i++) {
//            sequence[i] = i;
//        }
//
//        Random random = new Random();
//        int end = total - 1;
//
//        for (int i = 0; i < total; i++) {
//            int num = random.nextInt(end + 1);
//            output[i] = sequence[num];
//            sequence[num] = sequence[end];
//            end--;
//        }
//
//        return output;
//    }
//
//}
