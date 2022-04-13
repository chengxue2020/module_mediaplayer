package lib.kalu.mediaplayer.core.controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.PlayerType;
import lib.kalu.mediaplayer.core.controller.base.ControllerLayout;
import lib.kalu.mediaplayer.core.controller.component.ComponentError;
import lib.kalu.mediaplayer.core.controller.component.ComponentLoading;
import lib.kalu.mediaplayer.util.BaseToast;
import lib.kalu.mediaplayer.util.MediaLogUtil;
import lib.kalu.mediaplayer.util.PlayerUtils;
import lib.kalu.mediaplayer.widget.subtitle.widget.SimpleSubtitleView;


@Keep
public class ControllerLive extends ControllerLayout {

    public ControllerLive(@NonNull Context context) {
        super(context);
    }

    @Override
    public int initLayout() {
        return R.layout.module_mediaplayer_video_live;
    }

    @Override
    public void init() {
        super.init();
        // 错误界面view
        this.addComponent(new ComponentError(getContext()));
        // 加载界面view
        this.addComponent(new ComponentLoading(getContext()));

        // 字幕
//        this.addComponent(new ComponentSubtitle(getContext()));
    }

    @Override
    protected void onLockStateChanged(boolean isLocked) {
    }

    @Override
    protected void onVisibilityChanged(boolean isVisible, Animation anim) {
    }

    @Override
    protected void onPlayerStateChanged(int playerState) {
        super.onPlayerStateChanged(playerState);
        View view = findViewById(R.id.module_mediaplayer_controller_live_lock);
        switch (playerState) {
            case PlayerType.WindowType.NORMAL:
                setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                view.setVisibility(GONE);
                break;
            case PlayerType.WindowType.FULL:
                view.setVisibility(isShowing() ? VISIBLE : GONE);
                break;
        }

        if (mActivity != null && hasCutout()) {
            int orientation = mActivity.getRequestedOrientation();
            int dp24 = PlayerUtils.dp2px(getContext(), 24);
            int cutoutHeight = getCutoutHeight();
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                LayoutParams lblp = (LayoutParams) view.getLayoutParams();
                lblp.setMargins(dp24, 0, dp24, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                layoutParams.setMargins(dp24 + cutoutHeight, 0, dp24 + cutoutHeight, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                layoutParams.setMargins(dp24, 0, dp24, 0);
            }
        }
    }

    @Override
    protected void onPlayStateChanged(int playState) {
        super.onPlayStateChanged(playState);
        MediaLogUtil.log("ControllerLive => playState = " + playState);
        View view = findViewById(R.id.module_mediaplayer_controller_live_loading);
        switch (playState) {
            case PlayerType.StateType.STATE_BUFFERING_PAUSED:
            case PlayerType.StateType.STATE_BUFFERING_PLAYING:
                view.setVisibility(VISIBLE);
                break;
            default:
                view.setVisibility(GONE);
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
        if (isLocked()) {
            show();
            String string = getContext().getResources().getString(R.string.module_mediaplayer_string_lock_tip);
            BaseToast.showRoundRectToast(getContext(), string);
            return true;
        }
        if (mControllerWrapper.isFullScreen()) {
            return stopFullScreen();
        }
        Activity activity = PlayerUtils.scanForActivity(getContext());
        //如果不是全屏模式，则直接关闭页面activity
        if (PlayerUtils.isActivityLiving(activity)) {
            activity.finish();
        }
        return super.onBackPressed();
    }

    /**
     * 刷新进度回调，子类可在此方法监听进度刷新，然后更新ui
     *
     * @param duration 视频总时长
     * @param position 视频当前时长
     */
    @Override
    protected void setProgress(int duration, int position) {
        super.setProgress(duration, position);
    }

    @Override
    public void destroy() {
    }
}
