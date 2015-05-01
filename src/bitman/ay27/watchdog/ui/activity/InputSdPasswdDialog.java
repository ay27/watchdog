package bitman.ay27.watchdog.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import bitman.ay27.watchdog.R;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/29.
 */
public class InputSdPasswdDialog extends Dialog {

    private EditText passwdEdt;
    private Button okBtn;
    private InputFinishedCallback callback;
    private RadioGroup rg;

    public InputSdPasswdDialog(Context context, InputFinishedCallback callback) {
        super(context);
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.input_sd_passwd_dialog);

        passwdEdt = (EditText) findViewById(R.id.input_sd_passwd_dialog_pass_edt);
        okBtn = (Button) findViewById(R.id.input_sd_passwd_dialog_btn);
        rg = (RadioGroup)findViewById(R.id.input_sd_passwd_dialog_radio_group);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.finished(passwdEdt.getText().toString(), rg.getCheckedRadioButtonId() == R.id.input_sd_passwd_dialog_radio_btn_0 ? 0 :1);
                dismiss();
            }
        });
    }

    public interface InputFinishedCallback {
        public void finished(String passwd, int mode);
    }
}
