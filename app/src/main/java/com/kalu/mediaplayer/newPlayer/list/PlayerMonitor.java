package com.kalu.mediaplayer.newPlayer.list;

import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.kernel.video.utils.VideoLogUtils;
import lib.kalu.mediaplayer.ui.bridge.ControlWrapper;
import lib.kalu.mediaplayer.ui.tool.PlayerUtils;
import lib.kalu.mediaplayer.ui.ui.view.InterControlView;

public class PlayerMonitor implements InterControlView {

    private ControlWrapper mControlWrapper;
    private static final String TAG = "PlayerMonitor";

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
        mControlWrapper = controlWrapper;
    }

    @Override
    public View getView() {
        return null;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {
        VideoLogUtils.d(TAG + "---" + "onVisibilityChanged: " + isVisible);
    }

    @Override
    public void onPlayStateChanged(int playState) {
        VideoLogUtils.d(TAG + "---" + "onPlayStateChanged: " + PlayerUtils.playState2str(playState));
    }

    @Override
    public void onPlayerStateChanged(int playerState) {
        VideoLogUtils.d(TAG + "---" + "onPlayerStateChanged: " + PlayerUtils.playerState2str(playerState));
    }

    @Override
    public void setProgress(int duration, int position) {
        VideoLogUtils.d(TAG + "---" + "setProgress: duration: " + duration + " position: " + position + " buffered percent: " + mControlWrapper.getBufferedPercentage());
        VideoLogUtils.d(TAG + "---" + "network speed: " + mControlWrapper.getTcpSpeed());
    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        VideoLogUtils.d(TAG + "---" + "onLockStateChanged: " + isLocked);
    }
}
