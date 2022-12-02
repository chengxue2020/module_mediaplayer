package lib.kalu.mediaplayer.core.kernel.vlc;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.Build;
import android.view.Surface;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import org.videolan.libvlc.MediaPlayer;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.KernelApi;
import lib.kalu.mediaplayer.core.kernel.KernelEvent;
import lib.kalu.mediaplayer.util.MPLogUtil;
import lib.kalu.mediaplayer.util.SpeedUtil;

@Keep
public final class VlcMediaPlayer implements KernelApi, KernelEvent {

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

    //    private LibVLC mLibVLC;
    private KernelEvent mEvent;
    private org.videolan.libvlc.media.MediaPlayer mVlcPlayer;

    public VlcMediaPlayer(@NonNull KernelEvent event) {
        this.mEvent = event;
    }

    @NonNull
    @Override
    public VlcMediaPlayer getPlayer() {
        return this;
    }


    @Override
    public void createDecoder(@NonNull Context context, @NonNull boolean mute, @NonNull boolean logger) {
        //        ArrayList args = new ArrayList<>();//VLC参数
//        args.add("--rtsp-tcp");//强制rtsp-tcp，加快加载视频速度
//        args.add("--aout=opensles");
//        args.add("--audio-time-stretch");
//        args.add("-vvv");
//        mLibVLC = new LibVLC(context);
        mVlcPlayer = new org.videolan.libvlc.media.MediaPlayer(context);
        mVlcPlayer.setLooping(false);
        if (mute) {
            mVlcPlayer.setVolume(0f, 0f);
        }
        setOptions();
        initListener();
    }

    @Override
    public void releaseDecoder() {
        releaseExternalMusic();
        if (null != mVlcPlayer) {
            mVlcPlayer.release();
            mVlcPlayer = null;
        }
    }

    /**
     * MediaPlayer视频播放器监听listener
     */
    private void initListener() {
//        mVlcPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mVlcPlayer.setOnErrorListener(onErrorListener);
//        mVlcPlayer.setOnCompletionListener(onCompletionListener);
//        mVlcPlayer.setOnInfoListener(onInfoListener);
//        mVlcPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
//        mVlcPlayer.setOnPreparedListener(onPreparedListener);
//        mVlcPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
        mVlcPlayer.getVLC().setEventListener(new MediaPlayer.EventListener() {
            @Override
            public void onEvent(MediaPlayer.Event event) {
                MPLogUtil.log("K_VLC => event = " + event.type);
                // 首帧画面
                if (event.type == MediaPlayer.Event.Vout) {
                    mEvent.onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_LOADING_STOP);
                    mEvent.onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_VIDEO_START);

                    long seek = getSeek();
                    if (seek > 0) {
                        seekTo(seek);
                    }
                }
                // 解析开始
                else if (event.type == MediaPlayer.Event.MediaChanged) {
                    mEvent.onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_LOADING_START);
                }
                // 播放完成
                else if (event.type == MediaPlayer.Event.EndReached) {
                    mEvent.onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_VIDEO_END);
                }
                // 错误
                else if (event.type == MediaPlayer.Event.Stopped) {
                    mEvent.onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_LOADING_STOP);
                    mEvent.onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_ERROR_PARSE);
                }
            }
        });
    }

    /**
     * 用于播放raw和asset里面的视频文件
     */
    @Override
    public void setDataSource(AssetFileDescriptor fd) {
        try {
            mVlcPlayer.setDataSource(fd.getFileDescriptor());
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
            mVlcPlayer.start();
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
            mVlcPlayer.pause();
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
            mVlcPlayer.stop();
        } catch (IllegalStateException e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    /**
     * 是否正在播放
     */
    @Override
    public boolean isPlaying() {
        try {
            return mVlcPlayer.isPlaying();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 调整进度
     */
    @Override
    public void seekTo(long seek) {
        try {
            mVlcPlayer.seekTo(seek);
        } catch (IllegalStateException e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getPosition() {
        return mVlcPlayer.getCurrentPosition();
    }

    /**
     * 获取视频总时长
     */
    @Override
    public long getDuration() {
        return mVlcPlayer.getDuration();
    }

    /**
     * 获取缓冲百分比
     *
     * @return 获取缓冲百分比
     */
    @Override
    public int getBufferedPercentage() {
//        return mBufferedPercent;
        return 0;
    }

    @Override
    public void init(@NonNull Context context, @NonNull String url) {

        // 设置dataSource
        if (url == null || url.length() == 0) {
            mEvent.onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_LOADING_STOP);
            mEvent.onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_ERROR_URL);
            return;
        }
        try {
            mVlcPlayer.setDataSource(url);//

        } catch (Exception e) {
            mEvent.onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_ERROR_PARSE);
        }
        try {
            start();
        } catch (IllegalStateException e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    @Override
    public void setSurface(@NonNull Surface surface) {
        if (null != surface && null != mVlcPlayer) {
            try {
                mVlcPlayer.setSurface(surface);
            } catch (Exception e) {
                MPLogUtil.log(e.getMessage(), e);
            }
        }
    }

//    @Override
//    public void setReal(@NonNull Surface surface, @NonNull SurfaceHolder holder) {
//
//        // 设置渲染视频的View,主要用于SurfaceView
//        if (null != holder && null != mVlcPlayer) {
//            try {
//                mVlcPlayer.setDisplay(holder);
//            } catch (Exception e) {
//                MediaLogUtil.log(e.getMessage(), e);
//            }
//        }
//
//
//    }

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
                return mVlcPlayer.getSpeed();
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
                mVlcPlayer.setSpeed(speed);
            } catch (Exception e) {
                MPLogUtil.log(e.getMessage(), e);
            }
        }
    }

    /**
     * 获取当前缓冲的网速
     *
     * @return 获取网络
     */
    @Override
    public String getTcpSpeed(Context context) {
        return SpeedUtil.getNetSpeed(context);
    }

    /****************/

    @Override
    public void setVolume(float v1, float v2) {
        try {
            float value = Math.max(v1, v2);
            if (value > 1f) {
                value = 1f;
            }
            mVlcPlayer.getVLC().setVolume((int) value);
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
}
