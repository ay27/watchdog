package bitman.ay27.watchdog.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import bitman.ay27.watchdog.ui.activity.LoginDialog;
import com.tencent.android.tpush.*;
import com.tencent.android.tpush.horse.data.StrategyItem;

/**
 * Created by ay27 on 15-7-7.
 */
public class MessageByServerReceiver extends XGPushBaseReceiver {
    @Override
    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {

    }

    @Override
    public void onUnregisterResult(Context context, int i) {

    }

    @Override
    public void onSetTagResult(Context context, int i, String s) {

    }

    @Override
    public void onDeleteTagResult(Context context, int i, String s) {

    }

    @Override
    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {
        String cmd = xgPushTextMessage.getContent();
        Log.i("Send by WatchServer", cmd);
        Toast.makeText(context, cmd, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {

    }

    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {

    }
}
