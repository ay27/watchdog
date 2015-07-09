package bitman.ay27.watchdog.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.db.DbManager;
import bitman.ay27.watchdog.db.model.AngleChain;
import bitman.ay27.watchdog.db.model.KeyguardStatus;
import bitman.ay27.watchdog.processor.AngleChainProcessor;
import bitman.ay27.watchdog.processor.Curve;
import bitman.ay27.watchdog.ui.DrawingCanvas;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import java.util.ArrayList;
import java.util.List;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/6/25.
 */
public class FirstEnterCheckActivity extends Activity {
    private static final String TAG = "FirstEnterCheck";
    @InjectView(R.id.keyguard_img)
    DrawingCanvas dCanvas;
    @InjectView(R.id.keyguard_change_btn)
    Button changeModeBtn;
    @InjectView(R.id.keyguard_img_error_toast)
    View errorToast;
    @InjectView(R.id.keyguard_app_lock)
    TextView keyguardAppLock;

    private KeyguardStatus status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keyguard_img);

        ButterKnife.inject(this);

        keyguardAppLock.setVisibility(View.VISIBLE);

        List list = DbManager.getInstance().query(KeyguardStatus.class);
        if (list == null || list.size() == 0) {
            Log.e(TAG, "can not read keyguard status from DB");
            start2main();
            return;
        }
        status = (KeyguardStatus) list.get(0);
        if (status == null) {
            Toast.makeText(this, "keyguard status error", Toast.LENGTH_SHORT).show();
            start2main();
        }

        setupCanvas();

        if (status.imagePath != null) {
            dCanvas.setBackground(Drawable.createFromPath(status.imagePath));
        }


    }

    private void setupCanvas() {
        if (status.patternAngelChainIds == null || status.patternAngelChainIds.size() == 0) {
            Toast.makeText(this, "please setup password", Toast.LENGTH_SHORT).show();
            finish();
        }

        final ArrayList<AngleChain> patterns = new ArrayList<AngleChain>();

        final DbManager manager = DbManager.getInstance();
        AngleChain tmp;
        for (Long id : status.patternAngelChainIds) {
            if ((tmp = (AngleChain) manager.query(AngleChain.class, id)) != null) {
                patterns.add(tmp);
            }
        }

        dCanvas.setOnDrawFinishedListener(new DrawingCanvas.DrawingCallback() {
            @Override
            public void onDrawPause() {
                Animation animation = AnimationUtils.loadAnimation(FirstEnterCheckActivity.this, R.anim.abc_fade_in);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        errorToast.setVisibility(View.VISIBLE);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                errorToast.setVisibility(View.VISIBLE);
                                Animation fadeout = AnimationUtils.loadAnimation(FirstEnterCheckActivity.this, R.anim.abc_fade_out);
                                fadeout.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        errorToast.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                errorToast.startAnimation(fadeout);
                            }
                        }, 1500);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                errorToast.startAnimation(animation);
            }

            @Override
            public void onActionDown() {
                Animation animation = AnimationUtils.loadAnimation(FirstEnterCheckActivity.this, R.anim.abc_fade_out);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        changeModeBtn.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                changeModeBtn.startAnimation(animation);
            }

            @Override
            public void onActionUp(ArrayList<Curve> rawCurves) {
                Animation fadein = AnimationUtils.loadAnimation(FirstEnterCheckActivity.this, R.anim.abc_fade_in);
                fadein.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        changeModeBtn.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                changeModeBtn.startAnimation(fadein);


                AngleChainProcessor processor = new AngleChainProcessor(patterns, rawCurves);
                if (processor.compare()) {
                    processor.updatePattern();
                    manager.updateList(AngleChain.class, patterns);

                    start2main();
                }
            }
        });

    }

    private void start2main() {
        Intent intent = new Intent(FirstEnterCheckActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.keyguard_change_btn)
    public void changeClick(View v) {
        Intent intent = new Intent(this, FirstEnterCheck2Activity.class);
        intent.putExtra("Status", status);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
