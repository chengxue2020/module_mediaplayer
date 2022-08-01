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

    Boolean[] mMute = new Boolean[1]; // 静音
    Long[] mMax = new Long[1]; // 试播时常
    Long[] mSeek = new Long[1]; // 快进
    String[] mUrl = new String[1]; // 视频串
    MediaPlayer[] mMusicPlayer = new MediaPlayer[1]; // 配音音频

    /*---------------------------- 消息通知 ----------------------------------*/

    /*----------------------------第一部分：视频初始化实例对象方法----------------------------------*/

    @NonNull
    <T extends Object> T getPlayer();

    void createDecoder(@NonNull Context context);

    void releaseDecoder();

//    /**
//     * 视频播放器第二步： 设置数据
//     *
//     * @param url     播放地址
//     * @param headers 播放地址请求头
//     * @param config  视频缓存
//     */
//    void setDataSource(@NonNull Context context, @NonNull boolean live, @NonNull String url, @Nullable Map<String, String> headers, @NonNull CacheConfig config);

    /**
     * 用于播放raw和asset里面的视频文件
     */
    void setDataSource(AssetFileDescriptor fd);

    default void releaseMusic() {
        stopMusic();
        if (null != mMusicPlayer[0]) {
            mMusicPlayer[0].release();
            mMusicPlayer[0] = null;
        }
    }

    default void stopMusic() {
        try {
            if (mMusicPlayer[0].isLooping()) {
                mMusicPlayer[0].setLooping(false);
            }
            if (mMusicPlayer[0].isPlaying()) {
                mMusicPlayer[0].stop();
            }
            mMusicPlayer[0].reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    default void toggleMusic(@NonNull Context context, @NonNull String music) {

        if (null == mMusicPlayer[0]) {
            mMusicPlayer[0] = new MediaPlayer();
        }

        // 视频音源
        if (null != music && music.length() > 0) {

            // pause
            if (mMusicPlayer[0].isPlaying()) {
                stopMusic();
                pause();
                toggleMusic(context, music);
            }
            // next
            else {
                try {
                    stopMusic();
                    pause();
                    mMusicPlayer[0].setDataSource(context, Uri.parse(music));
                    mMusicPlayer[0].prepare();
                    mMusicPlayer[0].start();
                    long position = getPosition();
                    mMusicPlayer[0].seekTo((int) position);
                    mMusicPlayer[0].setOnPreparedListener(null);
                    mMusicPlayer[0].setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            setVolume(0, 0);
                            start();
                        }
                    });
                    mMusicPlayer[0].setOnErrorListener(null);
                    mMusicPlayer[0].setOnErrorListener(new MediaPlayer.OnErrorListener() {
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

    void init(@NonNull Context context, @NonNull long seek, @NonNull long max, @NonNull String url);

    default void create(@NonNull Context context, @NonNull long seek, @NonNull long max, @NonNull String url) {
        MediaLogUtil.log("K_LOG => create => seek = " + seek + ", max = " + max + ", url = " + url);
        mSeek[0] = seek;
        mMax[0] = max;
        mUrl[0] = url;
        init(context, seek, max, url);
    }

    default void update(@NonNull long seek, @NonNull long max, @NonNull boolean loop) {
        setLooping(loop);
        mSeek[0] = seek;
        mMax[0] = max;
    }

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
     * 设置音量
     *
     * @param v1 v1
     * @param v2 v2
     */

    default void setVolume(float v1, float v2) {
        mMute[0] = (v1 <= 0 || v2 <= 0);
    }

    default boolean isMute() {
        return null == mMute[0] ? false : mMute[0];
    }

    void setLooping(boolean loop);


    boolean isLooping();

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

    default String getUrl() {
        return mUrl[0];
    }

    default void setUrl(String url) {
        mUrl[0] = url;
    }

    default long getSeek() {
        MediaLogUtil.log("KernelApi => getSeek => seek = " + mSeek[0]);
        if (null == mSeek[0] || mSeek[0] < 0) {
            return 0L;
        } else {
            return mSeek[0];
        }
    }

//    default void setSeek(long seek) {
//        if (seek < 0) {
//            seek = 0;
//        }
//        mSeek[0] = seek;
//    }

    default long getMax() {
        MediaLogUtil.log("KernelApi => getMax => max = " + mMax[0]);
        if (null == mMax[0] || mMax[0] < 0) {
            return 0L;
        } else {
            return mMax[0];
        }
    }

//    default void setMax(long max) {
//        if (max > 0) {
//            mMax[0] = max;
//        }
//    }
}