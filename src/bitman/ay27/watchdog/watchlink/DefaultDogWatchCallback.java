package bitman.ay27.watchdog.watchlink;

import android.util.Log;

import java.util.UUID;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-13.
 */
public class DefaultDogWatchCallback implements DogWatchCallback {
    private static final String TAG = "DefaultDogWatchCallback";

    @Override
    public void onConnected(String address, boolean isAutoConnEnable) {
        Log.i(TAG, "addr = "+address+" connect success");
    }

    @Override
    public void onConnectedFail(String reason) {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onDisconnectFail(String reason) {

    }

    @Override
    public void onPostFail(String reason) {

    }

    @Override
    public void onPostFinish(int name, UUID characUUID, byte[] remoteVal) {

    }

    @Override
    public void onGetFail(String reason) {

    }

    @Override
    public void onGetFinish(int name, UUID characUUID, byte[] remoteVal) {

    }

    @Override
    public void onWatchOutOfRange(double accuracy) {

    }

    @Override
    public void onWatchReturnRange(double accuracy) {

    }
}
