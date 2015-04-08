package bitman.ay27.watchdog.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import bitman.ay27.watchdog.R;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends PreferenceActivity {

//    @InjectView(R.id.main_toolbar)
//    Toolbar toolbar;
//    @InjectView(R.id.main_login)
//    View loginPanel;
//    @InjectView(R.id.main_set_watch)
//    View setWatchPanel;
//    @InjectView(R.id.main_set_passwd)
//    View setPasswdPanel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference);

//        setContentView(R.layout.main);

//        ButterKnife.inject(this);
//        toolbar.setTitleTextColor(Color.WHITE);
//        setSupportActionBar(toolbar);
//
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.drawer_activity_content_frame, fragment).commit();

//        loginPanel.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

//    @Override
//    public void onClick(View v) {
//        Intent intent = new Intent();
//        if (v.getId() == loginPanel.getId()) {
//            intent.setClass(this, LoginActivity.class);
//        }
//        else if (v.getId() == setWatchPanel.getId()) {
//            intent.setClass(this, SetWatchActivity.class);
//        }
//        else if (v.getId() == setPasswdPanel.getId()) {
//            intent.setClass(this, SetDrawPasswdActivity.class);
//        }
//        else {
//            return;
//        }
//
//        startActivity(intent);
//    }
}
