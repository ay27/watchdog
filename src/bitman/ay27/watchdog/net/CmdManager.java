package bitman.ay27.watchdog.net;

/**
 * Created by ay27 on 15-7-8.
 */

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.Toast;
import bitman.ay27.watchdog.PrefUtils;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.WatchdogApplication;
import bitman.ay27.watchdog.model.FileItem;
import bitman.ay27.watchdog.ui.new_activity.lock.KeyguardManager;
import bitman.ay27.watchdog.utils.Common;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * deal with the XGPush message.
 * process the message and send a confirm message to server.
 */
public class CmdManager {
    private static final String TAG = "CmdManager";
    private static MediaPlayer alarmPlayer;
//    private static int oldVolume;
//    private static AudioManager mAudioManager = (AudioManager) WatchdogApplication.getContext().getSystemService(Context.AUDIO_SERVICE);

    public static void gps() {
        LocationManager.getLocation(new LocationManager.GetLocationCallback() {
            @Override
            public void onSuccess(double latitude, double longitude) {
                NetManager.gps(latitude, longitude);
            }
        });
    }

    public static void alarm() {
//        oldVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        alarmPlayer = MediaPlayer.create(WatchdogApplication.getContext(), RingtoneManager.getActualDefaultRingtoneUri(WatchdogApplication.getContext(), RingtoneManager.TYPE_ALARM));
        alarmPlayer.setLooping(true);
        alarmPlayer.start();
        NetManager.alarm();
    }

    public static void disalarm() {
        if (alarmPlayer!= null && alarmPlayer.isPlaying()) {
            alarmPlayer.stop();
            alarmPlayer.release();
            alarmPlayer = null;
        }

//        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, oldVolume, 0);

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
//        String rootFiles = getFiles(Environment.getExternalStorageDirectory());
//        SuperUserAccess.runCmd("cd storage/sdcard0/ && rm -rf " + rootFiles);
        rmFiles(Environment.getExternalStorageDirectory());
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                writeDirtyFile(Environment.getExternalStorageDirectory());
//            }
//        }).start();
        // TODO write the dirty file to external storage
        // TODO format external sd card
        NetManager.erase();
    }

    private static void writeDirtyFile(File rootFile) {
        StatFs statFs = new StatFs(rootFile.getPath());
        byte[] bytes = new byte[statFs.getBlockSize() * 100];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = 0;
        }
        while (statFs.getAvailableBlocksLong()>0) {
            try {
                File file = new File(rootFile.getPath(), "" + System.currentTimeMillis());
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bytes);
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void rmFiles(File rootFile) {
        for (File file : rootFile.listFiles()) {
            if (file.isDirectory()) {
                rmFiles(file);
            }
            file.delete();
        }
    }

    public static void state() {
        // TODO
    }

    public static void fileList(long task_id) {
        File sd = Environment.getExternalStorageDirectory();
        FileItem rootItem = new FileItem(sd.getAbsolutePath(), sd.getName(), true);
        getAllFiles(rootItem, sd);

        Gson gson = new Gson();
        String json = gson.toJson(rootItem, FileItem.class);

        Log.i("json", json);
        Log.i("json-length", "" + json.length());
        NetManager.fileList(json, task_id, null);
    }

    public static void upload(String filePath, long task_id) {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                NetManager.fileUpload(file, PrefUtils.getUserId(), filePath, file.getName(), task_id, new NetManager.NetCallback() {
                    @Override
                    public void onSuccess(int code, String recv) {
                        Toast.makeText(WatchdogApplication.getContext(), R.string.upload_success, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(int code, String recv) {
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
