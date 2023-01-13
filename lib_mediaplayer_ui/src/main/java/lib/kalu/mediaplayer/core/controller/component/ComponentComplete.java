package lib.kalu.mediaplayer.core.controller.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.controller.base.ControllerWrapper;
import lib.kalu.mediaplayer.core.controller.impl.ComponentApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

public class ComponentComplete extends RelativeLayout implements ComponentApi {

    private ControllerWrapper mControllerWrapper;

    public ComponentComplete(@NonNull Context context) {
        super(context);
        init();
    }

    public ComponentComplete(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ComponentComplete(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_complete, this, true);

        // 重试
        findViewById(R.id.module_mediaplayer_component_complete_message).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                restart(mControllerWrapper);
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

        switch (playState) {
            case PlayerType.StateType.STATE_END:
                MPLogUtil.log("ComponentEnd[show] => playState = " + playState);
                bringToFront();
                findViewById(R.id.module_mediaplayer_component_complete_bg).setVisibility(View.VISIBLE);
                findViewById(R.id.module_mediaplayer_component_complete_message).setVisibility(View.VISIBLE);
                break;
            case PlayerType.StateType.STATE_START:
            case PlayerType.StateType.STATE_RESUME:
            case PlayerType.StateType.STATE_RESTAER:
            case PlayerType.StateType.STATE_INIT:
                MPLogUtil.log("ComponentEnd[gone] => playState = " + playState);
                findViewById(R.id.module_mediaplayer_component_complete_bg).setVisibility(View.GONE);
                findViewById(R.id.module_mediaplayer_component_complete_message).setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onWindowStateChanged(int playerState) {
//        if (playerState == PlayerType.WindowType.FULL) {
//            View view = findViewById(R.id.controller_complete_back);
//            view.setVisibility(VISIBLE);
//        } else if (playerState == PlayerType.WindowType.NORMAL) {
//            View view = findViewById(R.id.controller_complete_back);
//            view.setVisibility(GONE);
//        }
//
//        Activity activity = PlayerUtils.scanForActivity(getContext());
//        if (activity != null && mControllerWrapper.hasCutout()) {
//            int orientation = activity.getRequestedOrientation();
//            int cutoutHeight = mControllerWrapper.getCutoutHeight();
//            View view = findViewById(R.id.controller_complete_back);
//            FrameLayout.LayoutParams sflp = (FrameLayout.LayoutParams) view.getLayoutParams();
//            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//                sflp.setMargins(0, 0, 0, 0);
//            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//                sflp.setMargins(cutoutHeight, 0, 0, 0);
//            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
//                sflp.setMargins(0, 0, 0, 0);
//            }
//        }
    }

    @Override
    public void onLockStateChanged(boolean isLock) {

    }

}
