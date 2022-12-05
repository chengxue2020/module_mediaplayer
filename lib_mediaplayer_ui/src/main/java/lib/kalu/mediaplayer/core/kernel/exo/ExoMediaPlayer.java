package lib.kalu.mediaplayer.core.kernel.exo;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.DefaultLoadControl;
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
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.video.VideoSize;

import lib.kalu.exoplayer2.ffmpeg.FFmpegDefaultRenderersFactory;
import lib.kalu.mediaplayer.config.player.PlayerBuilder;
import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.KernelApi;
import lib.kalu.mediaplayer.core.kernel.KernelEvent;
import lib.kalu.mediaplayer.util.MPLogUtil;
import lib.kalu.mediaplayer.util.SpeedUtil;

@Keep
public final class ExoMediaPlayer implements KernelApi, AnalyticsListener {

    private long mSeek = 0L; // 快进
    private long mMax = 0L; // 试播时常
    private boolean mLoop = false; // 循环播放
    private boolean mLive = false;
    private boolean mMute = false;
    private boolean mTimer = false;
    private String mUrl = null; // 视频串

    private boolean mInvisibleStop = false; // 不可见静音
    private boolean mInvisibleIgnore = false; // 不可见忽略, 什么也不做
    private boolean mInvisibleRelease = true; // 不可见生命周期自动销毁


    private String mExternalMusicPath = null;
    private boolean mExternalMusicPrepared = false;
    private boolean mExternalMusicLoop = false;
    private boolean mExternalMusicAuto = false;

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
    public void createDecoder(@NonNull Context context, @NonNull boolean mute, @NonNull boolean logger) {
        ExoPlayer.Builder builder = new ExoPlayer.Builder(context);
        builder.setAnalyticsCollector(new DefaultAnalyticsCollector(Clock.DEFAULT));
        builder.setBandwidthMeter(DefaultBandwidthMeter.getSingletonInstance(context));
        builder.setLoadControl(new DefaultLoadControl());
        builder.setMediaSourceFactory(new DefaultMediaSourceFactory(context));
        builder.setTrackSelector(new DefaultTrackSelector(context));
        builder.setRenderersFactory(new FFmpegDefaultRenderersFactory(context));
        mExoPlayer = builder.build();
        mExoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
        mExoPlayer.addAnalyticsListener(this);
        if (mute) {
            setVolume(0f, 0f);
        }
        setOptions();
        //播放器日志
        if (mExoPlayer.getTrackSelector() instanceof MappingTrackSelector) {
            mExoPlayer.addAnalyticsListener(new EventLogger((MappingTrackSelector) mExoPlayer.getTrackSelector(), "ExoPlayer"));
        }
    }

    @Override
    public void init(@NonNull Context context, @NonNull String url) {
        // loading-start
        mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_LOADING_START);

        // fail
        if (null == url || url.length() <= 0) {
            mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_LOADING_STOP);
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

            PlayerBuilder config = PlayerManager.getInstance().getConfig();
            MediaSource mediaSource = ExoMediaSourceHelper.getInstance().getMediaSource(context, false, url, null, config.getCacheType(), config.getCacheMax(), config.getCacheDir(), config.getCacheSalt());
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

            //准备播放
            mExoPlayer.setMediaSource(mediaSource);
            mExoPlayer.prepare();
        }
    }

    @Override
    public void setDataSource(AssetFileDescriptor fd) {
        //no support
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
    public void seekTo(@NonNull long position) {
        try {
            mExoPlayer.seekTo(position);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
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
//                MediaLogUtil.log(e.getMessage(), e);
//            }
//        }
//
//        if (null != surface && null != mExoPlayer) {
//            try {
//                mExoPlayer.setVideoSurface(surface);
//            } catch (Exception e) {
//                MediaLogUtil.log(e.getMessage(), e);
//            }
//        }
//    }

    @Override
    public void setSurface(@NonNull Surface surface) {
//        MediaLogUtil.log("setSurface => surface = " + surface + ", mExoPlayer = " + mExoPlayer);
        if (null != surface && null != mExoPlayer) {
            try {
                mExoPlayer.setVideoSurface(surface);
            } catch (Exception e) {
                MPLogUtil.log(e.getMessage(), e);
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
        try {
            return mSpeedPlaybackParameters.speed;
        } catch (Exception e) {
            return 1f;
        }
    }

    @Override
    public String getTcpSpeed(Context context) {
        return SpeedUtil.getNetSpeed(context);
    }

    /*****************/

    @Override
    public void onPlayWhenReadyChanged(EventTime eventTime, boolean playWhenReady, int reason) {

//        MPLogUtil.log("ExoMediaPlayer => onPlayWhenReadyChanged => playWhenReady = " + playWhenReady + ", reason = " + reason);

//        // loading-start
//        if (reason == 1 && !playWhenReady) {
//            MPLogUtil.log("ExoMediaPlayer => onPlayWhenReadyChanged => 准备fail");
//            mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_LOADING_START);
//        }
//        // loading-close
//        else if (reason == 1 && playWhenReady) {
//            MPLogUtil.log("ExoMediaPlayer => onPlayWhenReadyChanged => 准备succ");
//            mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_LOADING_STOP);
//        }
    }

    @Override
    public void onPlayerError(EventTime eventTime, PlaybackException error) {
        MPLogUtil.log("ExoMediaPlayer => onPlayerError => error = " + error.getMessage());

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
                mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_LOADING_STOP);
                mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_ERROR_SOURCE);
                break;
            default:
                mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_LOADING_STOP);
                mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_ERROR_RETRY);
                break;
        }

        mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_ERROR_SOURCE);
    }

    @Override
    public void onTimelineChanged(EventTime eventTime, int reason) {
        MPLogUtil.log("ExoMediaPlayer => onTimelineChanged => reason = " + reason + ", totalBufferedDurationMs = " + eventTime.totalBufferedDurationMs + ", realtimeMs = " + eventTime.realtimeMs);
    }

    @Override
    public void onEvents(Player player, Events events) {
//                    MediaLogUtil.log("ExoMediaPlayer => onEvents => isPlaying = " + player.isPlaying());
    }

    @Override
    public void onVideoSizeChanged(EventTime eventTime, VideoSize videoSize) {
        onChanged(PlayerType.KernelType.EXO, videoSize.width, videoSize.height, videoSize.unappliedRotationDegrees > 0 ? videoSize.unappliedRotationDegrees : -1);
    }

    @Override
    public void onIsPlayingChanged(EventTime eventTime, boolean isPlaying) {
        MPLogUtil.log("ExoMediaPlayer => onIsPlayingChanged => isPlaying = " + isPlaying);
    }

    @Override
    public void onPlaybackStateChanged(EventTime eventTime, int state) {
        MPLogUtil.log("ExoMediaPlayer => onPlaybackStateChanged => state = " + state + ", mute = " + isMute());

        // 播放错误
        if (state == Player.STATE_IDLE) {
            long position = getPosition();
            long seek = getSeek();
            MPLogUtil.log("ExoMediaPlayer => onPlaybackStateChanged[播放错误] => position = " + position + ", seek = " + seek);
            mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_LOADING_STOP);
            if (position > seek) {
                mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_ERROR_SOURCE);
            }
        }
        // 播放结束
        else if (state == Player.STATE_ENDED) {
            MPLogUtil.log("ExoMediaPlayer => onPlaybackStateChanged[播放结束] =>");
            mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_VIDEO_END);
        }
        // 播放开始
        else if (state == Player.STATE_READY) {
            long position = getPosition();
            long seek = getSeek();
            MPLogUtil.log("ExoMediaPlayer => onPlaybackStateChanged[播放开始] => position = " + position + ", seek = " + seek);
            mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_BUFFERING_STOP);
            mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_LOADING_STOP);
            if (position <= seek) {
                mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_VIDEO_START);
            }
        }
        // 播放缓冲
        else if (state == Player.STATE_BUFFERING) {
            long position = getPosition();
            long seek = getSeek();
            MPLogUtil.log("ExoMediaPlayer => onPlaybackStateChanged[播放缓冲] => position = " + position + ", seek = " + seek);
            if (position > seek) {
                mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_BUFFERING_START);
            } else {
                mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_LOADING_START);
            }
        }
        // 未知??
        else {
            MPLogUtil.log("ExoMediaPlayer => onPlaybackStateChanged[未知??] =>");
        }
    }

    @Override
    public void onRenderedFirstFrame(EventTime eventTime, Object output, long renderTimeMs) {

//                    long seek = getSeek();
//                    long position = getPosition();
//                    MediaLogUtil.log("ExoMediaPlayer => onRenderedFirstFrame => seek = " + seek + ", position = " + position);
//                    if (seek > 0 && seek == position) {
//                        mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_LOADING_STOP);
//                        mEvent.onEvent(PlayerType.KernelType.EXO, PlayerType.EventType.EVENT_VIDEO_START);
//                    }
    }

    @Override
    public void onVideoInputFormatChanged(EventTime eventTime, Format format, @Nullable DecoderReuseEvaluation decoderReuseEvaluation) {

        long seek = getSeek();
        long position = getPosition();
        MPLogUtil.log("ExoMediaPlayer => onVideoInputFormatChanged => seek = " + seek + ", position = " + position);
        // 快进 => begin
        if (seek > 0 && seek != position) {
            seekTo(seek);
        }
    }

    @Override
    public void onAudioInputFormatChanged(EventTime eventTime, Format format, @Nullable DecoderReuseEvaluation decoderReuseEvaluation) {
        MPLogUtil.log("ExoMediaPlayer => onAudioInputFormatChanged =>");
        // mute
//        if (isMute()) {
//            setVolume(0, 0);
//        }
    }

    /****************/

    @Override
    public void setVolume(float v1, float v2) {
        try {
            float value = Math.max(v1, v2);
            if (value > 1f) {
                value = 1f;
            }
            mExoPlayer.setVolume(value);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
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
    public boolean isLive() {
        return mLive;
    }

    @Override
    public void setLive(@NonNull boolean live) {
        this.mLive = live;
    }

    @Override
    public boolean isTimer() {
        return mTimer;
    }

    @Override
    public void setTimer(@NonNull boolean v) {
        mTimer = v;
    }

    @Override
    public void setLooping(boolean loop) {
        this.mLoop = loop;
    }

    @Override
    public boolean isLooping() {
        return mLoop;
    }

    @Override
    public boolean isInvisibleStop() {
        return mInvisibleStop;
    }

    @Override
    public void setInvisibleStop(boolean v) {
        mInvisibleStop = v;
    }

    @Override
    public boolean isInvisibleIgnore() {
        return mInvisibleIgnore;
    }

    @Override
    public void setInvisibleIgnore(boolean v) {
        mInvisibleIgnore = v;
    }

    @Override
    public boolean isInvisibleRelease() {
        return mInvisibleRelease;
    }

    @Override
    public void setInvisibleRelease(boolean v) {
        mInvisibleRelease = v;
    }

    /****************/

    @Override
    public boolean isExternalMusicPrepared() {
        return mExternalMusicPrepared;
    }

    @Override
    public void setExternalMusicPrepared(boolean v) {
        this.mExternalMusicPrepared = v;
    }

    @Override
    public boolean isExternalMusicLoop() {
        return mExternalMusicLoop;
    }

    @Override
    public void setExternalMusicLoop(boolean loop) {
        this.mExternalMusicLoop = loop;
    }

    @Override
    public boolean isExternalMusicAuto() {
        return mExternalMusicAuto;
    }

    @Override
    public void setExternalMusicAuto(boolean auto) {
        this.mExternalMusicAuto = auto;
    }

    @Override
    public void setExternalMusicPath(@NonNull String musicPath) {
        this.mExternalMusicPath = musicPath;
    }

    @Override
    public String getExternalMusicPath() {
        return this.mExternalMusicPath;
    }

    /************************/


    /**
     * 播放
     */
    @Override
    public void start() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            doStart();
        } else {
            mHandler.sendEmptyMessage(99991);
        }
    }

    private void doStart() {
        try {
//            mExoPlayer.prepare();
//            mExoPlayer.play();
            mExoPlayer.setPlayWhenReady(true);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    /**
     * 暂停
     */
    @Override
    public void pause() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            doPause();
        } else {
            mHandler.sendEmptyMessage(99992);
        }
    }

    private void doPause() {
        try {
//            mExoPlayer.pause();
            mExoPlayer.setPlayWhenReady(false);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    /**
     * 停止
     */
    @Override
    public void stop() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            doStop();
        } else {
            mHandler.sendEmptyMessage(99993);
        }
    }

    private void doStop() {
        try {
            mExoPlayer.stop();
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    @Override
    public void releaseDecoder() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            doReleaseDecoder();
        } else {
            mHandler.sendEmptyMessage(99995);
        }
    }

    private void doReleaseDecoder() {
        releaseExternalMusic();

        if (null != mExoPlayer) {
            mExoPlayer.addAnalyticsListener(null);
            mExoPlayer.setVideoSurface(null);
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

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            // start
            if (msg.what == 99991) {
                doStart();
            }
            // pause
            else if (msg.what == 99992) {
                doPause();
            }
            // stop
            else if (msg.what == 99993) {
                doStop();
            }
            // release
            else if (msg.what == 99995) {
                doReleaseDecoder();
            }
        }
    };

    /************************/
}
