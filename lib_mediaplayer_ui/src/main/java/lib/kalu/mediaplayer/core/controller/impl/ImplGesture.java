package lib.kalu.mediaplayer.core.controller.impl;

public interface ImplGesture extends ImplComponent {

    /**
     * 开始滑动
     */
    void onStartSlide();

    /**
     * 结束滑动
     * 这个是指，手指抬起或者意外结束事件的时候，调用这个方法
     */
    void onStopSlide();

    /**
     * 滑动调整进度
     *
     * @param slidePosition   滑动进度
     * @param currentPosition 当前播放进度
     * @param duration        视频总长度
     */
    void onPositionChange(int slidePosition, int currentPosition, int duration);

    /**
     * 滑动调整亮度
     *
     * @param percent 亮度百分比
     */
    void onBrightnessChange(int percent);

    /**
     * 滑动调整音量
     *
     * @param percent 音量百分比
     */
    void onVolumeChange(int percent);
}
