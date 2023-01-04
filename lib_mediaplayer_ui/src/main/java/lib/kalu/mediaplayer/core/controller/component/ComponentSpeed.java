package lib.kalu.mediaplayer.core.controller.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

    private ControllerWrapper mControllerWrapper;

    @Override
    public void attach(@NonNull ControllerWrapper wrapper) {
        mControllerWrapper = wrapper;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {
    }

    private long mUpdateTimeMillis = 0L;

    @Override
    public void onUpdateTimeMillis(@NonNull long seek, @NonNull long position, @NonNull long duration) {

        if (mUpdateTimeMillis > 0L) {
            long timeMillis = System.currentTimeMillis();
            long value = timeMillis - mUpdateTimeMillis;
            MPLogUtil.log("ComponentSpeed => onUpdateTimeMillis => value = " + value);
            if (value <= 1000) {
                return;
            } else {
                mUpdateTimeMillis = timeMillis;
            }
        }

        View v = findViewById(R.id.module_mediaplayer_component_speed_bg);
        int visibility = v.getVisibility();
        MPLogUtil.log("ComponentSpeed => onUpdateTimeMillis => visibility = " + visibility);
        if (visibility != View.VISIBLE)
            return;

        try {
            String speed = mControllerWrapper.getTcpSpeed();
            int length = speed.length();
            int start = length - 4;
            String unit = speed.substring(start, length);
            String num = speed.substring(0, start);
            MPLogUtil.log("ComponentSpeed => updateSpeed => speed = " + speed);
            TextView v1 = findViewById(R.id.module_mediaplayer_component_speed_txt);
            v1.setText(num);
            TextView v2 = findViewById(R.id.module_mediaplayer_component_speed_unit);
            v2.setText(unit);
        } catch (Exception e) {
        }
    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_BUFFERING_START:
                MPLogUtil.log("ComponentSpeed => onPlayStateChanged => playState = " + playState);
                bringToFront();
                findViewById(R.id.module_mediaplayer_component_speed_bg).setVisibility(View.VISIBLE);
                findViewById(R.id.module_mediaplayer_component_speed_pb).setVisibility(View.VISIBLE);
                findViewById(R.id.module_mediaplayer_component_speed_txt).setVisibility(View.VISIBLE);
                findViewById(R.id.module_mediaplayer_component_speed_unit).setVisibility(View.VISIBLE);
                break;
            case PlayerType.StateType.STATE_INIT:
            case PlayerType.StateType.STATE_LOADING_START:
            case PlayerType.StateType.STATE_BUFFERING_STOP:
                MPLogUtil.log("ComponentSpeed => onPlayStateChanged => playState = " + playState);
                findViewById(R.id.module_mediaplayer_component_speed_bg).setVisibility(View.GONE);
                findViewById(R.id.module_mediaplayer_component_speed_pb).setVisibility(View.GONE);
                findViewById(R.id.module_mediaplayer_component_speed_txt).setVisibility(View.GONE);
                findViewById(R.id.module_mediaplayer_component_speed_unit).setVisibility(View.GONE);
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
