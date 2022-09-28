package lib.kalu.mediaplayer.core.kernel.ijk;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Surface;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.KernelApi;
import lib.kalu.mediaplayer.core.kernel.KernelEvent;
import lib.kalu.mediaplayer.util.MediaLogUtil;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;

@Keep
public final class IjkMediaPlayer implements KernelApi, KernelEvent {

    private int mBufferedPercent;

    private long mSeek = 0L; // 快进
    private long mMax = 0L; // 试播时常
    private boolean mAutoRelease = false;
    private boolean mLoop = false; // 循环播放
    private boolean mLive = false;
    private boolean mMute = false;
    private String mUrl = null; // 视频串

    private String mExternalMusicPath = null;
    private boolean mExternalMusicPlaying = false;
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
    public void createDecoder(@NonNull Context context, @NonNull boolean mute) {
        if (null == mIjkPlayer) {
            mIjkPlayer = new tv.danmaku.ijk.media.player.IjkMediaPlayer();
            mIjkPlayer.setLooping(false);
            if (mute) {
                mIjkPlayer.setVolume(0f, 0f);
            }
            tv.danmaku.ijk.media.player.IjkMediaPlayer.native_setLogLevel(MediaLogUtil.isIsLog() ? tv.danmaku.ijk.media.player.IjkMediaPlayer.IJK_LOG_INFO : tv.danmaku.ijk.media.player.IjkMediaPlayer.IJK_LOG_SILENT);
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
            e.printStackTrace();
            mEvent.onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_ERROR_PARSE);
        }

        try {
            mIjkPlayer.prepareAsync();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOptions() {

        int player = tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_PLAYER;
        int codec = tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_CODEC;
        int format = tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_FORMAT;

        // 不显示字幕
        mIjkPlayer.setOption(player, "subtitle", 0);
        //是否有声音, 1无声音、0有声音
        mIjkPlayer.setOption(player, "an", 0);
        mIjkPlayer.setOption(player, "volume", 100);
        //是否有视频, 1无视频、0有视频
        mIjkPlayer.setOption(player, "vn", 0);

        /**
         *  IJK_AVDISCARD_NONE    =-16, ///< discard nothing
         *  IJK_AVDISCARD_DEFAULT =  0, ///< 如果包大小为0，责抛弃无效的包
         *  IJK_AVDISCARD_NONREF  =  8, ///< 抛弃非参考帧（I帧）
         *  IJK_AVDISCARD_BIDIR   = 16, ///< 抛弃B帧
         *  IJK_AVDISCARD_NONKEY  = 32, ///< 抛弃除关键帧以外的，比如B，P帧
         *  IJK_AVDISCARD_ALL     = 48, ///< 抛弃所有的帧
         */
        // 设置是否开启环路过滤: 0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小
        mIjkPlayer.setOption(codec, "skip_loop_filter", 48);
        // 预加载800K
        mIjkPlayer.setOption(format, "probesize", 1024 * 800);
        // 最大缓冲800K
        mIjkPlayer.setOption(player, "max-buffer-size", 1024 * 800);
//
        //设置播放前的最大探测时间
        mIjkPlayer.setOption(format, "analyzeduration", 30 * 1000 * 1000);
        mIjkPlayer.setOption(format, "analyzemaxduration", 30 * 1000 * 1000);
//        //设置是否开启变调isModifyTone?0:1
        mIjkPlayer.setOption(player, "soundtouch", 0);
        //每处理一个packet之后刷新io上下文
        mIjkPlayer.setOption(format, "flush_packets", 1);
        // 关闭播放器缓冲，这个必须关闭，否则会出现播放一段时间后，一直卡主，控制台打印 FFP_MSG_BUFFERING_START
        mIjkPlayer.setOption(player, "packet-buffering", 0);
        //播放重连次数
        mIjkPlayer.setOption(player, "reconnect", 1);
        // 跳帧处理,放CPU处理较慢时，进行跳帧处理，保证播放流程，画面和声音同步
        mIjkPlayer.setOption(player, "framedrop", 1);
        //最大fps
        mIjkPlayer.setOption(player, "max-fps", 30);

        // SeekTo设置优化
        // 1 seek超级慢
        // 某些视频在SeekTo的时候，会跳回到拖动前的位置，这是因为视频的关键帧的问题，通俗一点就是FFMPEG不兼容，视频压缩过于厉害，seek只支持关键帧，出现这个情况就是原始的视频文件中i 帧比较少
        mIjkPlayer.setOption(player, "enable-accurate-seek", 1);
        // 设置seekTo能够快速seek到指定位置并播放
        // 解决m3u8文件拖动问题 比如:一个3个多少小时的音频文件，开始播放几秒中，然后拖动到2小时左右的时间，要loading 10分钟
        mIjkPlayer.setOption(format, "fflags", "fastseek");

        // http、https混合存在时
        mIjkPlayer.setOption(format, "dns_cache_clear", 1);
        //超时时间，timeout参数只对http设置有效，若果你用rtmp设置timeout，ijkplayer内部会忽略timeout参数。rtmp的timeout参数含义和http的不一样。
        mIjkPlayer.setOption(format, "timeout", 30 * 1000 * 1000);

        //rtsp设置 https://ffmpeg.org/ffmpeg-protocols.html#rtsp
        mIjkPlayer.setOption(format, "rtsp_transport", "tcp");
        mIjkPlayer.setOption(format, "rtsp_flags", "prefer_tcp");

        // 根据媒体类型来配置 => bug => resp aac音频无声音
//         mIjkPlayer.setOption(format, "allowed_media_types", "video");
        // 缓冲800K
        mIjkPlayer.setOption(format, "buffer_size", 1024 * 800);
        // 无限读
        mIjkPlayer.setOption(format, "infbuf", 1);

//        mIjkPlayer.setOption(player, "opensles", 0);
//        mIjkPlayer.setOption(player, "overlay-format", tv.danmaku.ijk.media.player.IjkMediaPlayer.SDL_FCC_RV32);
//        mIjkPlayer.setOption(player, "start-on-prepared", 0);
//        mIjkPlayer.setOption(format, "http-detect-range-support", 0);
//
//        //jkPlayer支持硬解码和软解码。
//        //软解码时不会旋转视频角度这时需要你通过onInfo的what == IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED去获取角度，自己旋转画面。
//        //或者开启硬解硬解码，不过硬解码容易造成黑屏无声（硬件兼容问题），下面是设置硬解码相关的代码
        mIjkPlayer.setOption(player, "videotoolbox", 0);
        mIjkPlayer.setOption(player, "mediacodec", 0);//1为硬解 0为软解
        mIjkPlayer.setOption(player, "mediacodec-hevc", 0);//打开h265硬解
        mIjkPlayer.setOption(player, "mediacodec-auto-rotate", 0);
        mIjkPlayer.setOption(player, "mediacodec-handle-resolution-change", 0);
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 调整进度
     */
    @Override
    public void seekTo(long seek) {
        MediaLogUtil.log("IjkMediaPlayer => seekTo => seek = " + seek);
        try {
            mEvent.onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_BUFFERING_START);
            mIjkPlayer.seekTo(seek);
        } catch (IllegalStateException e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
                e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    @Override
    public boolean isMute() {
        return mMute;
//        try {
//                float volume = mVlcPlayer.getVLC().getVolume();
//                return volume <= 0;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
    }

    @Override
    public void setMute(boolean mute) {
        this.mMute = mute;
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
    public void setAutoRelease(boolean release) {
        this.mAutoRelease = release;
    }

    @Override
    public boolean isAutoRelease() {
        return this.mAutoRelease;
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
            MediaLogUtil.log("IjkMediaPlayer => onError => framework_err = " + framework_err + ", impl_err = " + impl_err);
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
            MediaLogUtil.log("IjkMediaPlayer => onCompletion =>");
            mEvent.onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_VIDEO_END);
        }
    };


    /**
     * 设置视频信息监听器
     */
    private IMediaPlayer.OnInfoListener onInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
            MediaLogUtil.log("IjkMediaPlayer => onInfo => what = " + what + ", extra = " + extra);
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
            MediaLogUtil.log("IjkMediaPlayer => onBufferingUpdate => percent = " + percent);
            mBufferedPercent = percent;
        }
    };


    /**
     * 设置准备视频播放监听事件
     */
    private IMediaPlayer.OnPreparedListener onPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            MediaLogUtil.log("IjkMediaPlayer => onPrepared => seek = " + mSeek);
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
            MediaLogUtil.log("IjkMediaPlayer => onVideoSizeChanged => width = " + width + ", height = " + height);
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
            MediaLogUtil.log("IjkMediaPlayer => onTimedText => text = " + ijkTimedText.getText());
        }
    };

    /**
     * 设置视频seek完成监听事件
     */
    private IMediaPlayer.OnSeekCompleteListener onSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer iMediaPlayer) {
            MediaLogUtil.log("IjkMediaPlayer => onSeekComplete => ");
        }
    };

    /****************/

    @Override
    public boolean isExternalMusicPlaying() {
        return mExternalMusicPlaying;
    }

    @Override
    public void setExternalMusicPlaying(boolean v) {
        this.mExternalMusicPlaying = v;
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
