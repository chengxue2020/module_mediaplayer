package lib.kalu.mediaplayer.core.controller;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.core.controller.base.ControllerLayoutDispatchTouchEvent;
import lib.kalu.mediaplayer.core.controller.component.ComponentEnd;
import lib.kalu.mediaplayer.core.controller.component.ComponentError;
import lib.kalu.mediaplayer.core.controller.component.ComponentFloat;

/**
 * desc  : 悬浮播放控制器
 */
public class ControllerFloat extends ControllerLayoutDispatchTouchEvent {

    public ControllerFloat(@NonNull Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public ControllerFloat(@NonNull Context context, @Nullable AttributeSet attrs) {
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
        addComponent(new ComponentEnd(getContext()));
        addComponent(new ComponentError(getContext()));
        addComponent(new ComponentFloat(getContext()));
    }
}
