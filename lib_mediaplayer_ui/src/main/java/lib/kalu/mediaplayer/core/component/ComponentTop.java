package lib.kalu.mediaplayer.core.component;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerType;

public class ComponentTop extends RelativeLayout implements ComponentApi {

    private BatteryReceiver mBatteryReceiver = new BatteryReceiver();

    public ComponentTop(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ComponentTop(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ComponentTop(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.module_mediaplayer_component_top, this, true);
        setVisibility(View.INVISIBLE);

        // 监听
        View view = findViewById(R.id.module_mediaplayer_controller_top_back);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                //点击返回键
//                Activity activity = PlayerUtils.scanForActivity(getContext());
//                if (activity != null && mControllerWrapper.isFull()) {
//                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                    mControllerWrapper.stopFullScreen();
//                }
//                //如果不是全屏模式，则直接关闭页面activity
//                else if (PlayerUtils.isActivityLiving(activity)) {
//                    activity.finish();
//                }
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

//    @Override
//    public void onVisibilityChanged(boolean isVisible, Animation anim) {
//        //只在全屏时才有效
//        if (isVisible) {
//            if (getVisibility() == GONE) {
//                TextView viewTime = findViewById(R.id.module_mediaplayer_controller_top_time);
//                viewTime.setText(PlayerUtils.getCurrentSystemTime());
//                setVisibility(VISIBLE);
//                if (anim != null) {
//                    startAnimation(anim);
//                }
//            }
//        } else {
//            if (getVisibility() == VISIBLE) {
//                setVisibility(GONE);
//                if (anim != null) {
//                    startAnimation(anim);
//                }
//            }
//        }
//        if (getVisibility() == VISIBLE) {
////            if (mControllerWrapper.isFullScreen()) {
////                // 显示电量
////                TextView viewBattery = findViewById(R.id.module_mediaplayer_controller_top_battery);
////                viewBattery.setVisibility(VISIBLE);
////                TextView viewTime = findViewById(R.id.module_mediaplayer_controller_top_title);
////                viewTime.setVisibility(VISIBLE);
////            } else {
////                // 不显示电量
////                TextView viewBattery = findViewById(R.id.module_mediaplayer_controller_top_battery);
////                viewBattery.setVisibility(GONE);
////                TextView viewTime = findViewById(R.id.module_mediaplayer_controller_top_title);
////                viewTime.setVisibility(GONE);
////            }
//        }
//    }


    @Override
    public void callPlayerEvent(@NonNull int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_INIT:
            case PlayerType.StateType.STATE_START_ABORT:
            case PlayerType.StateType.STATE_LOADING_START:
            case PlayerType.StateType.STATE_LOADING_STOP:
            case PlayerType.StateType.STATE_ERROR:
            case PlayerType.StateType.STATE_BUFFERING_START:
                setVisibility(GONE);
                break;
        }
    }

    @Override
    public void callWindowEvent(int state) {
//        if (state == PlayerType.WindowType.FULL) {
//            if (mControllerWrapper.isShowing() && !mControllerWrapper.isLocked()) {
//                setVisibility(VISIBLE);
//                TextView viewTime = findViewById(R.id.module_mediaplayer_controller_top_title);
//                viewTime.setText(PlayerUtils.getCurrentSystemTime());
//            }
//            TextView viewTitle = findViewById(R.id.module_mediaplayer_controller_top_title);
//            viewTitle.setSelected(true);
//        } else {
//            setVisibility(GONE);
//            TextView viewTitle = findViewById(R.id.module_mediaplayer_controller_top_title);
//            viewTitle.setSelected(false);
//        }

//        Activity activity = PlayerUtils.scanForActivity(getContext());
//        if (activity != null && getPlayerApi().hasCutout()) {
//            int orientation = activity.getRequestedOrientation();
//            int cutoutHeight = mControllerWrapper.getCutoutHeight();
//            View viewRoot = findViewById(R.id.module_mediaplayer_controller_top_root);
//            //设置屏幕的变化是，标题的值。后期有需要在暴露给开发者设置
//            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//                //切换成竖屏的时候调用
//                viewRoot.setPadding(PlayerUtils.dp2px(getContext(), 12),
//                        PlayerUtils.dp2px(getContext(), 10), PlayerUtils.dp2px(getContext(), 12), 0);
//            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//                //切换成横屏的时候调用
//                viewRoot.setPadding(cutoutHeight, 0, PlayerUtils.dp2px(getContext(), 12), 0);
//            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
//                viewRoot.setPadding(0, 0, cutoutHeight, 0);
//            } else {
//                viewRoot.setPadding(PlayerUtils.dp2px(getContext(), 12),
//                        PlayerUtils.dp2px(getContext(), 10), PlayerUtils.dp2px(getContext(), 12), 0);
//            }
//        }
    }

//    @Override
//    public void onLockStateChanged(boolean isLocked) {
//        if (isLocked) {
//            setVisibility(GONE);
//        } else {
//            setVisibility(VISIBLE);
//            TextView viewTime = findViewById(R.id.module_mediaplayer_controller_top_title);
//            viewTime.setText(PlayerUtils.getCurrentSystemTime());
//        }
//    }


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
