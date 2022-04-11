package lib.kalu.mediaplayer.core.controller;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.BlendMode;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.PlayerType;
import lib.kalu.mediaplayer.core.controller.base.ControllerLayout;
import lib.kalu.mediaplayer.core.controller.component.ComponentPause;
import lib.kalu.mediaplayer.core.controller.component.ComponentPrepare;

@Keep
public class ControllerEmpty extends ControllerLayout {

    public ControllerEmpty(@NonNull Context context) {
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
        this.removeComponentAll(false);
    }

    @Override
    protected void onPlayStateChanged(int playState) {
        super.onPlayStateChanged(playState);
    }

    @Override
    public void destroy() {
    }

    /***************/

    @Override
    public void setBackgroundColor(@NonNull View view, int id, int color) {
    }

    @Override
    public void setBackground(Drawable background) {
    }

    @Override
    public void setBackgroundColor(int color) {
    }

    @Override
    public void setBackgroundResource(int resid) {
    }

    @Override
    public void setBackgroundTintBlendMode(@Nullable BlendMode blendMode) {
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
    }

    @Override
    public void setBackgroundTintList(@Nullable ColorStateList tint) {
    }

    @Override
    public void setBackgroundTintMode(@Nullable PorterDuff.Mode tintMode) {
    }

    @Override
    public void setDrawingCacheBackgroundColor(int color) {
    }
}
