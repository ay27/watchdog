package bitman.ay27.watchdog.ui.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
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
import bitman.ay27.watchdog.utils.ImageDecodeUtils;
import bitman.ay27.watchdog.utils.Utils;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/16.
 */
public class SetDrawPasswdActivity extends Activity {

    private static final int GET_PICTURE = 0x1;
    private static final int TAKE_PHOTO = 0x2;
    private static final String TAG = "SetDrawPasswdActivity";
    @InjectView(R.id.set_draw_passwd_canvas)
    DrawingCanvas canvas;
    @InjectView(R.id.set_draw_passwd_widget)
    View widgetView;
    @InjectView(R.id.set_draw_passwd_change_img)
    Button changeImgBtn;
    @InjectView(R.id.set_draw_passwd_save)
    Button saveBtn;

    private Uri photoUri;
    private File photoFile;
    private ArrayList<Curve> curves;
    private String filePath;
    private KeyguardStatus status;
    private boolean statusInDB = false;

    private static Drawable decodeSource(Context context, Uri photoUri, int widthPixels) {
        ContentResolver cr = context.getContentResolver();
        try {
            InputStream is = cr.openInputStream(photoUri);
            return Drawable.createFromStream(is, photoUri.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @OnClick(R.id.set_draw_passwd_change_img)
    public void changeImgClick(View view) {
        chooseImage();
//        chooseImg();
    }
//
//    @OnClick(R.id.set_draw_passwd_comp)
//    public void compClick(View view) {
//        List<AngleChain> exists = DbManager.getInstance().query(AngleChain.class);
//        if (exists == null || exists.size() == 0) {
//            return;
//        }
//        AngleChainProcessor processor = new AngleChainProcessor(new ArrayList<AngleChain>(exists), curves);
//        boolean flag = processor.compare();
//        Toast.makeText(this, "" + flag, Toast.LENGTH_SHORT).show();
//        canvas.cleanCanvas();
////        canvas.drawChain(processor.getMatchingChains().get(0));
//    }

    @OnClick(R.id.set_draw_passwd_save)
    public void saveClick(View view) {
        AngleChainProcessor processor = new AngleChainProcessor(curves);
        List<AngleChain> chains = processor.fit_matching_curves();
        DbManager manager = DbManager.getInstance();
        List exists = manager.query(AngleChain.class);
        if (exists != null && exists.size() >= 1) {
            manager.bulkDelete(AngleChain.class, exists);
        }
        manager.bulkInsert(AngleChain.class, chains);
        chains = manager.query(AngleChain.class);
        ArrayList<Long> ids = new ArrayList<Long>();
        for (AngleChain chain : chains) {
            ids.add(chain.id);
        }

        status.patternAngelChainIds = ids;
        status.unlockType = KeyguardStatus.PasswdType.image;
        if (statusInDB) {
            manager.update(KeyguardStatus.class, status);
        } else {
            manager.insert(KeyguardStatus.class, status);
        }

        canvas.cleanCanvas();

        Toast.makeText(this, R.string.save_ok, Toast.LENGTH_SHORT).show();
//        canvas.drawChain(chains.get(0));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.set_draw_passwd);
        ButterKnife.inject(this);

        List tmp = DbManager.getInstance().query(KeyguardStatus.class);
        if (tmp == null || tmp.size() == 0) {
            status = new KeyguardStatus();
            statusInDB = false;
        } else {
            status = (KeyguardStatus) tmp.get(0);
            statusInDB = true;
        }
        if (status.imagePath != null) {
            canvas.setBackground(Drawable.createFromPath(status.imagePath));
        }

        canvas.setOnDrawFinishedListener(new DrawingCanvas.DrawingCallback() {
            @Override
            public void onDrawPause() {

            }

            @Override
            public void onActionDown() {
                Animation animation = AnimationUtils.loadAnimation(SetDrawPasswdActivity.this, R.anim.abc_fade_out);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        widgetView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                widgetView.startAnimation(animation);

            }

            @Override
            public void onActionUp(ArrayList<Curve> rawCurves) {

                curves = rawCurves;

                Animation animation = AnimationUtils.loadAnimation(SetDrawPasswdActivity.this, R.anim.abc_fade_in);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        widgetView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                widgetView.startAnimation(animation);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_PICTURE && resultCode == RESULT_OK) {
            photoUri = data.getData();

            canvas.setBackground(decodeSource(this, photoUri, getResources().getDisplayMetrics().widthPixels));
            try {
                filePath = Utils.write2Storage(this, ImageDecodeUtils.decodeSource(this, photoUri, getResources().getDisplayMetrics().widthPixels));
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "save picture error");
            }
            if (filePath != null) {
                status.imagePath = filePath;
                if (statusInDB) {
                    DbManager.getInstance().update(KeyguardStatus.class, status);
                } else {
                    DbManager.getInstance().insert(KeyguardStatus.class, status);
                }
            }

        } else if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
            canvas.setBackground(Drawable.createFromPath(photoFile.getAbsolutePath()));
//            canvas.setBackground(decodeSource(this, photoUri, getResources().getDisplayMetrics().widthPixels));
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GET_PICTURE);
    }

    private void takePhoto() {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        String dir = Utils.getExternalStorageDir(this);
        photoFile = new File(dir, "" + System.currentTimeMillis() + ".jpg");
        photoUri = Uri.fromFile(photoFile);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }
}
