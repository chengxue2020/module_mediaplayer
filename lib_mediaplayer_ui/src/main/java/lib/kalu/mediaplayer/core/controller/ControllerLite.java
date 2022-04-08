package lib.kalu.mediaplayer.core.controller;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.PlayerType;
import lib.kalu.mediaplayer.core.controller.base.ControllerLayout;

@Keep
public class ControllerLite extends ControllerLayout {

    public ControllerLite(@NonNull Context context) {
        super(context);
    }

    @Override
    public int initLayout() {
        return R.layout.module_mediaplayer_video_empty;
    }

    @Override
    public void init() {
        super.init();
        setEnabled(false);
        // 移除
        this.removeComponentAll(false);
    }

    @Override
    protected void onPlayStateChanged(int playState) {
        super.onPlayStateChanged(playState);
    }

    @Override
    public void destroy() {
    }
}
