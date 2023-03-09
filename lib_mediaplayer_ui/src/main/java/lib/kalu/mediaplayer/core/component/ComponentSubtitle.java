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
import lib.kalu.mediaplayer.util.MPLogUtil;

public class ComponentSubtitle extends RelativeLayout implements ComponentApi {

    public ComponentSubtitle(@NonNull Context context) {
        super(context);
        init();
    }

    public ComponentSubtitle(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ComponentSubtitle(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_subtitle, this, true);
        setVisibility(View.VISIBLE);
    }

    @Override
    public void callPlayerEvent(int playState) {
        MPLogUtil.log("ComponentPrepare => onPlayStateChanged => playState = " + playState);
//        switch (playState) {
//            case PlayerType.StateType.STATE_SUBTITLE_START:
//                bringToFront();
//                setVisibility(VISIBLE);
//                SimpleSubtitleView subtitle = findViewById(R.id.module_mediaplayer_controller_subtitle);
//
//                break;
//            default:
//                setVisibility(GONE);
//                break;
//        }
    }
}
