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
package lib.kalu.mediaplayer.core.controller.component;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.core.controller.base.ControllerWrapper;
import lib.kalu.mediaplayer.config.PlayerConfig;
import lib.kalu.mediaplayer.config.PlayerType;
import lib.kalu.mediaplayer.core.controller.impl.ImplComponent;
import lib.kalu.mediaplayer.util.PlayerUtils;

/**
 * description: 底部控制栏视图
 * created by kalu on 2021/11/23
 */
@Keep
public class ComponentBottom extends FrameLayout implements ImplComponent, View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    protected ControllerWrapper mControllerWrapper;
    private boolean mIsDragging;
    private boolean mIsShowBottomProgress = true;

    public ComponentBottom(@NonNull Context context) {
        super(context);
        init();
    }

    public ComponentBottom(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ComponentBottom(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(getLayoutId(), this, true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setVisibility(View.INVISIBLE);
        initListener();
        //5.1以下系统SeekBar高度需要设置成WRAP_CONTENT
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            ProgressBar progressBar = findViewById(R.id.module_mediaplayer_controller_bottom_progress);
            progressBar.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
    }

    public int getLayoutId() {
        return R.layout.module_mediaplayer_video_bottom;
    }

    private void initListener() {

        SeekBar seekBar = findViewById(R.id.module_mediaplayer_controller_bottom_seek);
        seekBar.setOnSeekBarChangeListener(this);

        View viewFull = findViewById(R.id.module_mediaplayer_controller_bottom_full);
        viewFull.setOnClickListener(this);
        View viewPlayer = findViewById(R.id.module_mediaplayer_controller_bottom_play);
        viewPlayer.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.module_mediaplayer_controller_bottom_full) {
            toggleFullScreen();
        } else if (v.getId() == R.id.module_mediaplayer_controller_bottom_play) {
            mControllerWrapper.toggle();
        }
    }

    /**
     * 是否显示底部进度条，默认显示
     */
    public void showBottomProgress(boolean isShow) {
        mIsShowBottomProgress = isShow;
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
        View view = findViewById(R.id.module_mediaplayer_controller_bottom_progress);
        View viewRoot = findViewById(R.id.module_mediaplayer_controller_bottom_root);
        if (isVisible) {
            viewRoot.setVisibility(VISIBLE);
            if (anim != null) {
                viewRoot.startAnimation(anim);
            }
            if (mIsShowBottomProgress) {
                view.setVisibility(GONE);
            }
        } else {
            viewRoot.setVisibility(GONE);
            if (anim != null) {
                viewRoot.startAnimation(anim);
            }
            if (mIsShowBottomProgress) {
                view.setVisibility(VISIBLE);
                AlphaAnimation animation = new AlphaAnimation(0f, 1f);
                animation.setDuration(300);
                view.startAnimation(animation);
            }
        }
    }

    @Override
    public void onPlayStateChanged(int playState) {
        View viewPlayer = findViewById(R.id.module_mediaplayer_controller_bottom_play);
        View viewRoot = findViewById(R.id.module_mediaplayer_controller_bottom_root);
        ProgressBar progressBar = findViewById(R.id.module_mediaplayer_controller_bottom_progress);
        switch (playState) {
            case PlayerType.StateType.STATE_INIT:
            case PlayerType.StateType.STATE_BUFFERING_PLAYING:
                setVisibility(GONE);
                progressBar.setProgress(0);
                progressBar.setSecondaryProgress(0);
                SeekBar seekBar = findViewById(R.id.module_mediaplayer_controller_bottom_seek);
                seekBar.setProgress(0);
                seekBar.setSecondaryProgress(0);
                break;
            case PlayerType.StateType.STATE_START_ABORT:
            case PlayerType.StateType.STATE_PREPARE_START:
            case PlayerType.StateType.STATE_PREPARE_END:
            case PlayerType.StateType.STATE_ERROR:
            case PlayerType.StateType.STATE_ONCE_LIVE:
                setVisibility(GONE);
                break;
            case PlayerType.StateType.STATE_START:
                viewPlayer.setSelected(true);
                if (mIsShowBottomProgress) {
                    if (mControllerWrapper.isShowing()) {
                        progressBar.setVisibility(GONE);
                        viewRoot.setVisibility(VISIBLE);
                    } else {
                        viewRoot.setVisibility(GONE);
                        progressBar.setVisibility(VISIBLE);
                    }
                } else {
                    viewRoot.setVisibility(GONE);
                }
                setVisibility(VISIBLE);
                //开始刷新进度
                mControllerWrapper.startProgress();
                break;
            case PlayerType.StateType.STATE_PAUSED:
                viewPlayer.setSelected(false);
                break;
            case PlayerType.StateType.STATE_BUFFERING_PAUSED:
            case PlayerType.StateType.STATE_END:
                viewPlayer.setSelected(mControllerWrapper.isPlaying());
                break;
        }
    }

    @Override
    public void onWindowStateChanged(int playerState) {
        View viewFull = findViewById(R.id.module_mediaplayer_controller_bottom_full);
        View viewRoot = findViewById(R.id.module_mediaplayer_controller_bottom_root);
        ProgressBar progressBar = findViewById(R.id.module_mediaplayer_controller_bottom_progress);
        switch (playerState) {
            case PlayerType.WindowType.NORMAL:
                viewFull.setSelected(false);
                break;
            case PlayerType.WindowType.FULL:
                viewFull.setSelected(true);
                break;
        }

        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity != null && mControllerWrapper.hasCutout()) {
            int orientation = activity.getRequestedOrientation();
            int cutoutHeight = mControllerWrapper.getCutoutHeight();
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                viewRoot.setPadding(0, 0, 0, 0);
                progressBar.setPadding(0, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                viewRoot.setPadding(cutoutHeight, 0, 0, 0);
                progressBar.setPadding(cutoutHeight, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                viewRoot.setPadding(0, 0, cutoutHeight, 0);
                progressBar.setPadding(0, 0, cutoutHeight, 0);
            }
        }
    }

    /**
     * 刷新进度回调，子类可在此方法监听进度刷新，然后更新ui
     *
     * @param duration 视频总时长
     * @param position 视频当前时长
     */
    @Override
    public void setProgress(int duration, int position) {
        if (mIsDragging) {
            return;
        }

        SeekBar seekBar = findViewById(R.id.module_mediaplayer_controller_bottom_seek);
        if (seekBar != null) {
            ProgressBar progressBar = findViewById(R.id.module_mediaplayer_controller_bottom_progress);
            if (duration > 0) {
                seekBar.setEnabled(true);
                int pos = (int) (position * 1.0 / duration * seekBar.getMax());
                seekBar.setProgress(pos);
                progressBar.setProgress(pos);
            } else {
                seekBar.setEnabled(false);
            }
            int percent = mControllerWrapper.getBufferedPercentage();
            if (percent >= 95) {
                //解决缓冲进度不能100%问题
                seekBar.setSecondaryProgress(seekBar.getMax());
                progressBar.setSecondaryProgress(progressBar.getMax());
            } else {
                seekBar.setSecondaryProgress(percent * 10);
                progressBar.setSecondaryProgress(percent * 10);
            }
        }

        TextView viewTotal = findViewById(R.id.module_mediaplayer_controller_bottom_total);
        if (viewTotal != null) {
            viewTotal.setText(PlayerUtils.formatTime(duration));
        }

        TextView viewTime = findViewById(R.id.module_mediaplayer_controller_bottom_time);
        if (viewTime != null) {
            viewTime.setText(PlayerUtils.formatTime(position));
        }


        if (PlayerConfig.newBuilder().build().mIsShowToast) {
            long time = PlayerConfig.newBuilder().build().mShowToastTime;
            if (time <= 0) {
                time = 5;
            }
            long currentPosition = mControllerWrapper.getPosition();
            Log.d("progress---", "duration---" + duration + "--currentPosition--" + currentPosition);
            if (duration - currentPosition < 2 * time * 1000) {
                //当前视频播放到最后3s时，弹出toast提示：即将自动为您播放下一个视频。
                if ((duration - currentPosition) / 1000 % 60 == time) {
                    Log.d("progress---", "即将自动为您播放下一个视频");
                    if (listener != null) {
                        listener.showToastOrDialog();
                    }
                }
            }
        }
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

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mIsDragging = true;
        mControllerWrapper.stopProgress();
        mControllerWrapper.stopFadeOut();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        ProgressBar progressBar = findViewById(R.id.module_mediaplayer_controller_bottom_progress);
        long duration = mControllerWrapper.getDuration();
        long newPosition = (duration * seekBar.getProgress()) / progressBar.getMax();
        mControllerWrapper.seekTo((int) newPosition);
        mIsDragging = false;
        mControllerWrapper.startProgress();
        mControllerWrapper.startFadeOut();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }
        ProgressBar progressBar = findViewById(R.id.module_mediaplayer_controller_bottom_progress);
        long duration = mControllerWrapper.getDuration();
        long newPosition = (duration * progress) / progressBar.getMax();

        TextView viewTime = findViewById(R.id.module_mediaplayer_controller_bottom_time);
        if (viewTime != null) {
            viewTime.setText(PlayerUtils.formatTime(newPosition));
        }
    }

    private OnToastListener listener;

    public void setListener(OnToastListener listener) {
        this.listener = listener;
    }

    public interface OnToastListener {
        void showToastOrDialog();
    }
}
