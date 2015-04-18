package bitman.ay27.watchdog.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.*;
import java.text.DecimalFormat;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 14-7-24.
 */
public class Utils {

    public static boolean isNull(String str) {
        if (str != null && !str.isEmpty())
            return false;
        return true;
    }


    public static Bitmap decodeSource(Context context, Uri picUri, int targetWidth) throws FileNotFoundException {
        ContentResolver cr = context.getContentResolver();
        BitmapFactory.Options o1 = new BitmapFactory.Options();
        o1.inJustDecodeBounds = true;

        if (targetWidth <= 0)
            return BitmapFactory.decodeStream(cr.openInputStream(picUri));

        BitmapFactory.decodeStream(cr.openInputStream(picUri), null, o1);

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = (int) ((double) o1.outWidth / (double) targetWidth);
        return BitmapFactory.decodeStream(cr.openInputStream(picUri), null, o2);
    }

    public static String getExternalStorageDir(Context context) {
        String path;
        path = context.getFilesDir().getPath();
//        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
//                || !Environment.isExternalStorageRemovable()) {
//            path = Environment.getExternalStorageDirectory().getPath();
//        } else {
//            path = Environment.getDataDirectory().getPath();
//        }
        return path;
    }

    public static String write2Storage(Context context, Bitmap data) throws IOException {
        String path = getExternalStorageDir(context);
        File file = new File(path, "" + System.currentTimeMillis() + ".png");

        FileOutputStream outputStream = new FileOutputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        data.compress(Bitmap.CompressFormat.PNG, 100, bos);

        outputStream.write(bos.toByteArray());
        return file.getAbsolutePath();
    }


    public static String getPathFromUri(Context context, Uri uri) {
        String fileName = null;
        if (uri != null) {
            if (uri.getScheme().toString().compareTo("content") == 0) {
                Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    fileName = cursor.getString(column_index);
                    cursor.close();
                }
            } else if (uri.getScheme().compareTo("file") == 0) {
                fileName = uri.toString().replace("file://", "");
            }
        }
        return Uri.decode(fileName);
    }


    /**
     * 检测网络是否连接
     *
     * @return
     */
    public static boolean isNetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo[] infos = cm.getAllNetworkInfo();
            if (infos != null) {
                for (NetworkInfo ni : infos) {
                    if (ni.isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 检测wifi是否连接
     *
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null
                    && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检测3G是否连接
     *
     * @return
     */
    public static boolean is3gConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null
                    && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }


    public static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString;
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }


    /**
     * Utility class to convert a byte array to a hexadecimal string.
     *
     * @param bytes Bytes to convert
     * @return String, containing hexadecimal representation.
     */
    public static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
