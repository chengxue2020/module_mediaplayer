package lib.kalu.mediaplayer.core.kernel.video.exo2;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.analytics.DefaultAnalyticsCollector;
import com.google.android.exoplayer2.decoder.DecoderReuseEvaluation;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.video.VideoSize;

import java.util.Arrays;

import lib.kalu.mediaplayer.config.player.PlayerBuilder;
import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.KernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.base.BasePlayer;
import lib.kalu.mediaplayer.core.player.PlayerApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

@Keep
public final class VideoExoPlayer2 extends BasePlayer {

    private boolean seekHelp = false;
    private long mSeek = 0L; // 快进
    private long mMax = 0L; // 试播时常
    private boolean mLoop = false; // 循环播放
    private boolean mLive = false;
    private boolean mMute = false;
    private boolean mPlayWhenReady = true;
    private PlaybackParameters mSpeedPlaybackParameters;

    private ExoPlayer mExoPlayer;
    private AnalyticsListener mAnalyticsListener;

    public VideoExoPlayer2(@NonNull PlayerApi musicApi, @NonNull KernelApiEvent eventApi) {
        super(musicApi, eventApi);
    }

    @NonNull
    @Override
    public ExoPlayer getPlayer() {
        return mExoPlayer;
    }

    @Override
    public void releaseDecoder(boolean isFromUser) {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            if (isFromUser) {
                setEvent(null);
            }
            stopExternalMusic(true);
            if (null != mAnalyticsListener) {
                mExoPlayer.removeAnalyticsListener(mAnalyticsListener);
            }
            mAnalyticsListener = null;
            mSpeedPlaybackParameters = null;
            mExoPlayer.setVideoSurface(null);
            mExoPlayer.setPlayWhenReady(false);
            mExoPlayer.release();
            mExoPlayer = null;
        } catch (Exception e) {
            MPLogUtil.log("VideoExoPlayer => releaseDecoder => " + e.getMessage());
        }
    }

    @Override
    public void createDecoder(@NonNull Context context, @NonNull boolean logger, @NonNull int seekParameters) {
        try {
            releaseDecoder(false);
            ExoPlayer.Builder builder = new ExoPlayer.Builder(context);
            builder.setAnalyticsCollector(new DefaultAnalyticsCollector(Clock.DEFAULT));
            builder.setBandwidthMeter(DefaultBandwidthMeter.getSingletonInstance(context));
            builder.setLoadControl(new DefaultLoadControl());
            builder.setMediaSourceFactory(new DefaultMediaSourceFactory(context));
            builder.setTrackSelector(new DefaultTrackSelector(context));
            PlayerBuilder config = PlayerManager.getInstance().getConfig();
            int exoFFmpeg = config.getExoFFmpeg();
            MPLogUtil.log("VideoExoPlayer => createDecoder => exoFFmpeg = " + exoFFmpeg);

            // on_high_all
            if (exoFFmpeg == PlayerType.FFmpegType.EXO_EXTENSION_RENDERER_ON_HIGH_ALL) {
                try {
                    Class<?> clazz = Class.forName("lib.kalu.exoplayer2.ffmpeg.BaseRenderersFactory");
                    if (null != clazz) {
                        builder.setRenderersFactory(new lib.kalu.exoplayer2.ffmpeg.BaseRenderersFactory(context));
                    }
                } catch (Exception e) {
                }
            }
            // on_high_audio
            else if (exoFFmpeg == PlayerType.FFmpegType.EXO_EXTENSION_RENDERER_ON_HIGH_AUDIO) {
                try {
                    Class<?> clazz = Class.forName("lib.kalu.exoplayer2.ffmpeg.FFmpegHighAudioRenderersFactory");
                    if (null != clazz) {
                        builder.setRenderersFactory(new lib.kalu.exoplayer2.ffmpeg.FFmpegHighAudioRenderersFactory(context));
                    }
                } catch (Exception e) {
                }
            }
            // on_low_all
            else if (exoFFmpeg == PlayerType.FFmpegType.EXO_EXTENSION_RENDERER_ON_LOW_ALL) {
                try {
                    Class<?> clazz = Class.forName("lib.kalu.exoplayer2.ffmpeg.BaseRenderersFactory");
                    if (null != clazz) {
                        builder.setRenderersFactory(new lib.kalu.exoplayer2.ffmpeg.BaseRenderersFactory(context));
                    }
                } catch (Exception e) {
                }
            }
            // on_low_audio
            else if (exoFFmpeg == PlayerType.FFmpegType.EXO_EXTENSION_RENDERER_ON_LOW_AUDIO) {
                try {
                    Class<?> clazz = Class.forName("lib.kalu.exoplayer2.ffmpeg.FFmpegLowAudioRenderersFactory");
                    if (null != clazz) {
                        builder.setRenderersFactory(new lib.kalu.exoplayer2.ffmpeg.FFmpegLowAudioRenderersFactory(context));
                    }
                } catch (Exception e) {
                }
            }
            // off_only_audio
            else if (exoFFmpeg == PlayerType.FFmpegType.EXO_EXTENSION_RENDERER_OFF_ONLY_AUDIO) {
                try {
                    Class<?> clazz = Class.forName("lib.kalu.exoplayer2.ffmpeg.BaseOnlyAudioRenderersFactory");
                    if (null != clazz) {
                        builder.setRenderersFactory(new lib.kalu.exoplayer2.ffmpeg.BaseOnlyAudioRenderersFactory(context));
                    }
                } catch (Exception e) {
                }
            }
            // off_only_video
            else if (exoFFmpeg == PlayerType.FFmpegType.EXO_EXTENSION_RENDERER_OFF_ONLY_VIDEO) {
                try {
                    Class<?> clazz = Class.forName("lib.kalu.exoplayer2.ffmpeg.BaseOnlyVideoRenderersFactory");
                    if (null != clazz) {
                        builder.setRenderersFactory(new lib.kalu.exoplayer2.ffmpeg.BaseOnlyVideoRenderersFactory(context));
                    }
                } catch (Exception e) {
                }
            }
            // off
            else {
                try {
                    Class<?> clazz = Class.forName("lib.kalu.exoplayer2.ffmpeg.BaseRenderersFactory");
                    if (null != clazz) {
                        builder.setRenderersFactory(new lib.kalu.exoplayer2.ffmpeg.BaseRenderersFactory(context));
                    }
                } catch (Exception e) {
                }
            }
            mExoPlayer = builder.build();
            mExoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
            setVolume(1F, 1F);
            MPLogUtil.log("VideoExoPlayer => " + "VideoExoPlayer => createDecoder => seekParameters = " + seekParameters);
            // seek model
            if (seekParameters == PlayerType.SeekType.EXO_SEEK_CLOSEST_SYNC) {
                mExoPlayer.setSeekParameters(SeekParameters.CLOSEST_SYNC);
            } else if (seekParameters == PlayerType.SeekType.EXO_SEEK_PREVIOUS_SYNC) {
                mExoPlayer.setSeekParameters(SeekParameters.PREVIOUS_SYNC);
            } else if (seekParameters == PlayerType.SeekType.EXO_SEEK_NEXT_SYNC) {
                mExoPlayer.setSeekParameters(SeekParameters.NEXT_SYNC);
            } else {
                mExoPlayer.setSeekParameters(SeekParameters.DEFAULT);
            }

            // log
            try {
                Class<?> clazz = Class.forName("com.google.android.exoplayer2.ext.ffmpeg.FfmpegLibrary");
                if (null != clazz) {
                    com.google.android.exoplayer2.ext.ffmpeg.FfmpegLibrary.ffmpegLogger(logger);
                }
            } catch (Exception e) {
            }

            if (null != mAnalyticsListener) {
                mAnalyticsListener = null;
            }
            mAnalyticsListener = new AnalyticsListener() {
                @Override
                public void onPlayWhenReadyChanged(EventTime eventTime, boolean playWhenReady, int reason) {
//        MPLogUtil.log("VideoExoPlayer => " + "VideoExoPlayer => onPlayWhenReadyChanged => playWhenReady = " + playWhenReady + ", reason = " + reason);
                }

                @Override
                public void onPlayerError(EventTime eventTime, PlaybackException error) {
                    MPLogUtil.log("VideoExoPlayer => " + "VideoExoPlayer => onPlayerError => error = " + error.getMessage() + ", mExoPlayer = " + mExoPlayer);

                    if (null == error)
                        return;
                    if (!(error instanceof ExoPlaybackException))
                        return;

                    switch (((ExoPlaybackException) error).type) {
                        case PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW:
                        case ExoPlaybackException.TYPE_RENDERER:
                        case ExoPlaybackException.TYPE_UNEXPECTED:
                        case ExoPlaybackException.TYPE_REMOTE:
                        case ExoPlaybackException.TYPE_SOURCE:
                            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_LOADING_STOP);
                            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_ERROR_SOURCE);
                            break;
                        default:
                            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_LOADING_STOP);
                            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_ERROR_RETRY);
                            break;
                    }
                    onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_ERROR_SOURCE);
                }

                @Override
                public void onTimelineChanged(EventTime eventTime, int reason) {
                    MPLogUtil.log("VideoExoPlayer => " + "VideoExoPlayer => onTimelineChanged => reason = " + reason + ", totalBufferedDurationMs = " + eventTime.totalBufferedDurationMs + ", realtimeMs = " + eventTime.realtimeMs);
                }

                @Override
                public void onEvents(Player player, Events events) {
//                    MediaLogUtil.log("VideoExoPlayer => onEvents => isPlaying = " + player.isPlaying());
                }

                @Override
                public void onVideoSizeChanged(EventTime eventTime, VideoSize videoSize) {
                    onChanged(PlayerType.KernelType.EXO_V2, videoSize.width, videoSize.height, videoSize.unappliedRotationDegrees > 0 ? videoSize.unappliedRotationDegrees : -1);
                }

                @Override
                public void onIsPlayingChanged(EventTime eventTime, boolean isPlaying) {
                    MPLogUtil.log("VideoExoPlayer => onIsPlayingChanged => isPlaying = " + isPlaying + ", mPlayWhenReady = " + mPlayWhenReady);
                    if (isPlaying && !mPlayWhenReady) {
                        pause();
                    }
                }

                @Override
                public void onPlaybackStateChanged(EventTime eventTime, int state) {
                    MPLogUtil.log("VideoExoPlayer => " + "VideoExoPlayer => onPlaybackStateChanged => state = " + state + ", mute = " + isMute());

                    // 播放错误
                    if (state == Player.STATE_IDLE) {
                        MPLogUtil.log("VideoExoPlayer => " + "VideoExoPlayer => onPlaybackStateChanged[播放错误] =>");
                        onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_LOADING_STOP);
                        onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_ERROR_SOURCE);
                    }
                    // 播放结束
                    else if (state == Player.STATE_ENDED) {
//                    String url = getUrl();
                        MPLogUtil.log("VideoExoPlayer => " + "VideoExoPlayer => onPlaybackStateChanged[播放结束] =>");
                        onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_VIDEO_END);
                    }
                    // 播放开始
                    else if (state == Player.STATE_READY) {

                        MPLogUtil.log("VideoExoPlayer => " + "VideoExoPlayer => onPlaybackStateChanged[播放开始] =>");
                        if (seekHelp) {
                            seekHelp = false;
                            long seek = getSeek();
                            seekTo(seek, false);
                        } else {
                            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_LOADING_STOP);
                            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_BUFFERING_STOP);
                            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_VIDEO_START);
                        }
                    }
                    // 播放缓冲
                    else if (state == Player.STATE_BUFFERING) {
                        MPLogUtil.log("VideoExoPlayer => " + "VideoExoPlayer => onPlaybackStateChanged[播放缓冲] =>");
                        long position = getPosition();
                        if (position > 0) {
                            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_BUFFERING_START);
                        }
                    }
                    // 未知??
                    else {
                        MPLogUtil.log("VideoExoPlayer => " + "VideoExoPlayer => onPlaybackStateChanged[未知??] =>");
                    }
                }

                @Override
                public void onRenderedFirstFrame(EventTime eventTime, Object output, long renderTimeMs) {
                    MPLogUtil.log("VideoExoPlayer => " + "VideoExoPlayer => onRenderedFirstFrame =>");
                }

                @Override
                public void onVideoInputFormatChanged(EventTime eventTime, Format format, @Nullable DecoderReuseEvaluation decoderReuseEvaluation) {
                    long seek = getSeek();
//                long position = getPosition();
                    MPLogUtil.log("VideoExoPlayer => " + "VideoExoPlayer => onVideoInputFormatChanged => seek = " + seek);
                    // 快进 => begin
                    if (seek > 0) {
                        seekTo(seek, false);
                    }
                }

                @Override
                public void onAudioInputFormatChanged(EventTime eventTime, Format format, @Nullable DecoderReuseEvaluation decoderReuseEvaluation) {
                    MPLogUtil.log("VideoExoPlayer => " + "VideoExoPlayer => onAudioInputFormatChanged =>");
                }
            };
            mExoPlayer.addAnalyticsListener(mAnalyticsListener);

            if (mSpeedPlaybackParameters != null) {
                mExoPlayer.setPlaybackParameters(mSpeedPlaybackParameters);
            }
//        mIsPreparing = true;

            //播放器日志
//        if (mExoPlayer.getTrackSelector() instanceof MappingTrackSelector) {
//            mExoPlayer.addAnalyticsListener(new EventLogger((MappingTrackSelector) mExoPlayer.getTrackSelector(), "ExoPlayer"));
//        }
        } catch (Exception e) {
        }
    }

    @Override
    public void startDecoder(@NonNull Context context, @NonNull String url) {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            if (url == null || url.length() == 0)
                throw new IllegalArgumentException("url error: " + url);
            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_LOADING_START);
            PlayerBuilder config = PlayerManager.getInstance().getConfig();
            int cacheType = config.getCacheType();
            int cacheMax = config.getCacheMax();
            String cacheDir = config.getCacheDir();
            if (isLive()) {
                cacheType = PlayerType.CacheType.NONE;
            }
//            mediaSource.addEventListener(new Handler(), new MediaSourceEventListener() {
//                @Override
//                public void onLoadStarted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
//                    MediaLogUtil.log("onEXOLoadStarted => ");
//                }
//
//                @Override
//                public void onLoadCompleted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
//                    MediaLogUtil.log("onEXOLoadCompleted => ");
//                }
//            });
            mExoPlayer.setMediaSource(createMediaSource(context, url, null, cacheType, cacheMax, cacheDir));
            mExoPlayer.setPlayWhenReady(mPlayWhenReady);
            mExoPlayer.prepare();
        } catch (Exception e) {
            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_LOADING_STOP);
            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_ERROR_URL);
        }
    }

    /**
     * 是否正在播放
     */
    @Override
    public boolean isPlaying() {
        if (mExoPlayer == null) {
            return false;
        }
        int state = mExoPlayer.getPlaybackState();
        switch (state) {
            case Player.STATE_BUFFERING:
            case Player.STATE_READY:
                return mExoPlayer.getPlayWhenReady();
            case Player.STATE_IDLE:
            case Player.STATE_ENDED:
            default:
                return false;
        }
    }

    @Override
    public void seekTo(@NonNull long position, @NonNull boolean help) {
        try {
            seekHelp = help;
            mExoPlayer.seekTo(position);
        } catch (Exception e) {
            MPLogUtil.log("VideoExoPlayer => " + e.getMessage(), e);
        }
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getPosition() {
        if (mExoPlayer == null) {
            return 0L;
        }
        return mExoPlayer.getCurrentPosition();
    }

    /**
     * 获取视频总时长
     */
    @Override
    public long getDuration() {
        if (mExoPlayer == null) {
            return 0L;
        }
        long duration = mExoPlayer.getDuration();
        if (duration < 0) {
            duration = 0L;
        }
        return duration;
    }

    @Override
    public void setSurface(@NonNull Surface surface, int w, int h) {
        if (null != surface && null != mExoPlayer) {
            try {
                mExoPlayer.setVideoSurface(surface);
            } catch (Exception e) {
                MPLogUtil.log("VideoExoPlayer => " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void setPlayWhenReady(boolean playWhenReady) {
        this.mPlayWhenReady = playWhenReady;
    }

    /**
     * 设置播放速度
     */
    @Override
    public void setSpeed(float speed) {
        PlaybackParameters playbackParameters = new PlaybackParameters(speed);
        mSpeedPlaybackParameters = playbackParameters;
        if (mExoPlayer != null) {
            mExoPlayer.setPlaybackParameters(playbackParameters);
        }
    }

    /**
     * 获取播放速度
     */
    @Override
    public float getSpeed() {
        try {
            return mSpeedPlaybackParameters.speed;
        } catch (Exception e) {
            return 1f;
        }
    }

    @Override
    public void setVolume(float v1, float v2) {
        try {
            float value;
            boolean mute = isMute();
            MPLogUtil.log("VideoExoPlayer => setVolume => mute = " + mute);
            if (mute) {
                value = 0F;
            } else {
                value = Math.max(v1, v2);
                if (value > 1f) {
                    value = 1f;
                }
            }
            MPLogUtil.log("VideoExoPlayer => setVolume => value = " + value);
            mExoPlayer.setVolume(value);
        } catch (Exception e) {
            MPLogUtil.log("VideoExoPlayer => setVolume => " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isMute() {
        return mMute;
    }

    @Override
    public void setMute(boolean v) {
        mMute = v;
        setVolume(v ? 0f : 1f, v ? 0f : 1f);
    }

    @Override
    public long getSeek() {
        return mSeek;
    }

    @Override
    public void setSeek(long seek) {
        if (seek < 0)
            return;
        mSeek = seek;
    }

    @Override
    public long getMax() {
        return mMax;
    }

    @Override
    public void setMax(long max) {
        if (max < 0)
            return;
        mMax = max;
    }

    @Override
    public boolean isLive() {
        return mLive;
    }

    @Override
    public void setLive(@NonNull boolean live) {
        this.mLive = live;
    }

    @Override
    public void setLooping(boolean loop) {
        this.mLoop = loop;
    }

    @Override
    public boolean isLooping() {
        return mLoop;
    }

    /**
     * 播放
     */
    @Override
    public void start() {
        try {
            if (null == mExoPlayer)
                throw new Exception();
            boolean externalMusicPlaying = isExternalMusicPlaying();
            setVolume(externalMusicPlaying ? 0F : 1F, externalMusicPlaying ? 0F : 1F);
            mExoPlayer.play();
        } catch (Exception e) {
            MPLogUtil.log("VideoExoPlayer => start => " + e.getMessage());
        }
    }

    /**
     * 暂停
     */
    @Override
    public void pause() {
        try {
            mExoPlayer.pause();
        } catch (Exception e) {
            MPLogUtil.log("VideoExoPlayer => pause => " + e.getMessage());
        }
    }

    /**
     * 停止
     */
    @Override
    public void stop() {
        try {
            mExoPlayer.pause();
            mExoPlayer.stop();
        } catch (Exception e) {
            MPLogUtil.log("VideoExoPlayer => stop => " + e.getMessage());
        }
    }

    /************************/

    public MediaSource createMediaSource(@NonNull Context context,
                                         @NonNull String mediaUrl,
                                         @Nullable String subtitleUrl,
                                         @PlayerType.CacheType int cacheType,
                                         @NonNull int cacheMax,
                                         @NonNull String cacheDir) {

        MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => mediaUrl = " + mediaUrl);
        MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => subtitleUrl = " + subtitleUrl);
        MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => cacheType = " + cacheType);
        MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => cacheMax = " + cacheMax);
        MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => cacheDir = " + cacheDir);

        String scheme;
        Uri uri = Uri.parse(mediaUrl);
        try {
            scheme = uri.getScheme();
        } catch (Exception e) {
            scheme = null;
        }
        MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => scheme = " + scheme);

        // rtmp
        if (PlayerType.SchemeType.RTMP.equals(scheme)) {
            // log
            try {
                Class<?> clazz = Class.forName("com.google.android.exoplayer2.ext.rtmp.RtmpDataSource");
                if (null == clazz)
                    throw new Exception();
                MediaItem mediaItem = MediaItem.fromUri(uri);
                return new ProgressiveMediaSource.Factory(new com.google.android.exoplayer2.ext.rtmp.RtmpDataSource.Factory()).createMediaSource(mediaItem);
            } catch (Exception e) {
                return null;
            }
        }
        // rtsp
        else if (PlayerType.SchemeType.RTSP.equals(scheme)) {
            MediaItem mediaItem = MediaItem.fromUri(uri);
            return new RtspMediaSource.Factory().createMediaSource(mediaItem);
        }
        // other
        else {
            // 1
            int contentType;
            try {
                String s = mediaUrl.toLowerCase();
                if (s.contains(PlayerType.SchemeType._MPD)) {
                    contentType = C.CONTENT_TYPE_DASH;
                } else if (s.contains(PlayerType.SchemeType._M3U)) {
                    contentType = C.CONTENT_TYPE_HLS;
                } else if (s.contains(PlayerType.SchemeType._M3U8)) {
                    contentType = C.CONTENT_TYPE_HLS;
                } else if (s.matches(PlayerType.SchemeType._MATCHES)) {
                    contentType = C.CONTENT_TYPE_SS;
                } else {
                    contentType = C.CONTENT_TYPE_OTHER;
                }
            } catch (Exception e) {
                contentType = C.CONTENT_TYPE_OTHER;
            }
            MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => contentType = " + contentType);

            // 2
            MediaItem.Builder builder = new MediaItem.Builder();
            builder.setUri(Uri.parse(mediaUrl));
            if (null != subtitleUrl && subtitleUrl.length() > 0) {
                MediaItem.SubtitleConfiguration.Builder subtitle = new MediaItem.SubtitleConfiguration.Builder(Uri.parse(mediaUrl));
                subtitle.setMimeType(MimeTypes.APPLICATION_SUBRIP);
                subtitle.setLanguage("en");
                subtitle.setSelectionFlags(C.SELECTION_FLAG_AUTOSELECT); // C.SELECTION_FLAG_DEFAULT
                builder.setSubtitleConfigurations(Arrays.asList(subtitle.build()));

//            MediaItem.SubtitleConfiguration.Builder builder = new MediaItem.SubtitleConfiguration.Builder(srtUri);
//            builder.setMimeType(MimeTypes.APPLICATION_SUBRIP);
//            builder.setMimeType(MimeTypes.TEXT_VTT);
//            builder.setLanguage("en");
//            builder.setSelectionFlags(C.SELECTION_FLAG_DEFAULT);
//            MediaItem.SubtitleConfiguration subtitle = builder.build();
//            MediaSource textMediaSource = new SingleSampleMediaSource.Factory(factory).createMediaSource(subtitle, C.TIME_UNSET);
//            textMediaSource.getMediaItem().mediaMetadata.subtitle.toString();
//            MediaLogUtil.log("SRT => " + subtitle);
//            return new MergingMediaSource(mediaSource, srtSource);
            }

            // okhttp
//            OkHttpClient.Builder builder = new OkHttpClient.Builder();
//            OkHttpClient client = builder.build();
//            OkHttpDataSource.Factory http = new OkHttpDataSource.Factory(client);
//            http.setUserAgent("(Linux;Android " + Build.VERSION.RELEASE + ") " + ExoPlayerLibraryInfo.VERSION_SLASHY);

            // head
//            refreshHeaders(httpFactory, headers);

            // http
            DefaultHttpDataSource.Factory httpFactory = new DefaultHttpDataSource.Factory();
            httpFactory.setUserAgent("(Linux;Android " + Build.VERSION.RELEASE + ") " + ExoPlayerLibraryInfo.VERSION_SLASHY);
            httpFactory.setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS);
            httpFactory.setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS);
            httpFactory.setAllowCrossProtocolRedirects(true);
            httpFactory.setKeepPostFor302Redirects(true);

            DataSource.Factory dataSource;
            if (cacheType == PlayerType.CacheType.NONE) {
                dataSource = new DefaultDataSource.Factory(context, httpFactory);
            } else {
                CacheDataSource.Factory cacheFactory = new CacheDataSource.Factory();
                SimpleCache cache = VideoExoPlayer2Cache.getSimpleCache(context, cacheMax, cacheDir);
                cacheFactory.setCache(cache);
                cacheFactory.setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
                cacheFactory.setUpstreamDataSourceFactory(httpFactory);
                dataSource = cacheFactory;
            }

            // 3
            MediaItem mediaItem = builder.build();
            switch (contentType) {
                case C.CONTENT_TYPE_DASH:
                    return new DashMediaSource.Factory(dataSource).createMediaSource(mediaItem);
                case C.CONTENT_TYPE_SS:
                    return new SsMediaSource.Factory(dataSource).createMediaSource(mediaItem);
                case C.CONTENT_TYPE_HLS:
                    return new HlsMediaSource.Factory(dataSource).createMediaSource(mediaItem);
                default:
                    DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                    extractorsFactory.setConstantBitrateSeekingEnabled(true);
                    return new ProgressiveMediaSource.Factory(dataSource, extractorsFactory).createMediaSource(mediaItem);
            }
        }
    }
}
