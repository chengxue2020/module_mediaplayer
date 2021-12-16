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
package lib.kalu.mediaplayer.widget.player;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.util.Map;

import lib.kalu.mediaplayer.config.PlayerType;

/**
 * <pre>
 *     @author yangchong
 *     blog  : https://github.com/yangchong211
 *     time  : 2017/11/21
 *     desc  : VideoPlayer抽象接口
 *     revise: 播放器基础属性获取和设置属性接口
 * </pre>
 */
public interface InterVideoPlayer {

    default void start(@NonNull String url) {
        start(false, url, null);
    }

    default void start(@NonNull boolean live, @NonNull String url) {
        start(live, url, null);
    }

    /**
     * 开始播放
     *
     * @param live
     * @param url
     * @param headers
     */
    void start(@NonNull boolean live, @NonNull String url, @NonNull Map<String, String> headers);

    default void restart() {
        restart(false);
    }

    /**
     * 重新播放
     *
     * @param reset 是否从头开始播放
     */
    void restart(@NonNull boolean reset);

//    /**
//     * 获取播放链接
//     *
//     * @return 链接
//     */
//    String getUrl();

    /**
     * 暂停播放
     */
    void pause();

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
    long getCurrentPosition();

    /**
     * 调整播放进度
     *
     * @param pos 位置
     */
    void seekTo(long pos);

    /**
     * 是否处于播放状态
     *
     * @return 是否处于播放状态
     */
    boolean isPlaying();

    /**
     * 获取当前缓冲百分比
     *
     * @return 百分比
     */
    int getBufferedPercentage();

    void startFullScreen();

    void stopFullScreen();

    boolean isFullScreen();

    void setMute(boolean isMute);

    boolean isMute();

    void setScreenScaleType(@PlayerType.ScaleType.Value int screenScaleType);

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
}
