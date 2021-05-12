package com.kalu.mediaplayer.newPlayer.tiktok;

import android.content.Context;

import lib.kalu.mediaplayer.videoui.surface.InterSurfaceView;
import lib.kalu.mediaplayer.videoui.surface.SurfaceFactory;
import lib.kalu.mediaplayer.videoui.surface.RenderTextureView;


public class TikTokRenderViewFactory extends SurfaceFactory {

    public static TikTokRenderViewFactory create() {
        return new TikTokRenderViewFactory();
    }

    @Override
    public InterSurfaceView createRenderView(Context context) {
        return new TikTokRenderView(new RenderTextureView(context));
    }
}
