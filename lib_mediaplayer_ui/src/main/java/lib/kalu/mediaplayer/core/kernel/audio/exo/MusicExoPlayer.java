package lib.kalu.mediaplayer.core.kernel.audio.exo;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;

import lib.kalu.exoplayer2.ffmpeg.DefaultAudioOnlyRenderersFactory;
import lib.kalu.exoplayer2.ffmpeg.FFmpegAudioOnlyRenderersFactory;
import lib.kalu.exoplayer2.ffmpeg.FFmpegAudioRenderersFactory;
import lib.kalu.exoplayer2.util.ExoLogUtil;
import lib.kalu.mediaplayer.config.player.PlayerBuilder;
import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.audio.OnMusicPlayerChangeListener;
import lib.kalu.mediaplayer.core.kernel.audio.MusicKernelApi;

@Keep
public final class MusicExoPlayer implements MusicKernelApi {

    private ExoPlayer mExoPlayer;
    private AnalyticsListener mAnalyticsListener;
    //
    private OnMusicPlayerChangeListener mOnMusicPlayerChangeListener;

    public MusicExoPlayer() {
    }

    @Override
    public void setMusicListener(@NonNull OnMusicPlayerChangeListener listener) {
        ExoLogUtil.log("MusicExoPlayer => setMusicListener => mExoPlayer = " + mExoPlayer + ", listener = " + listener);
        mOnMusicPlayerChangeListener = listener;
    }

    @Override
    public void createDecoder(@NonNull Context context) {
        if (null != mExoPlayer) {
            release();
        }
        if (null == mExoPlayer) {
            ExoLogUtil.log("MusicExoPlayer => createDecoder => mExoPlayer = " + mExoPlayer);
            ExoPlayer.Builder builder = new ExoPlayer.Builder(context);
//            builder.setAnalyticsCollector(new DefaultAnalyticsCollector(Clock.DEFAULT));
//        builder.setBandwidthMeter(DefaultBandwidthMeter.getSingletonInstance(context));
//        builder.setTrackSelector(new DefaultTrackSelector(context));
            builder.setLoadControl(new DefaultLoadControl());
            builder.setMediaSourceFactory(new DefaultMediaSourceFactory(context));
            PlayerBuilder config = PlayerManager.getInstance().getConfig();
            int exoFFmpeg = config.getExoFFmpeg();
            if (exoFFmpeg != PlayerType.FFmpegType.EXO_EXT_FFPEMG_NULL) {
                builder.setRenderersFactory(new FFmpegAudioOnlyRenderersFactory(context));
            } else {
                builder.setRenderersFactory(new DefaultAudioOnlyRenderersFactory(context));
            }
            mExoPlayer = builder.build();
            setVolume(1F);
            setLooping(false);
            setSeekParameters();
        }
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
    public void start(long position, OnMusicPlayerChangeListener l) {
        // 1
        if (null == l) {
            removeListener(true);
        } else {
            removeListener(false);
        }
        // 2
        if (null != l) {
            setMusicListener(l);
        }
        // 3
        addListener(position);
        // 3
        setVolume(1F);
        // 4
        if (null != mExoPlayer) {
            ExoLogUtil.log("MusicExoPlayer => start => mExoPlayer = " + mExoPlayer);
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
        removeListener(true);
        if (null != mExoPlayer) {
            ExoLogUtil.log("MusicExoPlayer => release => mExoPlayer = " + mExoPlayer);
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void addListener(long position) {

        if (null != mExoPlayer) {
            ExoLogUtil.log("MusicExoPlayer => addListener => mExoPlayer = " + mExoPlayer);
            final boolean[] status = {false};
            mAnalyticsListener = new AnalyticsListener() {

                @Override
                public void onPlayerError(EventTime eventTime, PlaybackException error) {
                    ExoLogUtil.log("MusicExoPlayer => onPlayerError => " + error.getMessage(), error);
                    if (null != mOnMusicPlayerChangeListener) {
                        mOnMusicPlayerChangeListener.onError();
                    }
                }

                @Override
                public void onPlaybackStateChanged(EventTime eventTime, int state) {
                    ExoLogUtil.log("MusicExoPlayer => onPlaybackStateChanged => state = " + state + ", mOnMusicPlayerChangeListener = " + mOnMusicPlayerChangeListener);
                    // 播放结束
                    if (state == Player.STATE_ENDED) {
                        if (null != mOnMusicPlayerChangeListener) {
                            mOnMusicPlayerChangeListener.onEnd();
                        }
                    }
                    // 播放开始
                    else if (state == Player.STATE_READY) {
                        if (!status[0] && position > 0) {
                            long duration = getDuration();
                            if (duration > 0 && position <= duration) {
                                status[0] = true;
                                seekTo(position);
                                ExoLogUtil.log("MusicExoPlayer => onPlaybackStateChanged => seekTo => state = " + state);
                            } else {
                                if (null != mOnMusicPlayerChangeListener) {
                                    ExoLogUtil.log("MusicExoPlayer => onPlaybackStateChanged => onStart => state = " + state);
                                    mOnMusicPlayerChangeListener.onStart();
                                }
                            }
                        } else {
                            if (null != mOnMusicPlayerChangeListener) {
                                ExoLogUtil.log("MusicExoPlayer => onPlaybackStateChanged => onStart => state = " + state);
                                mOnMusicPlayerChangeListener.onStart();
                            }
                        }
                    }
                }
            };
            mExoPlayer.addAnalyticsListener(mAnalyticsListener);
        }
    }

    @Override
    public void removeListener(boolean clear) {
        ExoLogUtil.log("MusicExoPlayer => removeListener => mExoPlayer = " + mExoPlayer);
        if (clear && null != mOnMusicPlayerChangeListener) {
            setMusicListener(null);
            mOnMusicPlayerChangeListener = null;
        }
        if (null != mAnalyticsListener) {
            if (null != mExoPlayer) {
                mExoPlayer.removeAnalyticsListener(mAnalyticsListener);
            }
            mAnalyticsListener = null;
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
    public long getPosition() {
        ExoLogUtil.log("MusicExoPlayer => getPosition =>  mExoPlayer = " + mExoPlayer);
        if (mExoPlayer == null)
            return 0L;
        return mExoPlayer.getCurrentPosition();
    }

    @Override
    public void setSeekParameters() {
        if (null != mExoPlayer) {
            ExoLogUtil.log("MusicExoPlayer => setSeekParameters => mExoPlayer = " + mExoPlayer);
            mExoPlayer.setSeekParameters(SeekParameters.DEFAULT);
        }
    }
}
