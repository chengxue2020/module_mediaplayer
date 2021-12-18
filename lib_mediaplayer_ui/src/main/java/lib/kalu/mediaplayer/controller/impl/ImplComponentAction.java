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
package lib.kalu.mediaplayer.controller.impl;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;


/**
 * <pre>
 *     @author yangchong
 *     blog  : https://github.com/yangchong211
 *     time  : 2017/11/9
 *     desc  : 自定义控制器接口
 *     revise: 如果需要添加自定义播放器视图，则需要继承InterControlView接口
 *             关于视频播放器播放状态和视图状态，都需要自定义视图去控制view的状态
 *             举一个例子：比如广告视图，
 * </pre>
 */
@Keep
public interface ImplComponentAction {

    /**
     * 添加控制组件，最后面添加的在最下面，合理组织添加顺序，可让ControlComponent位于不同的层级
     *
     * @param views
     */
    default void addComponent(@NonNull ImplComponent... views) {
        if (null == views)
            return;
        for (ImplComponent temp : views) {
            addComponent(temp, false);
        }
    }

    void addComponent(@NonNull ImplComponent controlView, @NonNull boolean isPrivate);

    void removeComponent(@NonNull ImplComponent view);

    void removeComponentAll(boolean isPrivate);
}
