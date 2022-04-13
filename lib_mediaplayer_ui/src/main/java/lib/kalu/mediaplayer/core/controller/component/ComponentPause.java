package lib.kalu.mediaplayer.core.controller.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.PlayerType;
import lib.kalu.mediaplayer.core.controller.base.ControllerWrapper;
import lib.kalu.mediaplayer.core.controller.impl.ImplComponent;

public class ComponentPause extends RelativeLayout implements ImplComponent {

    private ControllerWrapper mControllerWrapper;

    public ComponentPause(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ComponentPause(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ComponentPause(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.module_mediaplayer_component_pause, this, true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
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
            case PlayerType.StateType.STATE_PAUSED:
                bringToFront();
                findViewById(R.id.module_mediaplayer_component_pause_img).setVisibility(View.VISIBLE);
                break;
            default:
                findViewById(R.id.module_mediaplayer_component_pause_img).setVisibility(View.INVISIBLE);
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

    public final void setPauseImageResource(@DrawableRes int res) {
        setImageResource(this, R.id.module_mediaplayer_component_pause_img, res);
    }
}