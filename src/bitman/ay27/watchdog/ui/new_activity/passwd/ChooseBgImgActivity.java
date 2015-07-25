package bitman.ay27.watchdog.ui.new_activity.passwd;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.db.DbManager;
import bitman.ay27.watchdog.db.model.KeyguardStatus;
import bitman.ay27.watchdog.utils.ImageDecodeUtils;
import bitman.ay27.watchdog.utils.Utils;
import butterknife.ButterKnife;
import butterknife.InjectView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-21.
 */
public class ChooseBgImgActivity extends ActionBarActivity {

    private static final int GET_PICTURE = 0x1;
    private static final String TAG = "ChooseImgActivity";
    @InjectView(R.id.passwd_choose_img_img)
    ImageView passwdChooseImgImg;
    @InjectView(R.id.passwd_choose_img_choose)
    Button passwdChooseImgChoose;
    @InjectView(R.id.passwd_choose_img_next)
    Button chooseImgNext;
    @InjectView(R.id.passwd_choose_img_toolbar)
    Toolbar toolbar;

    private Uri photoUri;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passwd_choose_img);
        ButterKnife.inject(this);

        toolbar.setTitle(R.string.choose_bg);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        List tmp = DbManager.getInstance().query(KeyguardStatus.class);
        if (tmp == null || tmp.size() == 0) {
            status = new KeyguardStatus();
            statusInDB = false;
        } else {
            status = (KeyguardStatus) tmp.get(0);
            statusInDB = true;
        }
    }

    public void nextClick(View view) {
        Intent intent = new Intent(this, DrawPasswdActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        if (filePath != null && !filePath.isEmpty()) {
            intent.putExtra("ImgFilePath", filePath);
        }
        startActivity(intent);
    }

    public void chooseImgClick(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GET_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_PICTURE && resultCode == RESULT_OK) {
            photoUri = data.getData();

            passwdChooseImgImg.setImageDrawable(decodeSource(this, photoUri, getResources().getDisplayMetrics().widthPixels));
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
                    statusInDB = true;
                }
            }

        }
    }
}
