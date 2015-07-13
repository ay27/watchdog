package bitman.ay27.watchdog.watchlink;

import java.util.UUID;

/**
 * Created by Spartan on 2015/7/13.
 */
public interface DogWatchCallback {
    public void onConnected(String address, boolean isAutoConnEnable);
    public void onConnectedFail(String reason);

    public void onDisconnected();
    public void onDisconnectFail(String reason);

    public void onPostFail(String reason);
    public void onPostFinish(int name, UUID characUUID, byte[] remoteVal);

    public void onGetFail(String reason);
    public void onGetFinish(int name, UUID characUUID, byte[] remoteVal);

    public void onWatchOutOfRange(double accuracy);
    public void onWatchReturnRange(double accuracy);
}
