//package bitman.ay27.watchdog.widget;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import org.apache.http.entity.StringEntity;
//
//import java.util.ArrayList;
//import java.util.Random;
//
///**
//* Proudly to use Intellij IDEA.
//* Created by ay27 on 15-7-23.
//*/
//public class PasswdEdt extends LinearLayout {
//    public PasswdEdt(Context context) {
//        super(context);
//    }
//
//    public PasswdEdt(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public PasswdEdt(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    private ArrayList<EditText> editTexts;
//    private static final int MAX_LENGTH = 8;
//    private int passwdLength = 4;
//
//    public void setPasswdLength(int passwdLength) {
//        editTexts = new ArrayList<>(passwdLength);
//        this.passwdLength = passwdLength;
//        initial();
//    }
//
//    private boolean isDisturb = false;
//    private boolean[] disturbs;
//    private void initial() {
//        removeAllViewsInLayout();
//        if (isDisturb) {
//            disturbs = getRandomSequence(MAX_LENGTH, MAX_LENGTH-passwdLength);
//        } else {
//            disturbs = null;
//        }
//        editTexts = new ArrayList<EditText>();
//
//        if (isDisturb) {
//            for (int i = 0; i < MAX_LENGTH; i++) {
//                if (disturbs[i]) {
//                    editTexts[i] = generateEdt(true);
//                }
//            }
//        }
//    }
//
//    private EditText generateEdt(boolean randomNum) {
//        EditText editText =
//    }
//
//
//    private static boolean[] getRandomSequence(int total, int disturbNum) {
//        boolean[] sequence = new boolean[total];
//        boolean[] output = new boolean[total];
//
//        for (int i = 0; i < disturbNum; i++) {
//            sequence[i] = true;
//        }
//        for (int i = disturbNum; i < total; i++) {
//            sequence[i] = false;
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
//    public void setDisturbNum(boolean value) {
//        isDisturb = value;
//    }
//
//    public static interface PasswdFinishedCallback {
//        public void onEditFinished(String passwd);
//    }
//}
