package bitman.ay27.watchdog.ui.new_activity.lock;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;
import bitman.ay27.watchdog.PrefUtils;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.db.DbManager;
import bitman.ay27.watchdog.db.model.AngleChain;
import bitman.ay27.watchdog.db.model.KeyguardStatus;
import bitman.ay27.watchdog.processor.AngleChainProcessor;
import bitman.ay27.watchdog.processor.Curve;
import bitman.ay27.watchdog.ui.activity.widget.DrawingCanvas;
import bitman.ay27.watchdog.utils.Common;
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
    @InjectView(R.id.keyguard_img_error_toast)
    View errorToast;

    private WindowManager wm;
    private KeyguardStatus status;
    private View view;
    private boolean staticViewAdded = false;
    private BroadcastReceiver killKeyguardReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
    private boolean isSafe = true;

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

        if (status.imagePath != null) {
            dCanvas.setBackground(Drawable.createFromPath(status.imagePath));
        }


//        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
//        adapter.enableReaderMode(this, new NfcAdapter.ReaderCallback() {
//            @Override
//            public void onTagDiscovered(Tag tag) {
//                Log.i("NFC", "found : " + Utils.ByteArrayToHexString(tag.getId()));
//            }
//        }, NfcAdapter.FLAG_READER_NFC_A, null);

        registerReceiver(killKeyguardReceiver, new IntentFilter(Common.ACTION_KILL_KEYGUARD));
    }

    @Override
    protected void onDestroy() {
        if (staticViewAdded) {
            wm.removeViewImmediate(view);
        }

        unregisterReceiver(killKeyguardReceiver);

        super.onDestroy();
    }

    private void setupCanvas() {
        if (status.patternAngelChainIds == null || status.patternAngelChainIds.size() == 0) {
            Toast.makeText(this, "please setup password", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (!(isSafe = PrefUtils.isPhoneSafe())) {
            changeModeBtn.setVisibility(View.GONE);
        }

        if (getIntent().getBooleanExtra("Danger", false)) {
            isSafe = false;
            changeModeBtn.setVisibility(View.GONE);
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
                Animation animation = AnimationUtils.loadAnimation(KeyguardImgActivity.this, R.anim.abc_fade_in);
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
                                Animation fadeout = AnimationUtils.loadAnimation(KeyguardImgActivity.this, R.anim.abc_fade_out);
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

                if (isSafe) {

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
            }

            @Override
            public void onActionUp(ArrayList<Curve> rawCurves) {
                if (isSafe) {
                    Animation fadein = AnimationUtils.loadAnimation(KeyguardImgActivity.this, R.anim.abc_fade_in);
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
                }


                AngleChainProcessor processor = new AngleChainProcessor(patterns, rawCurves);
                if (processor.compare()) {
                    processor.updatePattern();
                    manager.updateList(AngleChain.class, patterns);


                    if (!isSafe) {
                        Intent intent = new Intent(KeyguardImgActivity.this, KeyguardKeyboardActivity.class);
                        intent.putExtra("Status", status);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                    }

                    finish();
                }
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
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
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
