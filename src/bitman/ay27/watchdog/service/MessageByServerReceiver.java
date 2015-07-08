package bitman.ay27.watchdog.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import bitman.ay27.watchdog.WatchdogApplication;
import com.tencent.android.tpush.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ay27 on 15-7-7.
 */
public class MessageByServerReceiver extends XGPushBaseReceiver {

    public static final String GPS = "gps";
    public static final String ALARM = "alarm";
    public static final String DISALARM = "disalarm";
    public static final String LOCK = "lock";
    public static final String UNLOCK = "unlock";
    public static final String ERASE = "erase";
    public static final String STATE = "state";
    public static final String FILE_LIST = "file_list";
    public static final String UPLOAD = "file_upload";
    private static final String DEVICE_ID = "deviceId";
    private static final String FILEPATH = "filePath";

    @Override
    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {
        Log.i("RegisterResult", "state: " + i + "\r\naccount: " + xgPushRegisterResult.getAccount() + "\r\ndeviceId: " + xgPushRegisterResult.getDeviceId() + "\r\ntoken: " + xgPushRegisterResult.getToken());
    }

    @Override
    public void onUnregisterResult(Context context, int i) {
        Log.i("UnRegisterResult", "state: " + i);
    }

    @Override
    public void onSetTagResult(Context context, int i, String s) {
        Log.i("SetTagResult", "state: " + i + "\r\ntag: " + s);
    }

    @Override
    public void onDeleteTagResult(Context context, int i, String s) {
        Log.i("DeleteTagResult", "state: " + i + "\r\ntag: " + s);
    }

    @Override
    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {
        String cmd = xgPushTextMessage.getContent();
        Log.i("Send by WatchServer", cmd);
        Toast.makeText(context, cmd, Toast.LENGTH_LONG).show();

        String path = cmd.substring(0, cmd.indexOf('{'));
        String content = cmd.substring(cmd.indexOf('{')+1, cmd.lastIndexOf('}'));
        if (content.isEmpty()) {
            return;
        }

        String[] pairs = content.split(",");
        Map<String, String> kv = new HashMap<String, String>();
        for (String pair : pairs) {
            String[] strs = pair.split(":");
            if (strs.length == 2)
                kv.put(strs[0], strs[1]);
        }

        if (kv.containsKey(DEVICE_ID) && kv.get(DEVICE_ID).equals(WatchdogApplication.DeviceId)) {
            if (path.equals(GPS)) {
                CmdManager.gps();
            }
            if (path.equals(ALARM)) {
                CmdManager.alarm();
            }
            if (path.equals(DISALARM)) {
                CmdManager.disalarm();
            }
            if (path.equals(LOCK)) {
                CmdManager.lock();
            }
            if (path.equals(UNLOCK)) {
                CmdManager.unlock();
            }
            if (path.equals(ERASE)) {
                CmdManager.erase();
            }
            if (path.equals(STATE)) {
                CmdManager.state();
            }
            if (path.equals(FILE_LIST)) {
                CmdManager.fileList();
            }
            if (path.equals(UPLOAD) && kv.containsKey(FILEPATH)) {
                CmdManager.upload(kv.get(FILEPATH));
            }
        }

    }

    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {

    }

    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {

    }
}
