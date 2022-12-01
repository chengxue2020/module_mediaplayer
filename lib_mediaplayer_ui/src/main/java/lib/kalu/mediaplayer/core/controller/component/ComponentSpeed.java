package lib.kalu.mediaplayer.core.controller.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.controller.base.ControllerWrapper;
import lib.kalu.mediaplayer.core.controller.impl.ComponentApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

public final class ComponentSpeed extends RelativeLayout implements ComponentApi {
    public ComponentSpeed(Context context) {
        super(context);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_speed, this, true);
    }

    @Override
    public void attach(@NonNull ControllerWrapper controllerWrapper) {
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
            case PlayerType.StateType.STATE_BUFFERING_START:
                MPLogUtil.log("ComponentSpeed => onPlayStateChanged => playState = " + playState);
                bringToFront();
                findViewById(R.id.module_mediaplayer_component_speed_bg).setVisibility(View.VISIBLE);
                findViewById(R.id.module_mediaplayer_component_speed_pb).setVisibility(View.VISIBLE);
                break;
            case PlayerType.StateType.STATE_BUFFERING_STOP:
                MPLogUtil.log("ComponentSpeed => onPlayStateChanged => playState = " + playState);
                findViewById(R.id.module_mediaplayer_component_speed_bg).setVisibility(View.GONE);
                findViewById(R.id.module_mediaplayer_component_speed_pb).setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onWindowStateChanged(int playerState) {
    }

    @Override
    public void onLockStateChanged(boolean isLocked) {

    }
}
