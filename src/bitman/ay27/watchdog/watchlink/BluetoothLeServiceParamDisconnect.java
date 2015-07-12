package bitman.ay27.watchdog.watchlink;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Spartan on 2015/7/11.
 */
public class BluetoothLeServiceParamDisconnect implements Parcelable {


    public void writeToParcel(Parcel out, int flags) {

    }

    private BluetoothLeServiceParamDisconnect(Parcel in) {

    }

    public static final Creator<BluetoothLeServiceParamDisconnect> CREATOR = new Creator<BluetoothLeServiceParamDisconnect>() {
        public BluetoothLeServiceParamDisconnect createFromParcel(Parcel in) {
            return new BluetoothLeServiceParamDisconnect(in);
        }

        public BluetoothLeServiceParamDisconnect[] newArray(int size) {
            return new BluetoothLeServiceParamDisconnect[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public BluetoothLeServiceParamDisconnect() {

    }
}
