package lib.kalu.mediaplayer.videoui.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.videoui.config.ConstantKeys;

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
        Log.e("BaseVideoControllerTV", "dispatchKeyEvent => " + event.getKeyCode());

        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            Toast.makeText(getContext(), "显示", Toast.LENGTH_SHORT).show();
            show();
        } else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            Toast.makeText(getContext(), "隐藏", Toast.LENGTH_SHORT).show();
            hide();
        } else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
            Toast.makeText(getContext(), "暂停", Toast.LENGTH_SHORT).show();
            mControlWrapper.togglePlay();
//            long currentPosition = mControlWrapper.getCurrentPosition();
//            mControlWrapper.seekTo(currentPosition + 1000);
        } else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            Toast.makeText(getContext(), "恢复", Toast.LENGTH_SHORT).show();
            mControlWrapper.togglePlay();
//            long currentPosition = mControlWrapper.getCurrentPosition();
//            mControlWrapper.seekTo(currentPosition - 1000);
        }
        return super.dispatchKeyEvent(event);
    }
}
