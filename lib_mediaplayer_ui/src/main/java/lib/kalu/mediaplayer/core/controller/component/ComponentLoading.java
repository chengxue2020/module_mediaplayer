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
import lib.kalu.mediaplayer.core.controller.impl.ImplComponent;
import lib.kalu.mediaplayer.util.MediaLogUtil;

public class ComponentLoading extends RelativeLayout implements ImplComponent {

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
        MediaLogUtil.log("ComponentLoading => playState = " + playState);
        switch (playState) {
            case PlayerType.StateType.STATE_ERROR_URL:
            case PlayerType.StateType.STATE_CLEAN:
            case PlayerType.StateType.STATE_ERROR:
            case PlayerType.StateType.STATE_ERROR_NETWORK:
            case PlayerType.StateType.STATE_ERROR_PARSE:
            case PlayerType.StateType.STATE_LOADING_COMPLETE:
                findViewById(R.id.module_mediaplayer_component_loading_bg).setVisibility(View.GONE);
                findViewById(R.id.module_mediaplayer_component_loading_pb).setVisibility(View.GONE);
                findViewById(R.id.module_mediaplayer_component_loading_message).setVisibility(View.GONE);
                break;
            case PlayerType.StateType.STATE_LOADING_START:
                findViewById(R.id.module_mediaplayer_component_loading_bg).setVisibility(View.VISIBLE);
                findViewById(R.id.module_mediaplayer_component_loading_pb).setVisibility(View.VISIBLE);
                findViewById(R.id.module_mediaplayer_component_loading_message).setVisibility(View.VISIBLE);
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

    public final void setBackgroundColor(@ColorInt int value) {
        setBackgroundColor(this, R.id.module_mediaplayer_component_loading_bg, value);
    }
}
