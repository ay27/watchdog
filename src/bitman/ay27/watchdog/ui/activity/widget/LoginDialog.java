package bitman.ay27.watchdog.ui.activity.widget;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.*;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.net.NetManager;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/8.
 */
public class LoginDialog extends Dialog {

    private RadioGroup radioGroup;
    private LinearLayout container;
    private View loginView, signUpView;
    private Callback cb;

    public LoginDialog(Context context, Callback cb) {
        super(context);
        this.cb = cb;
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

        setViews();

    }

    private void setViews() {
        Button loginOk = (Button)loginView.findViewById(R.id.login_login_btn);
        Button signUpOk = (Button)signUpView.findViewById(R.id.sign_up_btn);

        loginOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = ((TextView)loginView.findViewById(R.id.login_username)).getText().toString();
                final String password = ((TextView)loginView.findViewById(R.id.login_passwd)).getText().toString();
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getContext(), R.string.username_or_password_empty, Toast.LENGTH_LONG).show();
                    return;
                }
                final ProgressDialog pd = new ProgressDialog(getContext());
                pd.show();
                NetManager.signIn(username, password, new NetManager.NetCallback() {
                    @Override
                    public void onSuccess(int code, String recv) {
                        pd.dismiss();
//                        Toast.makeText(getContext(), R.string.sign_in_success, Toast.LENGTH_LONG).show();
                        cb.onSuccess(recv, username, password);
                        dismiss();
                    }

                    @Override
                    public void onError(int code, String recv) {
                        pd.dismiss();
//                        Toast.makeText(getContext(), R.string.sign_in_failed, Toast.LENGTH_LONG).show();
                        cb.onFailed();
                        dismiss();
                    }
                });
            }
        });

        signUpOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = ((TextView)signUpView.findViewById(R.id.sign_up_username)).getText().toString();
                String email = ((TextView)signUpView.findViewById(R.id.sign_up_email)).getText().toString();
                final String password = ((TextView)signUpView.findViewById(R.id.sign_up_passwd)).getText().toString();
                String passwordConfirm = ((TextView)signUpView.findViewById(R.id.sign_up_passwd_confirm)).getText().toString();

                if (username.isEmpty() || password.isEmpty() || email.isEmpty() || passwordConfirm.isEmpty()) {
                    Toast.makeText(getContext(), R.string.can_not_be_empty, Toast.LENGTH_LONG).show();
                    return;
                }
                if (!password.equals(passwordConfirm)) {
                    Toast.makeText(getContext(), R.string.passwd_can_not_match, Toast.LENGTH_LONG).show();
                    return;
                }

                final ProgressDialog pd = new ProgressDialog(getContext());
                pd.show();
                NetManager.signUp(username, email, password, new NetManager.NetCallback() {
                    @Override
                    public void onSuccess(int code, String recv) {

                        Toast.makeText(getContext(), R.string.sign_up_success, Toast.LENGTH_LONG).show();
                        pd.dismiss();
                        pd.setTitle(R.string.sign_in_processing);
                        pd.show();

                        NetManager.signIn(username, password, new NetManager.NetCallback() {
                            @Override
                            public void onSuccess(int code, String recv) {
                                pd.dismiss();
                                cb.onSuccess(recv, username, password);
                                dismiss();
                            }

                            @Override
                            public void onError(int code, String recv) {
                                pd.dismiss();
                                Toast.makeText(getContext(), R.string.sign_in_failed, Toast.LENGTH_LONG).show();
                                cb.onFailed();
                                dismiss();
                            }
                        });

                    }

                    @Override
                    public void onError(int code, String recv) {
                        pd.dismiss();
                        Toast.makeText(getContext(), R.string.sign_up_failed, Toast.LENGTH_LONG).show();
                        cb.onFailed();
                        dismiss();
                    }
                });
            }
        });
    }

    public interface Callback {
        public void onSuccess(String content, String username, String password);
        public void onFailed();
    }

}
