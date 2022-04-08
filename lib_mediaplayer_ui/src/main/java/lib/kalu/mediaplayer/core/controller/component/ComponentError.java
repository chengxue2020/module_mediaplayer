package lib.kalu.mediaplayer.core.controller.component;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.core.controller.base.ControllerWrapper;
import lib.kalu.mediaplayer.config.PlayerType;
import lib.kalu.mediaplayer.core.controller.impl.ImplComponent;
import lib.kalu.mediaplayer.util.PlayerUtils;

public class ComponentError extends LinearLayout implements ImplComponent {

    private float mDownX;
    private float mDownY;

    private ControllerWrapper mControllerWrapper;

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
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_video_error, this, true);
        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setVisibility(GONE);

        // 重试
        findViewById(R.id.module_mediaplayer_controller_error_retry).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(GONE);
                mControllerWrapper.repeat();
            }
        });

        // 返回
        findViewById(R.id.module_mediaplayer_controller_error_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击返回键
                if (mControllerWrapper.isFullScreen()) {
                    Activity activity = PlayerUtils.scanForActivity(getContext());
                    if (activity != null && !activity.isFinishing()) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        mControllerWrapper.stopFullScreen();
                    }
                }
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

        if (playState == PlayerType.StateType.STATE_ERROR_URL) {
            bringToFront();
            setVisibility(VISIBLE);

            View view = findViewById(R.id.module_mediaplayer_controller_error_back);
            view.setVisibility(mControllerWrapper.isFullScreen() ? VISIBLE : GONE);

        } else if (playState == PlayerType.StateType.STATE_CLEAN) {
            bringToFront();
            setVisibility(VISIBLE);

            View view = findViewById(R.id.module_mediaplayer_controller_error_back);
            view.setVisibility(mControllerWrapper.isFullScreen() ? VISIBLE : GONE);
        } else if (playState == PlayerType.StateType.STATE_ERROR) {
            bringToFront();
            setVisibility(VISIBLE);

            View view = findViewById(R.id.module_mediaplayer_controller_error_back);
            view.setVisibility(mControllerWrapper.isFullScreen() ? VISIBLE : GONE);
        } else if (playState == PlayerType.StateType.STATE_ERROR_NETWORK) {
            bringToFront();
            setVisibility(VISIBLE);

            View view = findViewById(R.id.module_mediaplayer_controller_error_back);
            view.setVisibility(mControllerWrapper.isFullScreen() ? VISIBLE : GONE);
        } else if (playState == PlayerType.StateType.STATE_ERROR_PARSE) {
            bringToFront();
            setVisibility(VISIBLE);

            View view = findViewById(R.id.module_mediaplayer_controller_error_back);
            view.setVisibility(mControllerWrapper.isFullScreen() ? VISIBLE : GONE);
            //mTvMessage.setText("视频解析异常");
        } else if (playState == PlayerType.StateType.STATE_INIT) {
            setVisibility(GONE);
        } else if (playState == PlayerType.StateType.STATE_ONCE_LIVE) {
            setVisibility(GONE);
        }
    }

    @Override
    public void onWindowStateChanged(int playerState) {

    }

    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLock) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                float absDeltaX = Math.abs(ev.getX() - mDownX);
                float absDeltaY = Math.abs(ev.getY() - mDownY);
                if (absDeltaX > ViewConfiguration.get(getContext()).getScaledTouchSlop() ||
                        absDeltaY > ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
