package lib.kalu.mediaplayer.core.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Message;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.buried.BuriedEvent;
import lib.kalu.mediaplayer.config.player.PlayerBuilder;
import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.core.controller.base.ControllerLayout;
import lib.kalu.mediaplayer.core.kernel.KernelApi;
import lib.kalu.mediaplayer.core.kernel.KernelEvent;
import lib.kalu.mediaplayer.core.kernel.KernelFactoryManager;
import lib.kalu.mediaplayer.core.render.RenderApi;
import lib.kalu.mediaplayer.core.render.RenderFactoryManager;
import lib.kalu.mediaplayer.listener.OnChangeListener;
import lib.kalu.mediaplayer.listener.OnFullChangeListener;
import lib.kalu.mediaplayer.util.ActivityUtils;
import lib.kalu.mediaplayer.util.BaseToast;
import lib.kalu.mediaplayer.util.MPLogUtil;
import lib.kalu.mediaplayer.util.PlayerUtils;
import lib.kalu.mediaplayer.util.TimerUtil;

/**
 * @description: 播放器具体实现类
 */
@Keep
public class VideoLayout extends RelativeLayout implements PlayerApi {

    private final int HANDLE_MESSAHE_WHAT_TIMER = 1;

    // 解码
    protected KernelApi mKernel;
    // 渲染
    protected RenderApi mRender;

    protected int mCurrentScreenScaleType;
    protected int[] mVideoSize = {0, 0};
    /**
     * assets文件
     */
    protected AssetFileDescriptor mAssetFileDescriptor;

    /**
     * OnStateChangeListener集合，保存了所有开发者设置的监听器
     */
    protected List<OnChangeListener> mOnStateChangeListeners;

    public VideoLayout(@NonNull Context context) {
        super(context);
        init(null);
    }

    public VideoLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public VideoLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VideoLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        MPLogUtil.log("onLife => onAttachedToWindow => this = " + this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        boolean invisibleStop = isInvisibleStop();
        boolean invisibleIgnore = isInvisibleIgnore();
        boolean invisibleRelease = isInvisibleRelease();
        MPLogUtil.log("onLife => onDetachedFromWindow => invisibleStop = " + invisibleStop + ", invisibleIgnore = " + invisibleIgnore + ", invisibleRelease = " + invisibleRelease + ", this = " + this);
        if (invisibleIgnore) {
        } else if (invisibleStop) {
            release(true);
        } else {
            release(false);
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {

        boolean invisibleIgnore = isInvisibleIgnore();
        MPLogUtil.log("onLife => onWindowVisibilityChanged => visibility = " + visibility + ", invisibleIgnore =  " + invisibleIgnore + ", this = " + this);
        if (invisibleIgnore) {
        } else {
            // visable
            if (visibility == View.VISIBLE) {
                startTimer();
                resume(false);
            }
            // not visable
            else {
                clearTimer();
                pause(true);
            }
        }
        super.onWindowVisibilityChanged(visibility);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
//        if (hasWindowFocus && isFull()) {
//
//            //重新获得焦点时保持全屏状态
//            ControllerLayout layout = getControlLayout();
//            if (null != layout) {
//                ViewGroup decorView = VideoHelper.instance().getDecorView(getContext().getApplicationContext(), layout);
//                VideoHelper.instance().hideSysBar(decorView, getContext().getApplicationContext(), layout);
//            }
//        }
    }

    private void init(AttributeSet attrs) {
        setBackgroundColor(Color.parseColor("#000000"));
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_player, this, true);
        setFocusable(false);
        setScaleType(PlayerType.ScaleType.SCREEN_SCALE_MATCH_PARENT);
        BaseToast.init(getContext().getApplicationContext());

        // 全局配置
        PlayerBuilder config = PlayerManager.getInstance().getConfig();
        mCurrentScreenScaleType = config.getScaleType();

        //读取xml中的配置，并综合全局配置
        try {
            TypedArray typed = getContext().getApplicationContext().obtainStyledAttributes(attrs, R.styleable.VideoPlayer);
            mCurrentScreenScaleType = typed.getInt(R.styleable.VideoPlayer_screenScaleType, mCurrentScreenScaleType);
            typed.recycle();
        } catch (Exception e) {
        }
    }

    /**
     * 意外销毁保存数据调用
     *
     * @return 返回Parcelable对象
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        String url = getUrl();
        MPLogUtil.log("onSaveInstanceState => url = " + url);
        if (null != url && url.length() > 0) {
            try {
                long position = getPosition();
                long duration = getDuration();
                MPLogUtil.log("onSaveInstanceState => position = " + position + ", duration = " + duration + ", url = " + url);
                saveBundle(getContext(), url, position, duration);
            } catch (Exception e) {
                MPLogUtil.log(e.getMessage(), e);
            }
        }
        return super.onSaveInstanceState();
    }

    /**
     * 是否显示移动网络提示，可在Controller中配置
     */
    public boolean showNetWarning() {
        if (null == mKernel)
            return false;

        //播放本地数据源时不检测网络
        String url = getUrl();
        if (VideoHelper.instance().isLocalDataSource(url, mAssetFileDescriptor)) {
            return false;
        }

        ControllerLayout layout = getControlLayout();
        if (null != layout) {
            return layout.showNetWarning();
        } else {
            return false;
        }
    }

    @Override
    public void start(@NonNull StartBuilder builder, @NonNull String url) {
        MPLogUtil.log("VideoLayout => start => url = " + url);
        if (null == url || url.length() <= 0)
            return;

        boolean timer = builder.isTimer();
        MPLogUtil.log("VideoLayout => start => timer = " + timer);
        long seek = builder.getSeek();
        MPLogUtil.log("VideoLayout => start => seek = " + seek);
        long max = builder.getMax();
        MPLogUtil.log("VideoLayout => start => max = " + max);
        boolean loop = builder.isLoop();
        MPLogUtil.log("VideoLayout => start => loop = " + loop);
        boolean live = builder.isLive();
        MPLogUtil.log("VideoLayout => start => live = " + live);
        boolean mute = builder.isMute();
        MPLogUtil.log("VideoLayout => start => mute = " + mute);
        boolean invisibleStop = builder.isInvisibleStop();
        MPLogUtil.log("VideoLayout => start => invisibleStop = " + invisibleStop);
        boolean invisibleIgnore = builder.isInvisibleIgnore();
        MPLogUtil.log("VideoLayout => start => invisibleIgnore = " + invisibleIgnore);
        boolean invisibleRelease = builder.isInvisibleRelease();
        MPLogUtil.log("VideoLayout => start => invisibleRelease = " + invisibleRelease);
        try {

            // log
            PlayerBuilder config = PlayerManager.getInstance().getConfig();
            int kernel = config.getKernel();
            MPLogUtil.setLogger(kernel, config.isLog());

            // step0
            callPlayerState(PlayerType.StateType.STATE_INIT);

            // step1
            callPlayerState(PlayerType.StateType.STATE_LOADING_START);

            // step2
            // exo
            if (kernel == PlayerType.KernelType.EXO) {
                if (null != mRender) {
                    int render = config.getRender();
                    if (render == PlayerType.RenderType.SURFACE_VIEW) {
                        releaseRender();
                    }
                }
            }
            // ijk
            else if (kernel == PlayerType.KernelType.IJK) {
                if (null != mKernel) {
                    release();
                }
            }
            // vlc
            else if (kernel == PlayerType.KernelType.VLC) {
            }
            // android
            else if (kernel == PlayerType.KernelType.ANDROID) {
            }

            // Render
            createRender();

            // Kernel
            createKernel(builder, config.isLog());

            // attachKernelRender
            attachKernelRender();

            // step4
            boolean showNetWarning = showNetWarning();
            if (showNetWarning) {
                callPlayerState(PlayerType.StateType.STATE_START_ABORT);
            }

            // step4
            mKernel.create(getContext(), builder, url);
            // step5
            setKeepScreenOn(true);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    @Override
    public void createRender() {
        MPLogUtil.log("createRender => mRender = " + mRender);
        if (null != mRender)
            return;
        try {
            mRender = RenderFactoryManager.getRender(getContext(), PlayerManager.getInstance().getConfig().getRender());
            addRender();
        } catch (Exception e) {
            MPLogUtil.log("createRender => " + e.getMessage(), e);
        }
    }

    @Override
    public void createKernel(@NonNull StartBuilder builder, @NonNull boolean logger) {
//// step1
//        release(true);

        MPLogUtil.log("createKernel => mKernel = " + mKernel);
        if (null != mKernel) {
            pause(true);
            return;
        }

        try {
            mKernel = KernelFactoryManager.getKernel(getContext(), PlayerManager.getInstance().getConfig().getKernel(), new KernelEvent() {
                @Override
                public void onEvent(int kernel, int event) {

                    MPLogUtil.log("onEvent => onKernel = " + kernel + ", event = " + event);

                    switch (event) {
                        // 网络拉流开始
                        case PlayerType.EventType.EVENT_OPEN_INPUT:
                            hideReal();
                            break;
                        // 初始化开始 => loading start
                        case PlayerType.EventType.EVENT_LOADING_START:
                            callPlayerState(PlayerType.StateType.STATE_LOADING_START);
                            break;
                        // 初始化完成 => loading stop
                        case PlayerType.EventType.EVENT_LOADING_STOP:
                            callPlayerState(PlayerType.StateType.STATE_LOADING_STOP);
                            break;
                        // 缓冲开始
                        case PlayerType.EventType.EVENT_BUFFERING_START:
                            callPlayerState(PlayerType.StateType.STATE_BUFFERING_START);
                            break;
                        // 缓冲结束
                        case PlayerType.EventType.EVENT_BUFFERING_STOP:
                            callPlayerState(PlayerType.StateType.STATE_BUFFERING_STOP);
                            break;
                        // 播放开始-快进
                        case PlayerType.EventType.EVENT_VIDEO_START_SEEK:
                            // step1
                            callPlayerState(PlayerType.StateType.STATE_LOADING_STOP);
                            callPlayerState(PlayerType.StateType.STATE_START_SEEK);
                            // step2
                            showReal();
                            // step4
                            resume(false);
                            // step5
                            boolean musicAuto = isExternalMusicAuto();
                            if (musicAuto) {
                                boolean musicLoop = isExternalMusicLoop();
                                boolean prepared = isExternalMusicPrepared();
                                if (musicLoop || !prepared) {
                                    enableExternalMusic(true, true);
                                } else {
                                    enableExternalMusic(false, true);
                                }
                            } else {
                                boolean prepared = isExternalMusicPrepared();
                                enableExternalMusic(false, prepared);
                            }

                            break;
                        // 播放开始
                        case PlayerType.EventType.EVENT_VIDEO_START:
//                        case PlayerType.EventType.EVENT_VIDEO_SEEK_RENDERING_START: // 视频开始渲染
//            case PlayerType.MediaType.MEDIA_INFO_AUDIO_SEEK_RENDERING_START: // 视频开始渲染


                            callPlayerState(PlayerType.StateType.STATE_START);

                            // step1
                            startTimer();
                            // step2
                            showReal();
                            // step3
                            checkReal();
                            // step4
                            boolean musicAuto1 = isExternalMusicAuto();
                            if (musicAuto1) {
                                boolean musicLoop = isExternalMusicLoop();
                                boolean prepared = isExternalMusicPrepared();
                                if (musicLoop || !prepared) {
                                    enableExternalMusic(true, true);
                                } else {
                                    enableExternalMusic(false, true);
                                }
                            } else {
                                boolean prepared = isExternalMusicPrepared();
                                enableExternalMusic(false, prepared);
                            }

                            // 埋点
                            try {
                                BuriedEvent buriedEvent = PlayerManager.getInstance().getConfig().getBuriedEvent();
                                buriedEvent.playerIn(getUrl());
                            } catch (Exception e) {
                            }

                            break;
                        // 播放结束
                        case PlayerType.EventType.EVENT_ERROR_URL:
                        case PlayerType.EventType.EVENT_ERROR_RETRY:
                        case PlayerType.EventType.EVENT_ERROR_SOURCE:
                        case PlayerType.EventType.EVENT_ERROR_PARSE:
                        case PlayerType.EventType.EVENT_ERROR_NET:

                            boolean connected = PlayerUtils.isConnected(getContext());
                            setKeepScreenOn(false);
                            callPlayerState(connected ? PlayerType.StateType.STATE_ERROR : PlayerType.StateType.STATE_ERROR_NET);

                            // step1
                            clearTimer();
                            // step2
                            pause(true);

                            // 埋点
                            try {
                                BuriedEvent buriedEvent = PlayerManager.getInstance().getConfig().getBuriedEvent();
                                buriedEvent.playerError(getUrl(), connected);
                            } catch (Exception e) {
                            }
                            break;
                        case PlayerType.EventType.EVENT_VIDEO_END:

                            callPlayerState(PlayerType.StateType.STATE_END);

                            // step1
                            clearTimer();
                            // step2
                            pause(true);

                            // 埋点
                            try {
                                BuriedEvent buriedEvent = PlayerManager.getInstance().getConfig().getBuriedEvent();
                                buriedEvent.playerCompletion(getUrl());
                            } catch (Exception e) {
                            }

                            boolean looping = isLooping();
                            // loop
                            if (looping) {

                                // step1
                                callPlayerState(PlayerType.StateType.STATE_LOADING_START);
                                hideReal();

                                // step2
                                pause(true);

                                // step3
                                seekTo(true, builder);
                            }
                            // sample
                            else {
                                // step1
                                pause(true);

                                // step2
                                playEnd();
                            }

                            break;
                        // 播放错误
                    }
                }

                @Override
                public void onChanged(int kernel, int width, int height, int rotation) {
                    mVideoSize[0] = width;
                    mVideoSize[1] = height;
                    if (mRender != null) {
                        mRender.setScaleType(mCurrentScreenScaleType);
                        mRender.setVideoSize(width, height);
                    }
                    if (mRender != null && rotation != -1) {
                        mRender.setVideoRotation(rotation);
                    }
                }
            });
            mKernel.createDecoder(getContext(), builder.isMute(), logger);
        } catch (Exception e) {
            MPLogUtil.log("createKernel => " + e.getMessage(), e);
        }
    }

    @Override
    public void attachKernelRender() {
        MPLogUtil.log("attachKernelRender => mRender = " + mRender);
        MPLogUtil.log("attachKernelRender => mKernel = " + mKernel);
        if (null == mRender || null == mKernel)
            return;
        try {
            mRender.setKernel(mKernel);
        } catch (Exception e) {
            MPLogUtil.log("attachKernelRender => " + e.getMessage(), e);
        }
    }

    @Override
    public void toggle(boolean cleanHandler) {
        boolean playing = isPlaying();
        if (playing) {
            pause(cleanHandler);
        } else {
            if (cleanHandler) {
                startTimer();
            }
            resume(true);
        }
    }

    @Override
    public void stopKernel(@NonNull boolean call) {
        setKeepScreenOn(false);
        clearTimer();
        try {
            if (call) {
                callPlayerState(PlayerType.StateType.STATE_KERNEL_STOP);
                callPlayerState(PlayerType.StateType.STATE_LOADING_START);
            }
            mKernel.stop();
        } catch (Exception e) {
            MPLogUtil.log("stopKernel => " + e.getMessage(), e);
        }
    }

    @Override
    public void pauseKernel(@NonNull boolean call) {
        setKeepScreenOn(false);
        clearTimer();
        try {
            // 自动不回调状态
            if (call) {
                callPlayerState(PlayerType.StateType.STATE_PAUSE);
            }
            // 用户手动，需要显示暂停图标
            else {
                callPlayerState(PlayerType.StateType.STATE_PAUSE_IGNORE);
            }
            mKernel.pause();
        } catch (Exception e) {
            MPLogUtil.log("pauseKernel => " + e.getMessage(), e);
        }
    }

    @Override
    public void pause(boolean auto, boolean clearTimer) {
        boolean playing = isPlaying();
        MPLogUtil.log("pauseME => auto = " + auto + ", clearTimer = " + clearTimer + ", playing = " + playing);
        if (!playing)
            return;
        if (clearTimer) {
            clearTimer();
        }
        pauseKernel(!auto);
    }

    @Override
    public void close() {
        boolean playing = isPlaying();
        if (!playing)
            return;
        callPlayerState(PlayerType.StateType.STATE_CLOSE);
        stopKernel(true);
    }

    @Override
    public void resume(boolean call) {
        String url = getUrl();
        MPLogUtil.log("onEvent => resume => url = " + url + ", mKernel = " + mKernel);
        if (null == url || url.length() <= 0)
            return;
        if (call) {
            callPlayerState(PlayerType.StateType.STATE_RESUME);
        } else {
            callPlayerState(PlayerType.StateType.STATE_RESUME_IGNORE);
        }
        setKeepScreenOn(true);
        mKernel.start();
    }

    @Override
    public void repeat() {
        String url = getUrl();
        MPLogUtil.log("onEvent => repeat => url = " + url);
        if (null == url || url.length() <= 0)
            return;
        callPlayerState(PlayerType.StateType.STATE_REPEAT);
        seekTo(true);
    }

    @Override
    public long getDuration() {
        try {
            long duration = mKernel.getDuration();
            return duration;
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getPosition() {
        boolean playing = isPlaying();
        if (!playing)
            return 0L;
        long position = mKernel.getPosition();
        if (position < 0) {
            position = 0L;
        }
        return position;
    }

    @Override
    public boolean isLooping() {
        try {
            return mKernel.isLooping();
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean isInvisibleStop() {
        try {
            return mKernel.isInvisibleStop();
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean isInvisibleIgnore() {
        try {
            return mKernel.isInvisibleIgnore();
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean isInvisibleRelease() {
        try {
            return mKernel.isInvisibleRelease();
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public long getSeek() {
        try {
            return mKernel.getSeek();
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
            return 0L;
        }
    }

    @Override
    public long getMax() {
        try {
            return mKernel.getMax();
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
            return -1L;
        }
    }

    @Override
    public String getUrl() {
        try {
            if (null == mKernel)
                return null;
            return mKernel.getUrl();
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean isTimer() {
        try {
            return mKernel.isTimer();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 是否处于播放状态
     */
    @Override
    public boolean isPlaying() {
        try {
            String url = getUrl();
            if (null == url || url.length() <= 0) {
                return false;
            } else {
                return mKernel.isPlaying();
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取当前缓冲百分比
     */
    @Override
    public int getBufferedPercentage() {
        return mKernel != null ? mKernel.getBufferedPercentage() : 0;
    }

    @Override
    public void seekTo(@NonNull boolean force, @NonNull StartBuilder builder) {

        long seek = builder.getSeek();
        long max = builder.getMax();
        boolean loop = builder.isLoop();

        String url = getUrl();
        MPLogUtil.log("onEvent => seekTo => seek = " + seek + ", max = " + max + ", loop = " + loop + ", force = " + force + ", url = " + url + ", mKernel = " + mKernel);
        if (null == url || url.length() < 0)
            return;

        // step1
        callPlayerState(PlayerType.StateType.STATE_LOADING_START);
        // step2
        clearTimer();
        // step3
        if (force) {
            mKernel.update(seek, max, loop);
            pause(true, true);
        }
        // step4
        mKernel.seekTo(seek);
    }

    @Override
    public boolean seekForward(@NonNull boolean callback) {
        ControllerLayout layout = getControlLayout();
        if (null == layout)
            return false;
        return layout.seekForward(callback);
    }

    @Override
    public boolean seekRewind(boolean callback) {
        ControllerLayout layout = getControlLayout();
        if (null == layout)
            return false;
        return layout.seekRewind(callback);
    }

    @Override
    public boolean isLive() {
        try {
            return mKernel.isLive();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isMute() {
        try {
            return mKernel.isMute();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取缓冲速度
     */

    @Override
    public String getTcpSpeed() {
        Context context = getContext();
        return mKernel.getTcpSpeed(context);
    }

    /**
     * 设置播放速度
     */
    @Override
    public void setSpeed(float speed) {
        boolean playing = isPlaying();
        if (!playing)
            return;
        mKernel.setSpeed(speed);
    }

    /**
     * 获取倍速速度
     *
     * @return 速度
     */
    @Override
    public float getSpeed() {
        boolean playing = isPlaying();
        if (!playing)
            return -1F;
        return mKernel.getSpeed();
    }

    /**
     * 用于播放assets里面的视频文件
     */
    public void setAssetFileDescriptor(AssetFileDescriptor fd) {
        this.mAssetFileDescriptor = fd;
    }

    /**
     * 设置音量 0.0f-1.0f 之间
     *
     * @param v1 左声道音量
     * @param v2 右声道音量
     */
    @Override
    public void setVolume(float v1, float v2) {
        if (mKernel != null) {
            mKernel.setVolume(v1, v2);
        }
    }

    @Override
    public void setMute(boolean v) {
        if (mKernel != null) {
            mKernel.setMute(v);
        }
    }

    /**
     * 循环播放， 默认不循环播放
     */
    public void setLooping(boolean looping) {
        if (mKernel != null) {
            mKernel.setLooping(looping);
        }
    }

    @Override
    public void setKernel(@PlayerType.KernelType.Value int v) {
        PlayerBuilder config = PlayerManager.getInstance().getConfig();
        PlayerBuilder.Builder builder = config.newBuilder();
        builder.setKernel(v);
        PlayerManager.getInstance().setConfig(config);
    }

    @Override
    public void setRender(@PlayerType.RenderType int v) {
        PlayerBuilder config = PlayerManager.getInstance().getConfig();
        PlayerBuilder.Builder builder = config.newBuilder();
        builder.setRender(v);
        PlayerManager.getInstance().setConfig(config);
    }

    @Override
    public void setScaleType(@PlayerType.ScaleType.Value int scaleType) {
        mCurrentScreenScaleType = scaleType;
//        if (mRender != null) {
//            mRender.setScaleType(scaleType);
//        }
    }

    /**
     * 设置镜像旋转，暂不支持SurfaceView
     */
    @Override
    public void setMirrorRotation(boolean enable) {
//        if (mRender != null) {
//            mRender.getView().setScaleX(enable ? -1 : 1);
//        }
    }

    /**
     * 获取视频宽高,其中width: mVideoSize[0], height: mVideoSize[1]
     */
    @Override
    public int[] getVideoSize() {
        return mVideoSize;
    }

    /**
     * 旋转视频画面
     *
     * @param rotation 角度
     */
    @Override
    public void setRotation(float rotation) {
//        if (mRender != null) {
//            mRender.setVideoRotation((int) rotation);
//        }
    }

    /**
     * 添加一个播放状态监听器，播放状态发生变化时将会调用。
     */
    public void addOnChangeListener(@NonNull OnChangeListener listener) {
        if (mOnStateChangeListeners == null) {
            mOnStateChangeListeners = new ArrayList<>();
        }
        mOnStateChangeListeners.add(listener);
    }

    /**
     * 移除某个播放状态监听
     */
    public void removeOnChangeListener(@NonNull OnChangeListener listener) {
        if (mOnStateChangeListeners != null) {
            mOnStateChangeListeners.remove(listener);
        }
    }

    /**
     * 设置一个播放状态监听器，播放状态发生变化时将会调用，
     * 如果你想同时设置多个监听器，推荐 {@link #addOnChangeListener(OnChangeListener)}。
     */
    public void setOnChangeListener(@NonNull OnChangeListener listener) {
        if (mOnStateChangeListeners == null) {
            mOnStateChangeListeners = new ArrayList<>();
        }

        clearListener();
        mOnStateChangeListeners.add(listener);
    }

    /**
     * 移除所有播放状态监听
     */
    private final void clearListener() {
        if (mOnStateChangeListeners != null) {
            mOnStateChangeListeners.clear();
        }
    }

    /**
     * 改变返回键逻辑，用于activity
     */
    public boolean onBackPressed() {
        ControllerLayout layout = getControlLayout();
        return null != layout && layout.onBackPressed();
    }

    /************************/

    /**
     * 设置控制器，传null表示移除控制器
     *
     * @param layout
     */
    public void setControllerLayout(@Nullable ControllerLayout layout) {
        ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_control);
        viewGroup.removeAllViews();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(params);
        viewGroup.addView(layout);
        layout.setMediaPlayer(this);
        // call
        callWindowState(PlayerType.WindowType.NORMAL);
    }

    public void clearControllerLayout() {
        ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_control);
        viewGroup.removeAllViews();
    }

    @Override
    public ControllerLayout getControlLayout() {

        View parent;
        boolean full = isFull();
        if (full) {
            Context context = getContext();
            Activity activity = ActivityUtils.getActivity(context);
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            int index = decorView.getChildCount();
            parent = decorView.getChildAt(index - 1);
        } else {
            parent = this;
        }

        try {
            ViewGroup viewGroup = parent.findViewById(R.id.module_mediaplayer_control);
            return (ControllerLayout) viewGroup.getChildAt(0);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void showReal() {
        try {
            ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_video);
            viewGroup.setVisibility(View.VISIBLE);
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = viewGroup.getChildAt(i);
                child.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    @Override
    public void hideReal() {
        try {
            ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_video);
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = viewGroup.getChildAt(i);
                child.setVisibility(View.INVISIBLE);
            }
            viewGroup.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    @Override
    public void checkReal() {
        int visibility = getWindowVisibility();
        MPLogUtil.log("onLife => checkReal => visibility = " + visibility);
        if (visibility == View.VISIBLE)
            return;
        clearTimer();
        pause();
    }

    @Override
    public void releaseKernel() {
        stopKernel(true);
        try {
            mKernel.pause();
            mKernel.releaseDecoder();
            mKernel = null;
        } catch (Exception e) {
        }
    }

    @Override
    public void addRender() {
        delRender();
        try {
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mRender.getReal().setLayoutParams(params);
            ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_video);
            viewGroup.removeAllViews();
            viewGroup.addView(mRender.getReal(), 0);
        } catch (Exception e) {
            MPLogUtil.log("addRender => " + e.getMessage(), e);
        }
    }

    @Override
    public void delRender() {
        try {
            ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_video);
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = viewGroup.getChildAt(i);
                if (null == child)
                    continue;
                if (child instanceof RenderApi) {
                    ((RenderApi) child).releaseReal();
                }
            }
            if (null != viewGroup) {
                viewGroup.removeView(mRender.getReal());
            }
        } catch (Exception e) {
            MPLogUtil.log("delRender => " + e.getMessage(), e);
        }
    }

    @Override
    public void releaseRender() {
        delRender();
        try {
            mRender.releaseReal();
            mRender = null;
        } catch (Exception e) {
            MPLogUtil.log("releaseRender => " + e.getMessage(), e);
        }
    }

    @Override
    public void release(@NonNull boolean onlyHandle) {
        MPLogUtil.log("onEvent => release => onlyHandle = " + onlyHandle + ", mKernel = " + mKernel);

        // step1
        clearTimer();
        // step2
        pause(!onlyHandle, true);

        // step3
        if (onlyHandle)
            return;

        boolean isFloat = isFloat();
        if (isFloat) {
            stopFloat();
        }

        boolean isFull = isFull();
        if (isFull) {
            stopFull();
        }

        // step51
        ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_video);
        if (null != viewGroup) {
            viewGroup.removeAllViews();
        }

        // step52
        releaseRender();

        // step7
        releaseKernel();
    }

    @Override
    public void startTimer() {
        boolean timer = isTimer();
        boolean live = isLive();
        MPLogUtil.log("Timer => startTimer => timer = " + timer + ", live = " + live);
        if (live || !timer)
            return;
        String url = getUrl();
        if (null == url || url.length() <= 0)
            return;
        clearTimer();
        sendMessage(null);
    }

    private void sendMessage(Object obj) {
        // 1
        boolean containsListener = TimerUtil.getInstance().containsListener(9002);
        if (!containsListener) {
            TimerUtil.getInstance().registTimer(9002, new TimerUtil.OnMessageChangeListener() {
                @Override
                public void onMessage(@NonNull Message msg) {
                    handleMessage(msg);
                }
            });
        }
        // 2
        Message message = new Message();
        message.what = HANDLE_MESSAHE_WHAT_TIMER;
        if (null == obj) {
            message.obj = System.currentTimeMillis();
            TimerUtil.getInstance().sendMessage(message);
        } else {
            message.obj = obj;
            TimerUtil.getInstance().sendMessageDelayed(message, 1000);
        }
    }

    @Override
    public void clearTimer() {
        boolean timer = isTimer();
        boolean live = isLive();
        MPLogUtil.log("Timer => clearTimer => timer = " + timer + ", live = " + live);
        if (live || !timer)
            return;
        TimerUtil.getInstance().clearMessage(HANDLE_MESSAHE_WHAT_TIMER);
    }

    @Override
    public void playEnd() {
        hideReal();
        setKeepScreenOn(false);
        callPlayerState(PlayerType.StateType.STATE_END);
    }

    @Override
    public void callPlayerState(@PlayerType.StateType.Value int playerState) {

        ControllerLayout layout = getControlLayout();
        if (null != layout) {
            layout.setPlayState(playerState);
        }
        if (mOnStateChangeListeners != null) {
            for (OnChangeListener l : PlayerUtils.getSnapshot(mOnStateChangeListeners)) {
                if (l != null) {
                    l.onChange(playerState);
                }
            }
        }
    }

    @Override
    public void callWindowState(@PlayerType.WindowType.Value int windowState) {
        ControllerLayout layout = getControlLayout();
        if (null != layout) {
            layout.setWindowState(windowState);
        }
        if (mOnStateChangeListeners != null) {
            for (OnChangeListener l : PlayerUtils.getSnapshot(mOnStateChangeListeners)) {
                if (l != null) {
                    l.onWindow(windowState);
                }
            }
        }
    }

    @Override
    public void enableExternalMusic(boolean enable, boolean release, boolean auto) {
        try {
            if (auto) {
                mKernel.setExternalMusicAuto(true);
            }
            mKernel.enableExternalMusic(enable, release);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    @Override
    public boolean isExternalMusicAuto() {
        try {
            boolean auto = mKernel.isExternalMusicAuto();
            MPLogUtil.log("isExternalMusicAuto => auto = " + auto);
            if (auto) {
                String path = mKernel.getExternalMusicPath();
                return null != path && path.length() > 0;
            } else {
                return false;
            }
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean isExternalMusicLoop() {
        try {
            return mKernel.isExternalMusicLoop();
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean isExternalMusicPrepared() {
        try {
            return mKernel.isExternalMusicPrepared();
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void clearSuface() {
        try {
            mRender.clearCanvas();
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    @Override
    public void updateSuface() {
        try {
            mRender.updateCanvas();
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    @Override
    public String screenshot() {
        try {
            return mRender.screenshot();
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void setExternalMusic(@NonNull StartBuilder bundle) {
        try {
            String url = bundle.getExternalMusicUrl();
            mKernel.setExternalMusicPath(url);
            boolean loop = bundle.isExternalMusicLoop();
            mKernel.setExternalMusicLoop(loop);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    /*****************************/

    private OnFullChangeListener mOnFullChangeListener;

    public void setOnFullChangeListener(@NonNull OnFullChangeListener listener) {
        this.mOnFullChangeListener = listener;
    }

    @Override
    public boolean isFull() {
        int count = getChildCount();
        return count <= 0;
    }

    @Override
    public void startFull() {
        Context context = getContext();
        Activity activity = ActivityUtils.getActivity(context);
        if (null == activity)
            return;

        int count = getChildCount();
        if (count <= 0)
            return;

        boolean playing = isPlaying();
        if (!playing)
            return;

        try {
            // 0
            setFocusable(true);
            // 1
            View real = getChildAt(0);
            removeAllViews();
            // 2
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            int index = decorView.getChildCount();
            decorView.addView(real, index);
            // 3
            // 4
            callWindowState(PlayerType.WindowType.FULL);
            // 5
            if (null != mOnFullChangeListener) {
                mOnFullChangeListener.onFull();
            }
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    @Override
    public void stopFull() {
        Context context = getContext();
        Activity activity = ActivityUtils.getActivity(context);
        if (null == activity)
            return;

        int count = getChildCount();
        if (count > 0)
            return;

        try {
            // 0
            setFocusable(false);
            // 1
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            // 2
            int index = decorView.getChildCount();
            View real = decorView.getChildAt(index - 1);
            decorView.removeView(real);
            // 2
            removeAllViews();
            addView(real, 0);
            // 4
            callWindowState(PlayerType.WindowType.NORMAL);
            // 5
            if (null != mOnFullChangeListener) {
                mOnFullChangeListener.onNormal();
            }
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    /*****************************/

    @Override
    public boolean isFloat() {
        try {
            int count = getChildCount();
            if (count <= 0) {
                Activity activity = ActivityUtils.getActivity(getContext());
                View decorView = activity.getWindow().getDecorView();
                View v = decorView.findViewById(R.id.module_mediaplayer_root);
                if (null != v) {
                    ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                    return !(layoutParams.width == ViewGroup.LayoutParams.MATCH_PARENT && layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT);
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void startFloat() {
        Context context = getContext();
        Activity activity = ActivityUtils.getActivity(context);
        if (null == activity)
            return;

        int count = getChildCount();
        if (count <= 0)
            return;

        boolean playing = isPlaying();
        if (!playing)
            return;

        try {
            // 1
            ViewGroup real = (ViewGroup) getChildAt(0);
            removeAllViews();
            // 2
            ViewGroup root = real.findViewById(R.id.module_mediaplayer_root);
            int width = getResources().getDimensionPixelOffset(R.dimen.module_mediaplayer_dimen_float_width);
            int height = width * 9 / 16;
            LayoutParams layoutParams = new LayoutParams(width, height);
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            root.setBackgroundColor(Color.BLACK);
            root.setLayoutParams(layoutParams);
            // 3
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            int index = decorView.getChildCount();
            decorView.addView(real, index);
            // 5
            callWindowState(PlayerType.WindowType.FLOAT);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    @Override
    public void stopFloat() {
        Context context = getContext();
        Activity activity = ActivityUtils.getActivity(context);
        if (null == activity)
            return;

        int count = getChildCount();
        if (count > 0)
            return;

        try {
            // 1
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            int index = decorView.getChildCount();
            View real = decorView.getChildAt(index - 1);
            decorView.removeView(real);
            // 2
            ViewGroup root = real.findViewById(R.id.module_mediaplayer_root);
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            root.setBackground(null);
            root.setLayoutParams(layoutParams);
            // 2
            removeAllViews();
            addView(real, 0);
            // 4
            callWindowState(PlayerType.WindowType.NORMAL);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    private final void handleMessage(@NonNull Message msg) {
        MPLogUtil.log("VideoLayout => handleMessage => what = " + msg.what + ", thread = " + Thread.currentThread().getName());

        if (msg.what != HANDLE_MESSAHE_WHAT_TIMER)
            return;

        long max = getMax();
        boolean looping = isLooping();
        long start = (long) msg.obj;
        long millis = System.currentTimeMillis();
        MPLogUtil.log("VideoLayout => handleMessage => max = " + max + ", looping = " + looping + ", start = " + start + ", millis = " + millis);

        // end
        if (max > 0 && ((millis - start) > max)) {

            // loop
            if (looping) {

                // step1
                hideReal();

                // step2
                // pause(true, true);

                // step3
                seekTo(true);
            }
            // stop
            else {

                // step1
                pause(true);

                // step2
                playEnd();
            }
        }
        // next
        else {

            sendMessage(msg.obj);

            long position = getPosition();
            long duration = getDuration();

            if (position > 0 && duration > 0) {
                ControllerLayout controlLayout = getControlLayout();
                if (null != controlLayout) {
                    controlLayout.seekProgress(false, position, duration);
                }

                if (mOnStateChangeListeners != null) {
                    for (OnChangeListener l : PlayerUtils.getSnapshot(mOnStateChangeListeners)) {
                        if (l != null) {
                            l.onProgress(position, duration);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        boolean focusable = isFocusable();
        if (focusable) {
            boolean isFull = isFull();
            boolean isFloat = isFloat();
            if (isFloat) {
                // stopFloat
                if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    MPLogUtil.log("dispatchKeyEvent => stopFloat =>");
                    stopFloat();
                }
                return true;
            } else if (isFull) {
                // toogle
                if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                    MPLogUtil.log("dispatchKeyEvent => toggle =>");
                    toggle();
                }
                // seekForward
                else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    int count = event.getRepeatCount();
                    boolean isLive = isLive();
                    MPLogUtil.log("dispatchKeyEvent => seekForward[false] => count = " + count + ", isLive = " + isLive);
                    if (!isLive) {
                        if (count > 0) {
                            clearTimer();
                            boolean seekForward = seekForward(false);
                            if (seekForward) {
                                setTag(R.id.module_mediaplayer_id_seek, "1");
                            }
                        } else {
                            callWindowState(PlayerType.WindowType.FULL);
                        }
                    }
                }
                // seekForward
                else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    int count = event.getRepeatCount();
                    boolean isLive = isLive();
                    MPLogUtil.log("dispatchKeyEvent => seekForward[true] => count = " + count + ", isLive = " + isLive);
                    if (!isLive) {
                        Object tag = getTag(R.id.module_mediaplayer_id_seek);
                        if (null != tag && "1".equals(tag)) {
                            startTimer();
                            boolean seekForward = seekForward(true);
                            if (seekForward) {
                                setTag(R.id.module_mediaplayer_id_seek, null);
                            }
                        }
                    }
                }
                // seekRewind
                else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    int count = event.getRepeatCount();
                    boolean isLive = isLive();
                    MPLogUtil.log("dispatchKeyEvent => seekRewind[false] => count = " + count + ", isLive = " + isLive);
                    if (!isLive) {
                        if (count > 0) {
                            clearTimer();
                            boolean seekRewind = seekRewind(false);
                            if (seekRewind) {
                                setTag(R.id.module_mediaplayer_id_seek, "1");
                            }
                        } else {
                            callWindowState(PlayerType.WindowType.FULL);
                        }
                    }
                }
                // seekRewind
                else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    int count = event.getRepeatCount();
                    boolean isLive = isLive();
                    MPLogUtil.log("dispatchKeyEvent => seekRewind[true] => count = " + count + ", isLive = " + isLive);
                    if (!isLive) {
                        Object tag = getTag(R.id.module_mediaplayer_id_seek);
                        if (null != tag && "1".equals(tag)) {
                            startTimer();
                            boolean seekRewind = seekRewind(true);
                            if (seekRewind) {
                                setTag(R.id.module_mediaplayer_id_seek, null);
                            }
                        }
                    }
                }
                // stopFull
                else if (isFull() && event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    MPLogUtil.log("decodeKeyEvent => stopFull =>");
                    stopFull();
                }
                // VOLUME_UP
                else if (isFull() && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
                    return super.dispatchKeyEvent(event);
                }
                // VOLUME_DOWN
                else if (isFull() && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    return super.dispatchKeyEvent(event);
                }
                // VOLUME_MUTE
                else if (isFull() && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_MUTE) {
                    return super.dispatchKeyEvent(event);
                }
                // VOICE_ASSIST
                else if (isFull() && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_VOICE_ASSIST) {
                    return super.dispatchKeyEvent(event);
                }
                return true;
            }
        }
        return false;
    }
}