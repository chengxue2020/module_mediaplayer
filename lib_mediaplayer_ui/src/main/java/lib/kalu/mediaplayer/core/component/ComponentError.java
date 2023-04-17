package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.util.MPLogUtil;

public final class ComponentError extends RelativeLayout implements ComponentApi {

    public ComponentError(Context context) {
        super(context);
        init();
    }

    public ComponentError(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ComponentError(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_error, this, true);
    }

    @Override
    public void callPlayerEvent(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_ERROR_NET:
            case PlayerType.StateType.STATE_ERROR:
            case PlayerType.StateType.STATE_ERROR_IGNORE:
                MPLogUtil.log("ComponentError[show] => playState = " + playState);
                show();
                break;
            case PlayerType.StateType.STATE_KERNEL_STOP:
            case PlayerType.StateType.STATE_INIT:
            case PlayerType.StateType.STATE_START:
            case PlayerType.StateType.STATE_START_SEEK:
            case PlayerType.StateType.STATE_RESUME:
            case PlayerType.StateType.STATE_RESUME_IGNORE:
            case PlayerType.StateType.STATE_RESTAER:
                MPLogUtil.log("ComponentError[gone] => playState = " + playState);
                gone();
                break;
        }
    }

    @Override
    public void show() {
        try {
            bringToFront();
            findViewById(R.id.module_mediaplayer_component_error_bg).setVisibility(View.VISIBLE);
            findViewById(R.id.module_mediaplayer_component_error_message).setVisibility(View.VISIBLE);
        }catch (Exception e){
        }
    }

    @Override
    public void gone() {
        try {
            findViewById(R.id.module_mediaplayer_component_error_bg).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_error_message).setVisibility(View.GONE);
        }catch (Exception e){
        }
    }

    public void setImage(@DrawableRes int res) {
        this.setCompoundDrawablesWithIntrinsicBounds(this, R.id.module_mediaplayer_component_error_message, 0, res, 0, 0);
    }

    public void setMessage(@NonNull String value) {
        this.setText(this, R.id.module_mediaplayer_component_error_message, value);
    }

    public void setMessage(@StringRes int value) {
        this.setText(this, R.id.module_mediaplayer_component_error_message, value);
    }

    public void setMessageSize(@DimenRes int value) {
        this.setTextSize(this, R.id.module_mediaplayer_component_error_message, value);
    }

    public void setComponentBackgroundColorInt(@ColorInt int value) {
        setBackgroundColorInt(this, R.id.module_mediaplayer_component_error_bg, value);
    }
}
