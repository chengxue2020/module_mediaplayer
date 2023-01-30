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

    public static void play() {
        if (null != mMusicPlayer) {
            MPLogUtil.log("MusicPlayerManager => play => mMusicPlayer = " + mMusicPlayer);
            mMusicPlayer.play();
        }
    }

    public static void seekTo(long v) {
        if (null != mMusicPlayer) {
            MPLogUtil.log("MusicPlayerManager => seekTo => v = " + v + ", mMusicPlayer = " + mMusicPlayer);
            mMusicPlayer.play();
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

    public static void start(@NonNull Context context, @NonNull long seek, @NonNull String musicUrl) {
        MPLogUtil.log("MusicPlayerManager => start => seek = " + seek + ", musicUrl = " + musicUrl + ", mMusicPlayer = " + mMusicPlayer);
        // 1
        MPLogUtil.log("MusicPlayerManager => start => create");
        create();
        // 2
        MPLogUtil.log("MusicPlayerManager => start => setDataSource");
        setDataSource(context, musicUrl);
        // 3
        MPLogUtil.log("MusicPlayerManager => start => play");
        play();
        // 4
        MPLogUtil.log("MusicPlayerManager => start => seekTo");
        seekTo(seek);
    }
}
