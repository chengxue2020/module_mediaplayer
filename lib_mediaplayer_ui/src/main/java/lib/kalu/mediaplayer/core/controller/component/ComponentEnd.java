package lib.kalu.mediaplayer.core.controller.component;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.core.controller.base.ControllerWrapper;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.controller.impl.ImplComponent;
import lib.kalu.mediaplayer.util.MediaLogUtil;
import lib.kalu.mediaplayer.util.PlayerUtils;

public class ComponentEnd extends RelativeLayout implements ImplComponent {

    private ControllerWrapper mControllerWrapper;

    public ComponentEnd(@NonNull Context context) {
        super(context);
        init();
    }

    public ComponentEnd(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ComponentEnd(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_end, this, true);

        // 重试
        findViewById(R.id.module_mediaplayer_component_end_message).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "点击", Toast.LENGTH_SHORT).show();
                repeat(mControllerWrapper);
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
                MediaLogUtil.log("ComponentEnd[show] => playState = " + playState);
                bringToFront();
                setVisibility(View.VISIBLE);
                break;
            case PlayerType.StateType.STATE_START:
            case PlayerType.StateType.STATE_RESUME:
            case PlayerType.StateType.STATE_REPEAT:
                MediaLogUtil.log("ComponentEnd[gone] => playState = " + playState);
                setVisibility(View.GONE);
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
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLock) {

    }

}
