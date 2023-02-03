package lib.kalu.mediaplayer.core.kernel.video.vlc;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.KernelApi;
import lib.kalu.mediaplayer.core.kernel.video.KernelEvent;
import lib.kalu.mediaplayer.util.MPLogUtil;
import lib.kalu.vlc.widget.OnVlcInfoChangeListener;
import lib.kalu.vlc.widget.VlcPlayer;

@Keep
public final class VideoVlcPlayer implements KernelApi {

    private long mSeek = 0L; // 快进
    private long mMax = 0L; // 试播时常
    private boolean mLoop = false; // 循环播放
    private boolean mLive = false;
    private boolean mMute = false;
    private String mUrl = null; // 视频串
    private boolean mReadying = false;

    private boolean mInvisibleStop = false; // 不可见静音
    private boolean mInvisibleIgnore = false; // 不可见忽略, 什么也不做
    private boolean mInvisibleRelease = true; // 不可见生命周期自动销毁


    private String mExternalMusicPath = null;
    private boolean mExternalMusicLoop = false;
    private boolean mExternalMusicPlayWhenReady = false;
    private boolean mExternalMusicEqualLength = true;

    private KernelEvent mEvent;
    private lib.kalu.vlc.widget.VlcPlayer mPlayer;
    private lib.kalu.vlc.widget.OnVlcInfoChangeListener mPlayerListener;

    public VideoVlcPlayer(@NonNull KernelEvent event) {
        setReadying(false);
        this.mEvent = event;
        MPLogUtil.log("VideoVlcPlayer =>");
    }

    @NonNull
    @Override
    public VideoVlcPlayer getPlayer() {
        return this;
    }

    @Override
    public void onUpdateTimeMillis() {
        if (null != mEvent) {
            long position = getPosition();
            long duration = getDuration();
            if (position > 0 && duration > 0) {
                long seek = getSeek();
                long max = getMax();
                boolean looping = isLooping();
                mEvent.onUpdateTimeMillis(looping, max, seek, position, duration);
            }
        }
    }

    @Override
    public void createDecoder(@NonNull Context context, @NonNull boolean logger, @NonNull int seekParameters) {
        releaseDecoder();
        mPlayer = new VlcPlayer(context);
        MPLogUtil.log("VideoVlcPlayer => createDecoder => mPlayer = " + mPlayer);
        setLooping(false);
        setVolume(1F, 1F);
        initListener();
    }

    @Override
    public void releaseDecoder() {
        releaseExternalMusic();
        setReadying(false);
        if (null != mEvent) {
            mEvent = null;
        }
        if (null != mPlayerListener) {
            if (null != mPlayer) {
                mPlayer.setOnVlcInfoChangeListener(null);
            }
            mPlayerListener = null;
        }
        if (null != mPlayer) {
            mPlayer.setSurface(null);
        }
        stop();
        if (null != mPlayer) {
            MPLogUtil.log("VideoVlcPlayer => releaseDecoder => mPlayer = " + mPlayer);
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void init(@NonNull Context context, @NonNull String url) {
        // loading-start
//        if (null != mEvent) {
//            mEvent.onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_LOADING_START);
//        }

        MPLogUtil.log("VideoVlcPlayer => init => url = " + url);
        MPLogUtil.log("VideoVlcPlayer => init => mPlayer = " + mPlayer);

        // 设置dataSource
        if (url == null || url.length() == 0) {
            if (null != mEvent) {
                mEvent.onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_LOADING_STOP);
                mEvent.onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_ERROR_URL);
            }
            return;
        }

        if (null != mPlayer) {
            mPlayer.setDataSource(Uri.parse(url));
            mPlayer.prepare();
            mPlayer.play();
        } else {
            if (null != mEvent) {
                mEvent.onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_ERROR_PARSE);
            }
        }
    }

    /**
     * MediaPlayer视频播放器监听listener
     */
    private void initListener() {
        MPLogUtil.log("VideoVlcPlayer => initListener =>");
        mPlayerListener = new OnVlcInfoChangeListener() {
            @Override
            public void onStart() {
                if (null != mEvent) {
                    mEvent.onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_LOADING_START);
                }
            }

            @Override
            public void onPlay() {
                if (null != mEvent) {
                    mEvent.onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_LOADING_STOP);
                    mEvent.onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_VIDEO_START);
                }

                long seek = getSeek();
                if (seek > 0) {
                    seekTo(seek, false);
                }
            }

            @Override
            public void onPause() {

            }

            @Override
            public void onResume() {

            }

            @Override
            public void onEnd() {
                if (null != mEvent) {
                    mEvent.onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_VIDEO_END);
                }
            }

            @Override
            public void onError() {
                if (null != mEvent) {
                    mEvent.onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_LOADING_STOP);
                    mEvent.onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_ERROR_PARSE);
                }
            }
        };
        mPlayer.setOnVlcInfoChangeListener(mPlayerListener);
    }

    /**
     * 播放
     */
    @Override
    public void start() {
        if (null != mPlayer) {
            mPlayer.play();
        }
    }

    /**
     * 暂停
     */
    @Override
    public void pause() {
        if (null != mPlayer) {
            mPlayer.pause();
        }
    }

    /**
     * 停止
     */
    @Override
    public void stop() {
        if (null != mPlayer) {
            mPlayer.pause();
            mPlayer.stop();
        }
    }

    /**
     * 是否正在播放
     */
    @Override
    public boolean isPlaying() {
        if (null != mPlayer) {
            return mPlayer.isPlaying();
        } else {
            return false;
        }
    }

    /**
     * 调整进度
     */
    @Override
    public void seekTo(long time, @NonNull boolean seekHelp) {
//        setReadying(false);
//        try {
//            mPlayer.seekTo((int) time);
//        } catch (IllegalStateException e) {
//            MPLogUtil.log(e.getMessage(), e);
//        }
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getPosition() {
        if (null != mPlayer) {
            return (long) mPlayer.getPosition();
        } else {
            return 0L;
        }
    }

    /**
     * 获取视频总时长
     */
    @Override
    public long getDuration() {
        if (null != mPlayer) {
            return mPlayer.getLength();
        } else {
            return 0L;
        }
    }

    @Override
    public void setSurface(@NonNull Surface sf) {
        MPLogUtil.log("VideoVlcPlayer => setSurface => sf = " + sf + ", mPlayer = " + mPlayer);
        if (null != sf && null != mPlayer) {
            mPlayer.setSurface(sf);
        }
    }

    /**
     * 获取播放速度
     *
     * @return 播放速度
     */
    @Override
    public float getSpeed() {
        if (null != mPlayer) {
            return mPlayer.getRate();
        } else {
            return 1F;
        }
    }

    /**
     * 设置播放速度
     *
     * @param speed 速度
     */
    @Override
    public void setSpeed(float speed) {
        if (null != mPlayer) {
            mPlayer.setRate(speed);
        }
    }

    /****************/

    @Override
    public void setVolume(float v1, float v2) {
        if (null != mPlayer) {
            boolean videoMute = isMute();
            if (videoMute) {
                mPlayer.setVolume(0F, 0F);
            } else {
                float value = Math.max(v1, v2);
                if (value > 1f) {
                    value = 1f;
                }
                mPlayer.setVolume(value, value);
            }
        }
    }

    @Override
    public boolean isExternalMusicPlayWhenReady() {
        return mExternalMusicPlayWhenReady;
    }

    @Override
    public void setisExternalMusicPlayWhenReady(boolean v) {
        this.mExternalMusicPlayWhenReady = v;
    }

    @Override
    public boolean isExternalMusicLooping() {
        return mExternalMusicLoop;
    }

    @Override
    public void setExternalMusicLooping(boolean v) {
        mExternalMusicLoop = v;
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
        setReadying(false);
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
    public boolean isReadying() {
        return mReadying;
    }

    @Override
    public void setReadying(boolean v) {
        mReadying = v;
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
    public boolean isExternalMusicEqualLength() {
        return mExternalMusicEqualLength;
    }

    @Override
    public void setExternalMusicEqualLength(boolean equal) {
        mExternalMusicEqualLength = equal;
    }

    @Override
    public void setExternalMusicPath(@NonNull String musicPath) {
        this.mExternalMusicPath = musicPath;
    }

    @Override
    public String getExternalMusicPath() {
        return this.mExternalMusicPath;
    }
}
