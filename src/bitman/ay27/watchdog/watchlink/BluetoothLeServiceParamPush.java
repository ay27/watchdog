package bitman.ay27.watchdog.watchlink;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Spartan on 2015/7/11.
 */
public class BluetoothLeServiceParamPush implements Parcelable {
    public final static int CHARA_TIME_YEAR = 0;  // RW uint16
    public final static int CHARA_TIME_MONTH = 1;  // RW uint8
    public final static int CHARA_TIME_DAY = 2;  // RW uint8
    public final static int CHARA_TIME_HOUR = 3;  // RW uint8
    public final static int CHARA_TIME_MIN = 4;  // RW uint8
    public final static int CHARA_TIME_SEC = 5;  // RW uint8
    public final static int CHARA_RF_CALIBRATE = 6;  // RW int8
    public final static int CHARA_RF_TXLEVEL = 7;  // RW uint8
    public final static int CHARA_VIBRATE_TRIGGER = 8;  //  W uint8
    public final static int CHARA_DISCONNECT_ALARM_SWITCH = 9;  //  W uint8 //TODO: NOT USE NOW

    public int name;
    public int len;
    public byte data[];

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(name);
        out.writeInt(len);
        out.writeByteArray(data);
    }

    private BluetoothLeServiceParamPush(Parcel in) {
        this.name = in.readInt();
        this.len = in.readInt();
        in.readByteArray(data);
    }

    public static final Creator<BluetoothLeServiceParamPush> CREATOR = new Creator<BluetoothLeServiceParamPush>() {
        public BluetoothLeServiceParamPush createFromParcel(Parcel in) {
            return new BluetoothLeServiceParamPush(in);
        }

        public BluetoothLeServiceParamPush[] newArray(int size) {
            return new BluetoothLeServiceParamPush[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public BluetoothLeServiceParamPush() {

    }
}
