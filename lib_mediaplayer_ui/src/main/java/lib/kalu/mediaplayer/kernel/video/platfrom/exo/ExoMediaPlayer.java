package lib.kalu.mediaplayer.kernel.video.platfrom.exo;

import static com.google.android.exoplayer2.ExoPlaybackException.TYPE_SOURCE;

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
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsCollector;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.LoadEventInfo;
import com.google.android.exoplayer2.source.MediaLoadData;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.EventLogger;

import java.util.Map;

import lib.kalu.mediaplayer.cache.CacheConfig;
import lib.kalu.mediaplayer.kernel.video.core.VideoPlayerCore;
import lib.kalu.mediaplayer.kernel.video.utils.VideoLogUtils;
import lib.kalu.mediaplayer.ui.config.PlayerType;

/**
 * <pre>
 *     @author yangchong
 *     blog  : https://github.com/yangchong211
 *     time  : 2018/11/9
 *     desc  : exo视频播放器实现类
 *     revise:
 * </pre>
 */
@Keep
public class ExoMediaPlayer extends VideoPlayerCore implements Player.Listener {

    protected SimpleExoPlayer mInternalPlayer;
    protected MediaSource mMediaSource;
    protected ExoMediaSourceHelper mMediaSourceHelper;
    private PlaybackParameters mSpeedPlaybackParameters;
    private int mLastReportedPlaybackState = Player.STATE_IDLE;
    private boolean mLastReportedPlayWhenReady = false;
    private boolean mIsPreparing;
    private boolean mIsBuffering;

    private LoadControl mLoadControl;
    private RenderersFactory mRenderersFactory;
    private TrackSelector mTrackSelector;

    public ExoMediaPlayer(@NonNull Context context) {
        mMediaSourceHelper = ExoMediaSourceHelper.getInstance(context);
    }

    @NonNull
    @Override
    public SimpleExoPlayer getPlayer() {
        return mInternalPlayer;
    }

    @Override
    public void initPlayer(@NonNull Context context, @NonNull String url) {

        //创建exo播放器
        mInternalPlayer = new SimpleExoPlayer.Builder(
                context,
                mRenderersFactory == null ? mRenderersFactory = new DefaultRenderersFactory(context) : mRenderersFactory,
                mTrackSelector == null ? mTrackSelector = new DefaultTrackSelector(context) : mTrackSelector,
                new DefaultMediaSourceFactory(context),
                mLoadControl == null ? mLoadControl = new DefaultLoadControl() : mLoadControl,
                DefaultBandwidthMeter.getSingletonInstance(context),
                new AnalyticsCollector(Clock.DEFAULT))
                .build();

        setOptions();

        //播放器日志
        if (VideoLogUtils.isIsLog() && mTrackSelector instanceof MappingTrackSelector) {
            mInternalPlayer.addAnalyticsListener(new EventLogger((MappingTrackSelector) mTrackSelector, "ExoPlayer"));
        }
        initListener();
    }

    /**
     * exo视频播放器监听listener
     */
    private void initListener() {
        mInternalPlayer.addListener(this);
        mInternalPlayer.addVideoListener(this);
    }

    public void setTrackSelector(TrackSelector trackSelector) {
        mTrackSelector = trackSelector;
    }

    public void setRenderersFactory(RenderersFactory renderersFactory) {
        mRenderersFactory = renderersFactory;
    }

    public void setLoadControl(LoadControl loadControl) {
        mLoadControl = loadControl;
    }

    /**
     * 设置播放地址
     *
     * @param path    播放地址
     * @param headers 播放地址请求头
     */
    @Override
    public void setDataSource(@NonNull Context context, @NonNull String path, @Nullable Map<String, String> headers, @NonNull CacheConfig config) {
        // 设置dataSource
        if (path == null || path.length() == 0) {
            if (getVideoPlayerChangeListener() != null) {
                getVideoPlayerChangeListener().onInfo(PlayerType.MediaType.MEDIA_INFO_URL_NULL, 0);
            }
            return;
        }
        mMediaSource = mMediaSourceHelper.getMediaSource(context, path, headers, config);
    }

    @Override
    public void setDataSource(AssetFileDescriptor fd) {
        //no support
    }

    /**
     * 准备开始播放（异步）
     */
    @Override
    public void prepareAsync() {
        if (mInternalPlayer == null) {
            return;
        }
        if (mMediaSource == null) {
            return;
        }
        if (mSpeedPlaybackParameters != null) {
            mInternalPlayer.setPlaybackParameters(mSpeedPlaybackParameters);
        }
        mIsPreparing = true;
        mMediaSource.addEventListener(new Handler(), mMediaSourceEventListener);
        //准备播放
        mInternalPlayer.prepare(mMediaSource);
    }

    /**
     * 播放
     */
    @Override
    public void start() {
        if (mInternalPlayer == null) {
            return;
        }
        mInternalPlayer.setPlayWhenReady(true);
    }

    /**
     * 暂停
     */
    @Override
    public void pause() {
        if (mInternalPlayer == null) {
            return;
        }
        mInternalPlayer.setPlayWhenReady(false);
    }

    /**
     * 停止
     */
    @Override
    public void stop() {
        if (mInternalPlayer == null) {
            return;
        }
        mInternalPlayer.stop();
    }

    private MediaSourceEventListener mMediaSourceEventListener = new MediaSourceEventListener() {

        @Override
        public void onLoadStarted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            if (getVideoPlayerChangeListener() != null && mIsPreparing) {
                getVideoPlayerChangeListener().onPrepared();
            }
        }
    };

    /**
     * 重置播放器
     */
    @Override
    public void reset() {
        if (mInternalPlayer != null) {
            mInternalPlayer.stop(true);
            mInternalPlayer.setVideoSurface(null);
            mIsPreparing = false;
            mIsBuffering = false;
            mLastReportedPlaybackState = Player.STATE_IDLE;
            mLastReportedPlayWhenReady = false;
        }
    }

    /**
     * 是否正在播放
     */
    @Override
    public boolean isPlaying() {
        if (mInternalPlayer == null) {
            return false;
        }
        int state = mInternalPlayer.getPlaybackState();
        switch (state) {
            case Player.STATE_BUFFERING:
            case Player.STATE_READY:
                return mInternalPlayer.getPlayWhenReady();
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
        if (mInternalPlayer == null) {
            return;
        }
        mInternalPlayer.seekTo(time);
    }

    /**
     * 释放播放器
     */
    @Override
    public void release() {
        if (mInternalPlayer != null) {
            mInternalPlayer.removeListener(this);
            mInternalPlayer.removeVideoListener(this);
            final SimpleExoPlayer player = mInternalPlayer;
            mInternalPlayer = null;

            // TODO: 2021-05-21  同步释放，防止卡顿
            player.release();
//            new Thread() {
//                @Override
//                public void run() {
//                    //异步释放，防止卡顿
//                    player.release();
//                }
//            }.start();
        }

        mIsPreparing = false;
        mIsBuffering = false;
        mLastReportedPlaybackState = Player.STATE_IDLE;
        mLastReportedPlayWhenReady = false;
        mSpeedPlaybackParameters = null;
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getCurrentPosition() {
        if (mInternalPlayer == null) {
            return 0;
        }
        return mInternalPlayer.getCurrentPosition();
    }

    /**
     * 获取视频总时长
     */
    @Override
    public long getDuration() {
        if (mInternalPlayer == null) {
            return 0;
        }
        return mInternalPlayer.getDuration();
    }

    /**
     * 获取缓冲百分比
     */
    @Override
    public int getBufferedPercentage() {
        return mInternalPlayer == null ? 0 : mInternalPlayer.getBufferedPercentage();
    }

    /**
     * 设置渲染视频的View,主要用于SurfaceView
     */
    @Override
    public void setSurface(Surface surface) {
        if (surface != null) {
            try {
                if (mInternalPlayer != null) {
                    mInternalPlayer.setVideoSurface(surface);
                }
            } catch (Exception e) {
                if (null != getVideoPlayerChangeListener()) {
                    getVideoPlayerChangeListener().onError(PlayerType.ErrorType.TYPE_UNEXPECTED, e.getMessage());
                }
            }
        }
    }

    @Override
    public void setDisplay(SurfaceHolder holder) {
        if (holder == null) {
            setSurface(null);
        } else {
            setSurface(holder.getSurface());
        }
    }

    /**
     * 设置音量
     */
    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        if (mInternalPlayer != null) {
            mInternalPlayer.setVolume((leftVolume + rightVolume) / 2);
        }
    }

    /**
     * 设置是否循环播放
     */
    @Override
    public void setLooping(boolean isLooping) {
        if (mInternalPlayer != null) {
            mInternalPlayer.setRepeatMode(isLooping ? Player.REPEAT_MODE_ALL : Player.REPEAT_MODE_OFF);
        }
    }

    @Override
    public void setOptions() {
        //准备好就开始播放
        mInternalPlayer.setPlayWhenReady(true);
    }

    /**
     * 设置播放速度
     */
    @Override
    public void setSpeed(float speed) {
        PlaybackParameters playbackParameters = new PlaybackParameters(speed);
        mSpeedPlaybackParameters = playbackParameters;
        if (mInternalPlayer != null) {
            mInternalPlayer.setPlaybackParameters(playbackParameters);
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
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (getVideoPlayerChangeListener() == null) {
            return;
        }
        if (mIsPreparing) {
            return;
        }
        if (mLastReportedPlayWhenReady != playWhenReady || mLastReportedPlaybackState != playbackState) {
            switch (playbackState) {
                //最开始调用的状态
                case Player.STATE_IDLE:
                    break;
                //开始缓充
                case Player.STATE_BUFFERING:
                    getVideoPlayerChangeListener().onInfo(PlayerType.MediaType.MEDIA_INFO_BUFFERING_START, getBufferedPercentage());
                    mIsBuffering = true;
                    break;
                //开始播放
                case Player.STATE_READY:
                    if (mIsBuffering) {
                        getVideoPlayerChangeListener().onInfo(PlayerType.MediaType.MEDIA_INFO_BUFFERING_END, getBufferedPercentage());
                        mIsBuffering = false;
                    }
                    break;
                //播放器已经播放完了媒体
                case Player.STATE_ENDED:
                    getVideoPlayerChangeListener().onCompletion();
                    break;
                default:
                    break;
            }
            mLastReportedPlaybackState = playbackState;
            mLastReportedPlayWhenReady = playWhenReady;
        }
    }

    public void onPlayerError(PlaybackException error) {
        if (getVideoPlayerChangeListener() != null) {
            int type = ((ExoPlaybackException) error).type;
            if (type == TYPE_SOURCE) {
                //错误的链接
                getVideoPlayerChangeListener().onError(PlayerType.ErrorType.TYPE_SOURCE, error.getMessage());
            } else if (type == ExoPlaybackException.TYPE_RENDERER
                    || type == ExoPlaybackException.TYPE_UNEXPECTED
                    || type == ExoPlaybackException.TYPE_REMOTE
                    || type == ExoPlaybackException.TYPE_SOURCE) {
                getVideoPlayerChangeListener().onError(PlayerType.ErrorType.TYPE_UNEXPECTED, error.getMessage());
            }
        }
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        if (getVideoPlayerChangeListener() != null) {
            getVideoPlayerChangeListener().onVideoSizeChanged(width, height);
            if (unappliedRotationDegrees > 0) {
                getVideoPlayerChangeListener().onInfo(PlayerType.MediaType.MEDIA_INFO_VIDEO_ROTATION_CHANGED, unappliedRotationDegrees);
            }
        }
    }

    @Override
    public void onRenderedFirstFrame() {
        if (getVideoPlayerChangeListener() != null && mIsPreparing) {
            getVideoPlayerChangeListener().onInfo(PlayerType.MediaType.MEDIA_INFO_VIDEO_RENDERING_START, 0);
            mIsPreparing = false;
        }
    }
}