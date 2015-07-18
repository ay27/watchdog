package bitman.ay27.watchdog.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-16.
 */
public class DeviceManagerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("device manager", "receive "+intent.getDataString());
        Toast.makeText(context, "receive device manager", Toast.LENGTH_LONG).show();
    }
}
