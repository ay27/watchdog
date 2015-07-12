package bitman.s117.libwatchcat;

/**
 * Created by Spartan on 2015/4/12.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Spartan on 2015/4/1.
 */
public class MediaWatcher extends BroadcastReceiver {
    File imagepath;
    IntentFilter intentFilter;

    public MediaWatcher() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_CHECKING);
        intentFilter.addAction(Intent.ACTION_MEDIA_NOFS);
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SHARED);
        intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTABLE);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intentFilter.addAction(Intent.ACTION_MEDIA_BUTTON);
        intentFilter.addDataScheme("file");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, intent.getAction() + " mData: " + intent.getDataString(), Toast.LENGTH_SHORT).show();
//        if (intent.getAction().equals("android.intent.action.MEDIA_MOUNTED")) //SD卡已经成功挂载
//        {
//            //imagepath = android.os.Environment.getExternalStorageDirectory();//你的SD卡路径
//            Toast.makeText(context, "media change, intent: " + intent.getAction(), Toast.LENGTH_LONG).show();
//        } else if (intent.getAction().equals("android.intent.action.MEDIA_REMOVED")//各种未挂载状态
//                || intent.getAction().equals("android.intent.action.ACTION_MEDIA_UNMOUNTED")
//                || intent.getAction().equals("android.intent.action.ACTION_MEDIA_BAD_REMOVAL")) {
//            //imagepath = android.os.Environment.getDataDirectory();//你的本地路径
//            Toast.makeText(context, "media remove, intent: " + intent.getAction(), Toast.LENGTH_LONG).show();
//        }
    }

    public IntentFilter getIntendFilter() {
        return intentFilter;
    }
}
