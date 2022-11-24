package lib.kalu.mediaplayer.core.kernel.ijk;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Surface;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.KernelApi;
import lib.kalu.mediaplayer.core.kernel.KernelEvent;
import lib.kalu.mediaplayer.util.MPLogUtil;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;

@Keep
public final class IjkMediaPlayer implements KernelApi, KernelEvent {

    private int mBufferedPercent;

    private long mSeek = 0L; // 快进
    private long mMax = 0L; // 试播时常
    private boolean mLoop = false; // 循环播放
    private boolean mLive = false;
    private boolean mMute = false;
    private String mUrl = null; // 视频串

    private boolean mInvisibleStop = false; // 不可见静音
    private boolean mInvisibleIgnore = false; // 不可见忽略, 什么也不做
    private boolean mInvisibleRelease = true; // 不可见生命周期自动销毁

    private String mExternalMusicPath = null;
    private boolean mExternalMusicPrepared = false;
    private boolean mExternalMusicLoop = false;
    private boolean mExternalMusicAuto = false;

    private KernelEvent mEvent;
    private tv.danmaku.ijk.media.player.IjkMediaPlayer mIjkPlayer;

    public IjkMediaPlayer(@NonNull KernelEvent event) {
        this.mEvent = event;
    }

    @NonNull
    @Override
    public IjkMediaPlayer getPlayer() {
        return this;
    }

//    @Override
//    public void createKernel(@NonNull Context context) {
//        // not null
//        if (null != mIjkPlayer) {
//            resetKernel();
//        }
//    }

    @Override
    public void createDecoder(@NonNull Context context, @NonNull boolean mute, @NonNull boolean logger) {
        if (null == mIjkPlayer) {
            mIjkPlayer = new tv.danmaku.ijk.media.player.IjkMediaPlayer();
            mIjkPlayer.setLooping(false);
            if (mute) {
                mIjkPlayer.setVolume(0f, 0f);
            }
            tv.danmaku.ijk.media.player.IjkMediaPlayer.native_setLogger(logger);
            tv.danmaku.ijk.media.player.IjkMediaPlayer.native_setLogLevel(tv.danmaku.ijk.media.player.IjkMediaPlayer.IJK_LOG_INFO);
            setOptions();
            initListener();
        }
    }

    @Override
    public void releaseDecoder() {
        releaseExternalMusic();
        if (null != mIjkPlayer) {
            mIjkPlayer.stop();
            mIjkPlayer.release();
            mIjkPlayer = null;
        }
    }

    @Override
    public void init(@NonNull Context context, @NonNull String url) {

        // 设置dataSource
        if (url == null || url.length() == 0) {
            mEvent.onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_LOADING_STOP);
            mEvent.onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_ERROR_URL);
            return;
        }
        try {
            //解析path
            Uri uri = Uri.parse(url);
            if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(uri.getScheme())) {
                RawDataSourceProvider rawDataSourceProvider = RawDataSourceProvider.create(context, uri);
                mIjkPlayer.setDataSource(rawDataSourceProvider);
            } else {
                //处理UA问题
//                if (headers != null) {
//                    String userAgent = headers.get("User-Agent");
//                    if (!TextUtils.isEmpty(userAgent)) {
//                        mIjkPlayer.setOption(tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_FORMAT, "user_agent", userAgent);
//                    }
//                }
                mIjkPlayer.setDataSource(context, uri, null);
            }
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
            mEvent.onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_ERROR_PARSE);
        }

        try {
            mIjkPlayer.prepareAsync();
        } catch (IllegalStateException e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    @Override
    public void setOptions() {

        int player = tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_PLAYER;
        int codec = tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_CODEC;
        int format = tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_FORMAT;

        // 字幕; 1显示。0禁止
        mIjkPlayer.setOption(player, "subtitle", 0);
        //是否有视频, 1无视频、0有视频
        mIjkPlayer.setOption(player, "vn", 0);
        //是否有声音, 1无声音、0有声音
        mIjkPlayer.setOption(player, "an", 0);
        mIjkPlayer.setOption(player, "volume", 100);

        // 0、未知
        // 根据媒体类型来配置 => bug => resp aac音频无声音
//         mIjkPlayer.setOption(format, "allowed_media_types", "video");

        // 不清楚 1、允许 0、不允许
        mIjkPlayer.setOption(format, "http-detect-range-support", 0);

        // 3、缓冲相关
        // 不额外优化
        mIjkPlayer.setOption(player, "fast", 1);
        // 31、探针大小，播放前的探测Size，默认是1M, 改小一点会出画面更快
        mIjkPlayer.setOption(format, "probesize", 102400);// 1024 * 1024
        // 32、ijkplayer中的buffering逻辑不适合低延迟直播场景，可以关闭。快直播传输层SDK是基于webrtc增强的半可靠传输协议，在一般弱网（20%）下能保证音视频正常播放，极端弱网（50%丢包）场景下，也可以保证音频正常播放，视频低帧率播放。
        mIjkPlayer.setOption(player, "packet-buffering", 0);
        // 33、设置缓冲区为100KB，目前我看来，多缓冲了4秒
        mIjkPlayer.setOption(format, "buffer_size", 102400); //1024 * 1024
        mIjkPlayer.setOption(player, "max-buffer-size", 102400);// 1024 * 1024
        // 34、视频的话，设置100帧即开始播放
        mIjkPlayer.setOption(player, "min-frames", 100);// 1024
        // 直播场景时实时推流，可以开启无限制buffer，这样可以尽可能快的读取数据，避免出现网络拥塞恢复后延迟累积的情况。
        mIjkPlayer.setOption(player, "infbuf", 1);
        // 解决m3u8文件拖动问题 比如:一个3个多少小时的音频文件，开始播放几秒中，然后拖动到2小时左右的时间，要loading 10分钟
        mIjkPlayer.setOption(format, "fflags", "fastseek");
//        mIjkPlayer.setOption(format, "fflags", "nobuffer");

        // 1、播放相关
        // sdl渲染
        mIjkPlayer.setOption(player, "overlay-format", tv.danmaku.ijk.media.player.IjkMediaPlayer.SDL_FCC_RV32);
        // 使用opensles 进行音频的解码播放 1、允许 0、不允许[1音频有稍许延迟]
        mIjkPlayer.setOption(player, "opensles", 0);
        // 11、视频缓存好之后是否自动播放 1、允许 0、不允许
        mIjkPlayer.setOption(player, "start-on-prepared", 1);
        // 12、设置是否开启环路过滤: 0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小
        // 12、IJK_AVDISCARD_NONE    =-16, ///< discard nothing
        // 12、IJK_AVDISCARD_DEFAULT =  0, ///< 如果包大小为0，责抛弃无效的包
        // 12、IJK_AVDISCARD_NONREF  =  8, ///< 抛弃非参考帧（I帧）
        // 12、IJK_AVDISCARD_BIDIR   = 16, ///< 抛弃B帧
        // 12、IJK_AVDISCARD_NONKEY  = 32, ///< 抛弃除关键帧以外的，比如B，P帧
        // 12、IJK_AVDISCARD_ALL     = 48, ///< 抛弃所有的帧
        mIjkPlayer.setOption(codec, "skip_loop_filter", 48);
        mIjkPlayer.setOption(codec, "skip_frame", 1); // 4
        // 丢帧是在视频帧处理不过来的时候丢弃一些帧达到同步的效果
        mIjkPlayer.setOption(player, "framedrop", 4); // 1
        // 13、最大fps
        mIjkPlayer.setOption(player, "fps", 30);
        mIjkPlayer.setOption(player, "max-fps", 30);


        // 2、 网络相关
        // 21、清空DNS,有时因为在APP里面要播放多种类型的视频(如:MP4,直播,直播平台保存的视频,和其他http视频),有时会造成因为DNS的问题而报10000问题的
        mIjkPlayer.setOption(format, "dns_cache_clear", 1);
        // 22、重连次数
        mIjkPlayer.setOption(format, "reconnect", 1);
        // 23、设置播放前的最大探测时间
        mIjkPlayer.setOption(format, "analyzeduration", 1);
        mIjkPlayer.setOption(format, "analyzemaxduration", 60);
        mIjkPlayer.setOption(format, "rtbufsize", 60);
        // 最大缓存时长
        mIjkPlayer.setOption(player, "max_cached_duration", 60);
        //倍速功能能够在所有android机型上正常使用，倍速时可能也存在声调问题，但是可以通过设置参数来解决：
        mIjkPlayer.setOption(player, "soundtouch", 1);
        //每处理一个packet之后刷新io上下文
        mIjkPlayer.setOption(format, "flush_packets", 1);

        // SeekTo设置优化
        // 1 seek超级慢
        // 某些视频在SeekTo的时候，会跳回到拖动前的位置，这是因为视频的关键帧的问题，通俗一点就是FFMPEG不兼容，视频压缩过于厉害，seek只支持关键帧，出现这个情况就是原始的视频文件中i 帧比较少
        mIjkPlayer.setOption(player, "enable-accurate-seek", 0);
        // 超时时间
        mIjkPlayer.setOption(format, "timeout", 10 * 1000 * 1000);

        //rtsp设置 https://ffmpeg.org/ffmpeg-protocols.html#rtsp
        mIjkPlayer.setOption(format, "rtsp_flags", "prefer_tcp");
        mIjkPlayer.setOption(format, "rtsp_transport", "tcp");

        /**
         * ijkPlayer支持硬解码和软解码。
         * 软解码时不会旋转视频角度这时需要你通过onInfo的what == IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED去获取角度，自己旋转画面。
         * 或者开启硬解硬解码，不过硬解码容易造成黑屏无声（硬件兼容问题），下面是设置硬解码相关的代码
         */
        // 选择avcodec 进行软件解码， 1为硬解 0为软解
        mIjkPlayer.setOption(player, "mediacodec", 0);
        // 打开h265硬解
        mIjkPlayer.setOption(player, "mediacodec-hevc", 0);
        // 硬解, 1
        mIjkPlayer.setOption(player, "mediacodec-auto-rotate", 0);
        // 硬解, 1
        mIjkPlayer.setOption(player, "mediacodec-handle-resolution-change", 0);
        // ios硬解开关
        // mIjkPlayer.setOption(player, "videotoolbox", 0);
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
        mIjkPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
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

    /**
     * 用于播放raw和asset里面的视频文件
     */
    @Override
    public void setDataSource(AssetFileDescriptor fd) {
        try {
            mIjkPlayer.setDataSource(new RawDataSourceProvider(fd));
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

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
    public void seekTo(long seek) {
        MPLogUtil.log("IjkMediaPlayer => seekTo => seek = " + seek);
        try {
            mEvent.onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_BUFFERING_START);
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

    /**
     * 获取缓冲百分比
     */
    @Override
    public int getBufferedPercentage() {
        return mBufferedPercent;
    }

    @Override
    public void setSurface(@NonNull Surface surface) {
        if (null != surface && null != mIjkPlayer) {
            try {
                mIjkPlayer.setSurface(surface);
            } catch (Exception e) {
                MPLogUtil.log(e.getMessage(), e);
            }
        }
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

    /**
     * 获取当前缓冲的网速
     */
    @Override
    public long getTcpSpeed() {
        return mIjkPlayer.getTcpSpeed();
    }

    /****************/

    @Override
    public void setVolume(float v1, float v2) {
        try {
            float value = Math.max(v1, v2);
            if (value > 1f) {
                value = 1f;
            }
            mIjkPlayer.setVolume(value, value);
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
            mEvent.onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_LOADING_STOP);
            mEvent.onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_ERROR_PARSE);
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
            mEvent.onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_VIDEO_END);
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
                mEvent.onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_LOADING_START);
            }
            // 首帧画面
            else if (what == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                mEvent.onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_LOADING_STOP);
                mEvent.onEvent(PlayerType.KernelType.IJK, what);
            }
            // 事件通知
            else {
                mEvent.onEvent(PlayerType.KernelType.IJK, what);
            }
            return true;
        }
    };

    /**
     * 设置视频缓冲更新监听事件
     */
    private IMediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
            MPLogUtil.log("IjkMediaPlayer => onBufferingUpdate => percent = " + percent);
            mBufferedPercent = percent;
        }
    };


    /**
     * 设置准备视频播放监听事件
     */
    private IMediaPlayer.OnPreparedListener onPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            MPLogUtil.log("IjkMediaPlayer => onPrepared => seek = " + mSeek);
            long seek = getSeek();
            if (seek > 0) {
                seekTo(seek);
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
