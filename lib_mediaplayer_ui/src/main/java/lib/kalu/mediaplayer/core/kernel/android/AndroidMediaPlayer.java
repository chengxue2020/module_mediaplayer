package lib.kalu.mediaplayer.core.kernel.android;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

import lib.kalu.mediaplayer.core.kernel.KernelApi;
import lib.kalu.mediaplayer.core.kernel.KernelEvent;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.util.MediaLogUtil;

@Keep
public final class AndroidMediaPlayer implements KernelApi {

    private KernelEvent mEvent;
    protected MediaPlayer mMediaPlayer;

    private long mSeek;
    private String mUrl;
    private int mBufferedPercent;

    public AndroidMediaPlayer(@NonNull KernelEvent event) {
        this.mEvent = event;
    }

    @NonNull
    @Override
    public AndroidMediaPlayer getPlayer() {
        return this;
    }

    @Override
    public void initKernel(@NonNull Context context) {
        if (null != mMediaPlayer)
            return;
        mMediaPlayer = new MediaPlayer();
        setOptions();
        initListener();
    }

    @Override
    public void resetKernel() {
        if (null == mMediaPlayer)
            return;
        mMediaPlayer.setLooping(false);
        mMediaPlayer.stop();
        mMediaPlayer.reset();
//        mMediaPlayer.setSurface(null);
//        mMediaPlayer.setDisplay(null);
        mMediaPlayer.setVolume(1, 1);
    }

    @Override
    public void releaseKernel() {
        if (null == mMediaPlayer)
            return;
        mMediaPlayer.setOnErrorListener(null);
        mMediaPlayer.setOnCompletionListener(null);
        mMediaPlayer.setOnInfoListener(null);
        mMediaPlayer.setOnBufferingUpdateListener(null);
        mMediaPlayer.setOnPreparedListener(null);
        mMediaPlayer.setOnVideoSizeChangedListener(null);
        mMediaPlayer.release();
//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    mMediaPlayer.release();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
    }

    /**
     * MediaPlayer视频播放器监听listener
     */
    private void initListener() {
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnErrorListener(onErrorListener);
        mMediaPlayer.setOnCompletionListener(onCompletionListener);
        mMediaPlayer.setOnInfoListener(onInfoListener);
        mMediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
        mMediaPlayer.setOnPreparedListener(onPreparedListener);
        mMediaPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
    }

    /**
     * 用于播放raw和asset里面的视频文件
     */
    @Override
    public void setDataSource(AssetFileDescriptor fd) {
        try {
            mMediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
        } catch (Exception e) {
            mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_UNEXPECTED);
        }
    }

    /**
     * 播放
     */
    @Override
    public void start() {
        try {
            mMediaPlayer.start();
        } catch (IllegalStateException e) {
            mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_UNEXPECTED);
        }
    }

    /**
     * 暂停
     */
    @Override
    public void pause() {
        try {
            mMediaPlayer.pause();
        } catch (IllegalStateException e) {
            mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_UNEXPECTED);
        }
    }

    /**
     * 停止
     */
    @Override
    public void stop() {
        try {
            mMediaPlayer.stop();
        } catch (IllegalStateException e) {
            mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_UNEXPECTED);
        }
    }

    /**
     * 是否正在播放
     */
    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    /**
     * 调整进度
     */
    @Override
    public void seekTo(long time) {
        try {
            mMediaPlayer.seekTo((int) time);
        } catch (IllegalStateException e) {
            mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_UNEXPECTED);
        }
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    /**
     * 获取视频总时长
     */
    @Override
    public long getDuration() {
        return mMediaPlayer.getDuration();
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

    /**
     * 设置渲染视频的View,主要用于TextureView
     *
     * @param surface surface
     */
    @Override
    public void setSurface(Surface surface) {
        if (surface != null) {
            try {
                mMediaPlayer.setSurface(surface);
            } catch (Exception e) {
                mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_UNEXPECTED);
            }
        }
    }

    @Override
    public void init(@NonNull Context context, @NonNull long seek, @NonNull String url, @Nullable Map<String, String> headers) {
        this.mSeek = seek;
        this.mUrl = url;

        // loading-start
        mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_INIT_START);

        //222222222222
        // 设置dataSource
        if (url == null || url.length() == 0) {
            mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_INIT_COMPILE);
            mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_URL);
            return;
        }
        try {
            Uri uri = Uri.parse(url.toString());
            mMediaPlayer.setDataSource(context, uri, headers);
        } catch (Exception e) {
            mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_PARSE);
        }
        try {
            mMediaPlayer.prepareAsync();
        } catch (IllegalStateException e) {
            mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_UNEXPECTED);
        }
    }

    /**
     * 设置渲染视频的View,主要用于SurfaceView
     *
     * @param holder holder
     */
    @Override
    public void setDisplay(SurfaceHolder holder) {
        try {
            mMediaPlayer.setDisplay(holder);
        } catch (Exception e) {
            mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_UNEXPECTED);
        }
    }

    /**
     * 设置音量
     *
     * @param v1 v1
     * @param v2 v2
     */
    @Override
    public void setVolume(float v1, float v2) {
        try {
            mMediaPlayer.setVolume(v1, v2);
        } catch (Exception e) {
            mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_UNEXPECTED);
        }
    }

    /**
     * 设置是否循环播放
     *
     * @param isLooping 布尔值
     */
    @Override
    public void setLooping(boolean isLooping) {
        try {
            mMediaPlayer.setLooping(isLooping);
        } catch (Exception e) {
            mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_UNEXPECTED);
        }
    }

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
                mMediaPlayer.setPlaybackParams(mMediaPlayer.getPlaybackParams().setSpeed(speed));
            } catch (Exception e) {
                mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_UNEXPECTED);
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
                return mMediaPlayer.getPlaybackParams().getSpeed();
            } catch (Exception e) {
                mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_UNEXPECTED);
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

    @Override
    public String getUrl() {
        return mUrl;
    }

    @Override
    public long getSeek() {
        return mSeek;
    }

    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            MediaLogUtil.log("K_ANDROID => onError => what = " + what);
            // ignore -38
            if (what == -38) {

            }
            // ignore 1
            else if (what == 1) {
                resetKernel();
                mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_INIT_COMPILE);
                mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_PARSE);
            }
            // next
            else {
                resetKernel();
                mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_INIT_COMPILE);
                mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_UNEXPECTED);
            }
            return true;
        }
    };

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            MediaLogUtil.log("K_ANDROID => onCompletion => ");
            mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_PLAYER_END);
        }
    };

    private MediaPlayer.OnInfoListener onInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            MediaLogUtil.log("K_ANDROID => onInfo => what = " + what);
            //解决MEDIA_INFO_VIDEO_RENDERING_START多次回调问题
//            MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START
            if (what == PlayerType.EventType.EVENT_VIDEO_RENDERING_START) {
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
            MediaLogUtil.log("K_ANDROID => onPrepared => ");

            mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_INIT_COMPILE);
//            int position = mp.getCurrentPosition();
//            long duration = getDuration();
//            getVideoPlayerChangeListener().onPrepared(mSeek, duration);

            start();
            if (mSeek > 0) {
                seekTo(mSeek);
                mSeek = 0;
            }

            mEvent.onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_VIDEO_RENDERING_START);
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
}
