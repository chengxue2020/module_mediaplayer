package lib.kalu.mediaplayer.core.kernel.exo;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.Handler;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.analytics.DefaultAnalyticsCollector;
import com.google.android.exoplayer2.decoder.DecoderReuseEvaluation;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.LoadEventInfo;
import com.google.android.exoplayer2.source.MediaLoadData;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.video.VideoSize;

import java.util.Map;

import lib.kalu.mediaplayer.config.cache.CacheConfig;
import lib.kalu.mediaplayer.config.cache.CacheConfigManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.KernelApi;
import lib.kalu.mediaplayer.core.kernel.KernelEvent;
import lib.kalu.mediaplayer.util.MediaLogUtil;

@Keep
public final class ExoMediaPlayer implements KernelApi, Player.Listener {

    private long mSeek = 0L; // 快进
    private long mMax = 0L; // 试播时常
    private boolean mLoop = false; // 循环播放
    private boolean mMute = false; // 静音
    private String mUrl = null; // 视频串
    private android.media.MediaPlayer mMusicPlayer = null; // 配音音频

    private PlaybackParameters mSpeedPlaybackParameters;

    private ExoPlayer mExoPlayer;
    private KernelEvent mEvent;

    public ExoMediaPlayer(@NonNull KernelEvent event) {
        this.mEvent = event;
    }

    @NonNull
    @Override
    public ExoPlayer getPlayer() {
        return mExoPlayer;
    }

    @Override
    public void createDecoder(@NonNull Context context) {
        ExoPlayer.Builder builder = new ExoPlayer.Builder(context);
        builder.setAnalyticsCollector(new DefaultAnalyticsCollector(Clock.DEFAULT));
        builder.setBandwidthMeter(DefaultBandwidthMeter.getSingletonInstance(context));
        builder.setLoadControl(new DefaultLoadControl());
        builder.setMediaSourceFactory(new DefaultMediaSourceFactory(context));
        builder.setTrackSelector(new DefaultTrackSelector(context));
        builder.setRenderersFactory(new DefaultRenderersFactory(context));
        mExoPlayer = builder.build();
        mExoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
        setOptions();
        //播放器日志
        if (mExoPlayer.getTrackSelector() instanceof MappingTrackSelector) {
            mExoPlayer.addAnalyticsListener(new EventLogger((MappingTrackSelector) mExoPlayer.getTrackSelector(), "ExoPlayer"));
        }
        // exo视频播放器监听listener
        mExoPlayer.addListener(this);
    }

    @Override
    public void releaseDecoder() {
        releaseMusic();
        if (null != mExoPlayer) {
            mExoPlayer.setVideoSurface(null);
            mExoPlayer.removeListener(this);
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }

        // TODO: 2021-05-21  同步释放，防止卡顿
//            new Thread() {
//                @Override
//                public void run() {
//                    //异步释放，防止卡顿
//                    player.release();
//                }
//            }.start();

//        mIsPreparing = false;
//        mIsBuffering = false;
//        mLastReportedPlaybackState = Player.STATE_IDLE;
//        mLastReportedPlayWhenReady = false;
        mSpeedPlaybackParameters = null;
    }

//    public void setTrackSelector(TrackSelector trackSelector) {
//        mTrackSelector = trackSelector;
//    }
//
//    public void setRenderersFactory(RenderersFactory renderersFactory) {
//        mRenderersFactory = renderersFactory;
//    }

//    public void setLoadControl(LoadControl loadControl) {
//        mLoadControl = loadControl;
//    }

//    /**
//     * 设置播放地址
//     *
//     * @param url     播放地址
//     * @param headers 播放地址请求头
//     */
////    @Override
//    public void setDataSource(@NonNull Context context, @NonNull boolean cache, @NonNull String url, @Nullable Map<String, String> headers, @NonNull CacheConfig config) {
//        // 设置dataSource
//        if (url == null || url.length() == 0) {
//            if (getVideoPlayerChangeListener() != null) {
//                getVideoPlayerChangeListener().onInfo(PlayerType.MediaType.MEDIA_INFO_URL_NULL, 0);
//            }
//            return;
//        }
//    }

    @Override
    public void setDataSource(AssetFileDescriptor fd) {
        //no support
    }

    /**
     * 播放
     */
    @Override
    public void start() {
        if (mExoPlayer == null) {
            return;
        }
        mExoPlayer.setPlayWhenReady(true);
    }

    /**
     * 暂停
     */
    @Override
    public void pause() {
        if (mExoPlayer == null) {
            return;
        }
        mExoPlayer.setPlayWhenReady(false);
    }

    /**
     * 停止
     */
    @Override
    public void stop() {
        if (mExoPlayer == null) {
            return;
        }
        mExoPlayer.stop();
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

    /**
     * 调整进度
     */
    @Override
    public void seekTo(long time) {
        if (mExoPlayer == null) {
            return;
        }
        mExoPlayer.seekTo(time);
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
        return mExoPlayer.getDuration();
    }

    /**
     * 获取缓冲百分比
     */
    @Override
    public int getBufferedPercentage() {
        return mExoPlayer == null ? 0 : mExoPlayer.getBufferedPercentage();
    }

//    @Override
//    public void setReal(@NonNull Surface surface, @NonNull SurfaceHolder holder) {
//
//        // 设置渲染视频的View,主要用于SurfaceView
//        if (null != holder && null != mExoPlayer) {
//            try {
//                mExoPlayer.setVideoSurface(holder.getSurface());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (null != surface && null != mExoPlayer) {
//            try {
//                mExoPlayer.setVideoSurface(surface);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    @Override
    public void init(@NonNull Context context, @NonNull long seek, @NonNull long max, @NonNull String url) {

        // loading-start
        mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_INIT_START);

        // fail
        if (null == url || url.length() <= 0) {
            mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_INIT_COMPILE);
            mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_ERROR_URL);
        }
        // next
        else {

            if (mExoPlayer == null) {
                return;
            }
//        if (mMediaSource == null) {
//            return;
//        }
            if (mSpeedPlaybackParameters != null) {
                mExoPlayer.setPlaybackParameters(mSpeedPlaybackParameters);
            }
//        mIsPreparing = true;

            CacheConfig config = CacheConfigManager.getInstance().getCacheConfig();
            MediaSource mediaSource = ExoMediaSourceHelper.getInstance().getMediaSource(context, false, url, null, config);
            mediaSource.addEventListener(new Handler(), new MediaSourceEventListener() {
                @Override
                public void onLoadStarted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
                    MediaLogUtil.log("onEXOLoadStarted => ");
                }

                @Override
                public void onLoadCompleted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
                    MediaLogUtil.log("onEXOLoadCompleted => ");
                }
            });

            //准备播放
            mExoPlayer.setMediaSource(mediaSource);
            mExoPlayer.prepare();
            mExoPlayer.addAnalyticsListener(new AnalyticsListener() {
                @Override
                public void onPlayWhenReadyChanged(EventTime eventTime, boolean playWhenReady, int reason) {
                    MediaLogUtil.log("ononPlayWhenReadyChanged => ");
                }

                @Override
                public void onRenderedFirstFrame(EventTime eventTime, Object output, long renderTimeMs) {
                    MediaLogUtil.log("ononRenderedFirstFrame => ");
                }

                @Override
                public void onIsPlayingChanged(EventTime eventTime, boolean isPlaying) {
                    MediaLogUtil.log("ononIsPlayingChanged => isPlaying = " + isPlaying);
                }

                @Override
                public void onPlaybackStateChanged(EventTime eventTime, int state) {
                    MediaLogUtil.log("ononPlaybackStateChanged => state = " + state);

                    // 播放结束
                    if (state == Player.STATE_ENDED) {
                        mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_PLAYER_END);
                    }
                }

                @Override
                public void onPlayerError(EventTime eventTime, PlaybackException error) {
                }

                @Override
                public void onTimelineChanged(EventTime eventTime, int reason) {
                    MediaLogUtil.log("ononTimelineChanged => reason = " + reason);
                }

                @Override
                public void onEvents(Player player, Events events) {
                    MediaLogUtil.log("onmEvent.onEvent => events = " + events);
                }

                @Override
                public void onVideoInputFormatChanged(EventTime eventTime, Format format, @Nullable DecoderReuseEvaluation decoderReuseEvaluation) {
                    MediaLogUtil.log("ononVideoInputFormatChanged => ");
                }

                @Override
                public void onAudioInputFormatChanged(EventTime eventTime, Format format, @Nullable DecoderReuseEvaluation decoderReuseEvaluation) {
                    MediaLogUtil.log("ononAudioInputFormatChanged => ");
                }
            });
        }
    }

    @Override
    public void setSurface(@NonNull Surface surface) {
        if (null != surface && null != mExoPlayer) {
            try {
                mExoPlayer.setVideoSurface(surface);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setOptions() {
        //准备好就开始播放
        mExoPlayer.setPlayWhenReady(true);
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
        if (mSpeedPlaybackParameters != null) {
            return mSpeedPlaybackParameters.speed;
        }
        return 1f;
    }

    /**
     * 获取当前缓冲的网速
     */
    @Override
    public long getTcpSpeed() {
        // no support
        return 0;
    }

    @Override
    public void onPlayWhenReadyChanged(boolean playWhenReady, @Player.PlayWhenReadyChangeReason int playbackState) {
        MediaLogUtil.log("onEXOWhenReadyChanged => playbackState = " + playbackState);
//        if (getVideoPlayerChangeListener() == null) {
//            return;
//        }
//        if (mIsPreparing) {
//            return;
//        }
//        if (mLastReportedPlayWhenReady != playWhenReady || mLastReportedPlaybackState != playbackState) {
//            switch (playbackState) {
//                //最开始调用的状态
//                case Player.STATE_IDLE:
//                    mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_INIT_START);
//                    break;
//                //开始缓充
//                case Player.STATE_BUFFERING:
//                    mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_BUFFERING_START);
//                    mIsBuffering = true;
//                    break;
//                //开始播放
//                case Player.STATE_READY:
//
//                    MappingTrackSelector trackSelector = (MappingTrackSelector) mExoPlayer.getTrackSelector();
//                    MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
//                    if (mappedTrackInfo != null) {
//                        for (int i = 0; i < mappedTrackInfo.getRendererCount(); i++) {
//                            TrackGroupArray rendererTrackGroups = mappedTrackInfo.getTrackGroups(i);
//                            if (C.TRACK_TYPE_AUDIO == mappedTrackInfo.getRendererType(i)) { //判断是否是音轨
//                                for (int groupIndex = 0; groupIndex < rendererTrackGroups.length; groupIndex++) {
//                                    TrackGroup trackGroup = rendererTrackGroups.get(groupIndex);
//                                    MediaLogUtil.log("SRT => checkAudio => " + trackGroup.getFormat(0).toString());
//                                }
//                            } else if (C.TRACK_TYPE_TEXT == mappedTrackInfo.getRendererType(i)) { //判断是否是字幕
//                                for (int groupIndex = 0; groupIndex < rendererTrackGroups.length; groupIndex++) {
//                                    TrackGroup trackGroup = rendererTrackGroups.get(groupIndex);
//                                    MediaLogUtil.log("SRT => checkSubTitle => " + trackGroup.getFormat(0).toString());
//                                }
//                            }
//                        }
//                    }
//
//                    if (mIsBuffering) {
//                        mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_BUFFERING_END);
//                        mIsBuffering = false;
//                    }
//                    break;
//                //播放器已经播放完了媒体
//                case Player.STATE_ENDED:
//                    getVideoPlayerChangeListener().onCompletion();
//                    break;
//                default:
//                    break;
//            }
//            mLastReportedPlaybackState = playbackState;
//            mLastReportedPlayWhenReady = playWhenReady;
//        }
    }

//    @Override
//    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//
//    }

    @Override
    public void onPlayerError(PlaybackException error) {
        MediaLogUtil.log("onEXOPlayerError => " + error);

        if (null == error || !(error instanceof ExoPlaybackException))
            return;

        MediaLogUtil.log("onPlayerError => type = " + ((ExoPlaybackException) error).type + ", error = " + error);
        switch (((ExoPlaybackException) error).type) {
            case PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW:
                mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_INIT_COMPILE);
                mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_ERROR_SOURCE);
                break;
            case ExoPlaybackException.TYPE_RENDERER:
            case ExoPlaybackException.TYPE_UNEXPECTED:
            case ExoPlaybackException.TYPE_REMOTE:
//            case ExoPlaybackException.TYPE_SOURCE:
                mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_INIT_COMPILE);
                break;
            default:
                mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_INIT_COMPILE);
                mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_ERROR_RETRY);
                break;
        }
    }

    @Override
    public void onPlayerErrorChanged(@Nullable PlaybackException error) {
        MediaLogUtil.log("onEXOPlayerErrorChanged => " + error);

//        String errorCodeName = error.getErrorCodeName();
//        MediaLogUtil.log("onPlayerErrorChanged => errorCodeName = " + errorCodeName);
    }

    //    public void onPlayerError(PlaybackException error) {
//
//        String errorCodeName = error.getErrorCodeName();
//        MediaLogUtil.log("onPlayerError => errorCodeName = " + errorCodeName);
//
//    }

    @Override
    public void onVideoSizeChanged(VideoSize videoSize) {
        MediaLogUtil.log("onEXOVideoSizeChanged => ");
        onChanged(PlayerType.KernelType.EXO, videoSize.width, videoSize.height, videoSize.unappliedRotationDegrees > 0 ? videoSize.unappliedRotationDegrees : -1);
    }

    @Override
    public void onRenderedFirstFrame() {
        MediaLogUtil.log("onEXORenderedFirstFrame => ");
        // loading-close
        mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_INIT_COMPILE);

        // start
        mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_VIDEO_START);

        // 快进
        long seek = getSeek();
        if (seek > 0) {
            seekTo(seek);
        }
    }

    /****************/

    @Override
    public void setVolume(float v1, float v2) {
        mMute = (v1 <= 0 || v2 <= 0);
        if (mExoPlayer != null) {
            mExoPlayer.setVolume((v1 + v2) / 2);
        }
    }

    @Override
    public boolean isMute() {
        return mMute;
    }

    @Override
    public void setMusicPlayer(@NonNull android.media.MediaPlayer player) {
        this.mMusicPlayer = player;
    }

    @Override
    public android.media.MediaPlayer getMusicPlayer() {
        return mMusicPlayer;
    }

    @Override
    public String getUrl() {
        return mUrl;
    }

    @Override
    public void setUrl(String url) {
        this.mUrl = url;
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
    public void setLooping(boolean loop) {
        this.mLoop = loop;
    }

    @Override
    public boolean isLooping() {
        return mLoop;
    }

    /****************/
}
