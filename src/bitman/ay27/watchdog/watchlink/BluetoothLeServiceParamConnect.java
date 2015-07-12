package bitman.ay27.watchdog.watchlink;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Spartan on 2015/7/11.
 */
public class BluetoothLeServiceParamConnect implements Parcelable {
    public String address = null;
    public int autoConnect = 0;

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(address);
        out.writeInt(autoConnect);
    }

    private BluetoothLeServiceParamConnect(Parcel in) {
        this.address = in.readString();
        this.autoConnect = in.readInt();
    }

    public static final Creator<BluetoothLeServiceParamConnect> CREATOR = new Creator<BluetoothLeServiceParamConnect>() {
        public BluetoothLeServiceParamConnect createFromParcel(Parcel in) {
            return new BluetoothLeServiceParamConnect(in);
        }

        public BluetoothLeServiceParamConnect[] newArray(int size) {
            return new BluetoothLeServiceParamConnect[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public BluetoothLeServiceParamConnect() {

    }
}
