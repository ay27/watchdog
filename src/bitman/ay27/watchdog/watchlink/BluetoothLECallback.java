package bitman.ay27.watchdog.watchlink;

/**
 * Created by Spartan on 2015/7/13.
 */
public interface BluetoothLeCallback {
    public void onConnected(String address, boolean isAutoConnEnable);
    public void onConnectedFail(String reason);

    public void onDisconnected();
    public void onDisconnectFail(String reason);

    public void onPostFail(String reason);
    public void onPostFinish(BluetoothLeServiceParamPost paramPost, String characUUID, byte[] remoteVal);

    public void onGetFail(String reason);
    public void onGetFinish(BluetoothLeServiceParamGet paramGet, String characUUID, byte[] remoteVal);

    public void onWatchOutOfRange(double accuracy);
}