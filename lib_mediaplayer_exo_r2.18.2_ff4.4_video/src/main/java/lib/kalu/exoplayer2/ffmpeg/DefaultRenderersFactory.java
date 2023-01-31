package lib.kalu.exoplayer2.ffmpeg;

import android.content.Context;

public final class DefaultRenderersFactory extends com.google.android.exoplayer2.DefaultRenderersFactory {

    public DefaultRenderersFactory(Context context) {
        super(context);
        setExtensionRendererMode(com.google.android.exoplayer2.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF);
    }
}
