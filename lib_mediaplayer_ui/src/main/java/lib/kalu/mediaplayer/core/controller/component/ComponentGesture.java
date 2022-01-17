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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.core.controller.base.ControllerWrapper;
import lib.kalu.mediaplayer.config.PlayerType;
import lib.kalu.mediaplayer.core.controller.impl.ImplGesture;
import lib.kalu.mediaplayer.util.PlayerUtils;
import lib.kalu.mediaplayer.util.MediaLogUtil;

/**
 * description:手势控制, 用于滑动改变亮度和音量的功能
 * created by kalu on 2021/11/23
 */
public class ComponentGesture extends FrameLayout implements ImplGesture {

    private ControllerWrapper mControllerWrapper;

    public ComponentGesture(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ComponentGesture(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ComponentGesture(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.module_mediaplayer_video_gesture, this, true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setVisibility(INVISIBLE);
        initListener();
    }

    private void initListener() {
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

    }

    @Override
    public void onWindowStateChanged(int playerState) {

    }

    /**
     * 开始滑动
     */
    @Override
    public void onStartSlide() {
        mControllerWrapper.hide();
        View viewRoot = findViewById(R.id.module_mediaplayer_controller_gesture_root);
        viewRoot.setVisibility(VISIBLE);
        viewRoot.setAlpha(1f);
    }

    /**
     * 结束滑动
     * 这个是指，手指抬起或者意外结束事件的时候，调用这个方法
     */
    @Override
    public void onStopSlide() {
        View viewRoot = findViewById(R.id.module_mediaplayer_controller_gesture_root);
        viewRoot.animate()
                .alpha(0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        viewRoot.setVisibility(GONE);
                    }
                })
                .start();
    }

    /**
     * 滑动调整进度
     *
     * @param slidePosition   滑动进度
     * @param currentPosition 当前播放进度
     * @param duration        视频总长度
     */
    @Override
    public void onPositionChange(int slidePosition, int currentPosition, int duration) {
        MediaLogUtil.log("onPositionChange => slidePosition = " + slidePosition + ", currentPosition = " + currentPosition + ", duration = " + duration);
        TextView viewText = findViewById(R.id.module_mediaplayer_controller_gesture_text);
        if (slidePosition > currentPosition) {
            viewText.setText("快进\n" + String.format("%s/%s", PlayerUtils.formatTime(slidePosition), PlayerUtils.formatTime(duration)));
        } else {
            viewText.setText("快退\n" + String.format("%s/%s", PlayerUtils.formatTime(slidePosition), PlayerUtils.formatTime(duration)));
        }
        ProgressBar viewProgress = findViewById(R.id.module_mediaplayer_controller_gesture_progress);
        viewProgress.setVisibility(GONE);
    }

    /**
     * 滑动调整亮度
     *
     * @param percent 亮度百分比
     */
    @Override
    public void onBrightnessChange(int percent) {
        TextView viewText = findViewById(R.id.module_mediaplayer_controller_gesture_text);
        viewText.setText("亮度\n" + percent + "%");
        ProgressBar viewProgress = findViewById(R.id.module_mediaplayer_controller_gesture_progress);
        viewProgress.setVisibility(VISIBLE);
        viewProgress.setProgress(percent);
    }

    /**
     * 滑动调整音量
     *
     * @param percent 音量百分比
     */
    @Override
    public void onVolumeChange(int percent) {
        TextView viewText = findViewById(R.id.module_mediaplayer_controller_gesture_text);
        if (percent <= 0) {
            viewText.setText("静音\n" + percent + "%");
        } else {
            viewText.setText("音量\n" + percent + "%");
        }
        ProgressBar viewProgress = findViewById(R.id.module_mediaplayer_controller_gesture_progress);
        viewProgress.setVisibility(VISIBLE);
        viewProgress.setProgress(percent);
    }

    @Override
    public void onPlayStateChanged(int playState) {
        if (playState == PlayerType.StateType.STATE_INIT
                || playState == PlayerType.StateType.STATE_START_ABORT
                || playState == PlayerType.StateType.STATE_PREPARE_START
                || playState == PlayerType.StateType.STATE_PREPARE_END
                || playState == PlayerType.StateType.STATE_ERROR
                || playState == PlayerType.StateType.STATE_BUFFERING_PLAYING
                || playState == PlayerType.StateType.STATE_ONCE_LIVE) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
        }
    }

    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLock) {

    }

}
