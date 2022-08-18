package lib.kalu.mediaplayer.core.kernel;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;

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

    default void create(@NonNull Context context, @NonNull long seek, @NonNull long max, @NonNull boolean loop, @NonNull boolean autoRelease, @NonNull String url) {
        MediaLogUtil.log("KernelApi => create => seek = " + seek + ", max = " + max + ", loop = " + loop + ", url = " + url + ", autoRelease = " + autoRelease);
        update(seek, max, loop, autoRelease, url);
        init(context, seek, max, url);
    }

    default void update(@NonNull long seek, @NonNull long max, @NonNull boolean loop) {
        boolean autoRelease = isAutoRelease();
        String url = getUrl();
        update(seek, max, loop, autoRelease, url);
    }

    default void update(@NonNull long seek, @NonNull long max, @NonNull boolean loop, @NonNull boolean autoRelease, @NonNull String url) {
        MediaLogUtil.log("KernelApi => update => seek = " + seek + ", max = " + max + ", loop = " + loop + ", autoRelease = " + autoRelease + ", url = " + url + ", mKernel = " + this);
        setSeek(seek);
        setMax(max);
        setLooping(loop);
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

    void setLooping(boolean loop);

    boolean isLooping();

    void setAutoRelease(boolean release);

    boolean isAutoRelease();

    void setVolume(float left, float right);

    boolean isMute();

    /********/

    boolean isMusicPrepare();

    void setMusicPrepare(boolean prepare);

    void setMusicPath(@NonNull String musicPath);

    String getMusicPath();

    android.media.MediaPlayer getMusicPlayer();

    default void releaseMusic() {
        stopMusic();
        MediaPlayer musicPlayer = getMusicPlayer();
        if (null != musicPlayer) {
            musicPlayer.release();
            musicPlayer = null;
        }
    }

    default void stopMusic() {

        String musicPath = getMusicPath();
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
    }

    default void resumeMusic() {

        String musicPath = getMusicPath();
        MediaLogUtil.log("KernelApiMusic => resumeMusic => musicPath = " + musicPath);
        if (null == musicPath || musicPath.length() <= 0)
            return;

        MediaPlayer musicPlayer = getMusicPlayer();
        if (musicPlayer.isLooping()) {
            musicPlayer.setLooping(false);
        }
        try {
            musicPlayer.prepare();
        } catch (Exception e) {
        }
        musicPlayer.start();
        long position = getPosition();
        musicPlayer.seekTo((int) position);
        musicPlayer.setOnPreparedListener(null);
        musicPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                MediaLogUtil.log("KernelApiMusic => resumeMusic => onPrepared = " + true);
                setMusicPrepare(true);
                setVolume(0, 0);
                start();
            }
        });
        musicPlayer.setOnErrorListener(null);
        musicPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                MediaLogUtil.log("KernelApiMusic => resumeMusic => onError = " + i + ">" + i1);
                return false;
            }
        });
    }

    default void startMusic(@NonNull String music) {

        // 1
        String musicPath = getMusicPath();
        MediaLogUtil.log("KernelApiMusic => startMusic => musicPath = " + musicPath);
        if (null == musicPath || musicPath.length() <= 0)
            return;

        // 2
        MediaPlayer musicPlayer = getMusicPlayer();
        try {
            musicPlayer.setDataSource(Uri.parse(music).toString());
            MediaLogUtil.log("KernelApiMusic => startMusic => setDataSource = " + true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3
        resumeMusic();
    }

    default void updateMusic(@NonNull String music, @NonNull boolean playMusic) {

        // 1
        setMusicPath(music);

        // 2
        if (playMusic) {
            startMusic(music);
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
            toggleMusicExtra();
        }
    }

    default void toggleMusicExtra() {

        String musicPath = getMusicPath();
        MediaLogUtil.log("KernelApiMusic => toggleMusicExtra => musicPath = " + musicPath);
        if (null == musicPath || musicPath.length() <= 0)
            return;

        // 视频音源
        boolean musicPrepare = isMusicPrepare();
        MediaLogUtil.log("KernelApiMusic => toggleMusicExtra => musicPrepare = " + musicPrepare);

        // 切换配音1
        if (musicPrepare) {
            pause();
            resumeMusic();
        }
        // 切换配音2
        else {
            pause();
            startMusic(musicPath);
        }
    }

    default void toggleMusicDafault() {

        String musicPath = getMusicPath();
        MediaLogUtil.log("KernelApiMusic => toggleMusicDafault => musicPath = " + musicPath);
        if (null == musicPath || musicPath.length() <= 0)
            return;

        boolean musicPrepare = isMusicPrepare();
        MediaLogUtil.log("KernelApiMusic => toggleMusicDafault => musicPrepare = " + musicPrepare);
        if (!musicPrepare)
            return;

        // 切换原声
        stopMusic();
        pause();
        setVolume(1, 1);
        start();
    }
}