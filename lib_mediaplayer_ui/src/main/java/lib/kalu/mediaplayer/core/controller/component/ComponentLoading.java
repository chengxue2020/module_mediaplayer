package lib.kalu.mediaplayer.core.controller.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.core.controller.base.ControllerWrapper;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.controller.impl.ComponentApi;
import lib.kalu.mediaplayer.util.MediaLogUtil;

public final class ComponentLoading extends RelativeLayout implements ComponentApi {

    private ControllerWrapper mControllerWrapper;

    public ComponentLoading(@NonNull Context context) {
        super(context);
        init();
    }

    public ComponentLoading(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ComponentLoading(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_loading, this, true);
    }

    @Override
    public void attach(@NonNull ControllerWrapper controllerWrapper) {
        mControllerWrapper = controllerWrapper;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {
    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_LOADING_START:
                MediaLogUtil.log("ComponentLoading[show] => playState = " + playState);
                bringToFront();
                setVisibility(View.VISIBLE);
                break;
            case PlayerType.StateType.STATE_LOADING_STOP:
                MediaLogUtil.log("ComponentLoading[gone] => playState = " + playState);
                setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onWindowStateChanged(int playerState) {
    }

    @Override
    public void setProgress(int duration, int position) {
    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
    }

    public final void setMessage(@NonNull String value) {
        setText(this, R.id.module_mediaplayer_component_loading_message, value);
    }

    public final void setMessage(@StringRes int value) {
        setText(this, R.id.module_mediaplayer_component_loading_message, value);
    }

    public final void setMessageSize(@DimenRes int value) {
        setTextSize(this, R.id.module_mediaplayer_component_loading_message, value);
    }

    public final void setMessageColor(@ColorInt int color) {
        setTextColor(this, R.id.module_mediaplayer_component_loading_message, color);
    }

    public final void setBackgroundColor(@ColorInt int value) {
//        setBackgroundColor(this, R.id.module_mediaplayer_component_loading_bg, value);
    }
}
