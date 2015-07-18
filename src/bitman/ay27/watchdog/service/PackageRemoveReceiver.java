package bitman.ay27.watchdog.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-16.
 */
public class PackageRemoveReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String packageName = intent.getDataString();
        Toast.makeText(context, "remove "+packageName, Toast.LENGTH_LONG).show();
    }
}
