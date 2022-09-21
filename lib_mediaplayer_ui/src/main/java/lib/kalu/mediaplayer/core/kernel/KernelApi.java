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

    default void update(@NonNull long seek) {
        setSeek(seek);
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
        setExternalMusicSeek(bundle.isExternalMusicSeek());
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

    boolean isExternalMusicSeek();

    void setExternalMusicSeek(boolean seek);

    void setExternalMusicPath(@NonNull String musicPath);

    String getExternalMusicPath();

    android.media.MediaPlayer getExternalMusicPlayer();

    default void enableExternalMusic(boolean enable, boolean release) {

        String path = getExternalMusicPath();
        boolean playing = isExternalMusicPlaying();
        MediaLogUtil.log("KernelApiMusic => enableExternalMusic => playing = " + playing + ", path = " + path);

        // 播放额外音频
        if (enable) {

            // 1a
            if (playing) {
                setExternalVolume(1f);
            }
            // 1b, 2. 设置额外音频地址
            else {

                // a
                releaseExternalMusic();

                // b
                MediaPlayer musicPlayer = getExternalMusicPlayer();
                try {
                    musicPlayer.setDataSource(Uri.parse(path).toString());
                } catch (IOException e) {
                }

                // c
                // 3-1. 播放额外音频
                pause();
                setVolume(0f, 0f);

                // 3.2. 播放额外音频
                boolean loop = isExternalMusicLoop();
                musicPlayer.setLooping(loop);
                try {
                    musicPlayer.prepare();
                } catch (Exception e) {
                }
                musicPlayer.setVolume(1f, 1f);
                musicPlayer.start();
                boolean seek = isExternalMusicSeek();
                if (seek) {
                    long position = getPosition();
                    if (position > 0) {
                        MediaLogUtil.log("KernelApiMusic => seekTo => position = " + position);
                        musicPlayer.seekTo((int) position);
                    }
                }
//        musicPlayer.setOnInfoListener(null);
//        musicPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
//            @Override
//            public boolean onInfo(MediaPlayer mp, int what, int extra) {
//                MediaLogUtil.log("KernelApiMusic => onInfo => what = " + what + ", extra = " + extra);
//                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
//                    boolean playing = isPlaying();
//                    MediaLogUtil.log("KernelApiMusic => onInfo => what = " + what + ", extra = " + extra + ", playing = " + playing);
//                    if (!playing) {
//                        setMusicPrepare(true);
//                        start();
//                    }
//                }
//                return false;
//            }
//        });
                MediaLogUtil.log("KernelApiMusic => setOnPreparedListener =>");
                musicPlayer.setOnPreparedListener(null);
                musicPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        boolean playing = isPlaying();
                        MediaLogUtil.log("KernelApiMusic => onPrepared => playing = " + playing);
                        if (!playing) {
                            setExternalMusicPlaying(true);
                            start();
                        }
                    }
                });
            }
        }
        // 停止额外音频
        else {

            // 1.暂停视频播放器
            pause();
            setExternalMusicPlaying(false);

            // 2a.销毁额外音频播放器
            if (release) {
                releaseExternalMusic();
            }
            // 2b.暂停额外音频播放器
            else {
                setExternalVolume(0f);
            }

            // 3.
            if (isMute()) {
            } else {
                setVolume(1f, 1f);
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
        MediaPlayer musicPlayer = getExternalMusicPlayer();
        if (musicPlayer.isLooping()) {
            musicPlayer.setLooping(false);
        }
        if (musicPlayer.isPlaying()) {
            musicPlayer.stop();
        }
        musicPlayer.reset();
        musicPlayer.release();
    }

    default void setExternalVolume(float v) {
        try {
            MediaPlayer musicPlayer = getExternalMusicPlayer();
            musicPlayer.setVolume(v, v);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    default boolean hasExternalMusicPath() {
        String path = getExternalMusicPath();
        return null != path && path.length() > 0;
    }

    /*************** 外部背景音乐 **************/
}