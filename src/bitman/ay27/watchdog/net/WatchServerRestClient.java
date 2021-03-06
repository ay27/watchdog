package bitman.ay27.watchdog.net;

import android.util.Log;
import bitman.ay27.watchdog.WatchdogApplication;
import com.loopj.android.http.*;
import org.apache.http.Header;
import org.apache.http.HttpEntity;

/**
 * Created by ay27 on 15-7-8.
 */
class WatchServerRestClient {

    //    private static final String BASE_URL = "http://test.cnss446.ml/";
    private static final String BASE_URL = "http://192.168.43.243/";
    private static final AsyncHttpClient client = new AsyncHttpClient();
    private static final String TAG = "WatchServerRestClient";
    private static ResponseHandlerInterface defaultHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {

        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

        }
    };

    private WatchServerRestClient() {
    }

    public static RequestHandle get(String url, RequestParams params, AsyncHttpResponseHandler handler) {
        Log.i(TAG, "get send: " + url + " content: " + params.toString());
        if (handler == null)
            return client.get(getAbsoluteUrl(url), params, defaultHandler);
        return client.get(getAbsoluteUrl(url), params, handler);
    }

    public static RequestHandle post(String url, HttpEntity entity, String contentType, AsyncHttpResponseHandler handler) {
        Log.i(TAG, "post send: " + url + " content: " + entity.toString());
        if (handler == null) {
            return client.post(WatchdogApplication.getContext(), getAbsoluteUrl(url), entity, contentType, defaultHandler);
        }
        return client.post(WatchdogApplication.getContext(), getAbsoluteUrl(url), entity, contentType, handler);
    }

    public static RequestHandle post(String url, RequestParams params, AsyncHttpResponseHandler handler) {
        Log.i(TAG, "post send: " + url + " content: " + params.toString());
        if (handler == null)
            return client.post(getAbsoluteUrl(url), params, defaultHandler);
        return client.post(getAbsoluteUrl(url), params, handler);
    }

    private static String getAbsoluteUrl(String url) {
        return BASE_URL + url;
    }
}
