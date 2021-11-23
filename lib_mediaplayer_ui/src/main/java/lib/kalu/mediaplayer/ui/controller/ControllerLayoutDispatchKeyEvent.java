package lib.kalu.mediaplayer.ui.controller;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import lib.kalu.mediaplayer.keycode.KeycodeImpl;
import lib.kalu.mediaplayer.ui.config.PlayerConfig;
import lib.kalu.mediaplayer.ui.config.PlayerConfigManager;
import lib.kalu.mediaplayer.util.LogUtil;

/**
 * description:
 * created by kalu on 2021/9/16
 */
abstract class ControllerLayoutDispatchKeyEvent extends ControllerLayout {

    public ControllerLayoutDispatchKeyEvent(@NonNull Context context) {
        super(context);
    }

    public ControllerLayoutDispatchKeyEvent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ControllerLayoutDispatchKeyEvent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ControllerLayoutDispatchKeyEvent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void init() {
        super.init();
        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        PlayerConfig config = PlayerConfigManager.getInstance().getConfig();
        KeycodeImpl mKeycodeImpl = config.mKeycodeImpl;
        // 返回
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == mKeycodeImpl.back() && isShowing()) {
            LogUtil.log("dispatchKeyEvent[返回] => " + event.getKeyCode());
            hide();
        }
        // 方向键：中间
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == mKeycodeImpl.dpadCenter()) {
            LogUtil.log("dispatchKeyEvent[方向键：中间] => " + event.getKeyCode());
            show();
        }
        // 快进
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == mKeycodeImpl.fastForward()) {
            LogUtil.log("dispatchKeyEvent[快进] => " + event.getKeyCode());
            long position = mControlWrapper.getCurrentPosition() + 1000;
            long duration = mControlWrapper.getDuration();
            if (position > duration) {
                position = duration;
            }
            mControlWrapper.seekTo(position);
        }
        // 快退
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == mKeycodeImpl.fastRewind()) {
            LogUtil.log("dispatchKeyEvent[快退] => " + event.getKeyCode());
            long position = mControlWrapper.getCurrentPosition() - 1000;
            if (position <= 0) {
                position = 0;
            }
            mControlWrapper.seekTo(position);
        }
        // 房子
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == mKeycodeImpl.home()) {
            LogUtil.log("dispatchKeyEvent[房子] => " + event.getKeyCode());
            Toast.makeText(getContext(), "房子", Toast.LENGTH_SHORT).show();
        }
        // 菜单
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == mKeycodeImpl.menu()) {
            LogUtil.log("dispatchKeyEvent[菜单] => " + event.getKeyCode());
            Toast.makeText(getContext(), "菜单", Toast.LENGTH_SHORT).show();
        }
//        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
//            Toast.makeText(getContext(), "显示", Toast.LENGTH_SHORT).show();
//            show();
//        } else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
//            Toast.makeText(getContext(), "隐藏", Toast.LENGTH_SHORT).show();
//            hide();
//        } else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
//            Toast.makeText(getContext(), "暂停", Toast.LENGTH_SHORT).show();
//            mControlWrapper.togglePlay();
////            long currentPosition = mControlWrapper.getCurrentPosition();
////            mControlWrapper.seekTo(currentPosition + 1000);
//        } else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
//            Toast.makeText(getContext(), "恢复", Toast.LENGTH_SHORT).show();
//            mControlWrapper.togglePlay();
////            long currentPosition = mControlWrapper.getCurrentPosition();
////            mControlWrapper.seekTo(currentPosition - 1000);
//        }
        return super.dispatchKeyEvent(event);
    }
}
