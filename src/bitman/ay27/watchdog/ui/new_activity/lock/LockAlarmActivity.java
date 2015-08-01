package bitman.ay27.watchdog.ui.new_activity.lock;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.db.DbManager;
import bitman.ay27.watchdog.db.model.KeyguardStatus;
import butterknife.ButterKnife;

import java.util.List;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-31.
 */
public class LockAlarmActivity extends Activity {

    private View view;
    private WindowManager wm;
    private Button lockBtn;
    private View.OnClickListener lockBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LockAlarmActivity.this, KeyguardImgActivity.class);


            List list = DbManager.getInstance().query(KeyguardStatus.class);
            if (list == null || list.size() == 0) {
//                Log.e(TAG, "can not read keyguard status from DB");
                return;
            }
            KeyguardStatus status = (KeyguardStatus) list.get(0);

            intent.putExtra("Status", status);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);

            wm.removeViewImmediate(view);

            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = getLayoutInflater().inflate(R.layout.alarm_lock, null);
        ButterKnife.inject(this, view);
        lockBtn = (Button) view.findViewById(R.id.alarm_lock_btn);

        wm = (WindowManager) getApplicationContext().getSystemService("window");

        addStaticView(view);

        lockBtn.setOnClickListener(lockBtnClick);
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
