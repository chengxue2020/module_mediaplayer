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
        boolean invisibleStop = bundle.isInvisibleStop();
        MPLogUtil.log("KernelApi => update => invisibleStop = " + invisibleStop);
        setInvisibleStop(invisibleStop);
        boolean invisibleIgnore = bundle.isInvisibleIgnore();
        MPLogUtil.log("KernelApi => update => invisibleIgnore = " + invisibleIgnore);
        setInvisibleIgnore(invisibleIgnore);
        boolean invisibleRelease = bundle.isInvisibleRelease();
        MPLogUtil.log("KernelApi => update => invisibleRelease = " + invisibleRelease);
        setInvisibleRelease(invisibleRelease);
        if (null != playUrl && playUrl.length() > 0) {
            setUrl(playUrl);
        }
        // 2
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

    void setDataSource(AssetFileDescriptor fd);

    void setSurface(@NonNull Surface surface);

    void seekTo(@NonNull long position, @NonNull boolean seekHelp);

    int getBufferedPercentage();

//    void setOptions();

    void setSpeed(float speed);

    float getSpeed();

    String getUrl();

    void setUrl(String url);

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

    boolean isInvisibleStop();

    void setInvisibleStop(boolean v);

    boolean isInvisibleIgnore();

    void setInvisibleIgnore(boolean v);

    boolean isInvisibleRelease();

    void setInvisibleRelease(boolean v);

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

    default void stopExternalMusic() {
        MPLogUtil.log("KernelApi => stopExternalMusic[销毁额外音频-每次都销毁音频播放器] =>");

        // 1 视频
        boolean muteVideo = isMute(); //视频强制静音
        MPLogUtil.log("KernelApi => stopExternalMusic[销毁额外音频-每次都销毁音频播放器] => muteVideo = " + muteVideo);
        setVolume(muteVideo ? 0F : 1f, muteVideo ? 0F : 1f);

        // 2 音频
        MPLogUtil.log("KernelApi => stopExternalMusic[销毁额外音频-每次都销毁音频播放器] => release");
        MusicPlayerManager.release();
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
        setVolume(0F, 0F);

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
        OnMusicPlayerChangeListener l;
        if (musicLooping) {
            l = null;
        } else {
            l = new OnMusicPlayerChangeListener() {
                @Override
                public void onComplete() {
                    MPLogUtil.log("KernelApi => startExternalMusic[播放额外音频-每次都创建音频播放器] => onComplete");
                    setVolume(1F, 1F);
                }

                @Override
                public void onError() {
                    MPLogUtil.log("KernelApi => startExternalMusic[播放额外音频-每次都创建音频播放器] => onError");
                    setVolume(1F, 1F);
                }
            };
        }
        MusicPlayerManager.start(context, position, musicUrl, l);
    }

    default void releaseExternalMusic() {
        releaseExternalMusic(true);
    }

    default void releaseExternalMusic(boolean clear) {
        if (clear) {
            setExternalMusicPath(null);
            setExternalMusicLooping(false);
        }
        MusicPlayerManager.release();
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

    default boolean isExternalMusicNull() {
        return MusicPlayerManager.isNull();
    }

    /*************** 外部背景音乐 **************/
}