package bitman.ay27.watchdog.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import bitman.ay27.watchdog.R;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.main_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.main_boot_loader_summer)
    TextView bootLoaderSummer;
    @InjectView(R.id.main_sd_encrypt_summer)
    TextView sdEncryptSummer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        ButterKnife.inject(this);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }

    @OnClick(R.id.main_login_panel)
    public void loginClick(View view) {
        new LoginDialog(this).show();
    }

    @OnCheckedChanged(R.id.main_boot_loader_lock_switch)
    public void bootLoaderCheckChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            bootLoaderSummer.setText(R.string.boot_loader_summer_true);
        } else {
            bootLoaderSummer.setText(R.string.boot_loader_summer_false);
        }
    }

    @OnCheckedChanged(R.id.main_sd_encrypt_switch)
    public void sdEncryptCheckChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            sdEncryptSummer.setText(R.string.sd_encrypt_summer_true);
        } else {
            sdEncryptSummer.setText(R.string.sd_encrypt_summer_false);
        }
    }

    @OnClick(R.id.main_draw_panel)
    public void drawPanelClick(View view) {
        Intent intent = new Intent(this, SetDrawPasswdActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.main_sec_passwd_panel)
    public void secPasswdPanelClick(View view) {
        Intent intent = new Intent(this, SetPasswdActivity.class);
        startActivity(intent);
    }


    @OnClick(R.id.main_about_panel)
    public void aboutClick(View view) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.about)
                .setMessage(R.string.about_content)
                .setPositiveButton(R.string.ok, null)
                .create()
                .show();
    }
}
