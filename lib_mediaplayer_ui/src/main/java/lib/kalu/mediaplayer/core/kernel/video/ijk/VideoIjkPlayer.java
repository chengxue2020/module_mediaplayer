package lib.kalu.mediaplayer.core.kernel.video.ijk;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.Surface;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.KernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.base.BasePlayer;
import lib.kalu.mediaplayer.core.player.PlayerApi;
import lib.kalu.mediaplayer.util.MPLogUtil;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;

@Keep
public final class VideoIjkPlayer extends BasePlayer {

    private long mSeek = 0L; // 快进
    private long mMax = 0L; // 试播时常
    private boolean mLoop = false; // 循环播放
    private boolean mLive = false;
    private boolean mMute = false;
    //    private String mUrl = null; // 视频串
    private boolean mReadying = false;

    private tv.danmaku.ijk.media.player.IjkMediaPlayer mIjkPlayer;

    public VideoIjkPlayer(@NonNull PlayerApi musicApi, @NonNull KernelApiEvent eventApi) {
        super(musicApi, eventApi);
        setReadying(false);
    }

    @NonNull
    @Override
    public VideoIjkPlayer getPlayer() {
        return this;
    }

    @Override
    public void createDecoder(@NonNull Context context, @NonNull boolean logger, @NonNull int seekParameters) {
        if (null == mIjkPlayer) {
            mIjkPlayer = new tv.danmaku.ijk.media.player.IjkMediaPlayer();
            mIjkPlayer.setLooping(false);
            setVolume(1F, 1F);
            setOptions();
            initListener();
            // log
            tv.danmaku.ijk.media.player.IjkMediaPlayer.native_setLogger(logger);
            tv.danmaku.ijk.media.player.IjkMediaPlayer.native_setLogLevel(tv.danmaku.ijk.media.player.IjkMediaPlayer.IJK_LOG_INFO);
        }
    }

    @Override
    public void releaseDecoder() {
        setEvent(null);
        stopExternalMusic(true);
        setReadying(false);
        if (null != mIjkPlayer) {
            // 设置视频错误监听器
            mIjkPlayer.setOnErrorListener(null);
            // 设置视频播放完成监听事件
            mIjkPlayer.setOnCompletionListener(null);
            // 设置视频信息监听器
            mIjkPlayer.setOnInfoListener(null);
            // 设置视频缓冲更新监听事件
            mIjkPlayer.setOnBufferingUpdateListener(null);
            // 设置准备视频播放监听事件
            mIjkPlayer.setOnPreparedListener(null);
            // 设置视频大小更改监听器
            mIjkPlayer.setOnVideoSizeChangedListener(null);
            // 设置视频seek完成监听事件
            mIjkPlayer.setOnSeekCompleteListener(null);
            // 设置时间文本监听器
            mIjkPlayer.setOnTimedTextListener(null);
            mIjkPlayer.setOnNativeInvokeListener(null);
            mIjkPlayer.setSurface(null);
        }
        stop();
        if (null != mIjkPlayer) {
            mIjkPlayer.release();
            mIjkPlayer = null;
        }
    }

    @Override
    public void init(@NonNull Context context, @NonNull String url) {

        // 设置dataSource
        if (url == null || url.length() == 0) {
            onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_LOADING_STOP);
            onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_ERROR_URL);
            return;
        }
        try {
            //处理UA问题
            //            if (headers != null) {
            //                String userAgent = headers.get("User-Agent");
            //                if (!TextUtils.isEmpty(userAgent)) {
            //                    mIjkPlayer.setOption(tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_FORMAT, "user_agent", userAgent);
            //                }
            //            }
            Uri uri = Uri.parse(url);
            mIjkPlayer.setDataSource(context, uri, null);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
            onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_ERROR_PARSE);
        }

        try {
            mIjkPlayer.prepareAsync();
        } catch (IllegalStateException e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    @Override
    public void setOptions() {

//        // player
//        try {
//            int player = tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_PLAYER;
//            // 硬解 1：开启 O:关闭
//            mIjkPlayer.setOption(player, "mediacodec", 0);
//            mIjkPlayer.setOption(player, "mediacodec-hevc", 0);
//            mIjkPlayer.setOption(player, "mediacodec-auto-rotate", 0);
//            mIjkPlayer.setOption(player, "mediacodec-handle-resolution-change", 0);
//            mIjkPlayer.setOption(player, "videotoolbox", 0);
//            // soundtouch倍速 1：开启 O:关闭
//            mIjkPlayer.setOption(player, "soundtouch", 0);
//            // 丢帧是在视频帧处理不过来的时候丢弃一些帧达到同步的效果
//            mIjkPlayer.setOption(player, "framedrop", 4); // 4
//            // sdl渲染
//            mIjkPlayer.setOption(player, "overlay-format", tv.danmaku.ijk.media.player.IjkMediaPlayer.SDL_FCC_RV32);
//            // 使用opensles 进行音频的解码播放 1、允许 0、不允许[1音频有稍许延迟]
//            mIjkPlayer.setOption(player, "opensles", 0);
//            // 直播场景时实时推流，可以开启无限制buffer，这样可以尽可能快的读取数据，避免出现网络拥塞恢复后延迟累积的情况。
//            // 是否无限读(如果设置了该属性infbuf为1，则设置max-buffer-size无效)
//            mIjkPlayer.setOption(player, "infbuf", 0);
//            // 视频帧处理不过来的时候丢弃一些帧达到同步的效果
//            mIjkPlayer.setOption(player, "framedrop", 5);
//            // 播放重连次数
//            mIjkPlayer.setOption(player, "reconnect", 1);
//            // 默认最小帧数
//            mIjkPlayer.setOption(player, "min-frames", 2);
//            // 最大缓存时长
//            mIjkPlayer.setOption(player, "max_cached_duration", 3);
//            // 自动旋屏 1显示。0禁止
//            mIjkPlayer.setOption(player, "mediacodec-auto-rotate", 0);
//            // 处理分辨率变化 1显示。0禁止
//            mIjkPlayer.setOption(player, "mediacodec-handle-resolution-change", 0);
//            // 不额外优化（使能非规范兼容优化，默认值0 ）
//            mIjkPlayer.setOption(player, "fast", 1);
//            // 是否开启预缓冲，通常直播项目会开启，达到秒开的效果，不过带来了播放丢帧卡顿的体验
//            mIjkPlayer.setOption(player, "packet-buffering", 0);
//            // 须要准备好后自动播放
//            mIjkPlayer.setOption(player, "start-on-prepared", 1);
//            // 字幕; 1显示。0禁止
//            mIjkPlayer.setOption(player, "subtitle", 0);
//            // 视频, 1黑屏 0原画面
//            mIjkPlayer.setOption(player, "vn", 0);
//            // 音频, 1静音 0原音
//            mIjkPlayer.setOption(player, "an", 0);
//            // 最大缓冲大小,单位kb
//            mIjkPlayer.setOption(player, "max-buffer-size", 20 * 1024 * 1024); // 4KB
//        } catch (Exception e) {
//        }
//
//        // format
//        try {
//            int format = tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_FORMAT;
//            // 不清楚 1、允许 0、不允许
//            mIjkPlayer.setOption(format, "http-detect-range-support", 0);
//            // 探针大小，播放前的探测Size，默认是1M, 改小一点会出画面更快
//            mIjkPlayer.setOption(format, "probesize", 20 * 1024 * 1024);// 2M
//            // 最大帧率 20
//            mIjkPlayer.setOption(format, "max-fps", 0);
//            // 设置播放前的探测时间 1,达到首屏秒开效果
//            mIjkPlayer.setOption(format, "analyzeduration", 100);
//            // 设置播放前的最大探测时间 （100未测试是否是最佳值）
//            mIjkPlayer.setOption(format, "analyzemaxduration", 100);
//            // 清空dns，因为多种协议播放会缓存协议导致播放h264后无法播放h265.
//            mIjkPlayer.setOption(format, "dns_cache_clear", 1);
//            // 若是是rtsp协议，能够优先用tcp(默认是用udp)
//            mIjkPlayer.setOption(format, "rtsp_transport", "tcp");
//            // 每处理一个packet以后刷新io上下文
//            mIjkPlayer.setOption(format, "flush_packets", 1);
//            // 缩短播放的rtmp视频延迟在1s内
//            mIjkPlayer.setOption(format, "fflags", "nobuffer");
//            // 超时时间
//            mIjkPlayer.setOption(format, "timeout", 10 * 1000 * 1000);
//        } catch (Exception e) {
//        }
//
//        // codec
//        try {
//            // IJK_AVDISCARD_NONE    =-16, ///< discard nothing
//            // IJK_AVDISCARD_DEFAULT =  0, ///< 如果包大小为0，责抛弃无效的包
//            // IJK_AVDISCARD_NONREF  =  8, ///< 抛弃非参考帧（I帧）
//            // IJK_AVDISCARD_BIDIR   = 16, ///< 抛弃B帧
//            // IJK_AVDISCARD_NONKEY  = 32, ///< 抛弃除关键帧以外的，比如B，P帧
//            // IJK_AVDISCARD_ALL     = 48, ///< 抛弃所有的帧
//            int codec = tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_CODEC;
//            // 设置是否开启环路过滤: 0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小
//            mIjkPlayer.setOption(codec, "skip_loop_filter", 48L);
//            // 跳过帧
//            mIjkPlayer.setOption(codec, "skip_frame", 0);
//        } catch (Exception e) {
//        }
//
//        // 未知1
//        try {
//            int format = tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_FORMAT;
//            // 根据媒体类型来配置 => bug => resp aac音频无声音
//            mIjkPlayer.setOption(format, "allowed_media_types", "video");
//            // rtsp设置 https://ffmpeg.org/ffmpeg-protocols.html#rtsp
//            mIjkPlayer.setOption(format, "rtsp_flags", "prefer_tcp");
//            mIjkPlayer.setOption(format, "rtsp_transport", "tcp");
//        } catch (Exception e) {
//        }
//
//        // 未知2
//        try {
//            int player = tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_PLAYER;
//            // seek超级慢
//            // 某些视频在SeekTo的时候，会跳回到拖动前的位置，这是因为视频的关键帧的问题，通俗一点就是FFMPEG不兼容，视频压缩过于厉害，seek只支持关键帧，出现这个情况就是原始的视频文件中i 帧比较少
//            mIjkPlayer.setOption(player, "enable-accurate-seek", 0);
//        } catch (Exception e) {
//        }
//
////            // 3、缓冲相关
////            // 解决m3u8文件拖动问题 比如:一个3个多少小时的音频文件，开始播放几秒中，然后拖动到2小时左右的时间，要loading 10分钟
////            mIjkPlayer.setOption(format, "fflags", "fastseek");
////    //        mIjkPlayer.setOption(format, "fflags", "nobuffer");
////
////            // 2、 网络相关
////            // 23、设置播放前的最大探测时间
////            mIjkPlayer.setOption(format, "rtbufsize", 60);
    }

    /**
     * ijk视频播放器监听listener
     */
    private void initListener() {
        // 设置监听，可以查看ijk中的IMediaPlayer源码监听事件
        // 设置视频错误监听器
        mIjkPlayer.setOnErrorListener(onErrorListener);
        // 设置视频播放完成监听事件
        mIjkPlayer.setOnCompletionListener(onCompletionListener);
        // 设置视频信息监听器
        mIjkPlayer.setOnInfoListener(onInfoListener);
        // 设置视频缓冲更新监听事件
        //        mIjkPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
        // 设置准备视频播放监听事件
        mIjkPlayer.setOnPreparedListener(onPreparedListener);
        // 设置视频大小更改监听器
        mIjkPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
        // 设置视频seek完成监听事件
        mIjkPlayer.setOnSeekCompleteListener(onSeekCompleteListener);
        // 设置时间文本监听器
        mIjkPlayer.setOnTimedTextListener(onTimedTextListener);
        mIjkPlayer.setOnNativeInvokeListener(new tv.danmaku.ijk.media.player.IjkMediaPlayer.OnNativeInvokeListener() {
            @Override
            public boolean onNativeInvoke(int i, Bundle bundle) {
                return true;
            }
        });
    }

    //    /**
    //     * 用于播放raw和asset里面的视频文件
    //     */
    //    @Override
    //    public void setDataSource(AssetFileDescriptor fd) {
    //        try {
    //            mIjkPlayer.setDataSource(new IMediaDataSourceForRaw(fd));
    //        } catch (Exception e) {
    //            MPLogUtil.log(e.getMessage(), e);
    //        }
    //    }

    /**
     * 暂停
     */
    @Override
    public void pause() {
        try {
            mIjkPlayer.pause();
        } catch (IllegalStateException e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    /**
     * 播放
     */
    @Override
    public void start() {
        setReadying(false);
        try {
            mIjkPlayer.start();
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
            mIjkPlayer.pause();
            mIjkPlayer.stop();
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
            return mIjkPlayer.isPlaying();
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
            return false;
        }
    }


    /**
     * 调整进度
     */
    @Override
    public void seekTo(long seek, @NonNull boolean seekHelp) {
        MPLogUtil.log("IjkMediaPlayer => seekTo => seek = " + seek);
        setReadying(false);
        try {
            onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_BUFFERING_START);
            mIjkPlayer.seekTo(seek);
        } catch (IllegalStateException e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getPosition() {
        try {
            return (int) mIjkPlayer.getCurrentPosition();
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * 获取视频总时长
     */
    @Override
    public long getDuration() {
        try {
            return (int) mIjkPlayer.getDuration();
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
            return 0L;
        }
    }

    //    /**
    //     * 获取缓冲百分比
    //     */
    //    @Override
    //    public int getBufferedPercentage() {
    //        return mBufferedPercent;
    //    }

    @Override
    public void setSurface(@NonNull Surface surface, int w, int h) {
        if (null != surface && null != mIjkPlayer) {
            try {
                mIjkPlayer.setSurface(surface);
            } catch (Exception e) {
                MPLogUtil.log(e.getMessage(), e);
            }
        }
    }

    @Override
    public void setPlayWhenReady(boolean playWhenReady) {

    }

    /**
     * 设置播放速度
     */
    @Override
    public void setSpeed(float speed) {
        mIjkPlayer.setSpeed(speed);
    }

    /**
     * 获取播放速度
     */
    @Override
    public float getSpeed() {
        return mIjkPlayer.getSpeed(0);
    }

    /****************/

    @Override
    public void setVolume(float v1, float v2) {
        try {
            boolean videoMute = isMute();
            if (videoMute) {
                mIjkPlayer.setVolume(0F, 0F);
            } else {
                float value = Math.max(v1, v2);
                if (value > 1f) {
                    value = 1f;
                }
                mIjkPlayer.setVolume(value, value);
            }
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

    //    @Override
    //    public boolean isHideStop() {
    //        return hideStop;
    //    }
    //
    //    @Override
    //    public void setHideStop(boolean v) {
    //        hideStop = v;
    //    }
    //
    //    @Override
    //    public boolean isHideRelease() {
    //        return hideRelease;
    //    }
    //
    //    @Override
    //    public void setHideRelease(boolean v) {
    //        hideRelease = v;
    //    }

    /****************/

    /**
     * 设置视频错误监听器
     * int MEDIA_INFO_VIDEO_RENDERING_START = 3;//视频准备渲染
     * int MEDIA_INFO_BUFFERING_START = 701;//开始缓冲
     * int MEDIA_INFO_BUFFERING_END = 702;//缓冲结束
     * int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001;//视频选择信息
     * int MEDIA_ERROR_SERVER_DIED = 100;//视频中断，一般是视频源异常或者不支持的视频类型。
     * int MEDIA_ERROR_IJK_PLAYER = -10000,//一般是视频源有问题或者数据格式不支持，比如音频不是AAC之类的
     * int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;//数据错误没有有效的回收
     */
    private IMediaPlayer.OnErrorListener onErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int framework_err, int impl_err) {
            MPLogUtil.log("IjkMediaPlayer => onError => framework_err = " + framework_err + ", impl_err = " + impl_err);
            onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_LOADING_STOP);
            onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_ERROR_PARSE);
            return true;
        }
    };

    /**
     * 设置视频播放完成监听事件
     */
    private IMediaPlayer.OnCompletionListener onCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            MPLogUtil.log("IjkMediaPlayer => onCompletion =>");
            onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_VIDEO_END);
        }
    };


    /**
     * 设置视频信息监听器
     */
    private IMediaPlayer.OnInfoListener onInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
            MPLogUtil.log("IjkMediaPlayer => onInfo => what = " + what + ", extra = " + extra);
            // loading-start
            if (what == IMediaPlayer.MEDIA_INFO_OPEN_INPUT) {
                onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_LOADING_START);
            }
            // 首帧画面
            else if (what == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_LOADING_STOP);
                onEvent(PlayerType.KernelType.IJK, what);
            }
            // 事件通知
            else {
                onEvent(PlayerType.KernelType.IJK, what);
            }
            return true;
        }
    };

    //    /**
    //     * 设置视频缓冲更新监听事件
    //     */
    //    private IMediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
    //        @Override
    //        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
    //            MPLogUtil.log("IjkMediaPlayer => onBufferingUpdate => percent = " + percent);
    //            mBufferedPercent = percent;
    //        }
    //    };


    /**
     * 设置准备视频播放监听事件
     */
    private IMediaPlayer.OnPreparedListener onPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            MPLogUtil.log("IjkMediaPlayer => onPrepared => seek = " + mSeek);
            long seek = getSeek();
            if (seek > 0) {
                seekTo(seek, false);
            }
        }
    };

    /**
     * 设置视频大小更改监听器
     */
    private IMediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int width, int height, int sar_num, int sar_den) {
            MPLogUtil.log("IjkMediaPlayer => onVideoSizeChanged => width = " + width + ", height = " + height);
            int videoWidth = iMediaPlayer.getVideoWidth();
            int videoHeight = iMediaPlayer.getVideoHeight();
            if (videoWidth != 0 && videoHeight != 0) {
                onChanged(PlayerType.KernelType.IJK, videoWidth, videoHeight, -1);
            }
        }
    };

    /**
     * 设置时间文本监听器
     */
    private IMediaPlayer.OnTimedTextListener onTimedTextListener = new IMediaPlayer.OnTimedTextListener() {
        @Override
        public void onTimedText(IMediaPlayer iMediaPlayer, IjkTimedText ijkTimedText) {
            MPLogUtil.log("IjkMediaPlayer => onTimedText => text = " + ijkTimedText.getText());
        }
    };

    /**
     * 设置视频seek完成监听事件
     */
    private IMediaPlayer.OnSeekCompleteListener onSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer iMediaPlayer) {
            MPLogUtil.log("IjkMediaPlayer => onSeekComplete => ");
        }
    };
}
