package lib.kalu.mediaplayer.core.kernel.impl;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

import lib.kalu.mediaplayer.core.kernel.video.listener.OnVideoPlayerChangeListener;


/**
 * @description: 播放器 - 抽象接口
 * @date: 2021-05-12 09:40
 */
@Keep
public interface ImplKernel {

    /*----------------------------第一部分：视频初始化实例对象方法----------------------------------*/

    @NonNull
    <T extends Object> T getPlayer();

    void initKernel(@NonNull Context context);

    void resetKernel();

    void releaseKernel();

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

    /**
     * 设置渲染视频的View,主要用于TextureView
     * 视频播放器第三步：设置surface
     *
     * @param surface surface
     */
    void setSurface(Surface surface);

    /**
     * 准备开始播放（异步）
     * 视频播放器第四步：开始加载【异步】
     */
    void prepare(@NonNull Context context, @NonNull long seek, @NonNull CharSequence url, @Nullable Map<String, String> headers);

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
    void setVolume(float v1, float v2);

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

    /***********************************************/

    /**
     * 绑定VideoView，监听播放异常，完成，开始准备，视频size变化，视频信息等操作
     */
    @Nullable
    void setOnVideoPlayerChangeListener(@Nullable OnVideoPlayerChangeListener onVideoPlayerChangeListener);
}

