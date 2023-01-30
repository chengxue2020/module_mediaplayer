package lib.kalu.mediaplayer.core.video.kernel.android;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.video.kernel.KernelApi;
import lib.kalu.mediaplayer.core.video.kernel.KernelEvent;
import lib.kalu.mediaplayer.util.MPLogUtil;

@Keep
public final class AndroidMediaPlayer implements KernelApi {

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
    private boolean mExternalMusicPrepared = false;
    private boolean mExternalMusicLoop = false;
    private boolean mExternalMusicAuto = false;

    private KernelEvent mEvent;
    private MediaPlayer mAndroidPlayer;

    private int mBufferedPercent;

    public AndroidMediaPlayer(@NonNull KernelEvent event) {
        setReadying(false);
        this.mEvent = event;
    }

    @NonNull
    @Override
    public AndroidMediaPlayer getPlayer() {
        return this;
    }

    @Override
    public void onUpdateTimeMillis() {
        if (null != mEvent) {
            long position = getPosition();
            long duration = getDuration();
            if(position > 0 && duration > 0){
                long seek = getSeek();
                long max = getMax();
                boolean looping = isLooping();
                mEvent.onUpdateTimeMillis(looping, max, seek, position, duration);
            }
        }
    }

    @Override
    public void createDecoder(@NonNull Context context, @NonNull boolean mute, @NonNull boolean logger, @NonNull int seekParameters) {
        releaseDecoder();
        mAndroidPlayer = new MediaPlayer();
        mAndroidPlayer.setLooping(false);
        if (mute) {
            mAndroidPlayer.setVolume(0f, 0f);
        }
        setOptions();
        initListener();
    }

    @Override
    public void releaseDecoder() {
        releaseExternalMusic();
        setReadying(false);
        if (null != mEvent) {
            mEvent = null;
        }
        if (null != mAndroidPlayer) {
            mAndroidPlayer.setOnErrorListener(null);
            mAndroidPlayer.setOnCompletionListener(null);
            mAndroidPlayer.setOnInfoListener(null);
            mAndroidPlayer.setOnBufferingUpdateListener(null);
            mAndroidPlayer.setOnPreparedListener(null);
            mAndroidPlayer.setOnVideoSizeChangedListener(null);
            mAndroidPlayer.setLooping(false);
            mAndroidPlayer.setSurface(null);
        }
        stop();
        if (null != mAndroidPlayer) {
            mAndroidPlayer.release();
            mAndroidPlayer = null;
        }

//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    mAndroidPlayer.release();
//                } catch (Exception e) {
//                    MediaLogUtil.log(e.getMessage(), e);
//                }
//            }
//        }.start();
    }

    @Override
    public void init(@NonNull Context context, @NonNull String url) {
        // loading-start
        mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_START);

        // 设置dataSource
        if (url == null || url.length() == 0) {
            mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_STOP);
            mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_URL);
            return;
        }
        try {
            Uri uri = Uri.parse(url);
            mAndroidPlayer.setDataSource(context, uri, null);
        } catch (Exception e) {
            mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_PARSE);
        }
        try {
            mAndroidPlayer.prepareAsync();
        } catch (IllegalStateException e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    /**
     * MediaPlayer视频播放器监听listener
     */
    private void initListener() {
        mAndroidPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mAndroidPlayer.setOnErrorListener(onErrorListener);
        mAndroidPlayer.setOnCompletionListener(onCompletionListener);
        mAndroidPlayer.setOnInfoListener(onInfoListener);
        mAndroidPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
        mAndroidPlayer.setOnPreparedListener(onPreparedListener);
        mAndroidPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
    }

    /**
     * 用于播放raw和asset里面的视频文件
     */
    @Override
    public void setDataSource(AssetFileDescriptor fd) {
        try {
            mAndroidPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    /**
     * 播放
     */
    @Override
    public void start() {
        try {
            mAndroidPlayer.start();
        } catch (IllegalStateException e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    /**
     * 暂停
     */
    @Override
    public void pause() {
        try {
            mAndroidPlayer.pause();
        } catch (IllegalStateException e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    /**
     * 停止
     */
    @Override
    public void stop() {
        try {
            mAndroidPlayer.pause();
            mAndroidPlayer.stop();
        } catch (IllegalStateException e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    /**
     * 是否正在播放
     */
    @Override
    public boolean isPlaying() {
        return mAndroidPlayer.isPlaying();
    }

    /**
     * 调整进度
     */
    @Override
    public void seekTo(long time, @NonNull boolean seekHelp) {
        setReadying(false);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mAndroidPlayer.seekTo(time, MediaPlayer.SEEK_CLOSEST);
            } else {
                mAndroidPlayer.seekTo((int) time);
            }
        } catch (IllegalStateException e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getPosition() {
        return mAndroidPlayer.getCurrentPosition();
    }

    /**
     * 获取视频总时长
     */
    @Override
    public long getDuration() {
        return mAndroidPlayer.getDuration();
    }

    /**
     * 获取缓冲百分比
     *
     * @return 获取缓冲百分比
     */
    @Override
    public int getBufferedPercentage() {
        return mBufferedPercent;
    }

    @Override
    public void setSurface(@NonNull Surface surface) {
        if (null != surface && null != mAndroidPlayer) {
            try {
                mAndroidPlayer.setSurface(surface);
            } catch (Exception e) {
                MPLogUtil.log(e.getMessage(), e);
            }
        }
    }

    @Override
    public void setOptions() {
    }

    /**
     * 获取播放速度
     *
     * @return 播放速度
     */
    @Override
    public float getSpeed() {
        // only support above Android M
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                return mAndroidPlayer.getPlaybackParams().getSpeed();
            } catch (Exception e) {
                MPLogUtil.log(e.getMessage(), e);
            }
        }
        return 1f;
    }

    /**
     * 设置播放速度
     *
     * @param speed 速度
     */
    @Override
    public void setSpeed(float speed) {
        // only support above Android M
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                mAndroidPlayer.setPlaybackParams(mAndroidPlayer.getPlaybackParams().setSpeed(speed));
            } catch (Exception e) {
                MPLogUtil.log(e.getMessage(), e);
            }
        }
    }

    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            MPLogUtil.log("K_ANDROID => onError => what = " + what);
            // ignore -38
            if (what == -38) {

            }
            // ignore 1
            else if (what == 1) {
//                resetKernel();
                mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_START);
                mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_PARSE);
            }
            // next
            else {
//                resetKernel();
                mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_STOP);
            }
            return true;
        }
    };

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_VIDEO_END);
        }
    };

    private MediaPlayer.OnInfoListener onInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            MPLogUtil.log("K_ANDROID => onInfo => what = " + what);
            //解决MEDIA_INFO_VIDEO_RENDERING_START多次回调问题
//            MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START
            if (what == PlayerType.EventType.EVENT_VIDEO_START) {
//                if (mIsPreparing) {
//                    mIsPreparing = false;
//                }
            } else {
                mEvent.onEvent(PlayerType.KernelType.ANDROID, what);
            }
            return true;
        }
    };

    private MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            mBufferedPercent = percent;
        }
    };


    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            MPLogUtil.log("K_ANDROID => onPrepared => ");

            mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_STOP);
//            int position = mp.getCurrentPosition();
//            long duration = getDuration();
//            getVideoPlayerChangeListener().onPrepared(mSeek, duration);

            start();
            long seek = getSeek();
            if (seek > 0) {
                seekTo(seek, false);
            }

            mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_VIDEO_START);
        }
    };

    private MediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            int videoWidth = mp.getVideoWidth();
            int videoHeight = mp.getVideoHeight();
            if (videoWidth != 0 && videoHeight != 0) {
                onChanged(PlayerType.KernelType.ANDROID, videoWidth, videoHeight, -1);
            }
        }
    };

    /****************/

    @Override
    public void setVolume(float v1, float v2) {
        try {
            float value = Math.max(v1, v2);
            if (value > 1f) {
                value = 1f;
            }
            mAndroidPlayer.setVolume(value, value);
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

//    @Override
//    public boolean isExternalMusicPrepared() {
//        return mExternalMusicPrepared;
//    }
//
//    @Override
//    public void setExternalMusicPrepared(boolean v) {
//        this.mExternalMusicPrepared = v;
//    }

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
}
