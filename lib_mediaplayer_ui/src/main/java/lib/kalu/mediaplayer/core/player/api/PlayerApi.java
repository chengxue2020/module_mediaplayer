package lib.kalu.mediaplayer.core.player.api;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.controller.base.ControllerLayout;

/**
 * revise: 播放器基础属性获取和设置属性接口
 */
public interface PlayerApi extends PlayerApiBase,
        PlayerApiComponent,
        PlayerApiSuface,
        PlayerApiExternal,
        PlayerApiCache,
        PlayerApiWindow,
        PlayerApiDispatchKeyEvent {

    /*********************/

    void stopKernel(@NonNull boolean call);

    void pauseKernel(@NonNull boolean call);

    void releaseKernel();

    /*********************/

    void setScaleType(@PlayerType.ScaleType.Value int scaleType);

    void setSpeed(float speed);

    float getSpeed();

    String getTcpSpeed();

    void setMirrorRotation(boolean enable);

    int[] getVideoSize();

    void setRotation(float rotation);

    ControllerLayout getControlLayout();

    void showReal();

    void hideReal();

    void checkReal();

    void addRender();

    void delRender();

    void releaseRender();

    default void release() {
        release(false);
    }

    void release(@NonNull boolean onlyHandle);

//    void startTimer();

//    void clearTimer();

    /*********/

    void playEnd();

    /*********/

    void setKernel(@PlayerType.KernelType.Value int v);

    void setRender(@PlayerType.RenderType int v);
}
