package lib.kalu.mediaplayer.core.player;

import android.content.Context;
import android.os.Build;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.core.video.KernelApi;
import lib.kalu.mediaplayer.core.player.api.PlayerApi;
import lib.kalu.mediaplayer.core.render.RenderApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

/**
 * @description: 播放器具体实现类
 */
@Keep
public class VideoLayout extends RelativeLayout implements PlayerApi {

    // 解码
    protected KernelApi mKernel;
    // 渲染
    protected RenderApi mRender;

    public VideoLayout(@NonNull Context context) {
        super(context);
        initAttribute(null);
    }

    public VideoLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttribute(null);
    }

    public VideoLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttribute(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VideoLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttribute(null);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        boolean invisibleStop = isInvisibleStop();
        boolean invisibleIgnore = isInvisibleIgnore();
        boolean invisibleRelease = isInvisibleRelease();
        MPLogUtil.log("VideoLayout => onDetachedFromWindow => invisibleStop = " + invisibleStop + ", invisibleIgnore = " + invisibleIgnore + ", invisibleRelease = " + invisibleRelease + ", this = " + this);
        if (invisibleIgnore) {
        } else if (invisibleStop) {
            pause(false);
        } else {
            release();
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        boolean invisibleIgnore = isInvisibleIgnore();
        MPLogUtil.log("VideoLayout => onWindowVisibilityChanged => visibility = " + visibility + ", invisibleIgnore =  " + invisibleIgnore + ", this = " + this);
        if (invisibleIgnore) {
        } else {
            // visable
            if (visibility == View.VISIBLE) {
                resume(false);
            }
            // not visable
            else {
                pause(true);
            }
        }
        super.onWindowVisibilityChanged(visibility);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        onSaveBundle();
        return super.onSaveInstanceState();
    }

    @Override
    public RenderApi getRender() {
        return mRender;
    }

    @Override
    public void setRender(@NonNull RenderApi render) {
        mRender = render;
    }

    @Override
    public KernelApi getKernel() {
        return mKernel;
    }

    @Override
    public void setKernel(@NonNull KernelApi kernel) {
        MPLogUtil.log("PlayerApiKernel => setKernel => kernel = " + kernel);
        mKernel = kernel;
    }

    @Override
    public ViewGroup getLayout() {
        return this;
    }

    @Override
    public void checkReal() {
        int visibility = getWindowVisibility();
        if (visibility == View.VISIBLE)
            return;
        pause();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return dispatchKeyEvent(this, event) || super.dispatchKeyEvent(event);
    }

    @Override
    public void setScreenKeep(boolean enable) {
        setKeepScreenOn(enable);
    }

    @Override
    public void clearComponent() {
        ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_control);
        viewGroup.removeAllViews();
    }
}