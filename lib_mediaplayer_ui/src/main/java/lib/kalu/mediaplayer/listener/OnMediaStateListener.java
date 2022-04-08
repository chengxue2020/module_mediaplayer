package lib.kalu.mediaplayer.listener;

import androidx.annotation.Keep;

import lib.kalu.mediaplayer.config.PlayerType;

@Keep
public interface OnMediaStateListener {

    /**
     * 播放模式
     * 普通模式，小窗口模式，正常模式三种其中一种
     *
     * @param state 播放模式
     */
    default void onWindowStateChanged(@PlayerType.WindowType.Value int state) {
    }

    /**
     * 播放状态
     *
     * @param state 播放状态，主要是指播放器的各种状态
     */
    default void onPlayStateChanged(@PlayerType.StateType.Value int state) {
    }
}
