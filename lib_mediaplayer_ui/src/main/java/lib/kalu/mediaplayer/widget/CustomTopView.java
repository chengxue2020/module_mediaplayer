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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.controller.ControlWrapper;
import lib.kalu.mediaplayer.config.PlayerType;
import lib.kalu.mediaplayer.util.PlayerUtils;

/**
 * description: 控制器菜单 => 顶部 => 返回、标题、电量、时间
 * created by kalu on 2021/11/22
 */
public class CustomTopView extends RelativeLayout implements ImplController {

    private ControlWrapper mControlWrapper;

    // 电量监控
    private BatteryReceiver mBatteryReceiver = new BatteryReceiver();

    public CustomTopView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CustomTopView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomTopView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.module_mediaplayer_video_top, this, true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setVisibility(View.INVISIBLE);

        // 监听
        View view = findViewById(R.id.module_mediaplayer_controller_top_back);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击返回键
                Activity activity = PlayerUtils.scanForActivity(getContext());
                if (activity != null && mControlWrapper.isFullScreen()) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    mControlWrapper.stopFullScreen();
                }
                //如果不是全屏模式，则直接关闭页面activity
                else if (PlayerUtils.isActivityLiving(activity)) {
                    activity.finish();
                }
            }
        });
    }

    public void setTitle(String title) {
        TextView viewTitle = findViewById(R.id.module_mediaplayer_controller_top_title);
        viewTitle.setText(null == title || title.length() == 0 ? "" : title);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != mBatteryReceiver) {
            getContext().unregisterReceiver(mBatteryReceiver);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (null != mBatteryReceiver) {
            getContext().registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }
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
        //只在全屏时才有效
        if (isVisible) {
            if (getVisibility() == GONE) {
                TextView viewTime = findViewById(R.id.module_mediaplayer_controller_top_time);
                viewTime.setText(PlayerUtils.getCurrentSystemTime());
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
        if (getVisibility() == VISIBLE) {
            if (mControlWrapper.isFullScreen()) {
                // 显示电量
                TextView viewBattery = findViewById(R.id.module_mediaplayer_controller_top_battery);
                viewBattery.setVisibility(VISIBLE);
                TextView viewTime = findViewById(R.id.module_mediaplayer_controller_top_title);
                viewTime.setVisibility(VISIBLE);
            } else {
                // 不显示电量
                TextView viewBattery = findViewById(R.id.module_mediaplayer_controller_top_battery);
                viewBattery.setVisibility(GONE);
                TextView viewTime = findViewById(R.id.module_mediaplayer_controller_top_title);
                viewTime.setVisibility(GONE);
            }
        }
    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_IDLE:
            case PlayerType.StateType.STATE_START_ABORT:
            case PlayerType.StateType.STATE_PREPARING:
            case PlayerType.StateType.STATE_PREPARED:
            case PlayerType.StateType.STATE_ERROR:
            case PlayerType.StateType.STATE_BUFFERING_PLAYING:
                setVisibility(GONE);
                break;
        }
    }

    @Override
    public void onWindowStateChanged(int playerState) {
        if (playerState == PlayerType.WindowType.FULL) {
            if (mControlWrapper.isShowing() && !mControlWrapper.isLocked()) {
                setVisibility(VISIBLE);
                TextView viewTime = findViewById(R.id.module_mediaplayer_controller_top_title);
                viewTime.setText(PlayerUtils.getCurrentSystemTime());
            }
            TextView viewTitle = findViewById(R.id.module_mediaplayer_controller_top_title);
            viewTitle.setSelected(true);
        } else {
            setVisibility(GONE);
            TextView viewTitle = findViewById(R.id.module_mediaplayer_controller_top_title);
            viewTitle.setSelected(false);
        }

        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity != null && mControlWrapper.hasCutout()) {
            int orientation = activity.getRequestedOrientation();
            int cutoutHeight = mControlWrapper.getCutoutHeight();
            View viewRoot = findViewById(R.id.module_mediaplayer_controller_top_root);
            //设置屏幕的变化是，标题的值。后期有需要在暴露给开发者设置
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                //切换成竖屏的时候调用
                viewRoot.setPadding(PlayerUtils.dp2px(getContext(), 12),
                        PlayerUtils.dp2px(getContext(), 10), PlayerUtils.dp2px(getContext(), 12), 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                //切换成横屏的时候调用
                viewRoot.setPadding(cutoutHeight, 0, PlayerUtils.dp2px(getContext(), 12), 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                viewRoot.setPadding(0, 0, cutoutHeight, 0);
            } else {
                viewRoot.setPadding(PlayerUtils.dp2px(getContext(), 12),
                        PlayerUtils.dp2px(getContext(), 10), PlayerUtils.dp2px(getContext(), 12), 0);
            }
        }
    }

    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        if (isLocked) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
            TextView viewTime = findViewById(R.id.module_mediaplayer_controller_top_title);
            viewTime.setText(PlayerUtils.getCurrentSystemTime());
        }
    }


    private final class BatteryReceiver extends BroadcastReceiver {

        public BatteryReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (null != extras) {
                int current = extras.getInt("level", -1);// 获得当前电量
                int total = extras.getInt("scale", -1);// 获得总电量
                if (current != -1 && total != -1 && current <= total) {
                    int percent = current * 100 / total;
                    TextView viewBattery = findViewById(R.id.module_mediaplayer_controller_top_battery);
                    viewBattery.setText(percent + "");
                }
            }
        }
    }
}
