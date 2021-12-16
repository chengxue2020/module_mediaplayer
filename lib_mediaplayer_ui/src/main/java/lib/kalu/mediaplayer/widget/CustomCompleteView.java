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
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.widget.controller.ControlWrapper;
import lib.kalu.mediaplayer.config.PlayerType;
import lib.kalu.mediaplayer.util.PlayerUtils;


/**
 * <pre>
 *     @author yangchong
 *     blog  : https://github.com/yangchong211
 *     time  : 2017/11/9
 *     desc  : 自动播放完成界面
 *     revise:
 * </pre>
 */
public class CustomCompleteView extends FrameLayout implements ImplController {

    private ControlWrapper mControlWrapper;

    public CustomCompleteView(@NonNull Context context) {
        super(context);
        init();
    }

    public CustomCompleteView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomCompleteView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_video_completed, this, true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setClickable(true);
        setVisibility(GONE);

        // 重试
        findViewById(R.id.controller_complete_retry).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                restart(mControlWrapper);
            }
        });

        // 返回
        findViewById(R.id.controller_complete_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(getContext(), mControlWrapper);
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
        if (playState == PlayerType.StateType.STATE_BUFFERING_PLAYING) {
            setVisibility(VISIBLE);
            View view = findViewById(R.id.controller_complete_back);
            view.setVisibility(mControlWrapper.isFullScreen() ? VISIBLE : GONE);
            bringToFront();
        } else {
            setVisibility(GONE);
        }
    }

    @Override
    public void onWindowStateChanged(int playerState) {
        if (playerState == PlayerType.WindowType.FULL) {
            View view = findViewById(R.id.controller_complete_back);
            view.setVisibility(VISIBLE);
        } else if (playerState == PlayerType.WindowType.NORMAL) {
            View view = findViewById(R.id.controller_complete_back);
            view.setVisibility(GONE);
        }

        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity != null && mControlWrapper.hasCutout()) {
            int orientation = activity.getRequestedOrientation();
            int cutoutHeight = mControlWrapper.getCutoutHeight();
            View view = findViewById(R.id.controller_complete_back);
            RelativeLayout.LayoutParams sflp = (RelativeLayout.LayoutParams) view.getLayoutParams();
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                sflp.setMargins(0, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                sflp.setMargins(cutoutHeight, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                sflp.setMargins(0, 0, 0, 0);
            }
        }
    }

    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLock) {

    }

}
