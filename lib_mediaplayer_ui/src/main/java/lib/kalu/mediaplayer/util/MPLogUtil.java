package lib.kalu.mediaplayer.util;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.config.config.ConfigType;

public final class MPLogUtil {

    private static String mTag = "MP_COMMON";
    private static boolean mLog = false;

    public static void setLogger(@ConfigType.KernelType int type, @NonNull boolean enable) {
        if (type == ConfigType.KernelType.IJK) {
            tv.danmaku.ijk.media.player.util.IjkLogUtil.setLogger(enable);
        } else if (type == ConfigType.KernelType.VLC) {
            org.videolan.libvlc.util.VlcLogUtil.setLogger(enable);
        }
        MPLogUtil.mLog = enable;
    }

    public static boolean isLog() {
        return mLog;
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
