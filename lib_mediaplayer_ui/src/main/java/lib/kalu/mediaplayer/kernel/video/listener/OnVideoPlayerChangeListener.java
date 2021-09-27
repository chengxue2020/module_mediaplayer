package lib.kalu.mediaplayer.kernel.video.listener;

import lib.kalu.mediaplayer.kernel.video.utils.PlayerConstant;

/**
 * @description: 播放器event监听
 * @date:  2021-05-12 09:47
 */
public interface OnVideoPlayerChangeListener {

    /**
     * 异常
     * 1          表示错误的链接
     * 2          表示解析异常
     * 3          表示其他的异常
     * @param type                          错误类型
     */
    void onError(@PlayerConstant.ErrorType int type , String error);

    /**
     * 完成
     */
    void onCompletion();

    /**
     * 视频信息
     * @param what                          what
     * @param extra                         extra
     */
    void onInfo(int what, int extra);

    /**
     * 准备
     */
    void onPrepared();

    /**
     * 视频size变化监听
     * @param width                         宽
     * @param height                        高
     */
    void onVideoSizeChanged(int width, int height);


}
