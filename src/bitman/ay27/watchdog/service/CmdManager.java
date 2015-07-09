package bitman.ay27.watchdog.service;

/**
 * Created by ay27 on 15-7-8.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.WatchdogApplication;
import bitman.ay27.watchdog.model.FileItem;
import bitman.ay27.watchdog.net.NetManager;
import bitman.ay27.watchdog.ui.KeyguardManager;
import bitman.ay27.watchdog.ui.activity.MainActivity;
import bitman.ay27.watchdog.utils.Common;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * deal with the XGPush message.
 * process the message and send a confirm message to server.
 */
public class CmdManager {
    private static final String TAG = "CmdManager";
    private static MediaPlayer alarmPlayer = MediaPlayer.create(WatchdogApplication.getContext(), RingtoneManager.getActualDefaultRingtoneUri(WatchdogApplication.getContext(), RingtoneManager.TYPE_ALARM));
    private static int oldVolume;
    private static AudioManager mAudioManager = (AudioManager) WatchdogApplication.getContext().getSystemService(Context.AUDIO_SERVICE);

    public static void gps() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度
        option.setScanSpan(5000); // 5s扫描一次

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
                    NetManager.gps(latitude, longitude);
                }
            }
        });
        mLocationClient.start();
    }

    public static void alarm() {
        oldVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        alarmPlayer.setLooping(true);
        alarmPlayer.start();
        NetManager.alarm();
    }

    public static void disalarm() {
        if (alarmPlayer.isPlaying())
            alarmPlayer.stop();

        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, oldVolume, 0);

        NetManager.disalarm();

    }

    public static void lock() {
        KeyguardManager manager = new KeyguardManager(WatchdogApplication.getContext());
        manager.launchKeyguard();
        NetManager.lock();
    }

    public static void unlock() {
        WatchdogApplication.getContext().sendBroadcast(new Intent(Common.ACTION_KILL_KEYGUARD));
        NetManager.unlock();
    }

    public static void erase() {
        // TODO
    }

    public static void state() {
        // TODO
    }

    public static void fileList() {
        File sd = Environment.getExternalStorageDirectory();
        FileItem rootItem = new FileItem(sd.getAbsolutePath(), sd.getName(), true);
        getAllFiles(rootItem, sd);

        Gson gson = new Gson();
        String json = gson.toJson(rootItem, FileItem.class);

        Log.i("json", json);
        Log.i("json-length", "" + json.length());
        NetManager.fileList(json, null);
    }

    public static void upload(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            SharedPreferences pref = WatchdogApplication.getContext().getSharedPreferences(MainActivity.PREF_NAME, Context.MODE_PRIVATE);
            try {
                NetManager.fileUpload(file, pref.getString("userId", ""), filePath, file.getName(), new NetManager.NetCallback() {
                    @Override
                    public void onSuccess(int code, String recv) {
                        Toast.makeText(WatchdogApplication.getContext(), R.string.upload_success, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(int code, String recv, Throwable throwable) {
                        Toast.makeText(WatchdogApplication.getContext(), R.string.upload_failed, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private static void getAllFiles(FileItem currentItem, File root) {
        File files[] = root.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    FileItem item = new FileItem(f.getAbsolutePath(), f.getName(), true);
                    currentItem.add(item);
                    getAllFiles(item, f);
                } else {
                    currentItem.add(f.getAbsolutePath(), f.getName(), false);
                }
            }
        }
    }
}
