package com.kalu.mediaplayer.newPlayer.tiktok;

import android.content.Context;

import lib.kalu.mediaplayer.ui.surface.InterSurfaceView;
import lib.kalu.mediaplayer.ui.surface.RenderTextureView;
import lib.kalu.mediaplayer.ui.surface.SurfaceFactory;

public class TikTokRenderViewFactory extends SurfaceFactory {

    public static TikTokRenderViewFactory create() {
        return new TikTokRenderViewFactory();
    }

    @Override
    public InterSurfaceView createRenderView(Context context) {
        return new TikTokRenderView(new RenderTextureView(context));
    }
}
