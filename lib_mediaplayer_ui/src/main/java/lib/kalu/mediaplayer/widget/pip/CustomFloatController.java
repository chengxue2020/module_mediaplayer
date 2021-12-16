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
package lib.kalu.mediaplayer.widget.pip;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.widget.controller.ControllerLayoutDispatchTouchEvent;
import lib.kalu.mediaplayer.widget.CustomCompleteView;
import lib.kalu.mediaplayer.widget.CustomErrorView;

/**
 * <pre>
 *     @author yangchong
 *     blog  : https://github.com/yangchong211
 *     time  : 2017/11/9
 *     desc  : 悬浮播放控制器
 *     revise:
 * </pre>
 */
public class CustomFloatController extends ControllerLayoutDispatchTouchEvent {

    public CustomFloatController(@NonNull Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public CustomFloatController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }


    @Override
    public void destroy() {

    }

    @Override
    public int initLayout() {
        return 0;
    }

    @Override
    public void init() {
        super.init();
        addControlComponent(new CustomCompleteView(getContext()));
        addControlComponent(new CustomErrorView(getContext()));
        addControlComponent(new CustomFloatView(getContext()));
    }
}
