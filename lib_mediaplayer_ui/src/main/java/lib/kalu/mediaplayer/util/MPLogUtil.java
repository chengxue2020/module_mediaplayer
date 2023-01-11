package lib.kalu.mediaplayer.util;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.config.player.PlayerType;

public final class MPLogUtil {

    private static String mTag = "MP_COMMON";
    private static boolean mLog = false;

    public static void setLogger(@PlayerType.KernelType int type, @NonNull boolean enable) {

        log("setLogger => type = " + type);
        if (type == PlayerType.KernelType.VLC) {
            try {
                Class<?> clazz = Class.forName("lib.kalu.vlcplayer.util.VlcLogUtil");
                if (null != clazz) {
                    lib.kalu.vlcplayer.util.VlcLogUtil.setLogger(enable);
                    log("setLogger => vlc succ");
                } else {
                    log("setLogger => vlc fail");
                }
            } catch (Exception e) {
                log("setLogger => vlc exception");
            }
        } else if (type == PlayerType.KernelType.IJK) {
            try {
                Class<?> clazz = Class.forName("lib.kalu.ijkplayer.util.IjkLogUtil");
                if (null != clazz) {
                    lib.kalu.ijkplayer.util.IjkLogUtil.setLogger(enable);
                    log("setLogger => ijk succ");
                } else {
                    log("setLogger => ijk fail");
                }
            } catch (Exception e) {
                log("setLogger => ijk exception");
            }
        } else if (type == PlayerType.KernelType.EXO) {
            try {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.util.ExoLogUtil");
                if (null != clazz) {
                    lib.kalu.exoplayer2.util.ExoLogUtil.setLogger(enable);
                    log("setLogger => exo succ");
                } else {
                    log("setLogger => exo fail");
                }
            } catch (Exception e) {
                log("setLogger => exo exception");
            }
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
