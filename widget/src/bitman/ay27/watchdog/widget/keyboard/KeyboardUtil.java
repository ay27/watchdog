package bitman.ay27.watchdog.widget.keyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import bitman.ay27.watchdog.widget.R;

import java.util.List;
import java.util.Random;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/14.
 */
public class KeyboardUtil {
    public boolean is_num = true;// 是否数据键盘
    public boolean is_upper = false;// 是否大写
    private Context context;
    private KeyboardView keyboardView;
    private Keyboard charBoard;// 字母键盘
    private Keyboard numericBoard;// 数字键盘
    private EditText bindEdt;
    private KeyboardCallback callback;

    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void onPress(int primaryCode) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Editable editable = bindEdt.getText();
            int start = bindEdt.getSelectionStart();
            if (primaryCode == Keyboard.KEYCODE_DONE) {// 完成
                hideKeyboard();
                callback.onInputFinished(bindEdt.getText().toString());
            } else if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
                if (editable != null && editable.length() > 0) {
                    if (start > 0) {
                        editable.delete(start - 1, start);
                    }
                }
            } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {// 大小写切换
                changeKey();
                keyboardView.setKeyboard(charBoard);

            } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE) {// 数字键盘切换
                if (is_num) {
                    is_num = false;
                    keyboardView.setKeyboard(charBoard);
                } else {
                    is_num = true;
                    keyboardView.setKeyboard(numericBoard);
                }
            } else if (primaryCode == -6) {
                bindEdt.setText("");
            }
            else {
                editable.insert(start, Character.toString((char) primaryCode));
            }
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void swipeUp() {
        }
    };

    public KeyboardUtil(Context context, KeyboardView keyboardView, EditText bindEdt, KeyboardCallback callback) {
        this.context = context;
        this.keyboardView = keyboardView;
        this.bindEdt = bindEdt;
        this.callback = callback;
        init();
    }

    private void init() {
        charBoard = new Keyboard(context, R.xml.qwerty);
        numericBoard = new Keyboard(context, R.xml.symbols);
        keyboardView.setKeyboard(numericBoard);
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(true);
        keyboardView.setOnKeyboardActionListener(listener);
        random();
    }

    private static final int[] numericCodes = new int[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57};

    private void random() {
        List<Keyboard.Key> keys = numericBoard.getKeys();
        int[] random = getRandomSequence(numericCodes.length);
        for (int i = 0; i < keys.size(); i++) {
            Keyboard.Key key = keys.get(i);
            if (key.codes[0]>=numericCodes[0] && key.codes[0]<=numericCodes[numericCodes.length-1]) {
                key.codes[0] = numericCodes[random[key.codes[0]-48]];
                key.label = new String(Character.toChars(key.codes[0]));
            }
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

    /**
     * 键盘大小写切换
     */
    private void changeKey() {
        List<Keyboard.Key> keylist = charBoard.getKeys();
        if (is_upper) {//大写切换小写
            is_upper = false;
            for (Keyboard.Key key : keylist) {
                if (key.label != null && isword(key.label.toString())) {
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] = key.codes[0] + 32;
                }
            }
        } else {//小写切换大写
            is_upper = true;
            for (Keyboard.Key key : keylist) {
                if (key.label != null && isword(key.label.toString())) {
                    key.label = key.label.toString().toUpperCase();
                    key.codes[0] = key.codes[0] - 32;
                }
            }
        }
    }

    public void showKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            keyboardView.setVisibility(View.VISIBLE);
        }
    }

    public void hideKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.VISIBLE) {
            keyboardView.setVisibility(View.INVISIBLE);
        }
    }

    private boolean isword(String str) {
        String wordstr = "abcdefghijklmnopqrstuvwxyz";
        if (wordstr.indexOf(str.toLowerCase()) > -1) {
            return true;
        }
        return false;
    }

}
