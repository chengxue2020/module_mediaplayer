package com.kalu.mediaplayer.newPlayer.list;

import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.controller.ControllerWrapper;
import lib.kalu.mediaplayer.util.PlayerUtils;
import lib.kalu.mediaplayer.core.controller.impl.ImplComponent;
import lib.kalu.mediaplayer.util.MediaLogUtil;

public class PlayerMonitor implements ImplComponent {

    private ControllerWrapper mControllerWrapper;
    private static final String TAG = "PlayerMonitor";

    @Override
    public void attach(@NonNull ControllerWrapper controllerWrapper) {
        mControllerWrapper = controllerWrapper;
    }

    @Override
    public View getView() {
        return null;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {
        MediaLogUtil.log(TAG + "---" + "onVisibilityChanged: " + isVisible);
    }

    @Override
    public void onPlayStateChanged(int playState) {
        MediaLogUtil.log(TAG + "---" + "onPlayStateChanged: " + PlayerUtils.playState2str(playState));
    }

    @Override
    public void onWindowStateChanged(int playerState) {
        MediaLogUtil.log(TAG + "---" + "onPlayerStateChanged: " + PlayerUtils.playerState2str(playerState));
    }

    @Override
    public void setProgress(int duration, int position) {
        MediaLogUtil.log(TAG + "---" + "setProgress: duration: " + duration + " position: " + position + " buffered percent: " + mControllerWrapper.getBufferedPercentage());
        MediaLogUtil.log(TAG + "---" + "network speed: " + mControllerWrapper.getTcpSpeed());
    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        MediaLogUtil.log(TAG + "---" + "onLockStateChanged: " + isLocked);
    }
}
