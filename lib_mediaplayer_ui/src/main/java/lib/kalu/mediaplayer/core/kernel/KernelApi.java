package lib.kalu.mediaplayer.core.kernel;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.view.Surface;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.core.kernel.music.MusicPlayerManager;
import lib.kalu.mediaplayer.util.MPLogUtil;


/**
 * @description: 播放器 - 抽象接口
 * @date: 2021-05-12 09:40
 */
@Keep
public interface KernelApi extends KernelEvent {

    @NonNull
    <T extends Object> T getPlayer();

    void createDecoder(@NonNull Context context, @NonNull boolean mute, @NonNull boolean logger);

    void releaseDecoder();

    void init(@NonNull Context context, @NonNull String url);

    default void create(@NonNull Context context, @NonNull StartBuilder bundle, @NonNull String url) {
        update(bundle, url);
        init(context, url);
    }

    default void update(@NonNull long seek, @NonNull long max, @NonNull boolean loop) {
        setSeek(seek);
        setMax(max);
        setLooping(loop);
    }

    default void update(@NonNull StartBuilder bundle, @NonNull String playUrl) {

        MPLogUtil.log("KernelApi => update => playUrl = " + playUrl);
        boolean timer = bundle.isTimer();
        MPLogUtil.log("KernelApi => update => timer = " + timer);
        setTimer(timer);
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
    }

    void setDataSource(AssetFileDescriptor fd);

    void setSurface(@NonNull Surface surface);

    void seekTo(@NonNull long position);

    int getBufferedPercentage();

    void setOptions();

    void setSpeed(float speed);

    float getSpeed();

    long getTcpSpeed();

    String getUrl();

    void setUrl(String url);

    long getSeek();

    void setSeek(long seek);

    long getMax();

    void setMax(long max);

    boolean isLive();

    void setLive(@NonNull boolean v);

    boolean isTimer();

    void setTimer(@NonNull boolean v);

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

    boolean isExternalMusicPrepared();

    void setExternalMusicPrepared(boolean v);

    boolean isExternalMusicLoop();

    void setExternalMusicLoop(boolean loop);

    boolean isExternalMusicAuto();

    void setExternalMusicAuto(boolean auto);

    void setExternalMusicPath(@NonNull String musicPath);

    String getExternalMusicPath();

    default void enableExternalMusic(boolean enable, boolean release) {

        String path = getExternalMusicPath();
        boolean prepared = isExternalMusicPrepared();
        boolean aNull = MusicPlayerManager.isNull();
        boolean mute = isMute(); //视频强制静音
        if (aNull) {
            prepared = false;
        }
        MPLogUtil.log("KernelApiMusic => enableExternalMusic => enable = " + enable + ", release = " + release + ", prepared = " + prepared + ", mute = " + mute);

        // 播放额外音频
        if (enable) {

            setVolume(0f, 0f);
            pause();

            // 1a
            if (prepared && !release) {
                long position = getPosition();
                long seek = getSeek();
                long start = position - seek;
                if (start <= 0) {
                    start = 0;
                }
                MPLogUtil.log("KernelApiMusic => enableExternalMusic => start = " + start);
                if (start > 0) {
                    MusicPlayerManager.restart(start, new MediaPlayer.OnSeekCompleteListener() {
                        @Override
                        public void onSeekComplete(MediaPlayer mp) {
                            MPLogUtil.log("KernelApiMusic => enableExternalMusic => onSeekComplete => ");
                            setExternalMusicPrepared(true);
                            start();
                        }
                    });
                } else {
                    MPLogUtil.log("KernelApiMusic => enableExternalMusic => onSeekComplete => ");
                    MusicPlayerManager.restart();
                    setExternalMusicPrepared(true);
                    start();
                }
            }
            // 1b, 2. 设置额外音频地址
            else {

                // a
                if (release) {
                    releaseExternalMusic(false);
                }

                // c
                long position = getPosition();
                long seek = getSeek();
                long start = position - seek;
                if (start < 0) {
                    start = 0;
                }
                MPLogUtil.log("KernelApiMusic => enableExternalMusic => start = " + start);
                MusicPlayerManager.start(start, path, new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        MPLogUtil.log("KernelApiMusic => enableExternalMusic => onPrepared => ");
                        setExternalMusicPrepared(true);
                        start();
                    }
                });
            }
        }
        // 停止额外音频
        else {

            // 1.暂停视频播放器
            pause();

            // 2. 销毁外部音源播放器
            if (prepared) {
                MPLogUtil.log("SEKK11 => enableExternalMusic => 销毁外部音源播放器 => prepared = " + prepared);

                // 2
                MusicPlayerManager.setVolume(0f);

                // 3.销毁额外音频播放器
                if (release) {
                    releaseExternalMusic(true);
                }

                // 4.
                MPLogUtil.log("SEKK11 => enableExternalMusic => 视频播放器恢复音频 => mute = " + mute);
                if (mute) {
                } else {
                    setVolume(1f, 1f);
                }
            }

            // 4.
            start();
        }
    }

    default void releaseExternalMusic() {
        releaseExternalMusic(true);
    }

    default void releaseExternalMusic(boolean clear) {

        // 1
        if (clear) {
            setExternalMusicPath(null);
            setExternalMusicLoop(false);
            setExternalMusicPrepared(false);
        }

        // 2
        MusicPlayerManager.release();
    }

    /*************** 外部背景音乐 **************/
}