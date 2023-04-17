package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.util.MPLogUtil;

public class ComponentPause extends RelativeLayout implements ComponentApi {

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
    public void callPlayerEvent(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_PAUSE:
                MPLogUtil.log("ComponentPause[show] => playState = " + playState);
                show();
                break;
            case PlayerType.StateType.STATE_LOADING_START:
            case PlayerType.StateType.STATE_START:
            case PlayerType.StateType.STATE_RESUME:
            case PlayerType.StateType.STATE_RESUME_IGNORE:
            case PlayerType.StateType.STATE_RESTAER:
                MPLogUtil.log("ComponentPause[gone] => playState = " + playState);
                gone();
                break;
        }
    }

    @Override
    public void gone() {
       try {
           findViewById(R.id.module_mediaplayer_component_pause_img).setVisibility(View.GONE);
           findViewById(R.id.module_mediaplayer_component_pause_bg).setVisibility(View.GONE);
           findViewById(R.id.module_mediaplayer_component_pause_ad).setVisibility(View.GONE);
       }catch (Exception e){
       }
    }

    @Override
    public void show() {
        try {
            bringToFront();
            findViewById(R.id.module_mediaplayer_component_pause_img).setVisibility(View.VISIBLE);
            findViewById(R.id.module_mediaplayer_component_pause_bg).setVisibility(View.VISIBLE);
            findViewById(R.id.module_mediaplayer_component_pause_ad).setVisibility(View.GONE);
        }catch (Exception e){
        }
    }

    public final void setPauseImageResource(@DrawableRes int res) {
        setImageResource(this, R.id.module_mediaplayer_component_pause_img, res);
    }

    public final void setComponentBackgroundColorRes(@ColorRes int color) {
        setBackgroundColorRes(this, R.id.module_mediaplayer_component_pause_bg, color);
    }

    public final void setComponentBackgroundColorInt(@ColorInt int color) {
        setBackgroundColorInt(this, R.id.module_mediaplayer_component_pause_bg, color);
    }
}