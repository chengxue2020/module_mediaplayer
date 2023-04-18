package lib.kalu.mediaplayer.core.player;

import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.util.MPLogUtil;

public interface PlayerApi extends PlayerApiBuriedEvent, PlayerApiBase, PlayerApiKernel, PlayerApiDevice, PlayerApiComponent, PlayerApiCache, PlayerApiRender, PlayerApiExternalMusic {

    default boolean dispatchEvent(@NonNull KeyEvent event) {

        boolean isFull = isFull();
        boolean isFloat = isFloat();
        MPLogUtil.log("PlayerApi => dispatchEvent => isFloat = " + isFloat + ", isFull = " + isFull);

        // full
        if (isFull) {
            // volume_up
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
                return false;
            }
            // volume_down
            else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                return false;
            }
            // volume_mute
            else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_MUTE) {
                return false;
            }
            // voice_assist
            else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_VOICE_ASSIST) {
                return false;
            }
            // stopFull
            else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                MPLogUtil.log("PlayerApi => dispatchEvent => stopFull =>");
                stopFull();
            }
            // center
            else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                MPLogUtil.log("PlayerApi => dispatchEvent => toggle =>");
                toggle();
            }
            // component
            else {
                dispatchEventComponent(event);
            }
            return true;
        }
        // float
        else if (isFloat) {
            // stopFloat
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                MPLogUtil.log("PlayerApi => dispatchEvent => stopFloat =>");
                stopFloat();
                return true;
            }
        }
        return false;
    }

    default void checkOnWindowVisibilityChanged(int visibility) {

        String url = getUrl();
        boolean playing = isPlaying();
        boolean windowVisibilityChangedRelease = isWindowVisibilityChangedRelease();
        MPLogUtil.log("PlayerApi => checkOnWindowVisibilityChanged => url = " + url + ", playing = " + playing + ", visibility = " + visibility + ", windowVisibilityChangedRelease = " + windowVisibilityChangedRelease + ", this = " + this);
        if (null == url || url.length() <= 0)
            return;

        // show
        if (visibility == View.VISIBLE) {
            if (playing)
                return;
            if (windowVisibilityChangedRelease) {
                restart();
            } else {
                resume(false);
            }
        }
        // hide
        else {
            if (windowVisibilityChangedRelease) {
                release();
            } else {
                pause(true);
            }
        }
    }

    default void checkOnDetachedFromWindow() {

        String url = getUrl();
        MPLogUtil.log("PlayerApi => checkOnDetachedFromWindow => url = " + url + ", this = " + this);
        if (null == url || url.length() <= 0)
            return;

        release();
    }

    default void checkOnAttachedToWindow() {

        String url = getUrl();
        boolean playing = isPlaying();
        MPLogUtil.log("PlayerApi => checkOnAttachedToWindow => url = " + url + ", playing = " + playing + ", this = " + this);
        if (null == url || url.length() <= 0 || playing) return;

        restart();
    }

    default void onSaveBundle() {
        try {
            String url = getUrl();
            if (null == url || url.length() <= 0)
                return;
            long position = getPosition();
            long duration = getDuration();
            saveBundle(getBaseContext(), url, position, duration);
        } catch (Exception e) {
        }
    }
}
