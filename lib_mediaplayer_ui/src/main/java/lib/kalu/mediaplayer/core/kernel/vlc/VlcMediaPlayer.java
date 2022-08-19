package lib.kalu.mediaplayer.core.kernel.vlc;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.interfaces.IVLCVout;

import java.util.Map;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.KernelApi;
import lib.kalu.mediaplayer.core.kernel.KernelEvent;
import lib.kalu.mediaplayer.util.MediaLogUtil;

@Keep
public final class VlcMediaPlayer implements KernelApi, KernelEvent {

    private long mSeek = 0L; // 快进
    private long mMax = 0L; // 试播时常
    private boolean mAutoRelease = false;
    private boolean mLoop = false; // 循环播放
    private boolean mMute = false; // 静音
    private String mUrl = null; // 视频串

    private String mMusicPath = null;
    private boolean mMusicPrepare = false;
    private boolean mMusicLoop = false;
    private boolean mMusicSeek = false;
    private android.media.MediaPlayer mMusicPlayer = null; // 配音音频

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
    public void createDecoder(@NonNull Context context) {
        //        ArrayList args = new ArrayList<>();//VLC参数
//        args.add("--rtsp-tcp");//强制rtsp-tcp，加快加载视频速度
//        args.add("--aout=opensles");
//        args.add("--audio-time-stretch");
//        args.add("-vvv");
//        mLibVLC = new LibVLC(context);
        mVlcPlayer = new org.videolan.libvlc.media.MediaPlayer(context);
        mVlcPlayer.setLooping(false);
        setOptions();
        initListener();
    }

    @Override
    public void releaseDecoder() {
        releaseMusic();
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
                MediaLogUtil.log("K_VLC => event = " + event.type);
                // 首帧画面
                if (event.type == MediaPlayer.Event.Vout) {
                    mEvent.onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_INIT_COMPILE);
                    mEvent.onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_VIDEO_START);

                    long seek = getSeek();
                    if (seek > 0) {
                        seekTo(seek);
                    }
                }
                // 解析开始
                else if (event.type == MediaPlayer.Event.MediaChanged) {
                    mEvent.onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_INIT_START);
                }
                // 播放完成
                else if (event.type == MediaPlayer.Event.EndReached) {
                    mEvent.onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_PLAYER_END);
                }
                // 错误
                else if (event.type == MediaPlayer.Event.Stopped) {
                    mEvent.onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_INIT_COMPILE);
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            boolean musicPrepare = isMusicPrepare();
            boolean musicLoop = isMusicLoop();
            String musicPath = getMusicPath();
            if (null != musicPath && musicPath.length() > 0 && musicPrepare && musicLoop) {
                toggleMusicExtra();
            }else {
                toggleMusicDafault(true);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
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
    public void init(@NonNull Context context, @NonNull long seek, @NonNull long max, @NonNull String url) {

        // 设置dataSource
        if (url == null || url.length() == 0) {
            mEvent.onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_INIT_COMPILE);
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
            e.printStackTrace();
        }
    }

    @Override
    public void setSurface(@NonNull Surface surface) {
        if (null != surface && null != mVlcPlayer) {
            try {
                mVlcPlayer.setSurface(surface);
            } catch (Exception e) {
                e.printStackTrace();
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
//                e.printStackTrace();
//            }
//        }
//
//
//    }

    @Override
    public void setOptions() {
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
                e.printStackTrace();
            }
        }
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
                e.printStackTrace();
            }
        }
        return 1f;
    }

    /**
     * 获取当前缓冲的网速
     *
     * @return 获取网络
     */
    @Override
    public long getTcpSpeed() {
        // no support
        return 0;
    }

    /****************/

    @Override
    public void setVolume(float v1, float v2) {
        mMute = (v1 <= 0 || v2 <= 0);
        mVlcPlayer.setVolume(v1, v2);
    }

    @Override
    public boolean isMute() {
        return mMute;
    }

    @Override
    public boolean isMusicPrepare() {
        return mMusicPrepare;
    }

    @Override
    public void setMusicPrepare(boolean prepare) {
        this.mMusicPrepare = prepare;
    }

    @Override
    public boolean isMusicLoop() {
        return mMusicLoop;
    }

    @Override
    public void setMusicLoop(boolean loop) {
        this.mMusicLoop = loop;
    }

    @Override
    public boolean isMusicSeek() {
        return mMusicSeek;
    }

    @Override
    public void setMusicSeek(boolean seek) {
        this.mMusicSeek = seek;
    }

    @Override
    public void setMusicPath(@NonNull String musicPath) {
        this.mMusicPath = musicPath;
    }

    @Override
    public String getMusicPath() {
        return this.mMusicPath;
    }

    @Override
    public android.media.MediaPlayer getMusicPlayer() {
        if (null == mMusicPlayer) {
            mMusicPlayer = new android.media.MediaPlayer();
        }
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

    @Override
    public void setAutoRelease(boolean release) {
        this.mAutoRelease = release;
    }

    @Override
    public boolean isAutoRelease() {
        return this.mAutoRelease;
    }

    /****************/
}
