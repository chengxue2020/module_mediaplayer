package lib.kalu.mediaplayer.core.controller.component;

import android.content.Context;
import android.os.Message;
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
import lib.kalu.mediaplayer.util.TimerUtil;

public final class ComponentSpeed extends RelativeLayout implements ComponentApi {

    private static final int KEY = 9001;
    private static final int WHAT = 90011;

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

    private void startListener() {
        boolean containsListener = TimerUtil.getInstance().containsListener(KEY);
        MPLogUtil.log("ComponentSpeed => startListener => containsListener = " + containsListener);
        if (containsListener)
            return;
        TimerUtil.getInstance().registTimer(KEY, new TimerUtil.OnMessageChangeListener() {
            @Override
            public void onMessage(@NonNull Message msg) {
                MPLogUtil.log("ComponentSpeed => onMessage => what = " + msg.what);
                if (msg.what != WHAT)
                    return;
                sendMessage();
            }
        });
        sendMessage();
    }

    private void sendMessage() {
        // 1
        updateSpeed();
        // 2
        Message message = new Message();
        message.what = WHAT;
        TimerUtil.getInstance().sendMessageDelayed(message, 500);
    }

    private void updateSpeed() {
        try {
            String speed = mControllerWrapper.getTcpSpeed();
            int length = speed.length();
            int start = length - 4;
            String unit = speed.substring(start, length);
            String num = speed.substring(0, start);
            MPLogUtil.log("ComponentSpeed => updateSpeed => speed = " + speed);
            TextView textView1 = findViewById(R.id.module_mediaplayer_component_speed_txt);
            textView1.setText(num);
            TextView textView2 = findViewById(R.id.module_mediaplayer_component_speed_unit);
            textView2.setText(unit);
        } catch (Exception e) {
        }
    }

    private void stopListener() {
        TimerUtil.getInstance().clearMessage(WHAT);
        TimerUtil.getInstance().clearListener(KEY);
    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_BUFFERING_START:
                MPLogUtil.log("ComponentSpeed => onPlayStateChanged => playState = " + playState);
                // 1
                startListener();
                // 2
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
                // 1
                stopListener();
                // 2
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
