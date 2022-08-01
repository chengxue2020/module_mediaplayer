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
    Long[] mMaxLength = new Long[1];
    Integer[] mMaxNum = new Integer[1];
    Long[] mSeek = new Long[1];
    String[] mUrl = new String[1];
    MediaPlayer[] mMediaPlayer = new MediaPlayer[1];

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

    void setSurface(Surface surface);

    default void releaseMusic() {
        stopMusic();
        if (null != mMediaPlayer[0]) {
            mMediaPlayer[0].release();
            mMediaPlayer[0] = null;
        }
    }

    default void stopMusic() {
        try {
            if (mMediaPlayer[0].isLooping()) {
                mMediaPlayer[0].setLooping(false);
            }
            if (mMediaPlayer[0].isPlaying()) {
                mMediaPlayer[0].stop();
            }
            mMediaPlayer[0].reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    default void toggleMusic(@NonNull Context context, @NonNull String music) {

        if (null == mMediaPlayer[0]) {
            mMediaPlayer[0] = new MediaPlayer();
        }

        // 视频音源
        if (null != music && music.length() > 0) {

            // pause
            if (mMediaPlayer[0].isPlaying()) {
                stopMusic();
                pause();
                toggleMusic(context, music);
            }
            // next
            else {
                try {
                    stopMusic();
                    pause();
                    mMediaPlayer[0].setDataSource(context, Uri.parse(music));
                    mMediaPlayer[0].prepare();
                    mMediaPlayer[0].setOnPreparedListener(null);
                    mMediaPlayer[0].start();
                    long position = getPosition();
                    mMediaPlayer[0].seekTo((int) position);
                    mMediaPlayer[0].setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            setVolume(0, 0);
                            start();
                        }
                    });
                    mMediaPlayer[0].setOnErrorListener(null);
                    mMediaPlayer[0].setOnErrorListener(new MediaPlayer.OnErrorListener() {
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

    default void create(@NonNull Context context, @NonNull long seek, @NonNull long maxLength, @NonNull int maxNum, @NonNull String url) {
        MediaLogUtil.log("K_LOG => create => seek = " + seek + ", maxLength = " + maxLength + ", maxNum = " + maxNum + ", url = " + url);
        init(context, seek, maxLength, maxNum, url);
    }

    default void init(@NonNull Context context, @NonNull long seek, @NonNull long maxLength, @NonNull int maxNum, @NonNull String url) {
        mSeek[0] = seek;
        mMaxNum[0] = maxNum;
        mMaxLength[0] = maxLength;
        mUrl[0] = url;
    }

    default void update(@NonNull long seek, @NonNull long maxLength, @NonNull int maxNum) {
        mSeek[0] = seek;
        mMaxNum[0] = maxNum;
        mMaxLength[0] = maxLength;
    }

    default void update(@NonNull long maxLength, @NonNull int maxNum) {
        mMaxNum[0] = maxNum;
        mMaxLength[0] = maxLength;
    }

    /*----------------------------第二部分：视频播放器状态方法----------------------------------*/

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
     * 设置渲染视频的View,主要用于SurfaceView
     *
     * @param holder holder
     */
    void setDisplay(SurfaceHolder holder);

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

    /**
     * 设置是否循环播放
     *
     * @param isLooping 布尔值
     */
    void setLooping(boolean isLooping);

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
        if (null == mSeek[0]) {
            return 0;
        } else {
            return mSeek[0];
        }
    }

    default void setSeek(long seek) {
        mSeek[0] = seek;
    }

    default long getMaxLength() {
        if (null == mMaxLength[0]) {
            return 0;
        } else {
            return mMaxLength[0];
        }
    }

    default void setMaxLength(long max) {
        mMaxLength[0] = max;
    }

    default int getMaxNum() {
        if (null == mMaxNum[0]) {
            return 0;
        } else {
            return mMaxNum[0];
        }
    }

    default void setMaxNum(int num) {
        mMaxNum[0] = num;
    }
}