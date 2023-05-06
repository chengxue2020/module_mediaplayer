package lib.kalu.mediaplayer.core.kernel.video.android;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.KernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.base.BasePlayer;
import lib.kalu.mediaplayer.core.player.PlayerApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

@Keep
public final class VideoAndroidPlayer extends BasePlayer {

    private long mSeek = 0L; // 快进
    private long mMax = 0L; // 试播时常
    private boolean mLoop = false; // 循环播放
    private boolean mLive = false;
    private boolean mMute = false;

    private MediaPlayer mAndroidPlayer;
    private boolean mPlayWhenReady = true;

    public VideoAndroidPlayer(@NonNull PlayerApi musicApi, @NonNull KernelApiEvent eventApi) {
        super(musicApi, eventApi);
    }

    @NonNull
    @Override
    public VideoAndroidPlayer getPlayer() {
        return this;
    }

    @Override
    public void createDecoder(@NonNull Context context, @NonNull boolean logger, @NonNull int seekParameters) {
        if (null == mAndroidPlayer) {
            mAndroidPlayer = new MediaPlayer();
            mAndroidPlayer.setLooping(false);
        }
        setVolume(1F, 1F);
        initListener();
    }

    @Override
    public void releaseDecoder() {
        setEvent(null);
        stopExternalMusic(true);
        if (null != mAndroidPlayer) {
            stop();
            mAndroidPlayer.setOnErrorListener(null);
            mAndroidPlayer.setOnCompletionListener(null);
            mAndroidPlayer.setOnInfoListener(null);
            mAndroidPlayer.setOnBufferingUpdateListener(null);
            mAndroidPlayer.setOnPreparedListener(null);
            mAndroidPlayer.setOnVideoSizeChangedListener(null);
            mAndroidPlayer.setLooping(false);
            mAndroidPlayer.setSurface(null);
            mAndroidPlayer.release();
            mAndroidPlayer = null;
        }
    }

    @Override
    public void init(@NonNull Context context, @NonNull String url) {
        // loading-start
        onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_START);

        // 设置dataSource
        if (url == null || url.length() == 0) {
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_STOP);
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_URL);
            return;
        }
        try {
            Uri uri = Uri.parse(url);
            mAndroidPlayer.setDataSource(context, uri, null);
        } catch (Exception e) {
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_PARSE);
        }
        try {
            mAndroidPlayer.prepareAsync();
        } catch (IllegalStateException e) {
            MPLogUtil.log("VideoAndroidPlayer => " + e.getMessage());
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
//        mAndroidPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
        mAndroidPlayer.setOnPreparedListener(onPreparedListener);
        mAndroidPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
    }

//    /**
//     * 用于播放raw和asset里面的视频文件
//     */
//    @Override
//    public void setDataSource(AssetFileDescriptor fd) {
//        try {
//            mAndroidPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
//        } catch (Exception e) {
//            MPLogUtil.log("VideoAndroidPlayer => " + e.getMessage());
//        }
//    }

    /**
     * 播放
     */
    @Override
    public void start() {
        try {
            mAndroidPlayer.start();
        } catch (IllegalStateException e) {
            MPLogUtil.log("VideoAndroidPlayer => " + e.getMessage());
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
            MPLogUtil.log("VideoAndroidPlayer => " + e.getMessage());
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
            MPLogUtil.log("VideoAndroidPlayer => " + e.getMessage());
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
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mAndroidPlayer.seekTo(time, MediaPlayer.SEEK_CLOSEST);
            } else {
                mAndroidPlayer.seekTo((int) time);
            }
        } catch (IllegalStateException e) {
            MPLogUtil.log("VideoAndroidPlayer => " + e.getMessage());
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

    @Override
    public void setSurface(@NonNull Surface surface, int w, int h) {
        try {
            if (null == surface)
                throw new Exception("surface error: null");
            if (null == mAndroidPlayer)
                throw new Exception("mAndroidPlayer error: null");
            mAndroidPlayer.setSurface(surface);
        } catch (Exception e) {
            MPLogUtil.log("VideoAndroidPlayer => " + e.getMessage());
        }
    }

    @Override
    public void setPlayWhenReady(boolean playWhenReady) {
        this.mPlayWhenReady = playWhenReady;
    }

    @Override
    public float getSpeed() {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                throw new Exception("only support above Android M");
            if (null == mAndroidPlayer)
                throw new Exception("mAndroidPlayer error: null");
            return mAndroidPlayer.getPlaybackParams().getSpeed();
        } catch (Exception e) {
            MPLogUtil.log("getSpeed => " + e.getMessage());
            return 1f;
        }
    }

    @Override
    public void setSpeed(float speed) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                throw new Exception("only support above Android M");
            if (null == mAndroidPlayer)
                throw new Exception("mAndroidPlayer error: null");
            mAndroidPlayer.setPlaybackParams(mAndroidPlayer.getPlaybackParams().setSpeed(speed));
        } catch (Exception e) {
            MPLogUtil.log("VideoAndroidPlayer => " + e.getMessage());
        }
    }

    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            MPLogUtil.log("VideoAndroidPlayer => onError => what = " + what);
            // ignore -38
            if (what == -38) {

            }
            // error
            else {
                onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_STOP);
                onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_PARSE);
            }
            return true;
        }
    };

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            MPLogUtil.log("VideoAndroidPlayer => onCompletion =>");
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_VIDEO_END);
        }
    };

    private MediaPlayer.OnInfoListener onInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            MPLogUtil.log("VideoAndroidPlayer => onInfo => what = " + what);
            // 缓冲开始
            if (what == PlayerType.EventType.EVENT_BUFFERING_START) {
                long position = getPosition();
                long seek = getSeek();
                long duration = getDuration();
                if (duration > 0 && position > seek) {
                    onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_BUFFERING_START);
                }
            }
            // 缓冲结束
            else if (what == PlayerType.EventType.EVENT_BUFFERING_STOP) {
                onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_BUFFERING_STOP);
            }
            // 开始播放
            else if (what == PlayerType.EventType.EVENT_VIDEO_START) {
                onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_STOP);
                onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_VIDEO_START);
                if (!mPlayWhenReady) {
                    pause();
                }
            }
            return true;
        }
    };

    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            long seek = getSeek();
            MPLogUtil.log("VideoAndroidPlayer => onPrepared => seek = " + seek);
            if (seek > 0) {
                seekTo(seek, false);
            }
            start();
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
            if (null == mAndroidPlayer)
                throw new Exception("mAndroidPlayer error: null");
            float value;
            if (isMute()) {
                value = 0F;
            } else {
                value = Math.max(v1, v2);
            }
            if (value > 1f) {
                value = 1f;
            }
            mAndroidPlayer.setVolume(value, value);
        } catch (Exception e) {
            MPLogUtil.log("VideoAndroidPlayer => " + e.getMessage());
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
}
