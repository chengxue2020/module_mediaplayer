package lib.kalu.exoplayer2.ffmpeg;

import android.content.Context;
import android.os.Handler;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.AudioSink;
import com.google.android.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import java.util.ArrayList;

import lib.kalu.exoplayer2.util.ExoLogUtil;

public final class FFmpegDefaultRenderersFactory extends DefaultRenderersFactory {

    public FFmpegDefaultRenderersFactory(Context context) {
        super(context);
        setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON);
    }

    @Override
    protected void buildAudioRenderers(Context context, @ExtensionRendererMode int extensionRendererMode, MediaCodecSelector mediaCodecSelector, boolean enableDecoderFallback, AudioSink audioSink, Handler eventHandler, AudioRendererEventListener eventListener, ArrayList<Renderer> out) {
        ExoLogUtil.log("FFmpegDefaultRenderersFactory => buildAudioRenderers => ");
        out.add(0, new FfmpegAudioRenderer());
        for (int i = 0; i < out.size(); i++) {
            Renderer renderer = out.get(i);
            ExoLogUtil.log("FFmpegDefaultRenderersFactory => buildAudioRenderers => " + renderer);
        }
        super.buildAudioRenderers(context, extensionRendererMode, mediaCodecSelector, enableDecoderFallback, audioSink, eventHandler, eventListener, out);
    }

    @Override
    protected void buildVideoRenderers(Context context, @ExtensionRendererMode int extensionRendererMode, MediaCodecSelector mediaCodecSelector, boolean enableDecoderFallback, Handler eventHandler, VideoRendererEventListener eventListener, long allowedVideoJoiningTimeMs, ArrayList<Renderer> out) {
        ExoLogUtil.log("FFmpegDefaultRenderersFactory => buildVideoRenderers => ");
        for (int i = 0; i < out.size(); i++) {
            Renderer renderer = out.get(i);
            ExoLogUtil.log("FFmpegDefaultRenderersFactory => buildVideoRenderers => " + renderer);
        }
        super.buildVideoRenderers(context, extensionRendererMode, mediaCodecSelector, enableDecoderFallback, eventHandler, eventListener, allowedVideoJoiningTimeMs, out);
    }
}
