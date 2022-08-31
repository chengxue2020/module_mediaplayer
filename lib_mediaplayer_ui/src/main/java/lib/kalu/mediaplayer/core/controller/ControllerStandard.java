package lib.kalu.mediaplayer.core.controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.controller.base.ControllerLayoutDispatchTouchEvent;
import lib.kalu.mediaplayer.core.controller.component.ComponentSeek;
import lib.kalu.mediaplayer.core.controller.component.ComponentEnd;
import lib.kalu.mediaplayer.core.controller.component.ComponentError;
import lib.kalu.mediaplayer.core.controller.component.ComponentGesture;
import lib.kalu.mediaplayer.core.controller.component.ComponentLoading;
import lib.kalu.mediaplayer.core.controller.component.ComponentTop;
import lib.kalu.mediaplayer.util.BaseToast;
import lib.kalu.mediaplayer.util.PlayerUtils;

@Keep
public class ControllerStandard extends ControllerLayoutDispatchTouchEvent {

    private ComponentTop titleView;
    private ComponentSeek vodControlView;

    public ControllerStandard(@NonNull Context context) {
        this(context, null);
    }

    public ControllerStandard(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControllerStandard(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int initLayout() {
        return R.layout.module_mediaplayer_controller_standard;
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
                mControllerWrapper.toggleLockState();
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
        //添加自动完成播放界面view
        ComponentEnd completeView = new ComponentEnd(getContext());
        completeView.setVisibility(GONE);
        this.addComponent(completeView);

        //添加错误界面view
        this.addComponent(new ComponentError(getContext()));

        //添加与加载视图界面view，准备播放界面
        this.addComponent(new ComponentLoading(getContext()));

        //添加标题栏
        titleView = new ComponentTop(getContext());
        titleView.setTitle("");
        titleView.setVisibility(VISIBLE);
        this.addComponent(titleView);

        //添加直播/回放视频底部控制视图
        changePlayType();

        //添加滑动控制视图
        ComponentGesture gestureControlView = new ComponentGesture(getContext());
        this.addComponent(gestureControlView);
    }


    /**
     * 切换直播/回放类型
     */
    public void changePlayType() {

        //添加底部播放控制条
        if (vodControlView == null) {
            vodControlView = new ComponentSeek(getContext());
            //是否显示底部进度条。默认显示
            vodControlView.showBottomProgress(true);
        }
//        this.removeComponent(vodControlView);
        this.addComponent(vodControlView);

        //正常视频，移除直播视图
//        if (liveControlView != null) {
//            this.removeComponent(liveControlView);
//        }
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
//        if (mControllerWrapper.isFullScreen()) {
//            View view = findViewById(R.id.module_mediaplayer_controller_center_lock);
//            if (isVisible) {
//                if (view.getVisibility() == GONE) {
//                    view.setVisibility(VISIBLE);
//                    if (anim != null) {
//                        view.startAnimation(anim);
//                    }
//                }
//            } else {
//                view.setVisibility(GONE);
//                if (anim != null) {
//                    view.startAnimation(anim);
//                }
//            }
//            postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    view.requestFocus();
//                }
//            }, 400);
//        }
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
    protected void onWindowStatusChanged(int playerState) {
        super.onWindowStatusChanged(playerState);
        View view = findViewById(R.id.module_mediaplayer_controller_center_lock);
        switch (playerState) {
            case PlayerType.WindowType.NORMAL:
                setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
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
                RelativeLayout.LayoutParams lblp = (RelativeLayout.LayoutParams) view.getLayoutParams();
                lblp.setMargins(dp24, 0, dp24, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.setMargins(dp24 + cutoutHeight, 0, dp24 + cutoutHeight, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.setMargins(dp24, 0, dp24, 0);
            }
        }
    }

    @Override
    protected void onPlayerStatusChanged(int playState) {
        super.onPlayerStatusChanged(playState);
        View view = findViewById(R.id.module_mediaplayer_controller_center_lock);
        View viewLoading = findViewById(R.id.module_mediaplayer_controller_center_loading);
        switch (playState) {
            //调用release方法会回到此状态
            case PlayerType.StateType.STATE_INIT:
                view.setSelected(false);
                viewLoading.setVisibility(GONE);
                break;
            case PlayerType.StateType.STATE_START:
            case PlayerType.StateType.STATE_PAUSE:
            case PlayerType.StateType.STATE_LOADING_STOP:
            case PlayerType.StateType.STATE_ERROR:
            case PlayerType.StateType.STATE_END:
                viewLoading.setVisibility(GONE);
                break;
            case PlayerType.StateType.STATE_LOADING_START:
            case PlayerType.StateType.STATE_BUFFERING_STOP:
                viewLoading.setVisibility(VISIBLE);
                break;
            case PlayerType.StateType.STATE_BUFFERING_START:
                viewLoading.setVisibility(GONE);
                view.setVisibility(GONE);
                view.setSelected(false);
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
//        if (isLocked()) {
//            show();
//            String string = getContext().getResources().getString(R.string.module_mediaplayer_string_lock_tip);
//            BaseToast.showRoundRectToast(getContext(), string);
//            return true;
//        }
//        if (mControllerWrapper.isFullScreen()) {
//            return stopFullScreen();
//        }
//        Activity activity = PlayerUtils.scanForActivity(getContext());
//        //如果不是全屏模式，则直接关闭页面activity
//        if (PlayerUtils.isActivityLiving(activity)) {
//            activity.finish();
//        }
        return super.onBackPressed();
    }

    @Override
    public void destroy() {

    }

    public void setTitle(String title) {
        if (titleView != null) {
            titleView.setTitle(title);
        }
    }
}
