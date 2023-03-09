package lib.kalu.mediaplayer.core.player;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.KernelApi;
import lib.kalu.mediaplayer.core.player.api.PlayerApi;
import lib.kalu.mediaplayer.core.render.RenderApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

@Keep
final class PlayerView extends RelativeLayout implements PlayerApi {

    // 解码
    protected KernelApi mKernel;
    // 渲染
    protected RenderApi mRender;

    public PlayerView(Context context) {
        super(context);
        init();
    }

    public PlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PlayerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setId(R.id.module_mediaplayer_root);
        setBackgroundColor(Color.parseColor("#000000"));
        setScaleType(PlayerType.ScaleType.SCREEN_SCALE_MATCH_PARENT);
        // player
        RelativeLayout layoutPlayer = new RelativeLayout(getContext());
        layoutPlayer.setId(R.id.module_mediaplayer_video);
        layoutPlayer.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        addView(layoutPlayer);
        // control
        RelativeLayout controlPlayer = new RelativeLayout(getContext());
        controlPlayer.setId(R.id.module_mediaplayer_control);
        controlPlayer.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        addView(controlPlayer);
    }

    @Override
    public RenderApi getRender() {
        return mRender;
    }

    @Override
    public void setRender(@NonNull RenderApi render) {
        MPLogUtil.log("VideoLayout => setRender => render = " + render);
        mRender = render;
    }

    @Override
    public KernelApi getKernel() {
        return mKernel;
    }

    @Override
    public void setKernel(@NonNull KernelApi kernel) {
        MPLogUtil.log("VideoLayout => setKernel => kernel = " + kernel);
        mKernel = kernel;
    }

    @Override
    public void checkReal() {
        if (getVisibility() == View.VISIBLE)
            return;
        pause();
    }

    @Override
    public void setScreenKeep(boolean enable) {
        setKeepScreenOn(enable);
    }
}