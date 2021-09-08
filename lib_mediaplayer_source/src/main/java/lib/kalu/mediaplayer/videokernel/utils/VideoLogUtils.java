package lib.kalu.mediaplayer.videokernel.utils;

import android.util.Log;

import androidx.annotation.Keep;

/**
 * @description: log工具
 * @date:  2021-05-12 14:48
 */
@Keep
public final class VideoLogUtils {

    private static final String TAG = "YCVideoPlayer";
    private static boolean isLog = false;

    /**
     * 设置是否开启日志
     * @param isLog                 是否开启日志
     */
    public static void setIsLog(boolean isLog) {
        VideoLogUtils.isLog = isLog;
    }

    public static boolean isIsLog() {
        return isLog;
    }

    public static void d(String message) {
        if(isLog){
            Log.d(TAG, message);
        }
    }

    public static void i(String message) {
        if(isLog){
            Log.i(TAG, message);
        }

    }

    public static void e(String msg) {
        if (isLog) {
            Log.e(TAG, msg);
        }
    }

    public static void e(String message, Throwable throwable) {
        if(isLog){
            Log.e(TAG, message, throwable);
        }
    }

}
