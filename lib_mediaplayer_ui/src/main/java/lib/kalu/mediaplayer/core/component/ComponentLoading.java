package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.player.PlayerApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

public final class ComponentLoading extends RelativeLayout implements ComponentApi {

    public ComponentLoading(@NonNull Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_loading, this, true);
    }

    @Override
    public void callPlayerEvent(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_LOADING_START:
                MPLogUtil.log("ComponentLoading => callPlayerEvent => show => playState = " + playState);
                show();
                break;
            case PlayerType.StateType.STATE_LOADING_STOP:
                MPLogUtil.log("ComponentLoading => callPlayerEvent => gone => playState = " + playState);
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
            PlayerApi playerApi = getPlayerApi();
            if(null == playerApi)
                throw new Exception("playerApi warning: null");
            boolean full = playerApi.isFull();
            findViewById(R.id.module_mediaplayer_component_loading_message).setVisibility(full ? View.VISIBLE : View.INVISIBLE);
        } catch (Exception e) {
            MPLogUtil.log("ComponentLoading => show => "+e.getMessage());
        }
    }

    @Override
    public void gone() {
        try {
            findViewById(R.id.module_mediaplayer_component_loading_bg).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_loading_pb).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_loading_message).setVisibility(View.GONE);
        } catch (Exception e) {
            MPLogUtil.log("ComponentLoading => gone => "+e.getMessage());
        }
    }

    /*************/

    @Override
    public void setComponentBackgroundColorInt(int value) {
        try {
            setBackgroundColorInt(this, R.id.module_mediaplayer_component_loading_bg, value);
        } catch (Exception e) {
        }
    }

    @Override
    public void setComponentBackgroundResource(int resid) {
        try {
            setBackgroundDrawableRes(this, R.id.module_mediaplayer_component_loading_bg, resid);
        } catch (Exception e) {
        }
    }

    @Override
    public void setComponentImageResource(int resid) {
        try {
            setImageResource(this, R.id.module_mediaplayer_component_loading_bg, resid);
        } catch (Exception e) {
        }
    }

    @Override
    public void setComponentImageUrl(@NonNull String url) {
        try {
            setImageUrl(this, R.id.module_mediaplayer_component_loading_bg, url);
        } catch (Exception e) {
        }
    }

    @Override
    public void setComponentText(int value) {
        try {
            setText(this, R.id.module_mediaplayer_component_loading_message, value);
        } catch (Exception e) {
        }
    }

    @Override
    public void setComponentText(@NonNull String value) {
        try {
            setText(this, R.id.module_mediaplayer_component_loading_message, value);
        } catch (Exception e) {
        }
    }

    @Override
    public void setComponentTextSize(int value) {
        try {
            setTextSize(this, R.id.module_mediaplayer_component_loading_message, value);
        } catch (Exception e) {
        }
    }

    @Override
    public void setComponentTextColor(int color) {
        try {
            setTextColor(this, R.id.module_mediaplayer_component_loading_message, color);
        } catch (Exception e) {
        }
    }
}
