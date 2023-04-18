package lib.kalu.mediaplayer.core.kernel.video;

import android.content.Context;

import lib.kalu.mediaplayer.core.kernel.audio.MusicPlayerManager;
import lib.kalu.mediaplayer.core.kernel.audio.OnMusicPlayerChangeListener;
import lib.kalu.mediaplayer.util.MPLogUtil;

 interface KernelApiExternalMusic extends KernelApiBase {

    boolean isExternalMusicPlayWhenReady();

    boolean isExternalMusicLooping();

    boolean isExternalMusicSeek();

    String getExternalMusicPath();

    default void stopExternalMusic(boolean release) {
        MPLogUtil.log("KernelApi => stopExternalMusic[销毁额外音频-每次都销毁音频播放器] => release = " + release);

        // 1 视频
        setMute(false);
        setVolume(1F, 1F);

        // 2 音频
        MPLogUtil.log("KernelApi => stopExternalMusic[销毁额外音频-每次都销毁音频播放器] => release");
        MusicPlayerManager.release(release);
    }

    default void startExternalMusic(Context context) {

        String musicUrl = getExternalMusicPath();
        MPLogUtil.log("KernelApi => startExternalMusic => musicUrl = " + musicUrl);
        if (null == musicUrl || musicUrl.length() <= 0)
            return;

        // 播放额外音频【每次都创建音频播放器】
        boolean musicSeek = isExternalMusicSeek();
        boolean musicLooping = isExternalMusicLooping();
        MPLogUtil.log("KernelApi => startExternalMusic[播放额外音频-每次都创建音频播放器] => musicSeek = " + musicSeek + ", musicLooping = " + musicLooping);

        // 1 视频
        setMute(true);
        setVolume(0F, 0F);
        pause();

        // 2 音频
        long position = 0;
        if (musicSeek) {
            position = getPosition() - getSeek();
            if (position <= 0) {
                position = 0;
            }
        }
        MPLogUtil.log("KernelApi => startExternalMusic[播放额外音频-每次都创建音频播放器] => position = " + position);

        // 3
        OnMusicPlayerChangeListener l = new OnMusicPlayerChangeListener() {
            @Override
            public void onStart() {
                MPLogUtil.log("KernelApi => startExternalMusic[播放额外音频-每次都创建音频播放器] => onStart");
                start();
            }

            @Override
            public void onEnd() {
                MPLogUtil.log("KernelApi => startExternalMusic[播放额外音频-每次都创建音频播放器] => onEnd");
                setVolume(1F, 1F);
            }

            @Override
            public void onError() {
                MPLogUtil.log("KernelApi => startExternalMusic[播放额外音频-每次都创建音频播放器] => onError");
                setVolume(1F, 1F);
            }
        };
        MusicPlayerManager.start(context, position, musicUrl, l);
    }

    default void pauseExternalMusic() {
        MusicPlayerManager.pause();
    }

    default void resetExternalMusic() {
        MusicPlayerManager.reset();
    }

    default void resumeExternalMusic() {
        MusicPlayerManager.resume();
    }

    default boolean isExternalMusicPlaying() {
        return MusicPlayerManager.isPlaying();
    }

    default boolean isExternalMusicRelease() {
        return MusicPlayerManager.isRelease();
    }

//    default boolean isExternalMusicNull() {
//        return MusicPlayerManager.isNull();
//    }
}
