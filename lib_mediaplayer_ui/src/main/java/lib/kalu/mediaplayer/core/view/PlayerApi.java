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

    default void start(@NonNull String url) {
        start(0, 0, 0, url);
    }

    default void start(@NonNull long seek, @NonNull String url) {
        start(seek, 0, 0, url);
    }

    /**
     * 开始播放
     *
     * @param seek
     * @param maxLength
     * @param url
     */
    void start(@NonNull long seek, @NonNull long maxLength, @NonNull int maxNum, @NonNull String url);

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

    long getSeek();

    long getMaxLength();

    int getMaxNum();

    String getUrl();

    /**
     * 获取当前缓冲百分比
     *
     * @return 百分比
     */
    int getBufferedPercentage();

    default void seekTo(@NonNull long seek) {
        seekTo(false, seek, 0, 0);
    }

    default void seekTo(@NonNull boolean force, @NonNull long seek) {
        seekTo(force, seek, 0, 0);
    }

    void seekTo(@NonNull boolean force, @NonNull long seek, @NonNull long maxLength, @NonNull int maxNum);

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

    void showReal();

    void goneReal();

//    View getReal();

    void create();

    default void release() {
        releaseRender();
        releaseKernel();
    }

    void releaseKernel();

    void releaseRender();

    void startLoop();

    void clearLoop();

    /*********/

    void playEnd();
}
