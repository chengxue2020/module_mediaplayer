/*
Copyright 2017 yangchong211（github.com/yangchong211）

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package lib.kalu.mediaplayer.listener;


import androidx.annotation.Keep;

import lib.kalu.mediaplayer.ui.config.PlayerType;

/**
 * <pre>
 *     @author yangchong
 *     blog  : https://github.com/yangchong211
 *     time  : 2017/11/9
 *     desc  : 视频播放器状态监听
 *     revise:
 * </pre>
 */
@Keep
public interface OnVideoStateListener {

    /**
     * 播放模式
     * 普通模式，小窗口模式，正常模式三种其中一种
     *
     * @param playerState 播放模式
     */
    default void onWindowStateChanged(@PlayerType.WindowType.Value int playerState) {
    }

    /**
     * 播放状态
     *
     * @param playState 播放状态，主要是指播放器的各种状态
     */
    default void onPlayStateChanged(@PlayerType.StateType.Value int playState) {
    }
}
