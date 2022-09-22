package lib.kalu.mediaplayer.core.kernel;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Surface;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import java.io.IOException;

import lib.kalu.mediaplayer.config.builder.BundleBuilder;
import lib.kalu.mediaplayer.core.kernel.music.MusicPlayerManager;
import lib.kalu.mediaplayer.util.MediaLogUtil;


/**
 * @description: 播放器 - 抽象接口
 * @date: 2021-05-12 09:40
 */
@Keep
public interface KernelApi extends KernelEvent {

    @NonNull
    <T extends Object> T getPlayer();

    void createDecoder(@NonNull Context context, @NonNull boolean mute);

    void releaseDecoder();

    void init(@NonNull Context context, @NonNull String url);
//    void init(@NonNull Context context, @NonNull BundleBuilder bundle, @NonNull String url);

    default void create(@NonNull Context context, @NonNull BundleBuilder bundle, @NonNull String url) {
        update(bundle, url);
        init(context, url);
    }

    default void update(@NonNull long seek, @NonNull long max, @NonNull boolean loop) {
        setSeek(seek);
        setMax(max);
        setLooping(loop);
    }

    default void update(@NonNull BundleBuilder bundle, @NonNull String playUrl) {
        MediaLogUtil.log("KernelApi => update => this = " + this + ", playUrl = " + playUrl);
        MediaLogUtil.log("KernelApi => update => bundle = " + bundle);
        // 1
        setSeek(bundle.getSeek());
        setMax(bundle.getMax());
        setMute(bundle.isMute());
        setLooping(bundle.isLoop());
        setLive(bundle.isLive());
        setAutoRelease(bundle.isRelease());
        if (null != playUrl && playUrl.length() > 0) {
            setUrl(playUrl);
        }
        // 2
        setExternalMusicPath(bundle.getExternalMusicUrl());
        setExternalMusicLoop(bundle.isExternalMusicLoop());
        setExternalMusicAuto(bundle.isExternalMusicAuto());
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

    void setLive(@NonNull boolean live);

    void setLooping(boolean loop);

    boolean isLooping();

    void setAutoRelease(boolean release);

    boolean isAutoRelease();

    boolean isMute();

    void setMute(boolean mute);

    void start();

    void pause();

    void stop();

    boolean isPlaying();

    long getPosition();

    long getDuration();

    void setVolume(float left, float right);

    /*************** 外部背景音乐 **************/

    boolean isExternalMusicPlaying();

    void setExternalMusicPlaying(boolean v);

    boolean isExternalMusicLoop();

    void setExternalMusicLoop(boolean loop);

    boolean isExternalMusicAuto();

    void setExternalMusicAuto(boolean auto);

    void setExternalMusicPath(@NonNull String musicPath);

    String getExternalMusicPath();

    default void enableExternalMusic(boolean enable, boolean release) {

        String path = getExternalMusicPath();
        boolean playing = isExternalMusicPlaying();
        MediaLogUtil.log("KernelApiMusic => enableExternalMusic => enable = " + enable + ", release = " + release + ", playing = " + playing);

        // 播放额外音频
        if (enable) {

            setVolume(0f, 0f);
            pause();

            // 1a
            if (playing) {
                long position = getPosition();
                long seek = getSeek();
                long start = position - seek;
                if (start <= 0) {
                    start = 1;
                }
                MediaLogUtil.log("KernelApiMusic => enableExternalMusic => start = " + start);
                MusicPlayerManager.restart(start, new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mp) {
                        setExternalMusicPlaying(true);
                        start();
                    }
                });
            }
            // 1b, 2. 设置额外音频地址
            else {

                // a
                if (release) {
                    releaseExternalMusic();
                }

                // c
                long position = getPosition();
                long seek = getSeek();
                long start = position - seek;
                if (start <= 0) {
                    start = 1;
                }
                MediaLogUtil.log("KernelApiMusic => enableExternalMusic => start = " + start);
                MusicPlayerManager.start(start, path, new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        setExternalMusicPlaying(true);
                        start();
                    }
                });
            }
        }
        // 停止额外音频
        else {

            if (playing) {
                // 1.暂停视频播放器
                pause();

                // 2
                MusicPlayerManager.setVolume(0f);

                // 3.销毁额外音频播放器
                if (release) {
                    releaseExternalMusic();
                }

                // 4.
                if (isMute()) {
                } else {
                    setVolume(1f, 1f);
                }
            }

            // 4.
            start();
        }
    }

    default void releaseExternalMusic() {

        // 1
        setExternalMusicPath(null);
        setExternalMusicLoop(false);
        setExternalMusicPlaying(false);

        // 2
        MusicPlayerManager.release();
    }

    /*************** 外部背景音乐 **************/
}