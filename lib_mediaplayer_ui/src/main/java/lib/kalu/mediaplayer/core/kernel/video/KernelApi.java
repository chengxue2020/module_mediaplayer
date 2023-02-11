package lib.kalu.mediaplayer.core.kernel.video;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.view.Surface;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.core.kernel.audio.MusicPlayerManager;
import lib.kalu.mediaplayer.core.kernel.audio.OnMusicPlayerChangeListener;
import lib.kalu.mediaplayer.util.MPLogUtil;


/**
 * @description: 播放器 - 抽象接口
 * @date: 2021-05-12 09:40
 */
@Keep
public interface KernelApi extends KernelEvent {

    void onUpdateTimeMillis();

    @NonNull
    <T extends Object> T getPlayer();

    void createDecoder(@NonNull Context context, @NonNull boolean logger, @NonNull int seekParameters);

    void releaseDecoder();

    void init(@NonNull Context context, @NonNull String url);

    default void setOptions() {
    }

    default void update(@NonNull Context context, @NonNull StartBuilder bundle, @NonNull String playUrl) {

        MPLogUtil.log("KernelApi => update => playUrl = " + playUrl);
        long seek = bundle.getSeek();
        MPLogUtil.log("KernelApi => update => seek = " + seek);
        setSeek(seek);
        long max = bundle.getMax();
        MPLogUtil.log("KernelApi => update => max = " + max);
        setMax(max);
        boolean mute = bundle.isMute();
        MPLogUtil.log("KernelApi => update => mute = " + mute);
        setMute(mute);
        boolean loop = bundle.isLoop();
        MPLogUtil.log("KernelApi => update => loop = " + loop);
        setLooping(loop);
        boolean live = bundle.isLive();
        MPLogUtil.log("KernelApi => update => live = " + live);
        setLive(live);

        String musicUrl = bundle.getExternalMusicUrl();
        MPLogUtil.log("KernelApi => update => musicUrl = " + musicUrl);
        setExternalMusicPath(musicUrl);
        boolean musicLoop = bundle.isExternalMusicLoop();
        MPLogUtil.log("KernelApi => update => musicLoop = " + musicLoop);
        setExternalMusicLooping(musicLoop);
        boolean musicPlayWhenReady = bundle.isExternalMusicPlayWhenReady();
        MPLogUtil.log("KernelApi => update => musicPlayWhenReady = " + musicPlayWhenReady);
        setisExternalMusicPlayWhenReady(musicPlayWhenReady);

        init(context, playUrl);
    }

    default void update(@NonNull long seek, @NonNull long max, @NonNull boolean loop) {
        setSeek(seek);
        setMax(max);
        setLooping(loop);
    }

    void setSurface(@NonNull Surface surface, int w, int h);

    void seekTo(@NonNull long position, @NonNull boolean seekHelp);

    void setSpeed(float speed);

    float getSpeed();

    long getSeek();

    void setSeek(long seek);

    long getMax();

    void setMax(long max);

    boolean isReadying();

    void setReadying(boolean v);

    boolean isLive();

    void setLive(@NonNull boolean v);

    void setLooping(boolean loop);

    boolean isLooping();

    boolean isMute();

    void setMute(boolean v);

    void start();

    void pause();

    void stop();

    boolean isPlaying();

    long getPosition();

    long getDuration();

    void setVolume(float left, float right);

    /*************** 外部背景音乐 **************/

    boolean isExternalMusicPlayWhenReady();

    void setisExternalMusicPlayWhenReady(boolean v);

    boolean isExternalMusicLooping();

    void setExternalMusicLooping(boolean loop);

    boolean isExternalMusicEqualLength();

    void setExternalMusicEqualLength(boolean equal);

    void setExternalMusicPath(@NonNull String musicPath);

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

    default void startExternalMusic(Context context, boolean musicLooping, boolean musicEqual) {

        String musicUrl = getExternalMusicPath();
        MPLogUtil.log("KernelApi => startExternalMusic => musicUrl = " + musicUrl);
        if (null == musicUrl || musicUrl.length() <= 0)
            return;

        // 播放额外音频【每次都创建音频播放器】
        setExternalMusicLooping(musicLooping);
        setExternalMusicEqualLength(musicEqual);
        MPLogUtil.log("KernelApi => startExternalMusic[播放额外音频-每次都创建音频播放器] => musicEqual = " + musicEqual + ", musicLooping = " + musicLooping);

        // 1 视频
        setMute(true);
        setVolume(0F, 0F);
        pause();

        // 2 音频
        long position = 0;
        if (musicEqual) {
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

    default boolean isExternalMusicEnable() {
        return MusicPlayerManager.isEnable();
    }

    default boolean isExternalMusicNull() {
        return MusicPlayerManager.isNull();
    }

    /*************** 外部背景音乐 **************/
}