package lib.kalu.mediaplayer.core.audio.kernel.exo;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.analytics.DefaultAnalyticsCollector;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Clock;

import lib.kalu.exoplayer2.ffmpeg.FFmpegAudioRenderersFactory;
import lib.kalu.exoplayer2.util.ExoLogUtil;
import lib.kalu.mediaplayer.config.player.PlayerBuilder;
import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.audio.kernel.MusicKernelApi;

@Keep
public final class MusicExoPlayer implements MusicKernelApi {

    private ExoPlayer mExoPlayer;
    private AnalyticsListener mAnalyticsListener;

    public MusicExoPlayer() {
    }

    @Override
    public void createDecoder(@NonNull Context context) {
        if (null != mExoPlayer) {
            release();
        }
        if (null == mExoPlayer) {
            ExoLogUtil.log("MusicExoPlayer => createDecoder => mExoPlayer = " + mExoPlayer);
            ExoPlayer.Builder builder = new ExoPlayer.Builder(context);
//        builder.setAnalyticsCollector(new DefaultAnalyticsCollector(Clock.DEFAULT));
//        builder.setBandwidthMeter(DefaultBandwidthMeter.getSingletonInstance(context));
            builder.setLoadControl(new DefaultLoadControl());
            builder.setMediaSourceFactory(new DefaultMediaSourceFactory(context));
//        builder.setTrackSelector(new DefaultTrackSelector(context));
            PlayerBuilder config = PlayerManager.getInstance().getConfig();
            int exoFFmpeg = config.getExoFFmpeg();
            builder.setRenderersFactory(new FFmpegAudioRenderersFactory(context, exoFFmpeg));
            mExoPlayer = builder.build();
            setVolume(1F);
            setLooping(false);
            setSeekParameters();
        }
        // log
//        FfmpegLibrary.ffmpegLogger(logger);
//        ExoLogUtil.setLogger(logger);
    }

    @Override
    public void setDataSource(@NonNull Context context, @NonNull String musicUrl) {
        // 2
        createDecoder(context);
        // 3
        if (null != mExoPlayer) {
            ExoLogUtil.log("MusicExoPlayer => setDataSource => musicUrl = " + musicUrl + ", mExoPlayer = " + mExoPlayer);
            MediaSource mediaSource = MusicExoPlayerHelper.getInstance().createMediaSource(context, musicUrl);
            mExoPlayer.setMediaSource(mediaSource);
            mExoPlayer.setPlayWhenReady(false);
            mExoPlayer.prepare();
        }
    }

    @Override
    public void start() {
        ExoLogUtil.log("MusicExoPlayer => start => mExoPlayer = " + mExoPlayer);
//        if (null != listener) {
//            if (null != mAnalyticsListener) {
//                mAnalyticsListener = null;
//            }
//            mAnalyticsListener = new AnalyticsListener() {
//                @Override
//                public void onPlayWhenReadyChanged(EventTime eventTime, boolean playWhenReady, int reason) {
//                }
//
//                @Override
//                public void onPlayerError(EventTime eventTime, PlaybackException error) {
//                    ExoLogUtil.log("MusicExoPlayer => onPlayerError => " + error.getMessage(), error);
//                    if (null == error)
//                        return;
//                    if (!(error instanceof ExoPlaybackException))
//                        return;
//
//                    switch (((ExoPlaybackException) error).type) {
//                        case PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW:
//                        case ExoPlaybackException.TYPE_RENDERER:
//                        case ExoPlaybackException.TYPE_UNEXPECTED:
//                        case ExoPlaybackException.TYPE_REMOTE:
//                        case ExoPlaybackException.TYPE_SOURCE:
//                            break;
//                        default:
//                            break;
//                    }
//                }
//
//                @Override
//                public void onTimelineChanged(EventTime eventTime, int reason) {
//                }
//
//                @Override
//                public void onEvents(Player player, Events events) {
//                }
//
//                @Override
//                public void onVideoSizeChanged(EventTime eventTime, VideoSize videoSize) {
//                }
//
//                @Override
//                public void onIsPlayingChanged(EventTime eventTime, boolean isPlaying) {
//                }
//
//                @Override
//                public void onPlaybackStateChanged(EventTime eventTime, int state) {
//                    ExoLogUtil.log("MusicExoPlayer => onPlaybackStateChanged => state = " + state);
//
//                    // 播放错误
//                    if (state == Player.STATE_IDLE) {
//                    }
//                    // 播放结束
//                    else if (state == Player.STATE_ENDED) {
//                    }
//                    // 播放开始
//                    else if (state == Player.STATE_READY) {
//                    }
//                    // 播放缓冲
//                    else if (state == Player.STATE_BUFFERING) {
//                    }
//                    // 未知??
//                    else {
//                    }
//                }
//
//                @Override
//                public void onRenderedFirstFrame(EventTime eventTime, Object output, long renderTimeMs) {
//                }
//
//                @Override
//                public void onVideoInputFormatChanged(EventTime eventTime, Format format, @Nullable DecoderReuseEvaluation decoderReuseEvaluation) {
//                }
//
//                @Override
//                public void onAudioInputFormatChanged(EventTime eventTime, Format format, @Nullable DecoderReuseEvaluation decoderReuseEvaluation) {
//                    ExoLogUtil.log("MusicExoPlayer => onAudioInputFormatChanged =>");
//                    if (null != listener) {
//                        listener.onComplete();
//                    }
//                }
//            };
//            mExoPlayer.addAnalyticsListener(mAnalyticsListener);
//        }
        // 2
        setVolume(1F);
        // 3
        play();
    }

    @Override
    public void play() {
        if (null != mExoPlayer) {
            ExoLogUtil.log("MusicExoPlayer => play => mExoPlayer = " + mExoPlayer);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void stop() {
        if (null != mExoPlayer) {
            ExoLogUtil.log("MusicExoPlayer => stop => mExoPlayer = " + mExoPlayer);
            mExoPlayer.stop();
        }
    }

    @Override
    public void pause() {
        if (null != mExoPlayer) {
            ExoLogUtil.log("MusicExoPlayer => pause => mExoPlayer = " + mExoPlayer);
            mExoPlayer.pause();
        }
    }

    @Override
    public void release() {
        if (null != mAnalyticsListener) {
            if (null != mExoPlayer) {
                mExoPlayer.removeAnalyticsListener(mAnalyticsListener);
            }
            mAnalyticsListener = null;
        }
        if (null != mExoPlayer) {
            ExoLogUtil.log("MusicExoPlayer => release => mExoPlayer = " + mExoPlayer);
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void setLooping(boolean v) {
        if (null != mExoPlayer) {
            ExoLogUtil.log("MusicExoPlayer => setLooping => v = " + v + ", mExoPlayer = " + mExoPlayer);
            mExoPlayer.setRepeatMode(v ? Player.REPEAT_MODE_ALL : Player.REPEAT_MODE_OFF);
        }
    }

    @Override
    public void setVolume(float v) {
        if (null != mExoPlayer) {
            ExoLogUtil.log("MusicExoPlayer => setVolume => v = " + v + ", mExoPlayer = " + mExoPlayer);
            mExoPlayer.setVolume(v);
        }
    }

    @Override
    public boolean isPlaying() {
        ExoLogUtil.log("MusicExoPlayer => isPlaying => mExoPlayer = " + mExoPlayer);
        if (mExoPlayer == null)
            return false;
        return mExoPlayer.isPlaying();
    }

    @Override
    public void seekTo(long v) {
        if (null != mExoPlayer) {
            ExoLogUtil.log("MusicExoPlayer => seekTo => v = " + v + ", mExoPlayer = " + mExoPlayer);
            long duration = getDuration();
            ExoLogUtil.log("MusicExoPlayer => seekTo =>  duration = " + duration);
            if (v < duration) {
                mExoPlayer.seekTo(v);
            }
        }
    }

    @Override
    public long getDuration() {
        ExoLogUtil.log("MusicExoPlayer => getDuration =>  mExoPlayer = " + mExoPlayer);
        if (mExoPlayer == null)
            return 0L;
        return mExoPlayer.getDuration();
    }

    @Override
    public void setSeekParameters() {
        if (null != mExoPlayer) {
            ExoLogUtil.log("MusicExoPlayer => setSeekParameters => mExoPlayer = " + mExoPlayer);
            mExoPlayer.setSeekParameters(SeekParameters.DEFAULT);
        }
    }
}
