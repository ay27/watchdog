package bitman.ay27.watchdog.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.*;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Random;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-23.
 */
public class PasswdEdt extends LinearLayout {
    private static final int MAX_LENGTH = 8;
    private ArrayList<EditText> editTexts;
    private int passwdLength = 4;
    private boolean isDisturb = false;
    private boolean[] disturbs;
    private boolean[] corrects;
    private PasswdFinishedCallback cb;
    private int editPos = 0;
    private String passwd = "";

    public PasswdEdt(Context context) {
        super(context);
    }

    public PasswdEdt(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PasswdEdt(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private static boolean[] getRandomSequence(int total, int disturbNum) {
        boolean[] sequence = new boolean[total];
        boolean[] output = new boolean[total];

        for (int i = 0; i < disturbNum; i++) {
            sequence[i] = true;
        }
        for (int i = disturbNum; i < total; i++) {
            sequence[i] = false;
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


    public void setPasswdLength(int passwdLength) {
        editTexts = new ArrayList<>(passwdLength);
        this.passwdLength = passwdLength;
        initial();
    }

    private void initial() {
        removeAllViewsInLayout();

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        setClickable(false);
        setEnabled(false);

        if (isDisturb) {
            disturbs = getRandomSequence(MAX_LENGTH, MAX_LENGTH - passwdLength);
            corrects = new boolean[MAX_LENGTH];
            for (int i = 0; i < MAX_LENGTH; i++) {
                corrects[i] = true;
            }
        } else {
            disturbs = null;
            corrects = null;
        }
        editTexts = new ArrayList<>();

        if (isDisturb) {
            for (int i = 0; i < MAX_LENGTH; i++) {
                if (disturbs[i]) {
                    editTexts.add(generateEdt(true));
                } else {
                    editTexts.add(generateEdt(false));
                }
            }
        } else {
            for (int i = 0; i < passwdLength; i++) {
                editTexts.add(generateEdt(false));
            }
        }

        LayoutParams params = new LayoutParams(72, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        for (int i = 0; i < editTexts.size(); i++) {
            addView(editTexts.get(i), params);
        }

        editPos = 0;
        editTexts.get(0).requestFocus();
        if (isDisturb && disturbs[0]) {
            enableFocusEdit(editTexts.get(0), true);
        }

        invalidate();
    }

    private void enableFocusEdit(EditText editText, boolean value) {
        if (value) {
            editText.setTextColor(Color.BLACK);
        } else {
            editText.setTextColor(getResources().getColor(R.color.gray));
        }
    }

    private EditText generateEdt(boolean randomNum) {
        final EditText editText = (EditText) (LayoutInflater.from(getContext()).inflate(R.layout.one_passwd_edt, null).findViewById(R.id.one_passwd_edt));
        editText.setEnabled(false);
        editText.setClickable(false);
        if (randomNum) {
            editText.setText("" + new Random().nextInt(10));
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
        }

        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int old = editText.getInputType();
                    editText.setInputType(InputType.TYPE_NULL);
//                    edt.setFocusable(true);
                    editText.setInputType(old);
                    editText.setSelection(editText.getText().length());
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    editText.requestFocus();
                }
                return true;
            }
        });

        return editText;
    }

    public void setDisturbNum(boolean value) {
        isDisturb = value;
    }

    public void edit(CharSequence text) {

        if (disturbs != null && disturbs[editPos]) {
            corrects[editPos] = editTexts.get(editPos).getText().toString().equals(text.toString());
        } else {
            editTexts.get(editPos).setText(text);
            passwd += text;
        }

        enableFocusEdit(editTexts.get(editPos), false);

        editPos++;

        if ((!isDisturb && editPos == passwdLength) || (isDisturb && editPos == MAX_LENGTH)) {
            boolean flag = true;
            if (corrects != null) {
                for (boolean correct : corrects) {
                    if (!correct) {
                        flag = false;
                        break;
                    }
                }
            }
            cb.onEditFinished(flag, passwd);
            cancelEdit();
            return;
        }

        editTexts.get(editPos).requestFocus();
        enableFocusEdit(editTexts.get(editPos), true);
    }

    public void cancelEdit() {
        if (!isDisturb) {
            for (EditText editText : editTexts) {
                editText.setText("");
            }
            if (editPos == passwdLength) {
                editPos--;
            }
            enableFocusEdit(editTexts.get(editPos), false);
            editPos = 0;
            passwd = "";
            enableFocusEdit(editTexts.get(editPos), true);
            return;
        }

        for (int i = 0; i < corrects.length; i++) {
            corrects[i] = true;
        }

        if (editPos == MAX_LENGTH)
            enableFocusEdit(editTexts.get(editPos - 1), false);

        editPos = 0;
        passwd = "";
        editTexts.get(0).requestFocus();
        enableFocusEdit(editTexts.get(editPos), true);

        for (int i = 0; i < editTexts.size(); i++) {
            if (!disturbs[i]) {
                editTexts.get(i).setText("");
            }
        }

    }

    public void backEdit() {
        if (!isDisturb) {
            if (editPos == 0) {
                return;
            }
            enableFocusEdit(editTexts.get(editPos), false);
            editPos--;
            editTexts.get(editPos).setText("");
            editTexts.get(editPos).requestFocus();
            enableFocusEdit(editTexts.get(editPos), true);

            return;
        }
        if (editPos == 0) {
            return;
        }

        enableFocusEdit(editTexts.get(editPos), false);

        editPos--;
        if (disturbs[editPos]) {
            corrects[editPos] = true;
        } else {
            editTexts.get(editPos).setText("");
        }

        editTexts.get(editPos).requestFocus();
        enableFocusEdit(editTexts.get(editPos), true);

    }

    public void registerFinishedCallback(PasswdFinishedCallback cb) {
        this.cb = cb;
    }

    public void unregisterCallback() {
        this.cb = null;
    }

    public static interface PasswdFinishedCallback {
        public void onEditFinished(boolean disturbCorrect, String passwd);
    }
}
