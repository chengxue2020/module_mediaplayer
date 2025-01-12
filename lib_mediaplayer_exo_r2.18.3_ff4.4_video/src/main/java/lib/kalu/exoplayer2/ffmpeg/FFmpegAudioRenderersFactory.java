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

/**
 * EXTENSION_RENDERER_MODE_OFF: 关闭扩展
 * EXTENSION_RENDERER_MODE_ON: 打开扩展，优先使用硬解
 * EXTENSION_RENDERER_MODE_PREFER: 打开扩展，优先使用软解
 */
public final class FFmpegAudioRenderersFactory extends DefaultRenderersFactory {

    public FFmpegAudioRenderersFactory(Context context) {
        super(context);
        setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON);
    }

    @Override
    protected void buildAudioRenderers(Context context, @ExtensionRendererMode int extensionRendererMode, MediaCodecSelector mediaCodecSelector, boolean enableDecoderFallback, AudioSink audioSink, Handler eventHandler, AudioRendererEventListener eventListener, ArrayList<Renderer> out) {
        out.add(0, new FfmpegAudioRenderer());
        for (int i = 0; i < out.size(); i++) {
            Renderer renderer = out.get(i);
            ExoLogUtil.log("FFmpegAudioRenderersFactory => buildAudioRenderers => renderer = " + renderer);
        }
        super.buildAudioRenderers(context, extensionRendererMode, mediaCodecSelector, enableDecoderFallback, audioSink, eventHandler, eventListener, out);
    }

    @Override
    protected void buildVideoRenderers(Context context, @ExtensionRendererMode int extensionRendererMode, MediaCodecSelector mediaCodecSelector, boolean enableDecoderFallback, Handler eventHandler, VideoRendererEventListener eventListener, long allowedVideoJoiningTimeMs, ArrayList<Renderer> out) {
        for (int i = 0; i < out.size(); i++) {
            Renderer renderer = out.get(i);
            ExoLogUtil.log("FFmpegAudioRenderersFactory => buildVideoRenderers => renderer = " + renderer);
        }
        super.buildVideoRenderers(context, extensionRendererMode, mediaCodecSelector, enableDecoderFallback, eventHandler, eventListener, allowedVideoJoiningTimeMs, out);
    }
}
