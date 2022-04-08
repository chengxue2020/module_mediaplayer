package lib.kalu.mediaplayer.core.controller.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.core.controller.base.ControllerWrapper;
import lib.kalu.mediaplayer.config.PlayerConfigManager;
import lib.kalu.mediaplayer.config.PlayerType;
import lib.kalu.mediaplayer.core.controller.impl.ImplComponent;
import lib.kalu.mediaplayer.util.MediaLogUtil;

public class ComponentPrepare extends FrameLayout implements ImplComponent {

    private ControllerWrapper mControllerWrapper;

    public ComponentPrepare(@NonNull Context context) {
        super(context);
        init();
    }

    public ComponentPrepare(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ComponentPrepare(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_video_prepare, this, true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setVisibility(View.VISIBLE);

        findViewById(R.id.tv_start).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.fl_net_warning).setVisibility(GONE);
                PlayerConfigManager.instance().setPlayOnMobileNetwork(true);
                mControllerWrapper.repeat();
            }
        });
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
        MediaLogUtil.log("ComponentPrepare => onPlayStateChanged => playState = " + playState);
        View viewPlay = findViewById(R.id.module_mediaplayer_controller_prepare_play);
        View viewBackground = findViewById(R.id.module_mediaplayer_controller_prepare_background);
        View viewProgress = findViewById(R.id.module_mediaplayer_controller_prepare_progress);
        View viewTip = findViewById(R.id.module_mediaplayer_controller_prepare_tip);
        switch (playState) {
            case PlayerType.StateType.STATE_PREPARE_START:
                bringToFront();
                setVisibility(VISIBLE);
                viewPlay.setVisibility(View.GONE);
                findViewById(R.id.fl_net_warning).setVisibility(GONE);
                viewProgress.setVisibility(View.VISIBLE);
                viewTip.setVisibility(View.VISIBLE);
                break;
            case PlayerType.StateType.STATE_START:
            case PlayerType.StateType.STATE_PAUSED:
            case PlayerType.StateType.STATE_ERROR:
            case PlayerType.StateType.STATE_BUFFERING_PAUSED:
            case PlayerType.StateType.STATE_BUFFERING_COMPLETE:
            case PlayerType.StateType.STATE_END:
            case PlayerType.StateType.STATE_BUFFERING_PLAYING:
            case PlayerType.StateType.STATE_ONCE_LIVE:
            case PlayerType.StateType.STATE_PREPARE_END:
                viewProgress.setVisibility(View.GONE);
                setVisibility(GONE);
                break;
            case PlayerType.StateType.STATE_INIT:
                setVisibility(VISIBLE);
                bringToFront();
                viewProgress.setVisibility(View.GONE);
                viewTip.setVisibility(View.GONE);
                findViewById(R.id.fl_net_warning).setVisibility(GONE);
                viewPlay.setVisibility(View.GONE);
                viewBackground.setVisibility(View.VISIBLE);
                break;
            case PlayerType.StateType.STATE_START_ABORT:
                setVisibility(VISIBLE);
                findViewById(R.id.fl_net_warning).setVisibility(VISIBLE);
                findViewById(R.id.fl_net_warning).bringToFront();
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
}
