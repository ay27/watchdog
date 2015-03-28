package bitman.ay27.watchdog.utils;

import android.os.AsyncTask;
import android.os.Build;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/1/26.
 */
public class TaskUtils {

    public static <Params, Progress, Result> void executeAsyncTask(
            AsyncTask<Params, Progress, Result> task, Params... params) {
        if (Build.VERSION.SDK_INT >= 11) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            task.execute(params);
        }
    }
}
