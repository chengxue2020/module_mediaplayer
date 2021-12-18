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
package lib.kalu.mediaplayer.controller;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.widget.ImplController;

/**
 * <pre>
 *     @author yangchong
 *     blog  : https://github.com/yangchong211
 *     time  : 2017/11/21
 *     desc  : 视频控制器负责自定义视图add/remove操作
 *     revise:
 * </pre>
 */
public interface InterViewController {

    /**
     * 添加控制组件，最后面添加的在最下面，合理组织添加顺序，可让ControlComponent位于不同的层级
     *
     * @param views
     */
    default void add(@NonNull ImplController... views) {
        if (null == views)
            return;
        for (ImplController temp : views) {
            add(temp, false);
        }
    }

    void add(@NonNull ImplController controlView, @NonNull boolean isPrivate);

    void remove(@NonNull ImplController view);

    void removeAll(boolean isPrivate);
}
