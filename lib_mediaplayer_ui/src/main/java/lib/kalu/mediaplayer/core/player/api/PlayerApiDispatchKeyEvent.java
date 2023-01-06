package lib.kalu.mediaplayer.core.player.api;

import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.controller.base.ControllerLayout;
import lib.kalu.mediaplayer.util.MPLogUtil;

interface PlayerApiDispatchKeyEvent extends PlayerApiMedia, PlayerApiWindow {

    default boolean dispatchKeyEvent(@NonNull View view, @NonNull KeyEvent event) {

        boolean focusable = view.isFocusable();
        if (focusable) {
            boolean isFull = isFull();
            boolean isFloat = isFloat();
            if (isFloat) {
                // stopFloat
                if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    MPLogUtil.log("dispatchKeyEvent => stopFloat =>");
                    stopFloat();
                }
                return true;
            } else if (isFull) {
                // toogle
                if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                    MPLogUtil.log("dispatchKeyEvent => toggle =>");
                    toggle();
                }
                // seekForward
                else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    ControllerLayout layout = getControlLayout();
                    if (null != layout) {
                        boolean live = isLive();
                        MPLogUtil.log("dispatchKeyEvent => seekForward[ACTION_DOWN] => live = " + live);
                        layout.seekForwardDown(!live);
                    }
                }
                // seekForward
                else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    ControllerLayout layout = getControlLayout();
                    if (null != layout) {
                        boolean live = isLive();
                        MPLogUtil.log("dispatchKeyEvent => seekForward[ACTION_UP] => live = " + live);
                        layout.seekForwardUp(!live);
                    }
                }
                // seekRewind
                else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    ControllerLayout layout = getControlLayout();
                    if (null != layout) {
                        boolean live = isLive();
                        MPLogUtil.log("dispatchKeyEvent => seekRewind[ACTION_DOWN] => live = " + live);
                        layout.seekRewindDown(!live);
                    }
                }
                // seekRewind
                else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    ControllerLayout layout = getControlLayout();
                    if (null != layout) {
                        boolean live = isLive();
                        MPLogUtil.log("dispatchKeyEvent => seekRewind[ACTION_UP] => live = " + live);
                        layout.seekRewindUp(!live);
                    }
                }
                // stopFull
                else if (isFull() && event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    MPLogUtil.log("decodeKeyEvent => stopFull =>");
                    stopFull();
                }
                // VOLUME_UP
                else if (isFull() && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
                    return false;
                }
                // VOLUME_DOWN
                else if (isFull() && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    return false;
                }
                // VOLUME_MUTE
                else if (isFull() && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_MUTE) {
                    return false;
                }
                // VOICE_ASSIST
                else if (isFull() && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_VOICE_ASSIST) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }
}
