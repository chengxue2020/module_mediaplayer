package lib.kalu.mediaplayer.core.controller.base;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.controller.ControllerApi;
import lib.kalu.mediaplayer.core.controller.impl.ComponentApi;
import lib.kalu.mediaplayer.core.player.api.PlayerApi;

@Keep
public final class ControllerWrapper {

    // 播放器
    private PlayerApi mPlayer;
    // 控制器
    private ControllerApi mController;

    public ControllerWrapper(@NonNull PlayerApi player, @NonNull ControllerApi controller) {
        this.mPlayer = player;
        this.mController = controller;
    }

    public String getTcpSpeed() {
        try {
            return mPlayer.getTcpSpeed();
        } catch (Exception e) {
            return "0kb/s";
        }
    }

    public long getDuration() {
        try {
            return mPlayer.getDuration();
        } catch (Exception e) {
            return 0L;
        }
    }

    public long getPosition() {
        try {
            return mPlayer.getPosition();
        } catch (Exception e) {
            return 0L;
        }
    }

    public void seekTo(long time) {
        try {
            mPlayer.seekTo(time);
        } catch (Exception e) {
        }
    }

    public void seekTo(boolean foce, long time) {
        try {
            mPlayer.seekTo(foce, time);
        } catch (Exception e) {
        }
    }

    public void toggle() {
        try {
            mPlayer.toggle();
        } catch (Exception e) {
        }
    }

    public void restart() {
        try {
            mPlayer.restart();
        } catch (Exception e) {
        }
    }

    public boolean isPlaying() {
        try {
            return mPlayer.isPlaying();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isFull() {
        try {
            return mPlayer.isFull();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLive() {
        try {
            return mPlayer.isLive();
        } catch (Exception e) {
            return false;
        }
    }

    public void toggleLockState() {
    }

    public void toggleShowState() {
    }

    public void hide() {
    }

    public boolean isShowing() {
        return false;
    }

    public boolean isLocked() {
        return false;
    }

    public boolean hasCutout() {
        return false;
    }

    public int getCutoutHeight() {
        return 0;
    }
}
