/*
Copyright 2017 yangchong211（github.com/yangchong211）

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package lib.kalu.mediaplayer.core.controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.PlayerType;
import lib.kalu.mediaplayer.core.controller.base.ControllerLayout;
import lib.kalu.mediaplayer.core.controller.base.ControllerLayoutDispatchTouchEvent;
import lib.kalu.mediaplayer.core.controller.component.ComponentError;
import lib.kalu.mediaplayer.core.controller.component.ComponentPrepare;
import lib.kalu.mediaplayer.util.BaseToast;
import lib.kalu.mediaplayer.util.PlayerUtils;


/**
 * description: 控制器 - mobile
 * 如果想定制ui，你可以直接继承GestureVideoController或者BaseVideoController实现
 * created by kalu on 2021/9/16
 */
@Keep
public class ControllerLive extends ControllerLayout {

    public ControllerLive(@NonNull Context context) {
        this(context, null);
    }

    public ControllerLive(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControllerLive(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int initLayout() {
        return R.layout.module_mediaplayer_video_center;
    }

    @Override
    public void init() {
        super.init();
        setEnabled(false);
        initConfig();
    }

    private void initConfig() {
        //先移除多有的视图view
        removeComponentAll(false);

        // 错误界面view
        this.addComponent(new ComponentError(getContext()));

        // 加载界面view
        this.addComponent(new ComponentPrepare(getContext()));
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
        View view = findViewById(R.id.module_mediaplayer_controller_center_lock);
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
        View view = findViewById(R.id.module_mediaplayer_controller_center_lock);
        View viewLoading = findViewById(R.id.module_mediaplayer_controller_center_loading);
        switch (playState) {
            //调用release方法会回到此状态
            case PlayerType.StateType.STATE_IDLE:
                view.setSelected(false);
                viewLoading.setVisibility(GONE);
                break;
            case PlayerType.StateType.STATE_PLAYING:
            case PlayerType.StateType.STATE_PAUSED:
            case PlayerType.StateType.STATE_PREPARED:
            case PlayerType.StateType.STATE_ERROR:
            case PlayerType.StateType.STATE_COMPLETED:
                viewLoading.setVisibility(GONE);
                break;
            case PlayerType.StateType.STATE_PREPARING:
            case PlayerType.StateType.STATE_BUFFERING_PAUSED:
                viewLoading.setVisibility(VISIBLE);
                break;
            case PlayerType.StateType.STATE_BUFFERING_PLAYING:
                viewLoading.setVisibility(GONE);
                view.setVisibility(GONE);
                view.setSelected(false);
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

    @Nullable
    @Override
    public ProgressBar findPrepareProgress() {
        ProgressBar progressBar = findViewById(R.id.module_mediaplayer_controller_prepare_progress);
        return progressBar;
    }

    @Nullable
    @Override
    public ImageView findPrepareBackground() {
        ImageView imageView = findViewById(R.id.module_mediaplayer_controller_prepare_background);
        return imageView;
    }

    @Nullable
    @Override
    public TextView findPrepareTip() {
        TextView textView = findViewById(R.id.module_mediaplayer_controller_prepare_tip);
        return textView;
    }

    @Override
    public void destroy() {
    }
}
