package bitman.ay27.watchdog.ui.new_activity.passwd;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.db.DbManager;
import bitman.ay27.watchdog.db.model.AngleChain;
import bitman.ay27.watchdog.db.model.KeyguardStatus;
import bitman.ay27.watchdog.processor.AngleChainProcessor;
import bitman.ay27.watchdog.processor.Curve;
import bitman.ay27.watchdog.ui.activity.widget.DrawingCanvas;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import java.util.ArrayList;
import java.util.List;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-21.
 */
public class DrawPasswdActivity extends Activity {

    @InjectView(R.id.passwd_set_draw_canvas)
    DrawingCanvas drawCanvas;
    @InjectView(R.id.passwd_set_draw_clear)
    Button clearBtn;
    @InjectView(R.id.passwd_set_draw_next)
    Button nextBtn;
    @InjectView(R.id.passwd_set_draw_widget_panel)
    RelativeLayout widgetPanel;

    private Drawable background;
    private ArrayList<Curve> curves;
    private int drawCount = 0;
    private KeyguardStatus status;

    private DrawingCanvas.DrawingCallback drawFinishedListener = new DrawingCanvas.DrawingCallback() {
        @Override
        public void onDrawPause() {

        }

        @Override
        public void onActionDown() {
            Animation animation = AnimationUtils.loadAnimation(DrawPasswdActivity.this, R.anim.abc_fade_out);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    widgetPanel.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            widgetPanel.startAnimation(animation);

        }

        @Override
        public void onActionUp(ArrayList<Curve> rawCurves) {
            curves = rawCurves;

            Animation animation = AnimationUtils.loadAnimation(DrawPasswdActivity.this, R.anim.abc_fade_in);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    widgetPanel.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            widgetPanel.startAnimation(animation);
        }
    };
    private List<AngleChain> chains;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passwd_set_draw);
        ButterKnife.inject(this);

        String filePath = getIntent().getStringExtra("ImgFilePath");
        if (filePath == null || filePath.isEmpty()) {
            background = getResources().getDrawable(R.drawable.default_draw_background);
        } else {
            background = Drawable.createFromPath(filePath);
        }

        drawCanvas.setBackground(background);
        drawCanvas.setOnDrawFinishedListener(drawFinishedListener);

        status = new KeyguardStatus();
    }

    @OnClick(R.id.passwd_set_draw_next)
    public void nextClick(View view) {
        if (curves == null || curves.isEmpty()) {
            Toast.makeText(this, R.string.please_draw_passwd, Toast.LENGTH_SHORT).show();
            return;
        }

        switch (drawCount) {
            case 0:
                AngleChainProcessor processor = new AngleChainProcessor(curves);
                chains = processor.fit_matching_curves();
                break;
            case 1:
                AngleChainProcessor processor1 = new AngleChainProcessor(new ArrayList<>(chains), curves);
                if (!processor1.compare()) {
                    Toast.makeText(this, R.string.draw_passwd_not_match, Toast.LENGTH_LONG).show();
                    return;
                }
                processor1.updatePattern();
                break;
            case 2:
                AngleChainProcessor processor2 = new AngleChainProcessor(new ArrayList<>(chains), curves);
                if (!processor2.compare()) {
                    Toast.makeText(this, R.string.draw_passwd_not_match, Toast.LENGTH_LONG).show();
                    return;
                }
                processor2.updatePattern();
                write2Db();
                start2NextActivity();
                break;
        }

        drawCount++;
        updateNextBtn(drawCount);
        drawCanvas.cleanCanvas();
    }

    private void start2NextActivity() {
        Intent intent = new Intent(this, DynamicKeyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private void write2Db() {
        DbManager manager = DbManager.getInstance();
        List exists = manager.query(AngleChain.class);
        if (exists != null && !exists.isEmpty()) {
            manager.bulkDelete(AngleChain.class, exists);
        }
        manager.bulkInsert(AngleChain.class, chains);

        exists = manager.query(KeyguardStatus.class);
        if (exists != null && !exists.isEmpty()) {
            manager.bulkDelete(KeyguardStatus.class, exists);
        }

        chains = manager.query(AngleChain.class);
        ArrayList<Long> ids = new ArrayList<Long>();
        for (AngleChain chain : chains) {
            ids.add(chain.id);
        }

        status.patternAngelChainIds = ids;
        status.unlockType = KeyguardStatus.PasswdType.image;
        manager.insert(KeyguardStatus.class, status);

        Toast.makeText(this, R.string.draw_passwd_save_ok, Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.passwd_set_draw_clear)
    public void clearClick(View view) {
        curves = null;
        chains = null;
        drawCount = 0;
        drawCanvas.cleanCanvas();
        updateNextBtn(drawCount);
    }


    private void updateNextBtn(int count) {
        switch (count) {
            case 0:
                nextBtn.setText(R.string.set_passwd_1);
                break;
            case 1:
                nextBtn.setText(R.string.set_passwd_2);
                break;
            case 2:
                nextBtn.setText(R.string.set_passwd_3);
                break;
        }
    }
}
