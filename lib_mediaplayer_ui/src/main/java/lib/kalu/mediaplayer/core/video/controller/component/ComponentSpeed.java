package lib.kalu.mediaplayer.core.video.controller.component;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.video.controller.base.ControllerWrapper;
import lib.kalu.mediaplayer.core.video.controller.impl.ComponentApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

public final class ComponentSpeed extends RelativeLayout implements ComponentApi {

    private Handler mHandler = null;
    private long mUpdateTimeMillis = 0L;

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
                if (null == mHandler) {
                    mHandler = new Handler(Looper.myLooper()) {
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            super.handleMessage(msg);
                            if (null != msg && msg.what == 7677) {
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
                            mHandler.sendEmptyMessageDelayed(7677, 1000);
                        }
                    };
                    mHandler.sendEmptyMessage(7677);
                }
                break;
            case PlayerType.StateType.STATE_INIT:
            case PlayerType.StateType.STATE_LOADING_START:
            case PlayerType.StateType.STATE_BUFFERING_STOP:
                MPLogUtil.log("ComponentSpeed => onPlayStateChanged => playState = " + playState);
                findViewById(R.id.module_mediaplayer_component_speed_bg).setVisibility(View.GONE);
                findViewById(R.id.module_mediaplayer_component_speed_pb).setVisibility(View.GONE);
                findViewById(R.id.module_mediaplayer_component_speed_txt).setVisibility(View.GONE);
                findViewById(R.id.module_mediaplayer_component_speed_unit).setVisibility(View.GONE);
                if (null != mHandler) {
                    mHandler.removeMessages(7677);
                    mHandler = null;
                }
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
