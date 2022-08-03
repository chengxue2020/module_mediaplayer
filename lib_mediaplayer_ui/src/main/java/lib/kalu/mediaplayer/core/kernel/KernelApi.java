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

    default void create(@NonNull Context context, @NonNull long seek, @NonNull long max, @NonNull boolean loop, @NonNull String url) {
        MediaLogUtil.log("KernelApi => create => seek = " + seek + ", max = " + max + ", loop = " + loop + ", url = " + url);
        update(seek, max, loop, url);
        init(context, seek, max, url);
    }

    default void update(@NonNull long seek, @NonNull long max, @NonNull boolean loop) {
        update(seek, max, loop, null);
    }

    default void update(@NonNull long seek, @NonNull long max, @NonNull boolean loop, @NonNull String url) {
        MediaLogUtil.log("KernelApi => update => seek = " + seek + ", max = " + max + ", loop = " + loop + ", url = " + url + ", mKernel = " + this);
        setSeek(seek);
        setMax(max);
        setLooping(loop);
        if (null != url && url.length() > 0) {
            setUrl(url);
        }
    }

    void setDataSource(AssetFileDescriptor fd);

    void setSurface(@NonNull Surface surface);

    /**
     * 播放
     */
    void start();

    /**
     * 暂停
     */
    void pause();

    /**
     * 停止
     */
    void stop();


    /**
     * 是否正在播放
     *
     * @return 是否正在播放
     */
    boolean isPlaying();

    /**
     * 调整进度
     */
    void seekTo(long time);

    /**
     * 获取当前播放的位置
     *
     * @return 获取当前播放的位置
     */
    long getPosition();

    /**
     * 获取视频总时长
     *
     * @return 获取视频总时长
     */
    long getDuration();

    /**
     * 获取缓冲百分比
     *
     * @return 获取缓冲百分比
     */
    int getBufferedPercentage();

    /**
     * 设置其他播放配置
     */
    void setOptions();

    /**
     * 设置播放速度
     *
     * @param speed 速度
     */
    void setSpeed(float speed);

    /**
     * 获取播放速度
     *
     * @return 播放速度
     */
    float getSpeed();

    /**
     * 获取当前缓冲的网速
     *
     * @return 获取网络
     */
    long getTcpSpeed();

    String getUrl();

    void setUrl(String url);

    long getSeek();

    void setSeek(long seek);

    long getMax();

    void setMax(long max);

    void setLooping(boolean loop);

    boolean isLooping();

    void setVolume(float left, float right);

    boolean isMute();

    android.media.MediaPlayer getMusicPlayer();

    void setMusicPlayer(@NonNull android.media.MediaPlayer player);

    default void releaseMusic() {
        stopMusic();
        MediaPlayer musicPlayer = getMusicPlayer();
        if (null != musicPlayer) {
            musicPlayer.release();
            musicPlayer = null;
        }
    }

    default void stopMusic() {
        try {
            MediaPlayer musicPlayer = getMusicPlayer();
            if (null == musicPlayer) {
                if (musicPlayer.isLooping()) {
                    musicPlayer.setLooping(false);
                }
                if (musicPlayer.isPlaying()) {
                    musicPlayer.stop();
                }
                musicPlayer.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    default void toggleMusic(@NonNull Context context, @NonNull String music) {

        MediaPlayer musicPlayer = getMusicPlayer();
        if (null == musicPlayer) {
            musicPlayer = new MediaPlayer();
            setMusicPlayer(musicPlayer);
        }

        // 视频音源
        if (null != music && music.length() > 0) {

            // pause
            if (musicPlayer.isPlaying()) {
                stopMusic();
                pause();
                toggleMusic(context, music);
            }
            // next
            else {
                try {
                    stopMusic();
                    pause();
                    musicPlayer.setDataSource(context, Uri.parse(music));
                    musicPlayer.prepare();
                    musicPlayer.start();
                    long position = getPosition();
                    musicPlayer.seekTo((int) position);
                    musicPlayer.setOnPreparedListener(null);
                    musicPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            setVolume(0, 0);
                            start();
                        }
                    });
                    musicPlayer.setOnErrorListener(null);
                    musicPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                            toggleMusic(context, null);
                            return false;
                        }
                    });
                } catch (Exception e) {
                    toggleMusic(context, null);
                    e.printStackTrace();
                }
            }
        }
        // 外部音源
        else {
            stopMusic();
            pause();
            setVolume(1, 1);
            start();
        }
    }
}