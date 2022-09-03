package lib.kalu.mediaplayer.core.kernel;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Surface;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.util.MediaLogUtil;


/**
 * @description: 播放器 - 抽象接口
 * @date: 2021-05-12 09:40
 */
@Keep
public interface KernelApi extends KernelEvent {

//    Boolean[] mLoop = new Boolean[1]; // 循环播放
//    Boolean[] mMute = new Boolean[1]; // 静音
//    Long[] mMax = new Long[1]; // 试播时常
//    String[] mUrl = new String[1]; // 视频串
//    MediaPlayer[] mMusicPlayer = new MediaPlayer[1]; // 配音音频

    @NonNull
    <T extends Object> T getPlayer();

    void createDecoder(@NonNull Context context);

    void releaseDecoder();

    void init(@NonNull Context context, @NonNull long seek, @NonNull long max, @NonNull String url);

    default void create(@NonNull Context context, @NonNull long seek, @NonNull long max, @NonNull boolean loop, @NonNull boolean live, @NonNull boolean autoRelease, @NonNull String url) {
        MediaLogUtil.log("KernelApi => create => seek = " + seek + ", max = " + max + ", loop = " + loop + ", url = " + url + ", autoRelease = " + autoRelease + ", live = " + live);
        update(seek, max, loop, live, autoRelease, url);
        init(context, seek, max, url);
    }

    default void update(@NonNull long seek, @NonNull long max, @NonNull boolean loop, @NonNull boolean live) {
        boolean autoRelease = isAutoRelease();
        String url = getUrl();
        update(seek, max, loop, live, autoRelease, url);
    }

    default void update(@NonNull long seek, @NonNull long max, @NonNull boolean loop, @NonNull boolean live, @NonNull boolean autoRelease, @NonNull String url) {
        MediaLogUtil.log("KernelApi => update => seek = " + seek + ", max = " + max + ", loop = " + loop + ", live = " + live + ", autoRelease = " + autoRelease + ", url = " + url + ", mKernel = " + this);
        setSeek(seek);
        setMax(max);
        setLooping(loop);
        setLive(live);
        setAutoRelease(autoRelease);
        if (null != url && url.length() > 0) {
            setUrl(url);
        }
    }

    void setDataSource(AssetFileDescriptor fd);

    void setSurface(@NonNull Surface surface);

    void start();

    void pause();

    void stop();

    boolean isPlaying();

    void seekTo(long time);

    long getPosition();

    long getDuration();

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

    void setVolume(float left, float right);

    boolean isMute();

    /********/

    boolean isMusicPrepare();

    void setMusicPrepare(boolean prepare);

    boolean isMusicLoop();

    void setMusicLoop(boolean loop);

    boolean isMusicSeek();

    void setMusicSeek(boolean seek);

    void setMusicPath(@NonNull String musicPath);

    String getMusicPath();

    android.media.MediaPlayer getMusicPlayer();

    default void releaseMusic() {

        String musicPath = getMusicPath();
        MediaLogUtil.log("KernelApiMusic => releaseMusic => musicPath = " + musicPath);
        if (null == musicPath || musicPath.length() <= 0)
            return;

        // 1
        setMusicPrepare(false);

        // 2
        MediaPlayer musicPlayer = getMusicPlayer();
        if (musicPlayer.isLooping()) {
            musicPlayer.setLooping(false);
        }
        if (musicPlayer.isPlaying()) {
            musicPlayer.stop();
        }
        musicPlayer.reset();
        musicPlayer.release();
    }

    default void resumeMusic() {

        String musicPath = getMusicPath();
        MediaLogUtil.log("KernelApiMusic => resumeMusic => musicPath = " + musicPath);
        if (null == musicPath || musicPath.length() <= 0)
            return;

        // step1
        pause();
        setVolume(0, 0);

        // step2
        MediaPlayer musicPlayer = getMusicPlayer();
        if (musicPlayer.isLooping()) {
            musicPlayer.setLooping(false);
        }
        try {
            musicPlayer.prepare();
        } catch (Exception e) {
        }
        musicPlayer.start();
        boolean musicSeek = isMusicSeek();
        if (musicSeek) {
            long position = getPosition();
            musicPlayer.seekTo((int) position);
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
        musicPlayer.setOnPreparedListener(null);
        musicPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                boolean playing = isPlaying();
                MediaLogUtil.log("KernelApiMusic => onPrepared => playing = " + playing);
                if (!playing) {
                    setMusicPrepare(true);
                    start();
                }
            }
        });
    }

    default void startMusic() {

        // 1
        String musicPath = getMusicPath();
        MediaLogUtil.log("KernelApiMusic => startMusic => musicPath = " + musicPath);
        if (null == musicPath || musicPath.length() <= 0)
            return;

        // 2
        MediaPlayer musicPlayer = getMusicPlayer();
        try {
            musicPlayer.setDataSource(Uri.parse(musicPath).toString());
            MediaLogUtil.log("KernelApiMusic => startMusic => setDataSource = " + true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3
        resumeMusic();
    }


    default void updateMusic(@NonNull String musicPath, @NonNull boolean musicPlay, @NonNull boolean musicLoop, @NonNull boolean musicSeek) {

        // step1
        setMusicPath(musicPath);
        setMusicLoop(musicLoop);
        setMusicSeek(musicSeek);

        // step2
        releaseMusic();

        // step3
        if (musicPlay) {
            startMusic();
        }
    }

    default void toggleMusic() {

        String musicPath = getMusicPath();
        MediaLogUtil.log("KernelApiMusic => toggleMusic => musicPath = " + musicPath);
        if (null == musicPath || musicPath.length() <= 0) {
            return;
        }

        // 视频音源
        MediaPlayer musicPlayer = getMusicPlayer();
        boolean playing = musicPlayer.isPlaying();
        boolean musicPrepare = isMusicPrepare();
        MediaLogUtil.log("KernelApiMusic => toggleMusic => playing = " + playing + ", musicPrepare = " + musicPrepare);

        // 切换原声
        if (musicPrepare && playing) {
            toggleMusicDafault();
        }
        // 切换配音
        else {
            toggleMusicExtra(false);
        }
    }

    default void toggleMusicExtra(boolean auto) {

        String musicPath = getMusicPath();
        MediaLogUtil.log("KernelApiMusic => toggleMusicExtra => musicPath = " + musicPath);
        if (null == musicPath || musicPath.length() <= 0)
            return;

        if (auto) {
            releaseMusic();
        }

        // 视频音源
        boolean musicPrepare = isMusicPrepare();
        if (!auto && null != musicPath && musicPath.length() > 0 && musicPrepare)
            return;

        // 切换配音1
        if (musicPrepare) {
            resumeMusic();
        }
        // 切换配音2
        else {
            startMusic();
        }
    }

    default void toggleMusicDafault() {
        boolean musicPrepare = isMusicPrepare();
        toggleMusicDafault(musicPrepare);
    }

    default void toggleMusicDafault(boolean musicPrepare) {

        String musicPath = getMusicPath();
        MediaLogUtil.log("KernelApiMusic => toggleMusicDafault => musicPrepare = " + musicPrepare + ", musicPath = " + musicPath);
        if (!musicPrepare && (null == musicPath || musicPath.length() <= 0))
            return;

        // 切换原声
        releaseMusic();
        pause();
        setVolume(1, 1);
        start();
    }

    default boolean hasMusicExtra() {
        boolean musicPrepare = isMusicPrepare();
        boolean musicLoop = isMusicLoop();
        String musicPath = getMusicPath();
        return (null != musicPath && musicPath.length() > 0 && musicPrepare && musicLoop);
    }
}