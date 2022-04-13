package lib.kalu.mediaplayer.core.controller.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.PlayerType;
import lib.kalu.mediaplayer.core.controller.base.ControllerWrapper;
import lib.kalu.mediaplayer.core.controller.impl.ImplComponent;

public class ComponentError extends RelativeLayout implements ImplComponent {

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
            case PlayerType.StateType.STATE_ERROR_URL:
            case PlayerType.StateType.STATE_CLEAN:
            case PlayerType.StateType.STATE_ERROR:
            case PlayerType.StateType.STATE_ERROR_NETWORK:
            case PlayerType.StateType.STATE_ERROR_PARSE:
                findViewById(R.id.module_mediaplayer_component_error_img).setVisibility(View.VISIBLE);
                findViewById(R.id.module_mediaplayer_component_error_message).setVisibility(View.VISIBLE);
                break;
            default:
                findViewById(R.id.module_mediaplayer_component_error_img).setVisibility(View.GONE);
                findViewById(R.id.module_mediaplayer_component_error_message).setVisibility(View.GONE);
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
    public void onLockStateChanged(boolean isLock) {
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mDownX = ev.getX();
//                mDownY = ev.getY();
//                getParent().requestDisallowInterceptTouchEvent(true);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                float absDeltaX = Math.abs(ev.getX() - mDownX);
//                float absDeltaY = Math.abs(ev.getY() - mDownY);
//                if (absDeltaX > ViewConfiguration.get(getContext()).getScaledTouchSlop() ||
//                        absDeltaY > ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
//                    getParent().requestDisallowInterceptTouchEvent(false);
//                }
//            case MotionEvent.ACTION_UP:
//                break;
//        }
//        return super.dispatchTouchEvent(ev);
//    }

    public final void setImage(@DrawableRes int value) {
        this.setImageResource(this, R.id.module_mediaplayer_component_error_img, value);
    }

    public final void setMessage(@NonNull String value) {
        this.setText(this, R.id.module_mediaplayer_component_error_message, value);
    }

    public final void setMessage(@StringRes int value) {
        this.setText(this, R.id.module_mediaplayer_component_error_message, value);
    }

    public final void setMessageSize(@DimenRes int value) {
        this.setTextSize(this, R.id.module_mediaplayer_component_error_message, value);
    }
}
