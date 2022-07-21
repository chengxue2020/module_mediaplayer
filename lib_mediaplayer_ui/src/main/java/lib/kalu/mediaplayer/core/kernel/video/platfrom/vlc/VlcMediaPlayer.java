package lib.kalu.mediaplayer.core.kernel.video.platfrom.vlc;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.interfaces.IVLCVout;

import java.util.ArrayList;
import java.util.Map;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.core.KernelCore;
import lib.kalu.mediaplayer.core.kernel.video.platfrom.PlatfromPlayer;
import lib.kalu.mediaplayer.util.MediaLogUtil;

@Keep
public class VlcMediaPlayer extends KernelCore implements PlatfromPlayer {

    private long mSeek;
    private LibVLC mLibVLC;
    protected MediaPlayer mMediaPlayer;

    public VlcMediaPlayer() {
    }

    @NonNull
    @Override
    public VlcMediaPlayer getPlayer() {
        return this;
    }

    @Override
    public void initKernel(@NonNull Context context) {
        if (null != mMediaPlayer)
            return;

//        ArrayList args = new ArrayList<>();//VLC参数
//        args.add("--rtsp-tcp");//强制rtsp-tcp，加快加载视频速度
//        args.add("--aout=opensles");
//        args.add("--audio-time-stretch");
//        args.add("-vvv");
        mLibVLC = new LibVLC(context);
        mMediaPlayer = new MediaPlayer(mLibVLC);
        setOptions();
        initListener();
    }

    @Override
    public void resetKernel() {
        if (null == mMediaPlayer)
            return;
//        mMediaPlayer.setLoop(false);
        mMediaPlayer.stop();
//        mMediaPlayer.reset();
//        mMediaPlayer.setSurface(null);
//        mMediaPlayer.setDisplay(null);
        mMediaPlayer.setVolume(1);
    }

    @Override
    public void releaseKernel() {
        if (null == mMediaPlayer)
            return;
//        mMediaPlayer.setOnErrorListener(null);
//        mMediaPlayer.setOnCompletionListener(null);
//        mMediaPlayer.setOnInfoListener(null);
//        mMediaPlayer.setOnBufferingUpdateListener(null);
//        mMediaPlayer.setOnPreparedListener(null);
//        mMediaPlayer.setOnVideoSizeChangedListener(null);
        mLibVLC.release();
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
//        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mMediaPlayer.setOnErrorListener(onErrorListener);
//        mMediaPlayer.setOnCompletionListener(onCompletionListener);
//        mMediaPlayer.setOnInfoListener(onInfoListener);
//        mMediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
//        mMediaPlayer.setOnPreparedListener(onPreparedListener);
//        mMediaPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
        mMediaPlayer.setEventListener(new MediaPlayer.EventListener() {
            @Override
            public void onEvent(MediaPlayer.Event event) {
                MediaLogUtil.log("VLCCCC => event = " + event.type);
                // 首帧画面
                if (event.type == MediaPlayer.Event.Vout) {
                    long length = mMediaPlayer.getLength();
                    getVideoPlayerChangeListener().onInfo(PlayerType.KernelType.VLC, PlayerType.MediaType.MEDIA_INFO_VIDEO_RENDERING_START, event.type, mSeek, length);
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
//            mMediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
        } catch (Exception e) {
            getVideoPlayerChangeListener().onError(PlayerType.ErrorType.ERROR_UNEXPECTED, e.getMessage());
        }
    }

    /**
     * 播放
     */
    @Override
    public void start() {
        try {
            mMediaPlayer.play();
            mMediaPlayer.setScale(0);//这行必须加，为了让视图填满布局
            mMediaPlayer.setVideoScale(MediaPlayer.ScaleType.SURFACE_ORIGINAL);//这行必须加，为了让视图填满布局
        } catch (IllegalStateException e) {
            getVideoPlayerChangeListener().onError(PlayerType.ErrorType.ERROR_UNEXPECTED, e.getMessage());
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
            getVideoPlayerChangeListener().onError(PlayerType.ErrorType.ERROR_UNEXPECTED, e.getMessage());
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
            getVideoPlayerChangeListener().onError(PlayerType.ErrorType.ERROR_UNEXPECTED, e.getMessage());
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
            mMediaPlayer.setPosition((int) time);
        } catch (IllegalStateException e) {
            getVideoPlayerChangeListener().onError(PlayerType.ErrorType.ERROR_UNEXPECTED, e.getMessage());
        }
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getPosition() {
        return (long) mMediaPlayer.getPosition();
    }

    /**
     * 获取视频总时长
     */
    @Override
    public long getDuration() {
        return mMediaPlayer.getLength();
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
    public void prepare(@NonNull Context context, @NonNull long seek, @NonNull CharSequence url, @Nullable Map<String, String> headers) {

        //222222222222
        // 设置dataSource
        if (url == null || url.length() == 0) {
            if (getVideoPlayerChangeListener() != null) {
                getVideoPlayerChangeListener().onInfo(PlayerType.KernelType.VLC, PlayerType.MediaType.MEDIA_INFO_URL_NULL, 0, getPosition(), getDuration());
            }
            return;
        }
        try {
            Uri uri = Uri.parse(url.toString());
            Media media = new Media(mLibVLC, uri);
            int cache = 10;
            media.addOption(":network-caching=" + cache);
            media.addOption(":file-caching=" + cache);
            media.addOption(":live-cacheing=" + cache);
            media.addOption(":sout-mux-caching=" + cache);
            media.addOption(":codec=mediacodec,iomx,all");
            mMediaPlayer.setMedia(media);//
            media.setHWDecoderEnabled(false, false);//设置后才可以录制和截屏,这行必须放在mMediaPlayer.setMedia(media)后面，因为setMedia会设置setHWDecoderEnabled为true
            media.parseAsync();

        } catch (Exception e) {
            getVideoPlayerChangeListener().onError(PlayerType.ErrorType.ERROR_PARSE, e.getMessage());
        }
        try {
            this.mSeek = seek;
            mMediaPlayer.play();
        } catch (IllegalStateException e) {
            getVideoPlayerChangeListener().onError(PlayerType.ErrorType.ERROR_UNEXPECTED, e.getMessage());
        }
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
                IVLCVout vout = mMediaPlayer.getVLCVout();
                vout.setVideoSurface(surface, null);
                vout.attachViews();
            } catch (Exception e) {
                getVideoPlayerChangeListener().onError(PlayerType.ErrorType.ERROR_UNEXPECTED, e.getMessage());
            }
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
            IVLCVout vout = mMediaPlayer.getVLCVout();
            vout.setVideoSurface(holder.getSurface(), holder);
            vout.attachViews();
        } catch (Exception e) {
            getVideoPlayerChangeListener().onError(PlayerType.ErrorType.ERROR_UNEXPECTED, e.getMessage());
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
            mMediaPlayer.setVolume((int) Math.min(v1, v2));
        } catch (Exception e) {
            getVideoPlayerChangeListener().onError(PlayerType.ErrorType.ERROR_UNEXPECTED, e.getMessage());
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
//            mMediaPlayer.setLooping(isLooping);
        } catch (Exception e) {
            getVideoPlayerChangeListener().onError(PlayerType.ErrorType.ERROR_UNEXPECTED, e.getMessage());
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
                mMediaPlayer.setRate(speed);
            } catch (Exception e) {
                getVideoPlayerChangeListener().onError(PlayerType.ErrorType.ERROR_UNEXPECTED, e.getMessage());
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
                return mMediaPlayer.getRate();
            } catch (Exception e) {
                getVideoPlayerChangeListener().onError(PlayerType.ErrorType.ERROR_UNEXPECTED, e.getMessage());
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
}
