package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.player.PlayerApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

public class ComponentLoading extends RelativeLayout implements ComponentApi {

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
    public void callPlayerEvent(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_PAUSE_IGNORE:
                MPLogUtil.log("ComponentLoading => onPlayStateChanged => playState = " + playState);
                try {
                    bringToFront();
                    findViewById(R.id.module_mediaplayer_component_loading_bg).setVisibility(View.VISIBLE);
                    findViewById(R.id.module_mediaplayer_component_loading_pb).setVisibility(View.GONE);
                    findViewById(R.id.module_mediaplayer_component_loading_message).setVisibility(View.GONE);
                } catch (Exception e) {
                }
                break;
            case PlayerType.StateType.STATE_LOADING_START:
                MPLogUtil.log("ComponentLoading => onPlayStateChanged => playState = " + playState);
                show();
                break;
            case PlayerType.StateType.STATE_INIT:
            case PlayerType.StateType.STATE_ERROR:
            case PlayerType.StateType.STATE_LOADING_STOP:
                MPLogUtil.log("ComponentLoading => onPlayStateChanged => playState = " + playState);
                gone();
                break;
        }
    }

    @Override
    public void callWindowEvent(int state) {
        switch (state) {
            case PlayerType.WindowType.FLOAT:
            case PlayerType.WindowType.NORMAL:
            case PlayerType.WindowType.FULL:
                try {
                    int visibility = findViewById(R.id.module_mediaplayer_component_loading_pb).getVisibility();
                    if (visibility == View.VISIBLE) {
                        try {
                            PlayerApi playerApi = getPlayerApi();
                            boolean full = playerApi.isFull();
                            findViewById(R.id.module_mediaplayer_component_loading_message).setVisibility(full ? View.VISIBLE : View.INVISIBLE);
                        } catch (Exception e) {
                            findViewById(R.id.module_mediaplayer_component_loading_message).setVisibility(View.INVISIBLE);
                        }
                    }
                } catch (Exception e) {
                }
                break;
        }
    }

    @Override
    public void show() {
        try {
            bringToFront();
            findViewById(R.id.module_mediaplayer_component_loading_bg).setVisibility(View.VISIBLE);
            findViewById(R.id.module_mediaplayer_component_loading_pb).setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }

        try {
            PlayerApi playerApi = getPlayerApi();
            boolean full = playerApi.isFull();
            findViewById(R.id.module_mediaplayer_component_loading_message).setVisibility(full ? View.VISIBLE : View.INVISIBLE);
        } catch (Exception e) {
            findViewById(R.id.module_mediaplayer_component_loading_message).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void gone() {
        try {
            findViewById(R.id.module_mediaplayer_component_loading_bg).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_loading_pb).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_loading_message).setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }

    public void setMessage(@NonNull String value) {
        setText(this, R.id.module_mediaplayer_component_loading_message, value);
    }

    public void setMessage(@StringRes int value) {
        setText(this, R.id.module_mediaplayer_component_loading_message, value);
    }

    public void setMessageSize(@DimenRes int value) {
        setTextSize(this, R.id.module_mediaplayer_component_loading_message, value);
    }

    public void setMessageColor(@ColorInt int color) {
        setTextColor(this, R.id.module_mediaplayer_component_loading_message, color);
    }

    public void setComponentBackgroundColorInt(@ColorInt int value) {
        setBackgroundColorInt(this, R.id.module_mediaplayer_component_loading_bg, value);
    }

    public void setComponentBackgroundResource(@DrawableRes int resid) {
        setBackgroundDrawableRes(this, R.id.module_mediaplayer_component_loading_bg, resid);
    }
}
