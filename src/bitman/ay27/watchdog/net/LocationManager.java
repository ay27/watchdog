package bitman.ay27.watchdog.net;

import bitman.ay27.watchdog.WatchdogApplication;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-11.
 */
public class LocationManager {

    public static void getLocation(final GetLocationCallback cb) {
        final LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度
        option.setScanSpan(1000); // 1s扫描一次

        final LocationClient mLocationClient = new LocationClient(WatchdogApplication.getContext());
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                int code = bdLocation.getLocType();
                if (code == 61 || code == 65 || code == 66 || code == 68 || code == 161) {
                    mLocationClient.stop();
                    double latitude = bdLocation.getLatitude();
                    double longitude = bdLocation.getLongitude();
                    cb.onSuccess(latitude, longitude);
                }
            }
        });
        mLocationClient.start();
    }

    public interface GetLocationCallback {
        void onSuccess(double latitude, double longitude);
    }

}
