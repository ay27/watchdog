package bitman.ay27.watchdog.net;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

/**
 * Created by ay27 on 15-7-8.
 */
class WatchServerRestClient {

    private static final String BASE_URL = "TODO/";
    private static final AsyncHttpClient client = new AsyncHttpClient();

    private WatchServerRestClient() {
    }

    public static RequestHandle get(String url, RequestParams params, AsyncHttpResponseHandler handler) {
        return client.get(getAbsoluteUrl(url), params, handler);
    }

    public static RequestHandle post(String url, RequestParams params, AsyncHttpResponseHandler handler) {
        return client.post(getAbsoluteUrl(url), params, handler);
    }

    private static String getAbsoluteUrl(String url) {
        return BASE_URL + url;
    }
}
