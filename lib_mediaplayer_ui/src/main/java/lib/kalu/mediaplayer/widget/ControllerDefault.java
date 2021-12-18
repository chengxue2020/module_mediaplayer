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
package lib.kalu.mediaplayer.widget;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.AttrRes;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.PlayerType;
import lib.kalu.mediaplayer.controller.ControllerLayoutDispatchTouchEvent;
import lib.kalu.mediaplayer.util.BaseToast;
import lib.kalu.mediaplayer.util.PlayerUtils;


/**
 * description: 控制器 - mobile
 * 如果想定制ui，你可以直接继承GestureVideoController或者BaseVideoController实现
 * created by kalu on 2021/9/16
 */
@Keep
public class ControllerDefault extends ControllerLayoutDispatchTouchEvent {

    private CustomTopView titleView;
    private CustomBottomView vodControlView;
    private CustomLiveControlView liveControlView;
    private CustomOncePlayView customOncePlayView;
    private TextView tvLiveWaitMessage;

    public ControllerDefault(@NonNull Context context) {
        this(context, null);
    }

    public ControllerDefault(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControllerDefault(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int initLayout() {
        return R.layout.module_mediaplayer_video_center;
    }

    @Override
    public void init() {
        super.init();
        initConfig();

        //
        View view = findViewById(R.id.module_mediaplayer_controller_center_lock);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mControlWrapper.toggleLockState();
            }
        });
    }

    private void initConfig() {
        //根据屏幕方向自动进入/退出全屏
        setEnableOrientation(true);
        //设置可以滑动调节进度
        setCanChangePosition(true);
        //竖屏也开启手势操作，默认关闭
        setEnableInNormal(true);
        //滑动调节亮度，音量，进度，默认开启
        setGestureEnabled(true);
        //先移除多有的视图view
        removeAll(false);
        //添加视图到界面
        addDefaultControlComponent("");
    }


    /**
     * 快速添加各个组件
     * 需要注意各个层级
     *
     * @param title 标题
     */
    public void addDefaultControlComponent(String title) {
        //添加自动完成播放界面view
        CustomCompleteView completeView = new CustomCompleteView(getContext());
        completeView.setVisibility(GONE);
        this.add(completeView);

        //添加错误界面view
        this.add(new CustomErrorView(getContext()));

        //添加与加载视图界面view，准备播放界面
        this.add(new CustomPrepareView(getContext()));

        //添加标题栏
        titleView = new CustomTopView(getContext());
        titleView.setTitle(title);
        titleView.setVisibility(VISIBLE);
        this.add(titleView);

        //添加直播/回放视频底部控制视图
        changePlayType();

        //添加滑动控制视图
        CustomGestureView gestureControlView = new CustomGestureView(getContext());
        this.add(gestureControlView);
    }


    /**
     * 切换直播/回放类型
     */
    public void changePlayType() {

        //添加底部播放控制条
        if (vodControlView == null) {
            vodControlView = new CustomBottomView(getContext());
            //是否显示底部进度条。默认显示
            vodControlView.showBottomProgress(true);
        }
        this.remove(vodControlView);
        this.add(vodControlView);

        //正常视频，移除直播视图
        if (liveControlView != null) {
            this.remove(liveControlView);
        }
        if (customOncePlayView != null) {
            this.add(customOncePlayView);
        }

        setCanChangePosition(!isEnabled());
    }


    @Override
    protected void onLockStateChanged(boolean isLocked) {
        View view = findViewById(R.id.module_mediaplayer_controller_center_lock);
        view.setSelected(isLocked ? true : false);
        String string = getContext().getResources().getString(isLocked ? R.string.module_mediaplayer_string_locked : R.string.module_mediaplayer_string_unlocked);
        BaseToast.showRoundRectToast(getContext(), string);
    }

    @Override
    protected void onVisibilityChanged(boolean isVisible, Animation anim) {
        if (mControlWrapper.isFullScreen()) {
            View view = findViewById(R.id.module_mediaplayer_controller_center_lock);
            if (isVisible) {
                if (view.getVisibility() == GONE) {
                    view.setVisibility(VISIBLE);
                    if (anim != null) {
                        view.startAnimation(anim);
                    }
                }
            } else {
                view.setVisibility(GONE);
                if (anim != null) {
                    view.startAnimation(anim);
                }
            }
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.requestFocus();
                }
            }, 400);
        }
    }

    /**
     * 播放模式
     * 普通模式，小窗口模式，正常模式三种其中一种
     * MODE_NORMAL              普通模式
     * MODE_FULL_SCREEN         全屏模式
     * MODE_TINY_WINDOW         小屏模式
     *
     * @param playerState 播放模式
     */
    @Override
    protected void onPlayerStateChanged(int playerState) {
        super.onPlayerStateChanged(playerState);
        View view = findViewById(R.id.module_mediaplayer_controller_center_lock);
        switch (playerState) {
            case PlayerType.WindowType.NORMAL:
                setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
                FrameLayout.LayoutParams lblp = (FrameLayout.LayoutParams) view.getLayoutParams();
                lblp.setMargins(dp24, 0, dp24, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                layoutParams.setMargins(dp24 + cutoutHeight, 0, dp24 + cutoutHeight, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                layoutParams.setMargins(dp24, 0, dp24, 0);
            }
        }

    }

    /**
     * 播放状态
     * -1               播放错误
     * 0                播放未开始
     * 1                播放准备中
     * 2                播放准备就绪
     * 3                正在播放
     * 4                暂停播放
     * 5                正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够后恢复播放)
     * 6                暂停缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停
     * 7                播放完成
     * 8                开始播放中止
     *
     * @param playState 播放状态，主要是指播放器的各种状态
     */
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
        if (mControlWrapper.isFullScreen()) {
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

    public void setTitle(String title) {
        if (titleView != null) {
            titleView.setTitle(title);
        }
    }

    public CustomBottomView getBottomView() {
        return vodControlView;
    }

    public TextView getTvLiveWaitMessage() {
        return tvLiveWaitMessage;
    }

    @Nullable
    @Override
    public ImageView findPrepare() {
        ImageView imageView = findViewById(R.id.module_mediaplayer_controller_prepare_image);
        return imageView;
    }
}
