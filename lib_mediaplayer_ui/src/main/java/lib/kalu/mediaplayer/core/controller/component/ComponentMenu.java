package lib.kalu.mediaplayer.core.controller.component;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.core.controller.base.ControllerWrapper;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.controller.impl.ImplComponent;
import lib.kalu.mediaplayer.util.PlayerUtils;

public class ComponentMenu extends RelativeLayout implements ImplComponent, View.OnClickListener {

    private Context mContext;
    private ControllerWrapper mControllerWrapper;
    private LinearLayout mLlBottomContainer;
    private ImageView mIvPlay;
    private ImageView mIvRefresh;
    private ImageView mIvFullScreen;

    public ComponentMenu(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ComponentMenu(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ComponentMenu(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.mContext = context;
        setVisibility(GONE);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_menu, this, true);
        initFindViewById(view);
        initListener();
    }

    private void initFindViewById(View view) {
        mLlBottomContainer = view.findViewById(R.id.ll_bottom_container);
        mIvPlay = view.findViewById(R.id.iv_play);
        mIvRefresh = view.findViewById(R.id.iv_refresh);
        mIvFullScreen = view.findViewById(R.id.iv_full_screen);
    }

    private void initListener() {
        mIvFullScreen.setOnClickListener(this);
        mIvRefresh.setOnClickListener(this);
        mIvPlay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_full_screen) {
            toggleFullScreen();
        } else if (id == R.id.iv_play) {
            mControllerWrapper.toggle();
        } else if (id == R.id.iv_refresh) {
            mControllerWrapper.repeat();
        }
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
        if (isVisible) {
            if (getVisibility() == GONE) {
                setVisibility(VISIBLE);
                if (anim != null) {
                    startAnimation(anim);
                }
            }
        } else {
            if (getVisibility() == VISIBLE) {
                setVisibility(GONE);
                if (anim != null) {
                    startAnimation(anim);
                }
            }
        }
    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_INIT:
            case PlayerType.StateType.STATE_START_ABORT:
            case PlayerType.StateType.STATE_LOADING_START:
            case PlayerType.StateType.STATE_LOADING_COMPLETE:
            case PlayerType.StateType.STATE_ERROR:
            case PlayerType.StateType.STATE_BUFFERING_PLAYING:
            case PlayerType.StateType.STATE_ONCE_LIVE:
                setVisibility(GONE);
                break;
            case PlayerType.StateType.STATE_START:
                mIvPlay.setSelected(true);
                break;
            case PlayerType.StateType.STATE_PAUSED:
                mIvPlay.setSelected(false);
                break;
            case PlayerType.StateType.STATE_BUFFERING_PAUSED:
            case PlayerType.StateType.STATE_END:
                mIvPlay.setSelected(mControllerWrapper.isPlaying());
                break;
        }
    }

    @Override
    public void onWindowStateChanged(int playerState) {
        switch (playerState) {
            case PlayerType.WindowType.NORMAL:
                mIvFullScreen.setSelected(false);
                break;
            case PlayerType.WindowType.FULL:
                mIvFullScreen.setSelected(true);
                break;
        }

        Activity activity = PlayerUtils.scanForActivity(mContext);
        if (activity != null && mControllerWrapper.hasCutout()) {
            int orientation = activity.getRequestedOrientation();
            int cutoutHeight = mControllerWrapper.getCutoutHeight();
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                mLlBottomContainer.setPadding(0, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                mLlBottomContainer.setPadding(cutoutHeight, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                mLlBottomContainer.setPadding(0, 0, cutoutHeight, 0);
            }
        }
    }

    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        onVisibilityChanged(!isLocked, null);
    }


    /**
     * 横竖屏切换
     */
    private void toggleFullScreen() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        mControllerWrapper.toggleFullScreen(activity);
    }
}
