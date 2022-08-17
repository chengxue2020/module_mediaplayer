package lib.kalu.mediaplayer.util;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class MediaLogUtil {

    private static boolean isLog = false;

    public static void setIsLog(boolean isLog) {
        MediaLogUtil.isLog = isLog;
    }

    public static boolean isIsLog() {
        return isLog;
    }

    public static void log(@NonNull String message) {
        log(message, null);
    }

    public static void log(@NonNull String message, @Nullable Throwable throwable) {

        if (!isLog)
            return;

        if (null == message || message.length() == 0)
            return;

        Log.e("module_mediaplayer", message, throwable);
    }
}
