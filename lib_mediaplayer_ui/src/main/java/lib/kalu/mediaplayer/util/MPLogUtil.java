package lib.kalu.mediaplayer.util;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.config.player.PlayerType;

public final class MPLogUtil {

    private static String mTag = "MP_COMMON";
    private static boolean mLog = false;

    public static void setLogger(@PlayerType.KernelType int type, @NonNull boolean enable) {
        if (type == PlayerType.KernelType.IJK) {
            lib.kalu.ijkplayer.util.IjkLogUtil.setLogger(enable);
        } else if (type == PlayerType.KernelType.VLC) {
            lib.kalu.vlcplayer.util.VlcLogUtil.setLogger(enable);
        } else if (type == PlayerType.KernelType.EXO) {
            lib.kalu.exoplayer2.util.ExoLogUtil.setLogger(enable);
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
