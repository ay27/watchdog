package bitman.ay27.watchdog.net;

import android.support.annotation.NonNull;
import bitman.ay27.watchdog.WatchdogApplication;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.android.tpush.XGPushManager;
import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.CookieHandler;
import java.net.CookieManager;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/3/28.
 */
public class NetManager {

    public static final String SIGN_UP = "signup";
    public static final String SIGN_IN = "signin";
    public static final String ONLINE = "online";
    public static final String OFFLINE = "offline";
    public static final String HEARTBEAT = "heartbeat";
    public static final String BIND = "bind";
    public static final String UPLOAD = "file_upload";


    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_DEVICE_ID = "deviceId";
    public static final String KEY_LATI = "latitude";
    public static final String KEY_LONI = "longitude";
    public static final String GPS = "gps";
    public static final String ALARM = "alarm";
    public static final String DISALARM = "disalarm";
    public static final String LOCK = "lock";
    public static final String UNLOCK = "unlock";
    public static final String ERASE = "erase";
    public static final String KEY_STATE = "state";
    public static final String STATE = "state";
    public static final String KEY_FILE_LIST = "file_list";
    public static final String FILE_LIST = "file_list";
    public static final String KEY_USERID = "userId";
    public static final String KEY_FILE_PATH = "filePath";
    public static final String KEY_FILE_NAME = "fileName";
    public static final String KEY_FILE = "file";

    private NetManager() {

    }

    public static void signIn(final String username, String password, final NetCallback callback) {
        RequestParams params = new RequestParams();
        params.put(KEY_USERNAME, username);
        params.put(KEY_PASSWORD, password);
//        callback.onSuccess(0, "");
        WatchServerRestClient.post(SIGN_IN, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                // login XGPush
                XGPushManager.registerPush(WatchdogApplication.getContext(), username);

                // set cookie, keep login
                CookieManager cookieManager = new CookieManager();
                CookieHandler.setDefault(cookieManager);

                if (bytes != null)
                    callback.onSuccess(i, new String(bytes));
                else
                    callback.onSuccess(i, null);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (bytes != null)
                    callback.onError(i, new String(bytes), throwable);
                else
                    callback.onError(i, null, throwable);
            }
        });
    }

    public static void signUp(String username, String email, String password, NetCallback callback) {
        RequestParams params = new RequestParams();
        params.put(KEY_USERNAME, username);
        params.put(KEY_EMAIL, email);
        params.put(KEY_PASSWORD, password);
//        callback.onSuccess(0, "");
        WatchServerRestClient.post(SIGN_UP, params, generateDefaultHandler(callback));
    }


    public static void online() {
        RequestParams params = new RequestParams();
        params.put(KEY_DEVICE_ID, WatchdogApplication.DeviceId);
        WatchServerRestClient.get(ONLINE, params, null);
    }

    public static void offline() {
        RequestParams params = new RequestParams();
        params.put(KEY_DEVICE_ID, WatchdogApplication.DeviceId);
        WatchServerRestClient.get(OFFLINE, params, null);
    }

    public static void heartbeat() {
        RequestParams params = new RequestParams();
        params.put(KEY_DEVICE_ID, WatchdogApplication.DeviceId);
        WatchServerRestClient.get(HEARTBEAT, params, null);
    }

    public static void bind(NetCallback cb) {
        RequestParams params = new RequestParams();
        params.put(KEY_DEVICE_ID, WatchdogApplication.DeviceId);
        WatchServerRestClient.get(BIND, params, generateDefaultHandler(cb));
    }

    public static void gps(double latitude, double longitude) {
        RequestParams params = new RequestParams();
        params.put(KEY_LATI, latitude);
        params.put(KEY_LONI, longitude);
        WatchServerRestClient.get(GPS, params, null);
    }

    public static void alarm() {
        WatchServerRestClient.get(ALARM, null, null);
    }

    public static void disalarm() {
        WatchServerRestClient.get(DISALARM, null, null);
    }

    public static void lock() {
        WatchServerRestClient.get(LOCK, null, null);
    }

    public static void unlock() {
        WatchServerRestClient.get(UNLOCK, null, null);
    }

    public static void erase() {
        WatchServerRestClient.get(ERASE, null, null);
    }

    public static void state(String type) {
        RequestParams params = new RequestParams(KEY_STATE, type);
        WatchServerRestClient.get(STATE, params, null);
    }

    public static void fileList(String json, NetCallback cb) {
        RequestParams params = new RequestParams();
        params.put(KEY_DEVICE_ID, WatchdogApplication.DeviceId);
        params.put(KEY_FILE_LIST, json);
        WatchServerRestClient.post(FILE_LIST, params, generateDefaultHandler(cb));
    }

    public static void fileUpload(File file, String userId, String filePath, String fileName, NetCallback cb) throws FileNotFoundException {
        RequestParams params = new RequestParams();
        params.put(KEY_USERID, userId);
        params.put(KEY_FILE_PATH, filePath);
        params.put(KEY_FILE_NAME, fileName);
        params.put(KEY_DEVICE_ID, WatchdogApplication.DeviceId);
        params.put(KEY_FILE, file);
        WatchServerRestClient.post(UPLOAD, params, generateDefaultHandler(cb));
    }


    private static AsyncHttpResponseHandler generateDefaultHandler(@NonNull final NetCallback callback) {

        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                callback.onSuccess(i, new String(bytes));
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (bytes != null)
                    callback.onError(i, new String(bytes), throwable);
                else
                    callback.onError(i, null, throwable);
            }
        };
    }

    public static interface NetCallback {
        public void onSuccess(int code, String recv);

        public void onError(int code, String recv, Throwable throwable);
    }


}
