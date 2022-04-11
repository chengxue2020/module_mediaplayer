package lib.kalu.mediaplayer.core.view;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.core.kernel.KernelFactory;
import lib.kalu.mediaplayer.core.kernel.KernelFactoryManager;
import lib.kalu.mediaplayer.core.kernel.impl.ImplKernel;
import lib.kalu.mediaplayer.core.kernel.video.listener.OnVideoPlayerChangeListener;
import lib.kalu.mediaplayer.core.render.ImplRender;
import lib.kalu.mediaplayer.core.render.RenderFactoryManager;
import lib.kalu.mediaplayer.listener.OnMediaStateListener;
import lib.kalu.mediaplayer.config.PlayerConfig;
import lib.kalu.mediaplayer.config.PlayerConfigManager;
import lib.kalu.mediaplayer.config.PlayerType;
import lib.kalu.mediaplayer.core.controller.base.ControllerLayout;
import lib.kalu.mediaplayer.listener.OnMediaProgressManager;
import lib.kalu.mediaplayer.util.BaseToast;
import lib.kalu.mediaplayer.util.PlayerUtils;
import lib.kalu.mediaplayer.util.MediaLogUtil;

/**
 * @description: 播放器具体实现类
 */
@Keep
public class VideoLayout extends RelativeLayout implements ImplPlayer, OnVideoPlayerChangeListener {


    private String mUrl ="";
    protected Map<String, String> mHeaders = null;

    // 解码
    protected ImplKernel mKernel;
    // 渲染
    protected ImplRender mRender;

    protected int mCurrentScreenScaleType;
    protected int[] mVideoSize = {0, 0};
    /**
     * 是否静音
     */
    protected boolean mIsMute;
    /**
     * assets文件
     */
    protected AssetFileDescriptor mAssetFileDescriptor;
    /**
     * 播放模式，普通模式，小窗口模式，正常模式等等
     * 存在局限性：比如小窗口下的正在播放模式，那么mCurrentMode就是STATE_PLAYING，而不是MODE_TINY_WINDOW并存
     **/
    protected int mCurrentPlayerState = PlayerType.WindowType.NORMAL;
    /**
     * 是否处于全屏状态
     */
    protected boolean mIsFullScreen;
    /**
     * 是否处于小屏状态
     */
    protected boolean mIsTinyScreen;
    protected int[] mTinyScreenSize = {0, 0};

    /**
     * OnStateChangeListener集合，保存了所有开发者设置的监听器
     */
    protected List<OnMediaStateListener> mOnStateChangeListeners;

    /**
     * 进度管理器，设置之后播放器会记录播放进度，以便下次播放恢复进度
     */
    @Nullable
    protected OnMediaProgressManager mProgressManager;

    /**
     * 循环播放
     */
    protected boolean mIsLooping;

//    /**
//     * {@link #mVideoContainer}背景色，默认黑色
//     */
//    private int mPlayerBackgroundColor;

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
//        initKernel();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        releaseKernel();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        if (visibility == View.VISIBLE) {
            repeat();
        } else {
            stop();
        }
        super.onWindowVisibilityChanged(visibility);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus && mIsFullScreen) {

            //重新获得焦点时保持全屏状态
            ControllerLayout layout = getControlLayout();
            if (null != layout) {
                ViewGroup decorView = VideoHelper.instance().getDecorView(getContext().getApplicationContext(), layout);
                VideoHelper.instance().hideSysBar(decorView, getContext().getApplicationContext(), layout);
            }
        }
    }

    private void init(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_root, this, true);

        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setScaleType(PlayerType.ScaleType.SCREEN_SCALE_MATCH_PARENT);
        BaseToast.init(getContext().getApplicationContext());

        // 全局配置
        PlayerConfig config = PlayerConfigManager.getInstance().getConfig();
        mProgressManager = config.mProgressManager;
        mCurrentScreenScaleType = config.mScreenScaleType;
        //设置是否打印日志
        MediaLogUtil.setIsLog(config.mIsEnableLog);

        //读取xml中的配置，并综合全局配置
        try {
            TypedArray typed = getContext().getApplicationContext().obtainStyledAttributes(attrs, R.styleable.VideoPlayer);
            mIsLooping = typed.getBoolean(R.styleable.VideoPlayer_looping, false);
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
        MediaLogUtil.log("onSaveInstanceState: ");
        //activity切到后台后可能被系统回收，故在此处进行进度保存
        saveProgress();
        return super.onSaveInstanceState();
    }


    @Override
    public void start(@NonNull long seek, @NonNull boolean live, @NonNull String url, @NonNull String subtitle, @NonNull Map<String, String> headers) {

        MediaLogUtil.log("start => seek = " + seek + ", live = " + live + ", url = " + url);

        // fail
        if (null == url || url.length() <= 0) {
            setPlayState(PlayerType.StateType.STATE_PREPARE_START);
            setPlayState(PlayerType.StateType.STATE_ERROR_URL);
        }
        // next
        else {
            mUrl = url;
            releaseKernel();
            initKernel();
            initRender();
            boolean prepare = prepare(url);
//            mKernel.start();
            setKeepScreenOn(prepare);
        }
    }

    protected boolean prepare(@NonNull String url) {

        //如果要显示移动网络提示则不继续播放
        boolean showNetWarning = showNetWarning();
        if (showNetWarning) {
            setPlayState(PlayerType.StateType.STATE_START_ABORT);
            return false;
        }
//        //读取播放进度
//        if (mProgressManager != null) {
//            mCurrentPosition = mProgressManager.getSavedProgress(mUrl);
//        }

        //准备开始播放
        setPlayState(PlayerType.StateType.STATE_PREPARE_START);
        mKernel.prepare(getContext(), url, mHeaders);
        return true;
    }

    /**
     * 是否显示移动网络提示，可在Controller中配置
     */
    public boolean showNetWarning() {
        if (null == mKernel)
            return false;

        //播放本地数据源时不检测网络
        if (VideoHelper.instance().isLocalDataSource(mUrl, mAssetFileDescriptor)) {
            return false;
        }

        ControllerLayout layout = getControlLayout();
        if (null != layout) {
            return layout.showNetWarning();
        } else {
            return false;
        }
    }

//    /**
//     * 播放状态下开始播放
//     */
//    protected void startInPlaybackState() {
//        mKernel.start();
//        setPlayState(PlayerType.StateType.STATE_START);
//    }

    @Override
    public void pause() {
        MediaLogUtil.log("onLife => pause => 1");
        if (null == mKernel)
            return;
        MediaLogUtil.log("onLife => pause => 2");
        if (!mKernel.isPlaying())
            return;
        MediaLogUtil.log("onLife => pause => 3");
        mKernel.pause();
        setPlayState(PlayerType.StateType.STATE_PAUSED);
        setKeepScreenOn(false);
    }

    @Override
    public void stop() {
        MediaLogUtil.log("onLife => stop => 1");
        if (null == mKernel)
            return;
        MediaLogUtil.log("onLife => stop => 2");
        if (!mKernel.isPlaying())
            return;
        MediaLogUtil.log("onLife => stop => 3");
        mKernel.stop();
        setPlayState(PlayerType.StateType.STATE_PAUSED);
        setKeepScreenOn(false);
    }

    @Override
    public void resume() {
        MediaLogUtil.log("onLife => resume => 1");
        if (null == mKernel)
            return;
        MediaLogUtil.log("onLife => resume => 2");
        if (mKernel.isPlaying())
            return;
        MediaLogUtil.log("onLife => resume => 3");
        mKernel.start();
        setPlayState(PlayerType.StateType.STATE_START);
        setKeepScreenOn(true);
    }

    @Override
    public void repeat() {
        MediaLogUtil.log("onLife => repeat => 1");
        start(0, mUrl);
    }

    /**
     * 保存播放进度
     */
    protected void saveProgress() {
//        long position = getPosition();
//        if (mProgressManager != null && mCurrentPosition > 0) {
//            MediaLogUtil.log("saveProgress: " + mCurrentPosition);
//            mProgressManager.saveProgress(mUrl, mCurrentPosition);
//        }
    }

    /**
     * 是否处于播放状态
     */
    protected boolean isInPlaybackState() {
        if (null == mKernel)
            return false;

        int state = getPlayState();
        return mKernel != null
                && state != PlayerType.StateType.STATE_ERROR
                && state != PlayerType.StateType.STATE_INIT
                && state != PlayerType.StateType.STATE_PREPARE_START
                && state != PlayerType.StateType.STATE_START_ABORT
                && state != PlayerType.StateType.STATE_BUFFERING_PLAYING;
    }

    /**
     * 是否处于未播放状态
     */
    protected boolean isInit() {
        int state = getPlayState();
        return state == PlayerType.StateType.STATE_INIT;
    }

    /**
     * 播放中止状态
     */
    private boolean isInStartAbortState() {
        int state = getPlayState();
        return state == PlayerType.StateType.STATE_START_ABORT;
    }

    /**
     * 获取视频总时长
     */
    @Override
    public int getDuration() {
        if (null == mKernel || !isInPlaybackState())
            return 0;
        int duration = mKernel.getDuration();
        if (duration < 0) {
            duration = 0;
        }
        return duration;
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public int getPosition() {
        if (null == mKernel || !isInPlaybackState())
            return 0;
        int position = mKernel.getPosition();
        if (position < 0) {
            position = 0;
        }
        return position;
    }

    /**
     * 调整播放进度
     */
    @Override
    public void seekTo(long pos) {
        long seek;
        if (pos < 0) {
            MediaLogUtil.log("设置参数-------设置开始跳转播放位置不能小于0");
            seek = 0;
        } else {
            seek = pos;
        }
        if (isInPlaybackState()) {
            mKernel.seekTo(seek);
        }
    }

    /**
     * 是否处于播放状态
     */
    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mKernel.isPlaying();
    }

    /**
     * 获取当前缓冲百分比
     */
    @Override
    public int getBufferedPercentage() {
        return mKernel != null ? mKernel.getBufferedPercentage() : 0;
    }

    /**
     * 设置静音
     */
    @Override
    public void setMute(boolean isMute) {
        if (mKernel != null) {
            this.mIsMute = isMute;
            float volume = isMute ? 0.0f : 1.0f;
            mKernel.setVolume(volume, volume);
        }
    }

    /**
     * 是否处于静音状态
     */
    @Override
    public boolean isMute() {
        return mIsMute;
    }

    @Override
    public void onInfo(int what, @NonNull int extra, @NonNull long position, @NonNull long duration) {
        MediaLogUtil.log("onInfo => what = " + what + ", extra = " + extra + ", position = " + position);

        switch (what) {
            // loading-start
            case PlayerType.MediaType.MEDIA_INFO_BUFFERING_START:
                setPlayState(PlayerType.StateType.STATE_BUFFERING_PAUSED);
                break;
            // loading-end
            case PlayerType.MediaType.MEDIA_INFO_BUFFERING_END:
            case PlayerType.MediaType.MEDIA_INFO_VIDEO_RENDERING_START:
                setPlayState(PlayerType.StateType.STATE_BUFFERING_COMPLETE);
                break;
            case PlayerType.MediaType.MEDIA_INFO_OPEN_INPUT:
                // seekTo
                if (position > 0) {
                    setPlayState(PlayerType.StateType.STATE_BUFFERING_PAUSED);
                } else {
                    setPlayState(PlayerType.StateType.STATE_START);
                    View layout = getVideoLayout();
                    if (null != layout && layout.getWindowVisibility() != VISIBLE) {
                        pause();
                    }
                }
                break;
            //            // play-begin
//            case PlayerType.MediaType.MEDIA_INFO_VIDEO_RENDERING_START: // 视频开始渲染
////            case PlayerType.MediaType.MEDIA_INFO_AUDIO_RENDERING_START: // 视频开始渲染
//                if (position <= 10) {
//                    setPlayState(PlayerType.StateType.STATE_START);
//                    if (mVideoContainer.getWindowVisibility() != VISIBLE) {
//                        pause();
//                    }
//                }
//                break;
            case PlayerType.MediaType.MEDIA_INFO_VIDEO_SEEK_RENDERING_START: // 视频开始渲染
//            case PlayerType.MediaType.MEDIA_INFO_AUDIO_SEEK_RENDERING_START: // 视频开始渲染

//                if (position > 0) {
                setPlayState(PlayerType.StateType.STATE_START);
                View layout = getVideoLayout();
                if (null != layout && layout.getWindowVisibility() != VISIBLE) {
                    pause();
                }
//                }
                break;
            case PlayerType.MediaType.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                if (mRender != null)
                    mRender.setVideoRotation(extra);
                break;
        }
    }

    /**
     * 视频播放出错回调
     */
    @Override
    public void onError(@PlayerType.ErrorType.Value int type, String error) {
        MediaLogUtil.log("onError => type = " + type + ", error = " + error);

        setKeepScreenOn(false);
        boolean connected = PlayerUtils.isConnected(getContext());
        if (!connected) {
            setPlayState(PlayerType.StateType.STATE_ERROR_NETWORK);
        } else if (type == PlayerType.ErrorType.ERROR_RETRY) {
            // TODO: 2021/12/16
//            restart(false);
            setPlayState(PlayerType.StateType.STATE_ERROR);
        } else if (type == PlayerType.ErrorType.ERROR_UNEXPECTED) {
            setPlayState(PlayerType.StateType.STATE_ERROR);
        } else if (type == PlayerType.ErrorType.ERROR_PARSE) {
            setPlayState(PlayerType.StateType.STATE_ERROR_PARSE);
        } else if (type == PlayerType.ErrorType.ERROR_SOURCE) {
            setPlayState(PlayerType.StateType.STATE_ERROR);
        } else {
            setPlayState(PlayerType.StateType.STATE_ERROR);
        }

        PlayerConfig config = PlayerConfigManager.getInstance().getConfig();
        if (config != null && config.mBuriedPointEvent != null) {
            //相当于进入了视频页面
            if (PlayerUtils.isConnected(getContext().getApplicationContext())) {
                config.mBuriedPointEvent.onError(mUrl, false);
            } else {
                config.mBuriedPointEvent.onError(mUrl, true);
            }
        }
    }

    /**
     * 视频播放完成回调
     */
    @Override
    public void onCompletion() {
        setKeepScreenOn(false);
//        mCurrentPosition = 0;
        if (mProgressManager != null) {
            //播放完成，清除进度
            mProgressManager.saveProgress(mUrl, 0);
        }
        setPlayState(PlayerType.StateType.STATE_END);
        PlayerConfig config = PlayerConfigManager.getInstance().getConfig();
        if (config != null && config.mBuriedPointEvent != null) {
            //视频播放完成
            config.mBuriedPointEvent.playerCompletion(mUrl);
        }
    }

    /**
     * 视频缓冲完毕，准备开始播放时回调
     */
    @Override
    public void onPrepared(@NonNull long duration) {

        PlayerConfig config = PlayerConfigManager.getInstance().getConfig();
        if (config != null && config.mBuriedPointEvent != null) {
            //相当于进入了视频页面
            config.mBuriedPointEvent.playerIn(mUrl);
        }

        setPlayState(PlayerType.StateType.STATE_PREPARE_END);

        long position = getPosition();
        if (position > 0) {
            seekTo(position);
        }
    }

    /**
     * 获取当前播放器的状态
     */
    public int getWindowState() {
        return mCurrentPlayerState;
    }

    /**
     * 获取缓冲速度
     */
    @Override
    public long getTcpSpeed() {
        return mKernel != null ? mKernel.getTcpSpeed() : 0;
    }

    /**
     * 设置播放速度
     */
    @Override
    public void setSpeed(float speed) {
        if (isInPlaybackState()) {
            mKernel.setSpeed(speed);
        }
    }

    /**
     * 获取倍速速度
     *
     * @return 速度
     */
    @Override
    public float getSpeed() {
        if (isInPlaybackState()) {
            return mKernel.getSpeed();
        }
        return 1f;
    }

//    /**
//     * 设置视频地址
//     */
//    @Override
//    public void setUrl(@NonNull String url) {
//        setUrl(false, url, null);
//    }
//
//    /**
//     * 设置视频地址
//     */
//    @Override
//    public void setUrl(@NonNull boolean cache, @NonNull String url) {
//        setUrl(cache, url, null);
//    }
//
//    /**
//     * 获取视频地址
//     *
//     * @return
//     */
//    @Override
//    public String getUrl() {
//        return this.mUrl;
//    }
//
//    /**
//     * 设置包含请求头信息的视频地址
//     *
//     * @param url     视频地址
//     * @param headers 请求头
//     */
//    public void setUrl(@NonNull boolean cache, @NonNull String url, Map<String, String> headers) {
//        mAssetFileDescriptor = null;
//        mCache = cache;
//        mUrl = url;
//        mHeaders = headers;
//        PlayerConfig config = PlayerConfigManager.getInstance().getConfig();
//        if (config != null && config.mBuriedPointEvent != null) {
//            //相当于进入了视频页面
//            config.mBuriedPointEvent.playerIn(url);
//        }
//    }

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
    public void setVolume(float v1, float v2) {
        if (mKernel != null) {
            mKernel.setVolume(v1, v2);
        }
    }

    /**
     * 设置进度管理器，用于保存播放进度
     */
    public void setProgressManager(@Nullable OnMediaProgressManager progressManager) {
        this.mProgressManager = progressManager;
    }

    /**
     * 循环播放， 默认不循环播放
     */
    public void setLooping(boolean looping) {
        mIsLooping = looping;
        if (mKernel != null) {
            mKernel.setLooping(looping);
        }
    }

    /**
     * 自定义播放核心，继承{@link KernelFactory}实现自己的播放核心
     */
    public void setKernel(@PlayerType.PlatformType.Value int type) {

//        try {
//            PlayerConfigManager.getInstance().getConfig().mType(playerFactory);
//        }catch (Exception e){
//        }
    }

    public void setRender(@PlayerType.RenderType int type) {
//        if (renderViewFactory == null) {
//            throw new VideoException(VideoException.CODE_NOT_RENDER_FACTORY, "RenderViewFactory can not be null!");
//        }
//        mRenderFactory = renderViewFactory;
    }

    /**
     * 判断是否处于全屏状态
     */
    @Override
    public boolean isFullScreen() {
        return mIsFullScreen;
    }

    /**
     * 是否是小窗口模式
     *
     * @return 是否是小窗口模式
     */
    public boolean isTinyScreen() {
        return mIsTinyScreen;
    }

    @Override
    public void onVideoSizeChanged(int videoWidth, int videoHeight) {
        mVideoSize[0] = videoWidth;
        mVideoSize[1] = videoHeight;
        if (mRender != null) {
            mRender.setScaleType(mCurrentScreenScaleType);
            mRender.setVideoSize(videoWidth, videoHeight);
        }
    }


    /**
     * 设置视频比例
     */
    @Override
    public void setScaleType(int scaleType) {
        mCurrentScreenScaleType = scaleType;
        if (mRender != null) {
            mRender.setScaleType(scaleType);
        }
    }

    /**
     * 设置镜像旋转，暂不支持SurfaceView
     */
    @Override
    public void setMirrorRotation(boolean enable) {
        if (mRender != null) {
            mRender.getView().setScaleX(enable ? -1 : 1);
        }
    }

    /**
     * 截图，暂不支持SurfaceView
     */
    @Override
    public Bitmap doScreenShot() {
        if (mRender != null) {
            return mRender.doScreenShot();
        }
        return null;
    }

    public View getRenderView() {
        return mRender.getView();
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
        if (mRender != null) {
            mRender.setVideoRotation((int) rotation);
        }
    }

    /**
     * 获取当前的播放状态
     */
    @PlayerType.StateType.Value
    public int getPlayState() {
        try {
            return (int) getTag(R.id.module_mediaplayer_id_state_code);
        } catch (Exception e) {
            return PlayerType.StateType.STATE_INIT;
        }
    }

    /**
     * 向Controller设置播放状态，用于控制Controller的ui展示
     * 这里使用注解限定符，不要使用1，2这种直观数字，不方便知道意思
     * 播放状态，主要是指播放器的各种状态
     * -1               播放错误
     * 0                播放未开始
     * 1                播放准备中
     * 2                播放准备就绪
     * 3                正在播放
     * 4                暂停播放
     * 5                正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够后恢复播放)
     * 6                暂停缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停
     * 7                播放完成
     * 8                开始播放中止
     */
    protected void setPlayState(@PlayerType.StateType.Value int state) {
        setTag(R.id.module_mediaplayer_id_state_code, state);

        ControllerLayout layout = getControlLayout();
        if (null != layout) {
            layout.setPlayState(state);
        }
        if (mOnStateChangeListeners != null) {
            for (OnMediaStateListener l : PlayerUtils.getSnapshot(mOnStateChangeListeners)) {
                if (l != null) {
                    l.onPlayStateChanged(state);
                }
            }
        }
    }

    /**
     * 向Controller设置播放器状态，包含全屏状态和非全屏状态
     * 播放模式
     * 普通模式，小窗口模式，正常模式三种其中一种
     * MODE_NORMAL              普通模式
     * MODE_FULL_SCREEN         全屏模式
     * MODE_TINY_WINDOW         小屏模式
     */
    protected void setWindowState(@PlayerType.WindowType.Value int windowState) {
        mCurrentPlayerState = windowState;

        ControllerLayout layout = getControlLayout();
        if (null != layout) {
            layout.setWindowState(windowState);
        }
        if (mOnStateChangeListeners != null) {
            for (OnMediaStateListener l : PlayerUtils.getSnapshot(mOnStateChangeListeners)) {
                if (l != null) {
                    l.onWindowStateChanged(windowState);
                }
            }
        }
    }

    /**
     * 添加一个播放状态监听器，播放状态发生变化时将会调用。
     */
    public void addOnStateChangeListener(@NonNull OnMediaStateListener listener) {
        if (mOnStateChangeListeners == null) {
            mOnStateChangeListeners = new ArrayList<>();
        }
        mOnStateChangeListeners.add(listener);
    }

    /**
     * 移除某个播放状态监听
     */
    public void removeOnStateChangeListener(@NonNull OnMediaStateListener listener) {
        if (mOnStateChangeListeners != null) {
            mOnStateChangeListeners.remove(listener);
        }
    }

    /**
     * 设置一个播放状态监听器，播放状态发生变化时将会调用，
     * 如果你想同时设置多个监听器，推荐 {@link #addOnStateChangeListener(OnMediaStateListener)}。
     */
    public void setOnMediaStateListener(@NonNull OnMediaStateListener listener) {
        if (mOnStateChangeListeners == null) {
            mOnStateChangeListeners = new ArrayList<>();
        }

        clearOnMediaStateListener();
        mOnStateChangeListeners.add(listener);
    }

    /**
     * 移除所有播放状态监听
     */
    public void clearOnMediaStateListener() {
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


    /*-----------------------------暴露api方法--------------------------------------**/
    /*-----------------------------暴露api方法--------------------------------------**/


    public void setVideoBuilder(VideoBuilder videoBuilder) {
//        if (mVideoContainer == null || videoBuilder == null) {
//            return;
//        }
//        //设置视频播放器的背景色
//        mVideoContainer.setBackgroundColor(videoBuilder.mColor);
//        //设置小屏的宽高
//        if (videoBuilder.mTinyScreenSize != null && videoBuilder.mTinyScreenSize.length > 0) {
//            mTinyScreenSize = videoBuilder.mTinyScreenSize;
//        }
//        //一开始播放就seek到预先设置好的位置
//        if (videoBuilder.mCurrentPosition > 0) {
//            this.mCurrentPosition = videoBuilder.mCurrentPosition;
//        }
    }

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        MediaLogUtil.log("dispatchKeyEvent => " + event.getKeyCode());
//        if (null != mControlLayout) {
//            mControlLayout.dispatchKeyEvent(event);
//        }
//        return super.dispatchKeyEvent(event);
//    }

    /*************************/

    /************************/

    /**
     * 设置控制器，传null表示移除控制器
     *
     * @param layout
     */
    public void setControllerLayout(@Nullable ControllerLayout layout) {
        ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_control);
        viewGroup.removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(params);
        viewGroup.addView(layout);
        layout.setMediaPlayer(this);
    }

    public void clearControllerLayout() {
        ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_control);
        viewGroup.removeAllViews();
    }

    @Override
    public ControllerLayout getControlLayout() {
        try {
            ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_control);
            return (ControllerLayout) viewGroup.getChildAt(0);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public View getVideoLayout() {
        try {
            ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_video);
            return viewGroup.getChildAt(0);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void initRender() {
        // 视频播放器
        if (mRender != null) {
            mRender.release();
        }
        mRender = RenderFactoryManager.getRender(getContext(), PlayerConfigManager.getInstance().getConfig().mRender);
        mRender.attachToPlayer(mKernel);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mRender.getView().setLayoutParams(params);
        setVideoLayout(mRender.getView());
    }

    private final void setVideoLayout(@NonNull View view) {
        clearVideoLayout();
        try {
            ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_video);
            viewGroup.addView(view);
        } catch (Exception e) {
        }
    }

    private final void clearVideoLayout() {
        try {
            ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_video);
            viewGroup.removeAllViews();
        } catch (Exception e) {
        }
    }

//    @Override
//    public void resetKernel() {
//        try {
//            mKernel.resetKernel();
//        } catch (Exception e) {
//        }
//    }

    @Override
    public void initKernel() {
        if (null != mKernel) {
            releaseKernel();
        }
        mKernel = KernelFactoryManager.getKernel(getContext(), PlayerConfigManager.getInstance().getConfig().mType);
        mKernel.setOnVideoPlayerChangeListener(this);
        mKernel.initKernel(getContext());
        mKernel.setLooping(mIsLooping);
    }

    @Override
    public void releaseKernel() {
        try {
            ControllerLayout layout = getControlLayout();
            layout.destroy();
        } catch (Exception e) {
        }
        try {
            clearVideoLayout();
        } catch (Exception e) {
        }
        try {
            mRender.release();
            mRender = null;
        } catch (Exception e) {
        }
        try {
            mKernel.releaseKernel();
            mKernel = null;
        } catch (Exception e) {
        }

        try {
            if (!isInit()) {
                PlayerConfig config = PlayerConfigManager.getInstance().getConfig();
                if (config != null && config.mBuriedPointEvent != null) {
                    //退出视频播放
                    config.mBuriedPointEvent.playerDestroy(mUrl);

                    //计算退出视频时候的进度
                    long duration = getDuration();
                    long position = getPosition();
                    float progress = (position * 1.0f) / (duration * 1.0f);
                    config.mBuriedPointEvent.playerOutProgress(mUrl, progress);
                    config.mBuriedPointEvent.playerOutProgress(mUrl, duration, position);
                }

                //释放Assets资源
                if (mAssetFileDescriptor != null) {
                    try {
                        mAssetFileDescriptor.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //关闭屏幕常亮
                setKeepScreenOn(false);
                //保存播放进度
                saveProgress();
                //重置播放进度
//            mCurrentPosition = 0;
                //切换转态
                setPlayState(PlayerType.StateType.STATE_CLEAN);
                setPlayState(PlayerType.StateType.STATE_INIT);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 开启小屏
     */
    public void startTinyScreen() {
//        if (mIsTinyScreen) {
//            return;
//        }
//        ViewGroup contentView = VideoHelper.instance().getContentView(getContext().getApplicationContext(), mControlLayout);
//        if (contentView == null) {
//            return;
//        }
//        this.removeView(mVideoContainer);
//        int width = mTinyScreenSize[0];
//        if (width <= 0) {
//            width = PlayerUtils.getScreenWidth(getContext(), false) / 2;
//        }
//        int height = mTinyScreenSize[1];
//        if (height <= 0) {
//            height = width * 9 / 16;
//        }
//        LayoutParams params = new LayoutParams(width, height);
//        params.gravity = Gravity.BOTTOM | Gravity.END;
//        contentView.addView(mVideoContainer, params);
//        mIsTinyScreen = true;
//        setWindowState(PlayerType.WindowType.TINY);
    }

    /**
     * 退出小屏
     */
    public void stopTinyScreen() {
//        if (!mIsTinyScreen) {
//            return;
//        }
//        ViewGroup contentView = VideoHelper.instance().getContentView(getContext().getApplicationContext(), mControlLayout);
//        if (contentView == null) {
//            return;
//        }
//        contentView.removeView(mVideoContainer);
//        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        this.addView(mVideoContainer, params);
//        mIsTinyScreen = false;
//        setWindowState(PlayerType.WindowType.NORMAL);
    }

    /**
     * 退出全屏
     */
    @Override
    public void stopFullScreen() {
//        if (!mIsFullScreen) {
//            return;
//        }
//        ViewGroup decorView = VideoHelper.instance().getDecorView(getContext().getApplicationContext(), mControlLayout);
//        if (decorView == null) {
//            return;
//        }
//        mIsFullScreen = false;
//        //显示NavigationBar和StatusBar
//        VideoHelper.instance().showSysBar(decorView, getContext().getApplicationContext(), mControlLayout);
//
//        //把播放器视图从DecorView中移除并添加到当前FrameLayout中即退出了全屏
//        decorView.removeView(mVideoContainer);
//        this.addView(mVideoContainer);
//        setWindowState(PlayerType.WindowType.NORMAL);
    }

    /**
     * 进入全屏
     */
    @Override
    public void startFullScreen() {
//        if (mIsFullScreen) {
//            return;
//        }
//
//        try {
//            ControllerLayout layout = findViewById(R.id.module_mediaplayer_id_control_layout);
//            ViewGroup decorView = VideoHelper.instance().getDecorView(getContext().getApplicationContext(), layout);
//            mIsFullScreen = true;
//            //隐藏NavigationBar和StatusBar
//            VideoHelper.instance().hideSysBar(decorView, getContext().getApplicationContext(), layout);
//            //从当前FrameLayout中移除播放器视图
//            this.removeView(mVideoContainer);
//            //将播放器视图添加到DecorView中即实现了全屏
//            decorView.addView(mVideoContainer);
//            setWindowState(PlayerType.WindowType.FULL);
//        } catch (Exception e) {
//        }
    }

    /******************/

//    @Override
//    public void addView(View child, int index, ViewGroup.LayoutParams params) {
//    }
//
//    @Override
//    public void addView(View child, int index) {
//    }
//
//    @Override
//    public void addView(View child) {
//    }
//
//    @Override
//    public void addView(View child, int width, int height) {
//    }
//
//    @Override
//    public void addView(View child, ViewGroup.LayoutParams params) {
//    }

    /******************/
}
