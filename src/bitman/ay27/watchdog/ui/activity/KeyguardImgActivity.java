package bitman.ay27.watchdog.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/5.
 */
public class KeyguardImgActivity extends Activity {

    @InjectView(R.id.keyguard_img)
    DrawingCanvas dCanvas;
    @InjectView(R.id.keyguard_change_btn)
    Button changeModeBtn;

    private WindowManager wm;
    private KeyguardStatus status;
    private View view;
    private boolean staticViewAdded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = getLayoutInflater().inflate(R.layout.keyguard_img, null);
        ButterKnife.inject(this, view);

        wm = (WindowManager) getApplicationContext().getSystemService("window");

        status = (KeyguardStatus) getIntent().getSerializableExtra("Status");
        if (status == null) {
            Toast.makeText(this, "keyguard status error", Toast.LENGTH_SHORT).show();
            finish();
        }

        setupCanvas();
    }

    @Override
    protected void onDestroy() {
        if (staticViewAdded) {
            wm.removeViewImmediate(view);
        }
        super.onDestroy();
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
            public void onDrawPause(ArrayList<Curve> rawCurves) {
                AngleChainProcessor processor = new AngleChainProcessor(patterns, rawCurves);
                if (processor.compare()) {
                    processor.updatePattern();
                    manager.updateList(AngleChain.class, patterns);
                    finish();
                }
            }

            @Override
            public void onActionDown() {
                Animation animation = AnimationUtils.loadAnimation(KeyguardImgActivity.this, R.anim.abc_fade_out);
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
            public void onActionUp() {
                Animation animation = AnimationUtils.loadAnimation(KeyguardImgActivity.this, R.anim.abc_fade_in);
                animation.setAnimationListener(new Animation.AnimationListener() {
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
                changeModeBtn.startAnimation(animation);
            }
        });

        addStaticView(view);
        staticViewAdded = true;
    }

    @OnClick(R.id.keyguard_change_btn)
    public void changeClick(View v) {
        Intent intent = new Intent(this, KeyguardKeyboardActivity.class);
        intent.putExtra("Status", status);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.finish();
    }

    private void addStaticView(View view) {
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

        /**
         *以下都是WindowManager.LayoutParams的相关属性
         * 具体用途请参考SDK文档
         */
        final int PARAMS = WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD; // | WindowManager.LayoutParams.FLAG_FULLSCREEN
//                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
//                | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;

        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;   //这里是关键，你也可以试试2003
        wmParams.format = PixelFormat.OPAQUE;
        /**
         *这里的flags也很关键
         *代码实际是wmParams.flags |= FLAG_NOT_FOCUSABLE;
         *40的由来是wmParams的默认属性（32）+ FLAG_NOT_FOCUSABLE（8）
         */
        wmParams.flags = PARAMS;
        wmParams.width = wmParams.MATCH_PARENT;
        wmParams.height = wmParams.MATCH_PARENT;
        wm.addView(view, wmParams);  //创建View
    }
}
