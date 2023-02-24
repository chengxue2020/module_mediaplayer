package lib.kalu.exoplayer2.ffmpeg;

import android.content.Context;
import android.os.Handler;

import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import java.util.ArrayList;

import lib.kalu.exoplayer2.util.ExoLogUtil;

public final class DefaultAudioOnlyRenderersFactory extends com.google.android.exoplayer2.DefaultRenderersFactory {

    public DefaultAudioOnlyRenderersFactory(Context context) {
        super(context);
        setExtensionRendererMode(com.google.android.exoplayer2.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF);
    }

    @Override
    protected void buildVideoRenderers(Context context, @ExtensionRendererMode int extensionRendererMode, MediaCodecSelector mediaCodecSelector, boolean enableDecoderFallback, Handler eventHandler, VideoRendererEventListener eventListener, long allowedVideoJoiningTimeMs, ArrayList<Renderer> out) {
        out.clear();
        int size = out.size();
        if (size <= 0) {
            ExoLogUtil.log("DefaultAudioOnlyRenderersFactory => buildVideoRenderers => renderer = null");
        } else {
            for (int i = 0; i < size; i++) {
                Renderer renderer = out.get(i);
                ExoLogUtil.log("DefaultAudioOnlyRenderersFactory => buildVideoRenderers => renderer = " + renderer);
            }
        }
        super.buildVideoRenderers(context, extensionRendererMode, mediaCodecSelector, enableDecoderFallback, eventHandler, eventListener, allowedVideoJoiningTimeMs, out);
    }
}
