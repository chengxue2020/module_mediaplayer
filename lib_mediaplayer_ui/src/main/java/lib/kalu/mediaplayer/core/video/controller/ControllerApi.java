package lib.kalu.mediaplayer.core.video.controller;

import androidx.annotation.NonNull;

public interface ControllerApi {

    /**
     * 初始化
     */
    void init();

    /**
     * 控制器意外销毁，比如手动退出，意外崩溃等等
     */
    void destroy();

    /**
     * 设置控制器布局文件，子类必须实现
     */
    int initLayout();

    /*******************/

    /**
     * 显示播放控制菜单
     */
    void hide();

    /**
     * 隐藏控制视图
     */
    void show();

    /*******************/

    /**
     * 开始控制视图自动隐藏倒计时
     */
    void startFadeOut();

    /**
     * 取消控制视图自动隐藏倒计时
     */
    void stopFadeOut();

    /**
     * 控制视图是否处于显示状态
     */
    boolean isShowing();

    /**
     * 设置锁定状态
     *
     * @param locked 是否锁定
     */
    void setLocked(boolean locked);

    /**
     * 是否处于锁定状态
     */
    boolean isLocked();

    /**
     * 是否需要适配刘海
     */
    boolean hasCutout();

    /**
     * 获取刘海的高度
     */
    int getCutoutHeight();

    void seekForwardDown(boolean enable);

    void seekForwardUp(boolean enable);

    void seekRewindDown(boolean enable);

    void seekRewindUp(boolean enable);

    default void onUpdateTimeMillis(@NonNull long seek, @NonNull long position, @NonNull long duration) {
    }
}
