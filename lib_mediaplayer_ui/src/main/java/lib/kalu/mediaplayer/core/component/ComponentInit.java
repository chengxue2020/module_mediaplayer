
package lib.kalu.mediaplayer.core.component;

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
import lib.kalu.mediaplayer.util.MPLogUtil;

public class ComponentInit extends RelativeLayout implements ComponentApi {

    public ComponentInit(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ComponentInit(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ComponentInit(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.module_mediaplayer_component_init, this, true);
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
            case PlayerType.StateType.STATE_INIT:
                MPLogUtil.log("ComponentInit[show] => playState = " + playState);
                bringToFront();
                show();
                break;
            default:
                MPLogUtil.log("ComponentInit[gone] => playState = " + playState);
                gone();
                break;
        }
    }

    @Override
    public void gone() {
        findViewById(R.id.module_mediaplayer_component_init_txt).setVisibility(View.GONE);
        findViewById(R.id.module_mediaplayer_component_init_bg).setVisibility(View.GONE);
    }

    @Override
    public void show() {
        findViewById(R.id.module_mediaplayer_component_init_txt).setVisibility(View.VISIBLE);
        findViewById(R.id.module_mediaplayer_component_init_bg).setVisibility(View.VISIBLE);
    }
}