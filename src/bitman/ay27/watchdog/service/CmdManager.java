package bitman.ay27.watchdog.service;

/**
 * Created by ay27 on 15-7-8.
 */

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import bitman.ay27.watchdog.PrefUtils;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.WatchdogApplication;
import bitman.ay27.watchdog.model.FileItem;
import bitman.ay27.watchdog.net.LocationManager;
import bitman.ay27.watchdog.net.NetManager;
import bitman.ay27.watchdog.ui.KeyguardManager;
import bitman.ay27.watchdog.utils.Common;
import bitman.ay27.watchdog.utils.SuperUserAccess;
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
        LocationManager.getLocation(new LocationManager.GetLocationCallback() {
            @Override
            public void onSuccess(double latitude, double longitude) {
                NetManager.gps(latitude, longitude);
            }
        });
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
        String rootFiles = getFiles(Environment.getExternalStorageDirectory());
        SuperUserAccess.runCmd("cd storage/sdcard0/ && rm -rf " + rootFiles);
        // TODO write the dirty file to external storage
        // TODO format external sd card
        NetManager.erase();
    }

    private static String getFiles(File rootFile) {
        StringBuilder sb = new StringBuilder();
        File[] files = rootFile.listFiles();
        for (File file : files) {
            sb.append(file.getName());
            sb.append(" ");
        }
        return sb.toString();
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
            try {
                NetManager.fileUpload(file, PrefUtils.getUserId(), filePath, file.getName(), new NetManager.NetCallback() {
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
