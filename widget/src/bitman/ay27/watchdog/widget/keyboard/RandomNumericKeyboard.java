package bitman.ay27.watchdog.widget.keyboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import bitman.ay27.watchdog.widget.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/1/29.
 */
public class RandomNumericKeyboard extends FrameLayout implements View.OnClickListener {
    private static final int[] ids = new int[]{
            R.id.key_btn_1, R.id.key_btn_2, R.id.key_btn_3, R.id.key_btn_4, R.id.key_btn_5,
            R.id.key_btn_6, R.id.key_btn_7, R.id.key_btn_8, R.id.key_btn_9, R.id.key_btn_0,
    };

    private static final int KEYBOARD_NUMBER_SUM = 10;

    private String passwdTxt = "";
    private EditText passwdEdt;
    private Button okBtn, cancelBtn, backBtn;

    private List<Button> btns;
    private ArrayList<KeyboardCallback> callbacks;

    public RandomNumericKeyboard(Context context) {
        super(context);
        init(null);
    }

    public RandomNumericKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RandomNumericKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
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

    private void init(AttributeSet attrs) {
        View keyboard = LayoutInflater.from(getContext()).inflate(R.layout.random_keyboard_numeric, null);
        btns = new ArrayList<Button>();
        findViews(keyboard);
        this.setEnabled(true);
        keyboard.setEnabled(true);
        randomIt();
        addView(keyboard);
    }

    private void findViews(View view) {
        for (int id : ids) {
            Button button = (Button) view.findViewById(id);
            button.setOnClickListener(this);
            btns.add(button);
        }
        okBtn = (Button) view.findViewById(R.id.numeric_button_ok);
        cancelBtn = (Button) view.findViewById(R.id.key_btn_cancel);
        backBtn = (Button) view.findViewById(R.id.key_btn_back);

        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        passwdEdt = (EditText) view.findViewById(R.id.numeric_passwd_input);
    }

    public void randomIt() {
        int[] randomSequence = getRandomSequence(KEYBOARD_NUMBER_SUM);
        for (int i = 0; i < KEYBOARD_NUMBER_SUM; i++) {
            btns.get(i).setText("" + randomSequence[i]);
        }
    }

    public void registerPasswdListener(KeyboardCallback callback) {
        if (callbacks == null) {
            callbacks = new ArrayList<KeyboardCallback>();
        }
        callbacks.add(callback);
    }

    public void unregisterPasswdListener(KeyboardCallback callback) {
        if (callbacks.contains(callback)) {
            callbacks.remove(callback);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.numeric_button_ok) {
            if (callbacks == null) {
                return;
            }
            for (KeyboardCallback callback : callbacks) {
                callback.onInputFinished(passwdTxt);
            }
        } else if (v.getId() == R.id.key_btn_cancel) {
            passwdEdt.setText(passwdTxt = "");
        } else if (v.getId() == R.id.key_btn_back) {
            if (passwdTxt == null || passwdTxt.equals("") || passwdTxt.length() == 0) {
                return;
            }
            passwdEdt.setText(passwdTxt = passwdTxt.substring(0, passwdTxt.length() - 1));
        } else {
            passwdEdt.setText(passwdTxt = passwdTxt + ((Button) v).getText());
        }
    }
}
