package lib.kalu.mediaplayer.core.kernel.audio;


import android.content.Context;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerBuilder;
import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.util.MPLogUtil;

public final class MusicPlayerManager {

    private static MusicKernelApi mMusicPlayer = null;

    public static void setVolume(float v) {
        if (null != mMusicPlayer) {
            MPLogUtil.log("MusicPlayerManager => setVolume => v = " + v + ", mMusicPlayer = " + mMusicPlayer);
            mMusicPlayer.setVolume(v);
        }
    }

    public static void reset() {
        if (null != mMusicPlayer) {
            MPLogUtil.log("MusicPlayerManager => reset => mMusicPlayer = " + mMusicPlayer);
            pause();
            mMusicPlayer.seekTo(1);
            start(0, null);
        }
    }

    public static void pause() {
        if (null != mMusicPlayer) {
            MPLogUtil.log("MusicPlayerManager => pause => mMusicPlayer = " + mMusicPlayer);
            mMusicPlayer.pause();
        }
    }

    public static void resume() {
        if (null != mMusicPlayer) {
            MPLogUtil.log("MusicPlayerManager => resume => mMusicPlayer = " + mMusicPlayer);
            long position = mMusicPlayer.getPosition();
            mMusicPlayer.start(position, null);
        }
    }

    public static void stop() {
        if (null != mMusicPlayer) {
            MPLogUtil.log("MusicPlayerManager => stop => mMusicPlayer = " + mMusicPlayer);
            mMusicPlayer.stop();
        }
    }

    private static void setDataSource(@NonNull Context context, @NonNull String musicUrl) {
        if (null != mMusicPlayer) {
            MPLogUtil.log("MusicPlayerManager => setDataSource => musicUrl = " + musicUrl + ", mMusicPlayer = " + mMusicPlayer);
            mMusicPlayer.setDataSource(context, musicUrl);
        }
    }

    private static void create() {
        MPLogUtil.log("MusicPlayerManager => create => step1");
        if (null != mMusicPlayer) {
            pause();
            stop();
            release(true);
        }
        MPLogUtil.log("MusicPlayerManager => create => step2");
        if (null == mMusicPlayer) {
            MPLogUtil.log("MusicPlayerManager => create => mMusicPlayer = " + mMusicPlayer);
            PlayerBuilder config = PlayerManager.getInstance().getConfig();
            int kernel = config.getKernel();
            mMusicPlayer = MusicKernelFactoryManager.getKernel(kernel);
        }
    }

    public static void release(boolean release) {
        setEnable(false);
        pause();
        if (release && null != mMusicPlayer) {
            MPLogUtil.log("MusicPlayerManager => release => mMusicPlayer = " + mMusicPlayer);
            mMusicPlayer.release();
            mMusicPlayer = null;
        }
    }

    private static void setEnable(boolean v) {
        if (null != mMusicPlayer) {
            MPLogUtil.log("MusicPlayerManager => setEnable => v = " + v + ", mMusicPlayer = " + mMusicPlayer);
            mMusicPlayer.setEnable(v);
        }
    }

    public static boolean isEnable() {
        if (null != mMusicPlayer) {
            MPLogUtil.log("MusicPlayerManager => isEnable => mMusicPlayer = " + mMusicPlayer);
            return mMusicPlayer.isEnable();
        } else {
            return true;
        }
    }

    public static boolean isNull() {
        MPLogUtil.log("MusicPlayerManager => isNull => mMusicPlayer = " + mMusicPlayer);
        if (null != mMusicPlayer) {
            return false;
        } else {
            return true;
        }
    }

    public static void start(long position, OnMusicPlayerChangeListener listener) {
        if (null != mMusicPlayer) {
            MPLogUtil.log("MusicPlayerManager => play => mMusicPlayer = " + mMusicPlayer);
            mMusicPlayer.start(position, listener);
        }
    }

    public static void seekTo(long position) {
        if (null != mMusicPlayer && position > 0) {
            MPLogUtil.log("MusicPlayerManager => seekTo => position = " + position + ", mMusicPlayer = " + mMusicPlayer);
            mMusicPlayer.seekTo(position);
        }
    }

    public static boolean isPlaying() {
        if (null != mMusicPlayer) {
            MPLogUtil.log("MusicPlayerManager => isPlaying => mMusicPlayer = " + mMusicPlayer);
            return mMusicPlayer.isPlaying();
        } else {
            return false;
        }
    }

    public static void start(@NonNull Context context, @NonNull long position, @NonNull String musicUrl) {
        start(context, position, musicUrl, null);
    }

    public static void start(@NonNull Context context, @NonNull long position, @NonNull String musicUrl, @NonNull OnMusicPlayerChangeListener listener) {
        MPLogUtil.log("MusicPlayerManager => start => position = " + position + ", musicUrl = " + musicUrl + ", mMusicPlayer = " + mMusicPlayer + ", listener = " + listener);
        // 1
        create();
        // 2
        setDataSource(context, musicUrl);
        // 3
        setEnable(true);
        // 4
        start(position, listener);
    }
}
