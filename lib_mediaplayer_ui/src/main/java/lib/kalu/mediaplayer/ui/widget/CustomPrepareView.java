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
package lib.kalu.mediaplayer.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.ui.bridge.ControlWrapper;
import lib.kalu.mediaplayer.ui.config.PlayerConfigManager;
import lib.kalu.mediaplayer.ui.config.PlayerType;


/**
 * <pre>
 *     @author yangchong
 *     blog  : https://github.com/yangchong211
 *     time  : 2017/11/9
 *     desc  : 预加载准备播放页面视图
 *     revise:
 * </pre>
 */
public class CustomPrepareView extends FrameLayout implements InterControlView {

    private ControlWrapper mControlWrapper;
    private FrameLayout mFlNetWarning;
    private TextView mTvStart;

    public CustomPrepareView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CustomPrepareView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomPrepareView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        setFocusable(true);
        setFocusableInTouchMode(true);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_video_prepare, this, true);
        initFindViewById(view);
        initListener();
    }

    private void initFindViewById(View view) {
        mFlNetWarning = view.findViewById(R.id.fl_net_warning);
        mTvStart = view.findViewById(R.id.tv_start);
    }

    private void initListener() {
        mTvStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mFlNetWarning.setVisibility(GONE);
                PlayerConfigManager.instance().setPlayOnMobileNetwork(true);
                mControlWrapper.restart();
            }
        });
    }

    /**
     * 设置点击此界面开始播放
     */
    public void setClickStart() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mControlWrapper.restart();
            }
        });
    }

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
        mControlWrapper = controlWrapper;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {

    }

    @Override
    public void onPlayStateChanged(int playState) {
        View viewLoading = findViewById(R.id.module_mediaplayer_controller_prepare_loading);
        View viewPlay = findViewById(R.id.module_mediaplayer_controller_prepare_play);
        View viewThumb = findViewById(R.id.module_mediaplayer_controller_prepare_thumb);
        switch (playState) {
            case PlayerType.StateType.STATE_PREPARING:
                bringToFront();
                setVisibility(VISIBLE);
                viewPlay.setVisibility(View.GONE);
                mFlNetWarning.setVisibility(GONE);
                viewLoading.setVisibility(View.VISIBLE);
                break;
            case PlayerType.StateType.STATE_PLAYING:
            case PlayerType.StateType.STATE_PAUSED:
            case PlayerType.StateType.STATE_ERROR:
            case PlayerType.StateType.STATE_BUFFERING_PAUSED:
            case PlayerType.StateType.STATE_COMPLETED:
            case PlayerType.StateType.STATE_BUFFERING_PLAYING:
            case PlayerType.StateType.STATE_ONCE_LIVE:
                setVisibility(GONE);
                break;
            case PlayerType.StateType.STATE_IDLE:
                setVisibility(VISIBLE);
                bringToFront();
                viewLoading.setVisibility(View.GONE);
                mFlNetWarning.setVisibility(GONE);
                viewPlay.setVisibility(View.VISIBLE);
                viewThumb.setVisibility(View.VISIBLE);
                break;
            case PlayerType.StateType.STATE_START_ABORT:
                setVisibility(VISIBLE);
                mFlNetWarning.setVisibility(VISIBLE);
                mFlNetWarning.bringToFront();
                break;
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {
    }

    @Override
    public void setProgress(int duration, int position) {
    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
    }

    @Override
    public ImageView getPrepare() {
        ImageView viewThumb = findViewById(R.id.module_mediaplayer_controller_prepare_thumb);
        return viewThumb;
    }
}
