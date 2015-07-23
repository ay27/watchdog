package bitman.ay27.watchdog.ui.new_activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.widget.PasswdEdt;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;

import java.util.Random;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-23.
 */
public class PasswdInputTest extends Activity {


    @InjectView(R.id.passwd_edt)
    PasswdEdt passwdEdt;
    @InjectView(R.id.key_btn_cancel)
    Button keyBtnCancel;
    @InjectView(R.id.key_btn_back)
    Button keyBtnBack;

    @InjectViews({R.id.key_btn_0, R.id.key_btn_1, R.id.key_btn_2, R.id.key_btn_3, R.id.key_btn_4, R.id.key_btn_5, R.id.key_btn_6,
            R.id.key_btn_7, R.id.key_btn_8, R.id.key_btn_9})
    Button[] btns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passwd_input_test);
        ButterKnife.inject(this);

        passwdEdt.setDisturbNum(true);
        passwdEdt.setPasswdLength(4);
        passwdEdt.registerFinishedCallback(new PasswdEdt.PasswdFinishedCallback() {
            @Override
            public void onEditFinished(boolean correct, String passwd) {
                Toast.makeText(PasswdInputTest.this, ""+correct+"  "+passwd, Toast.LENGTH_LONG).show();
            }
        });

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

}
