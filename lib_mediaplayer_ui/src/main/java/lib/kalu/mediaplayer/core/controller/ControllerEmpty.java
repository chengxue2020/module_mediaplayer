package lib.kalu.mediaplayer.core.controller;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.BlendMode;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.config.ConfigType;
import lib.kalu.mediaplayer.core.controller.base.ControllerLayout;

@Keep
public class ControllerEmpty extends ControllerLayout {

    public ControllerEmpty(@NonNull Context context) {
        super(context);
    }

    @Override
    public int initLayout() {
        return R.layout.module_mediaplayer_controller_empty;
    }

    @Override
    protected void onPlayerStatusChanged(int playState) {
        super.onPlayerStatusChanged(playState);
        switch (playState) {
            case ConfigType.StateType.STATE_START:
                super.setBackgroundColor(Color.TRANSPARENT);
                break;
            default:
                super.setBackgroundColor(Color.BLACK);
                break;
        }
    }

    @Override
    public void destroy() {
    }

    /***************/

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
