package bitman.ay27.watchdog.watchlink;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Spartan on 2015/7/11.
 */
public class BluetoothLeServiceParamGet implements Parcelable {
    public final static int CHARA_TIME_YEAR = 0;  // RW uint16
    public final static int CHARA_TIME_MONTH = 1;  // RW uint8
    public final static int CHARA_TIME_DAY = 2;  // RW uint8
    public final static int CHARA_TIME_HOUR = 3;  // RW uint8
    public final static int CHARA_TIME_MIN = 4;  // RW uint8
    public final static int CHARA_TIME_SEC = 5;  // RW uint8
    public final static int CHARA_RF_CALIBRATE = 6;  // RW uint8
    public final static int CHARA_RF_TXLEVEL = 7;  // RW uint8

    public int name;

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(name);
    }

    private BluetoothLeServiceParamGet(Parcel in) {
        name = in.readInt();
    }

    public static final Creator<BluetoothLeServiceParamGet> CREATOR = new Creator<BluetoothLeServiceParamGet>() {
        public BluetoothLeServiceParamGet createFromParcel(Parcel in) {
            return new BluetoothLeServiceParamGet(in);
        }

        public BluetoothLeServiceParamGet[] newArray(int size) {
            return new BluetoothLeServiceParamGet[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public BluetoothLeServiceParamGet() {

    }
}
