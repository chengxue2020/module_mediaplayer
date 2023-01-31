package lib.kalu.mediaplayer.core.audio;


import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerBuilder;
import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.audio.kernel.MusicKernelApi;
import lib.kalu.mediaplayer.core.audio.kernel.MusicKernelFactoryManager;
import lib.kalu.mediaplayer.util.MPLogUtil;

public final class MusicPlayerManager {

    private static MusicKernelApi mMusicPlayer = null;

    public static void setVolume(float v) {
        if (null != mMusicPlayer) {
            MPLogUtil.log("MusicPlayerManager => setVolume => v = " + v + ", mMusicPlayer = " + mMusicPlayer);
            mMusicPlayer.setVolume(v);
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
            mMusicPlayer.start();
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
            release();
        }
        MPLogUtil.log("MusicPlayerManager => create => step2");
        if (null == mMusicPlayer) {
            MPLogUtil.log("MusicPlayerManager => create => mMusicPlayer = " + mMusicPlayer);
            PlayerBuilder config = PlayerManager.getInstance().getConfig();
            int kernel = config.getKernel();
            mMusicPlayer = MusicKernelFactoryManager.getKernel(kernel);
        }
    }

    public static void release() {
        if (null != mMusicPlayer) {
            MPLogUtil.log("MusicPlayerManager => release => mMusicPlayer = " + mMusicPlayer);
            mMusicPlayer.release();
            mMusicPlayer = null;
        }
    }

    public static void start() {
        if (null != mMusicPlayer) {
            MPLogUtil.log("MusicPlayerManager => play => mMusicPlayer = " + mMusicPlayer);
            mMusicPlayer.start();
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

    public static void setMusicListener(@NonNull OnMusicPlayerChangeListener listener) {
        if (null != mMusicPlayer) {
            MPLogUtil.log("MusicPlayerManager => setMusicListener => listener = " + listener + ", mMusicPlayer = " + mMusicPlayer);
            mMusicPlayer.setMusicListener(listener);
        }
    }

    public static void setLooping(@NonNull boolean v) {
        if (null != mMusicPlayer) {
            MPLogUtil.log("MusicPlayerManager => setLooping => v = " + v + ", mMusicPlayer = " + mMusicPlayer);
            mMusicPlayer.setLooping(v);
        }
    }

    public static void start(@NonNull Context context, @NonNull long position, @NonNull boolean musicLooping, @NonNull String musicUrl) {
        start(context, position, musicLooping, musicUrl, null);
    }

    public static void start(@NonNull Context context, @NonNull long position, @NonNull boolean musicLooping, @NonNull String musicUrl, @NonNull OnMusicPlayerChangeListener listener) {
        MPLogUtil.log("MusicPlayerManager => start => position = " + position + ", musicLooping = " + musicLooping + ", musicUrl = " + musicUrl + ", mMusicPlayer = " + mMusicPlayer + ", listener = " + listener);
        // 1
        create();
        // 2
        setDataSource(context, musicUrl);
        // 3
        setMusicListener(listener);
        // 4
        setLooping(musicLooping);
        // 4
        start();
        // 5
        seekTo(position);
    }
}
