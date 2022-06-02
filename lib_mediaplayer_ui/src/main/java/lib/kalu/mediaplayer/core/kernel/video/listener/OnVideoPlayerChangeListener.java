package lib.kalu.mediaplayer.core.kernel.video.listener;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;

/**
 * @description: 播放器event监听
 * @date: 2021-05-12 09:47
 */
public interface OnVideoPlayerChangeListener {

    /**
     * 视频信息
     *
     * @param what  what
     * @param extra extra
     */
    void onInfo(@PlayerType.MediaType.Value int what, @NonNull int extra, @NonNull long position, @NonNull long duration);

    /**
     * 异常
     * 1          表示错误的链接
     * 2          表示解析异常
     * 3          表示其他的异常
     *
     * @param type 错误类型
     */
    void onError(@PlayerType.ErrorType.Value int type, String error);

    /**
     * 完成
     */
    void onCompletion();

    /**
     * 准备
     */
    void onPrepared(@NonNull long seek, @NonNull long duration);

    /**
     * 视频size变化监听
     *
     * @param width  宽
     * @param height 高
     */
    void onSize(@NonNull int width, @NonNull int height);
}
