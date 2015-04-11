package bitman.ay27.watchdog.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import bitman.ay27.watchdog.R;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/8.
 */
public class LoginDialog extends Dialog {

    private RadioGroup radioGroup;
    private LinearLayout container;
    private View loginView, signUpView;

    public LoginDialog(Context context) {
        super(context);
    }

    protected LoginDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public LoginDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.login_dialog);

        radioGroup = (RadioGroup) findViewById(R.id.login_dialog_radio_group);
        container = (LinearLayout) findViewById(R.id.login_dialog_blank_view);

        loginView = getLayoutInflater().inflate(R.layout.login, null);
        signUpView = getLayoutInflater().inflate(R.layout.sign_up, null);

        container.addView(loginView);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.login_dialog_radio_login) {
                    container.removeAllViews();
                    container.addView(loginView);
                } else if (checkedId == R.id.login_dialog_radio_sign_up) {
                    container.removeAllViews();
                    container.addView(signUpView);
                }
            }
        });

        radioGroup.check(R.id.login_dialog_radio_login);

    }

}
