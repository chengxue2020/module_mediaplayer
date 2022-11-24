package lib.kalu.ijkplayer.util;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class IjkLogUtil {

    private static String mTag = "MP_IJK";
    private static boolean mLog = false;

    public static void setLogger(boolean enable) {
        mLog = enable;
    }

    public static void log(@NonNull String message) {
        log(message, null);
    }

    public static void log(@NonNull String message, @Nullable Throwable throwable) {

        if (!mLog)
            return;

        if (null == message || message.length() == 0)
            return;

        Log.e(mTag, message, throwable);
    }
}