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

public final class FFmpegVideoRenderersFactory extends DefaultRenderersFactory {

    int mFFmpeg;
    public FFmpegVideoRenderersFactory(Context context, int ffmpeg) {
        super(context);
        this.mFFmpeg = ffmpeg;
        /**
         * int EXO_EXT_FFPEMG_NULL = 9_001;
         *         int EXO_EXT_FFPEMG_AUDIO = 9_002;
         *         int EXO_EXT_FFPEMG_VIDEO = 9_003;
         */
        /**
         * EXTENSION_RENDERER_MODE_OFF: 关闭扩展
         * EXTENSION_RENDERER_MODE_ON: 打开扩展，优先使用硬解
         * EXTENSION_RENDERER_MODE_PREFER: 打开扩展，优先使用软解
         */
        if (ffmpeg == 9001) {
            setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF);
        } else {
            setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON);
        }
    }

    @Override
    protected void buildAudioRenderers(Context context, @ExtensionRendererMode int extensionRendererMode, MediaCodecSelector mediaCodecSelector, boolean enableDecoderFallback, AudioSink audioSink, Handler eventHandler, AudioRendererEventListener eventListener, ArrayList<Renderer> out) {
        ExoLogUtil.log("FFmpegVideoRenderersFactory => buildAudioRenderers => ");

        /**
         * int EXO_EXT_FFPEMG_NULL = 9_001;
         *         int EXO_EXT_FFPEMG_AUDIO = 9_002;
         *         int EXO_EXT_FFPEMG_VIDEO = 9_003;
         */
        if (mFFmpeg == 9002) {
            out.add(0, new FfmpegAudioRenderer());
            for (int i = 0; i < out.size(); i++) {
                Renderer renderer = out.get(i);
                ExoLogUtil.log("FFmpegVideoRenderersFactory => buildAudioRenderers => " + renderer);
            }
        }
        super.buildAudioRenderers(context, extensionRendererMode, mediaCodecSelector, enableDecoderFallback, audioSink, eventHandler, eventListener, out);
    }

    @Override
    protected void buildVideoRenderers(Context context, @ExtensionRendererMode int extensionRendererMode, MediaCodecSelector mediaCodecSelector, boolean enableDecoderFallback, Handler eventHandler, VideoRendererEventListener eventListener, long allowedVideoJoiningTimeMs, ArrayList<Renderer> out) {
        ExoLogUtil.log("FFmpegVideoRenderersFactory => buildVideoRenderers => ");


        /**
         * int EXO_EXT_FFPEMG_NULL = 9_001;
         *         int EXO_EXT_FFPEMG_AUDIO = 9_002;
         *         int EXO_EXT_FFPEMG_VIDEO = 9_003;
         */
//        if (mFFmpeg == 9003) {
//            out.add(0, new FfmpegAudioRenderer());
//            for (int i = 0; i < out.size(); i++) {
//                Renderer renderer = out.get(i);
//                ExoLogUtil.log("FFmpegVideoRenderersFactory => buildAudioRenderers => " + renderer);
//            }
//        }
        super.buildVideoRenderers(context, extensionRendererMode, mediaCodecSelector, enableDecoderFallback, eventHandler, eventListener, allowedVideoJoiningTimeMs, out);
    }
}
