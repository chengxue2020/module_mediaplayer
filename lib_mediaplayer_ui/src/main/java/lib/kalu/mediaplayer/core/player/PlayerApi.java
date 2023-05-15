package lib.kalu.mediaplayer.core.player;

import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.util.MPLogUtil;

public interface PlayerApi extends PlayerApiBuriedEvent, PlayerApiBase, PlayerApiKernel, PlayerApiDevice, PlayerApiComponent, PlayerApiCache, PlayerApiRender, PlayerApiExternalMusic {

    default boolean dispatchKeyEventPlayer(@NonNull KeyEvent event) {

        MPLogUtil.log("PlayerApi => dispatchKeyEventPlayer => action = " + event.getAction() + ", keycode = " + event.getKeyCode() + ", this = " + this);

        // full
        if (isFull()) {
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
                stopFull();
            }
            // center
            else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                toggle();
            }
            // component
            else {
                dispatchEventComponent(event);
            }
            return true;
        }
        // float
        else if (isFloat()) {
            // stopFloat
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                stopFloat();
                return true;
            }
        }
        return false;
    }

    default void checkOnWindowVisibilityChanged(int visibility) {
        try {
            String url = getUrl();
            if (null == url || url.length() <= 0)
                throw new Exception("url error: " + url);
            boolean playing = isPlaying();
            boolean windowVisibilityChangedRelease = isWindowVisibilityChangedRelease();
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
        } catch (Exception e) {
            MPLogUtil.log("PlayerApi => checkOnWindowVisibilityChanged => " + e.getMessage());
        }
    }

    default void checkOnDetachedFromWindow(@NonNull boolean releaseTag) {
        try {
            String url = getUrl();
            if (null == url || url.length() <= 0)
                throw new Exception("url error: " + url);
            release(releaseTag, false);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApi => checkOnDetachedFromWindow => " + e.getMessage());
        }
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
