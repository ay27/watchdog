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
 * Created by ay27 on 15/3/28.
 */
public class RandomCharKeyboard extends FrameLayout implements View.OnClickListener {
    private static final int[] ids = new int[]{
            R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9, R.id.button0,
            R.id.buttona, R.id.buttonb, R.id.buttonc, R.id.buttond, R.id.buttone, R.id.buttonf, R.id.buttong, R.id.buttonh, R.id.buttoni, R.id.buttonj,
            R.id.buttonk, R.id.buttonl, R.id.buttonm, R.id.buttonn, R.id.buttono, R.id.buttonp, R.id.buttonq, R.id.buttonr, R.id.buttons, R.id.buttont,
            R.id.buttonu, R.id.buttonv, R.id.buttonw, R.id.buttonx, R.id.buttony, R.id.buttonz};

    private static final char[] character = new char[]{
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z'
    };

    private static final int KEYBOARD_NUMBER_SUM = 36;

    private EditText passwdEdt;
    private Button okBtn, cancelBtn, backBtn;

    private String passwdTxt = "";
    private ArrayList<KeyboardCallback> callbacks;
    private List<Button> btns;

    public RandomCharKeyboard(Context context) {
        super(context);
        init();
    }

    public RandomCharKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RandomCharKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
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

    private void init() {
        View keyboard = LayoutInflater.from(getContext()).inflate(R.layout.random_keyboard_char, null);
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
        okBtn = (Button) view.findViewById(R.id.char_button_ok);
        cancelBtn = (Button) view.findViewById(R.id.button_cancel);
        backBtn = (Button) view.findViewById(R.id.button_back);

        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        passwdEdt = (EditText) view.findViewById(R.id.char_passwd_input);
    }

    public void randomIt() {
        int[] randomSequence = getRandomSequence(KEYBOARD_NUMBER_SUM);
        for (int i = 0; i < KEYBOARD_NUMBER_SUM; i++) {
            btns.get(i).setText("" + character[randomSequence[i]]);
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
        if (v.getId() == R.id.char_button_ok) {
            if (callbacks == null) {
                return;
            }
            for (KeyboardCallback callback : callbacks) {
                callback.onInputFinshed(passwdTxt);
            }
        } else if (v.getId() == R.id.button_cancel) {
            passwdEdt.setText(passwdTxt = "");
        } else if (v.getId() == R.id.button_back) {
            if (passwdTxt == null || passwdTxt.equals("") || passwdTxt.length() == 0) {
                return;
            }
            passwdEdt.setText(passwdTxt = passwdTxt.substring(0, passwdTxt.length() - 1));
        } else {
            passwdEdt.setText(passwdTxt = passwdTxt + ((Button) v).getText());
        }
    }
}
