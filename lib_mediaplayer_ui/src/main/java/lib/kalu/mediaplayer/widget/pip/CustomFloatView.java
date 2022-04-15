package lib.kalu.mediaplayer.widget.pip;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.core.controller.base.ControllerWrapper;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.controller.impl.ImplComponent;

/**
 * desc  : 悬浮窗视图
 */
public class CustomFloatView extends FrameLayout implements ImplComponent, View.OnClickListener {

    private ControllerWrapper mControllerWrapper;
    private Context mContext;
    private ImageView mIvStartPlay;
    private ProgressBar mPbLoading;
    private ImageView mIvClose;
    private ImageView mIvSkip;
    private ProgressBar mPbBottomProgress;
    private boolean mIsShowBottomProgress = true;

    public CustomFloatView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CustomFloatView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomFloatView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.mContext = context;
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.module_mediaplayer_video_float, this, true);
        initFindViewById(view);
        initListener();
        //5.1以下系统SeekBar高度需要设置成WRAP_CONTENT
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            mPbBottomProgress.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
    }

    private void initFindViewById(View view) {
        mIvStartPlay = view.findViewById(R.id.iv_start_play);
        mPbLoading = view.findViewById(R.id.pb_loading);
        mIvClose = view.findViewById(R.id.iv_close);
        mIvSkip = view.findViewById(R.id.iv_skip);
        mPbBottomProgress = view.findViewById(R.id.pb_bottom_progress);
    }

    private void initListener() {
        mIvStartPlay.setOnClickListener(this);
        mIvClose.setOnClickListener(this);
        mIvSkip.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == mIvClose) {
            FloatVideoManager.getInstance(mContext).stopFloatWindow();
            FloatVideoManager.getInstance(mContext).reset();
        } else if (v == mIvStartPlay) {
            mControllerWrapper.toggle();
        } else if (v == mIvSkip) {
            if (FloatVideoManager.getInstance(mContext).getActClass() != null) {
                Intent intent = new Intent(getContext(), FloatVideoManager.getInstance(mContext).getActClass());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
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
            if (mIvStartPlay.getVisibility() == VISIBLE) {
                return;
            }
            mIvStartPlay.setVisibility(VISIBLE);
            mIvStartPlay.startAnimation(anim);
            if (mIsShowBottomProgress) {
                mPbBottomProgress.setVisibility(GONE);
            }
        } else {
            if (mIvStartPlay.getVisibility() == GONE) {
                return;
            }
            mIvStartPlay.setVisibility(GONE);
            mIvStartPlay.startAnimation(anim);
            if (mIsShowBottomProgress) {
                mPbBottomProgress.setVisibility(VISIBLE);
                AlphaAnimation animation = new AlphaAnimation(0f, 1f);
                animation.setDuration(300);
                mPbBottomProgress.startAnimation(animation);
            }
        }
    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_INIT:
                mIvStartPlay.setSelected(false);
                mIvStartPlay.setVisibility(VISIBLE);
                mPbLoading.setVisibility(GONE);
                break;
            case PlayerType.StateType.STATE_START:
                mIvStartPlay.setSelected(true);
                mIvStartPlay.setVisibility(GONE);
                mPbLoading.setVisibility(GONE);
                if (mIsShowBottomProgress) {
                    if (mControllerWrapper.isShowing()) {
                        mPbBottomProgress.setVisibility(GONE);
                    } else {
                        mPbBottomProgress.setVisibility(VISIBLE);
                    }
                }
                //开始刷新进度
                mControllerWrapper.startProgress();
                break;
            case PlayerType.StateType.STATE_PAUSED:
                mIvStartPlay.setSelected(false);
                mIvStartPlay.setVisibility(VISIBLE);
                mPbLoading.setVisibility(GONE);
                break;
            case PlayerType.StateType.STATE_LOADING_START:
                mIvStartPlay.setVisibility(GONE);
                mIvStartPlay.setVisibility(VISIBLE);
                break;
            case PlayerType.StateType.STATE_LOADING_COMPLETE:
                mIvStartPlay.setVisibility(GONE);
                mPbLoading.setVisibility(GONE);
                break;
            case PlayerType.StateType.STATE_ERROR:
                mPbLoading.setVisibility(GONE);
                mIvStartPlay.setVisibility(GONE);
                bringToFront();
                break;
            case PlayerType.StateType.STATE_BUFFERING_PAUSED:
                mIvStartPlay.setVisibility(GONE);
                mPbLoading.setVisibility(VISIBLE);
                break;
            case PlayerType.StateType.STATE_END:
                mIvStartPlay.setVisibility(GONE);
                mPbLoading.setVisibility(GONE);
                mIvStartPlay.setSelected(mControllerWrapper.isPlaying());
                break;
            case PlayerType.StateType.STATE_BUFFERING_PLAYING:
                bringToFront();
                mPbBottomProgress.setProgress(0);
                mPbBottomProgress.setSecondaryProgress(0);
                break;
        }
    }

    @Override
    public void onWindowStateChanged(int playerState) {

    }

    @Override
    public void setProgress(int duration, int position) {
        if (duration > 0) {
            int pos = (int) (position * 1.0 / duration * mPbBottomProgress.getMax());
            mPbBottomProgress.setProgress(pos);
        }
        int percent = mControllerWrapper.getBufferedPercentage();
        if (percent >= 95) {
            //解决缓冲进度不能100%问题
            mPbBottomProgress.setSecondaryProgress(mPbBottomProgress.getMax());
        } else {
            mPbBottomProgress.setSecondaryProgress(percent * 10);
        }
    }


    @Override
    public void onLockStateChanged(boolean isLocked) {

    }

}
