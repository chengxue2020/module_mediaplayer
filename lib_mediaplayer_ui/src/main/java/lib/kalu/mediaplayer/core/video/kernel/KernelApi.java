package lib.kalu.mediaplayer.core.video.kernel;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.view.Surface;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerBuilder;
import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.core.audio.MusicPlayerManager;
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

    void createDecoder(@NonNull Context context, @NonNull boolean mute, @NonNull boolean logger, @NonNull int seekParameters);

    void releaseDecoder();

    void init(@NonNull Context context, @NonNull String url);

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
        setExternalMusicLoop(musicLoop);
        boolean musicAuto = bundle.isExternalMusicAuto();
        MPLogUtil.log("KernelApi => update => musicAuto = " + musicAuto);
        setExternalMusicAuto(musicAuto);

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

    void setOptions();

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

//    boolean isExternalMusicPrepared();

//    void setExternalMusicPrepared(boolean v);

    boolean isExternalMusicLoop();

    void setExternalMusicLoop(boolean loop);

    boolean isExternalMusicAuto();

    void setExternalMusicAuto(boolean auto);

    void setExternalMusicPath(@NonNull String musicPath);

    String getExternalMusicPath();

    default void toggleExternalMusic(Context context, boolean enableMusic, boolean enableSeek) {

        String externalMusicPath = getExternalMusicPath();
        MPLogUtil.log("KernelApi => toggleExternalMusic => enableMusic = " + enableMusic + ", enableSeek = " + enableSeek);
        if (null == externalMusicPath || externalMusicPath.length() <= 0)
            return;

        // 播放额外音频【每次都创建音频播放器】
        if (enableMusic) {
            MPLogUtil.log("KernelApi => toggleExternalMusic[播放额外音频-每次都创建音频播放器] =>");

            // 1 视频
            boolean muteVideo = isMute(); //视频强制静音
            MPLogUtil.log("KernelApi => toggleExternalMusic[播放额外音频-每次都创建音频播放器] => muteVideo = "+muteVideo);
            setVolume(muteVideo ? 0F : 1f, muteVideo ? 0F : 1f);

            // 2 音频
            long start = 0;
            if (enableSeek) {
                start = getPosition() - getSeek();
                if (start <= 0) {
                    start = 0;
                }
            }
            MPLogUtil.log("KernelApi => toggleExternalMusic[播放额外音频-每次都创建音频播放器] => start");
            MusicPlayerManager.start(context, start, externalMusicPath);
        }
        // 销毁额外音频
        else {
            MPLogUtil.log("KernelApi => toggleExternalMusic[销毁额外音频-每次都销毁音频播放器] =>");

            // 1 视频
            boolean muteVideo = isMute(); //视频强制静音
            MPLogUtil.log("KernelApi => toggleExternalMusic[销毁额外音频-每次都销毁音频播放器] => muteVideo = "+muteVideo);
            setVolume(muteVideo ? 0F : 1f, muteVideo ? 0F : 1f);

            // 2 音频
            MPLogUtil.log("KernelApi => toggleExternalMusic[销毁额外音频-每次都销毁音频播放器] => release");
            MusicPlayerManager.release();
        }
    }

    default void releaseExternalMusic() {
        releaseExternalMusic(true);
    }

    default void releaseExternalMusic(boolean clearExternalMusicData) {

        // 1
        if (clearExternalMusicData) {
            setExternalMusicPath(null);
            setExternalMusicLoop(false);
        }

        // 2
        MusicPlayerManager.release();
    }

    default void pauseExternalMusic() {
        MusicPlayerManager.pause();
    }

    default void resumeExternalMusic() {
        MusicPlayerManager.resume();
    }

    default boolean isExternalMusicPlaying() {
        return MusicPlayerManager.isPlaying();
    }

    /*************** 外部背景音乐 **************/
}