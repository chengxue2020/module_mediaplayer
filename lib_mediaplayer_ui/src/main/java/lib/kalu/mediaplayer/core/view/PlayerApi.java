package lib.kalu.mediaplayer.core.view;

import android.graphics.Bitmap;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.Map;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.controller.base.ControllerLayout;

/**
 * revise: 播放器基础属性获取和设置属性接口
 */
public interface PlayerApi {

    default void start(@NonNull String url, @NonNull String subtitle) {
        start(0, false, url, subtitle, null);
    }

    default void start(@NonNull String url) {
        start(0, false, url, null, null);
    }

    default void start(@NonNull long seek, @NonNull String url) {
        start(seek, false, url, null, null);
    }

    default void start(@NonNull boolean live, @NonNull String url) {
        start(0, live, url, null, null);
    }

    default void start(@NonNull boolean live, @NonNull String url, @NonNull String subtitle) {
        start(0, live, url, subtitle, null);
    }

    /**
     * 开始播放
     *
     * @param live
     * @param url
     * @param headers
     */
    void start(@NonNull long seek, @NonNull boolean live, @NonNull String url, @NonNull String subtitle, @NonNull Map<String, String> headers);

    void pause();

    void stop();

    void resume();

    void repeat();

    /**
     * 获取视频总时长
     *
     * @return long类型
     */
    long getDuration();


    /**
     * 获取当前播放的位置
     *
     * @return long类型
     */
    long getPosition();

    /**
     * 获取当前缓冲百分比
     *
     * @return 百分比
     */
    int getBufferedPercentage();

    /**
     * 调整播放进度
     *
     * @param pos 位置 毫秒 => System.currentTimeMillis(), getTime()
     */
    void seekTo(long pos);

    /**
     * 是否处于播放状态
     *
     * @return 是否处于播放状态
     */
    boolean isPlaying();

    void startFullScreen();

    void stopFullScreen();

    boolean isFullScreen();

    void setMute(boolean isMute);

    boolean isMute();

    void setScaleType(@PlayerType.ScaleType.Value int scaleType);

    void setSpeed(float speed);

    float getSpeed();

    long getTcpSpeed();

    void setMirrorRotation(boolean enable);

    Bitmap doScreenShot();

    int[] getVideoSize();

    void setRotation(float rotation);

    void startTinyScreen();

    void stopTinyScreen();

    boolean isTinyScreen();

    ControllerLayout getControlLayout();

    View getVideoLayout();

    void initRender();

    void initKernel();
//    void resetKernel();

    void releaseKernel();
}
