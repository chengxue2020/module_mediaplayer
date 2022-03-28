package lib.kalu.mediaplayer.core.player;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.core.kernel.video.factory.PlayerFactory;
import lib.kalu.mediaplayer.core.kernel.video.impl.VideoPlayerImpl;
import lib.kalu.mediaplayer.core.kernel.video.listener.OnVideoPlayerChangeListener;
import lib.kalu.mediaplayer.listener.OnMediaStateListener;
import lib.kalu.mediaplayer.config.PlayerConfig;
import lib.kalu.mediaplayer.config.PlayerConfigManager;
import lib.kalu.mediaplayer.config.PlayerType;
import lib.kalu.mediaplayer.core.controller.base.ControllerLayout;
import lib.kalu.mediaplayer.core.player.impl.ImplPlayer;
import lib.kalu.mediaplayer.listener.OnMediaProgressManager;
import lib.kalu.mediaplayer.widget.surface.InterSurfaceView;
import lib.kalu.mediaplayer.widget.surface.SurfaceFactory;
import lib.kalu.mediaplayer.util.BaseToast;
import lib.kalu.mediaplayer.util.PlayerUtils;
import lib.kalu.mediaplayer.util.VideoException;
import lib.kalu.mediaplayer.util.MediaLogUtil;

/**
 * @description: 播放器具体实现类
 * @date: 2021-05-11 14:46
 */
@Keep
public class VideoLayout<P extends VideoPlayerImpl> extends FrameLayout implements ImplPlayer, OnVideoPlayerChangeListener {

    /**
     * 当前播放视频的地址
     */
    private String mUrl = null;
    /**
     * 直播
     */
    protected boolean mLive = false;
    /**
     * 当前视频地址的请求头
     */
    protected Map<String, String> mHeaders = null;

    /**
     * 播放器
     */
    protected P mMediaPlayer;
    /**
     * 实例化播放核心
     */
    protected PlayerFactory<P> mPlayerFactory;
    /**
     * 控制器
     */
    @Nullable
    protected ControllerLayout mVideoController;
    /**
     * 真正承载播放器视图的容器
     */
    protected FrameLayout mPlayerContainer;

    protected InterSurfaceView mRenderView;
    protected SurfaceFactory mRenderViewFactory;
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
     * 当前正在播放视频的位置
     */
    protected long mCurrentPosition;
//    /**
//     * 当前播放器的状态
//     * 比如：错误，开始播放，暂停播放，缓存中等等状态
//     */
//    protected int mCurrentPlayState = PlayerType.StateType.STATE_INIT;
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

    /**
     * {@link #mPlayerContainer}背景色，默认黑色
     */
    private int mPlayerBackgroundColor;

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

    private void init(AttributeSet attrs) {
        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        BaseToast.init(getContext().getApplicationContext());
        //读取全局配置
        initConfig();
        //读取xml中的配置，并综合全局配置
        initAttrs(attrs);
        initView();
    }

    private void initConfig() {
        PlayerConfig config = PlayerConfigManager.getInstance().getConfig();
        mProgressManager = config.mProgressManager;
        mPlayerFactory = config.mPlayerFactory;
        mCurrentScreenScaleType = config.mScreenScaleType;
        mRenderViewFactory = config.mRenderViewFactory;
        //设置是否打印日志
        MediaLogUtil.setIsLog(config.mIsEnableLog);
    }

    /**
     * onAttachedToWindow方法
     * 是在Activity resume的时候被调用的，也就是activity对应的window被添加的时候，且每个view只会被调用一次，
     * 父view的调用在前，不论view的visibility状态都会被调用，适合做些view特定的初始化操作；
     * <p>
     * 主要做什么：适合初始化操作
     * <p>
     * 代码流程：
     * ActivityThread.handleResumeActivity()--->WindowManager.addView()--->WindowManagerImpl.addView()
     * -->WindowManagerGlobal.addView()--->root.setView(view, wparams, panelParentView)
     * --->host.dispatchAttachedToWindow()[具体代码在ViewRootImpl类中]
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        MediaLogUtil.log("onAttachedToWindow");
        //init();
        //在构造函数初始化时addView
    }

    /**
     * onDetachedFromWindow方法
     * 是在Activity destroy的时候被调用的，也就是activity对应的window被删除的时候，且每个view只会被调用一次，
     * 父view的调用在后，也不论view的visibility状态都会被调用，适合做最后的清理操作；
     * <p>
     * 主要做什么：适合销毁清理操作
     * <p>
     * 代码流程：
     * ActivityThread.handleDestroyActivity() --> WindowManager.removeViewImmediate() -->
     * WindowManagerGlobal.removeViewLocked()方法 —> ViewRootImpl.die() --> doDie() -->
     * ViewRootImpl.dispatchDetachedFromWindow()
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        MediaLogUtil.log("onDetachedFromWindow");
        if (mVideoController != null) {
            mVideoController.destroy();
        }
        //onDetachedFromWindow方法是在Activity destroy的时候被调用的，也就是act对应的window被删除的时候，
        //且每个view只会被调用一次，父view的调用在后，也不论view的visibility状态都会被调用，适合做最后的清理操作
        //防止开发者没有在onDestroy中没有做销毁视频的优化
        release();
    }

    /**
     * View所在窗口获取焦点或者失去焦点时调用
     *
     * @param hasWindowFocus 是否获取window焦点
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus && mIsFullScreen) {
            //重新获得焦点时保持全屏状态
            ViewGroup decorView = VideoHelper.instance().getDecorView(getContext().getApplicationContext(), mVideoController);
            VideoHelper.instance().hideSysBar(decorView, getContext().getApplicationContext(), mVideoController);
        }
    }

    /**
     * View在xml文件里加载完成时调用
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    /**
     * 意外销毁保存数据调用
     *
     * @return 返回Parcelable对象
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        MediaLogUtil.log("onSaveInstanceState: " + mCurrentPosition);
        //activity切到后台后可能被系统回收，故在此处进行进度保存
        saveProgress();
        return super.onSaveInstanceState();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray a = getContext().getApplicationContext().obtainStyledAttributes(attrs, R.styleable.VideoPlayer);
        mIsLooping = a.getBoolean(R.styleable.VideoPlayer_looping, false);
        mCurrentScreenScaleType = a.getInt(R.styleable.VideoPlayer_screenScaleType, mCurrentScreenScaleType);
        mPlayerBackgroundColor = a.getColor(R.styleable.VideoPlayer_playerBackgroundColor, Color.BLACK);
        a.recycle();
    }

    /**
     * 初始化播放器视图
     */
    protected void initView() {
        mPlayerContainer = new FrameLayout(getContext());
        //设置背景颜色，目前设置为纯黑色
        mPlayerContainer.setBackgroundColor(mPlayerBackgroundColor);
        //将布局添加到该视图中
        this.addView(mPlayerContainer, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 设置控制器，传null表示移除控制器
     *
     * @param mediaController controller
     */
    public void setController(@Nullable ControllerLayout mediaController) {
        mPlayerContainer.removeView(mVideoController);
        mVideoController = mediaController;
        if (mediaController != null) {
            mediaController.setMediaPlayer(this);
            mPlayerContainer.addView(mVideoController, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    /**
     * 设置控制器，传null表示移除控制器
     */
    public void removeController() {
        mPlayerContainer.removeView(mVideoController);
        mVideoController = null;
    }

    /**
     * 开始播放，注意：调用此方法后必须调用{@link #release()}释放播放器，否则会导致内存泄漏
     */
    /**
     * public void setUrl(@NonNull boolean cache, @NonNull String url, Map<String, String> headers) {
     * //        mAssetFileDescriptor = null;
     * //        mCache = cache;
     * //        mUrl = url;
     * //        mHeaders = headers;
     * //        PlayerConfig config = PlayerConfigManager.getInstance().getConfig();
     * //        if (config != null && config.mBuriedPointEvent != null) {
     * //            //相当于进入了视频页面
     * //            config.mBuriedPointEvent.playerIn(url);
     * //        }
     * //    }
     *
     * @param live
     * @param url
     */
    @Override
    public void start(@NonNull long seekPosition, @NonNull boolean live, @NonNull String url, @NonNull String subtitle, @NonNull Map<String, String> headers) {
//        if (mVideoController == null) {
//            //在调用start方法前，请先初始化视频控制器，调用setController方法
//            throw new VideoException(VideoException.CODE_NOT_SET_CONTROLLER, "Controller must not be null , please setController first");
//        }

//        clearOnStateChangeListeners();
//        setOnStateChangeListener(listener);

        // release
//        if (null != mUrl && mUrl.length() > 0) {
        release();
        clearUrl();
//        }

        boolean isStarted = false;
        if (isInit() || isInStartAbortState()) {
            isStarted = startPlay(seekPosition, live, url, subtitle, headers);
        } else if (isInPlaybackState()) {
            startInPlaybackState();
            isStarted = true;
        }
        if (isStarted) {
            mPlayerContainer.setKeepScreenOn(true);
        }
    }

    @Override
    public void restart(@NonNull boolean reset) {
        if (null == mUrl || mUrl.length() <= 0)
            return;
        if (reset) {
            mCurrentPosition = 0;
        }
        if (reset) {
            initCanvas();
        }
        startPrepare(0, reset);
        mPlayerContainer.setKeepScreenOn(true);
    }

    /**
     * 第一次播放
     *
     * @return 是否成功开始播放
     */
    protected boolean startPlay(@NonNull long seekPosition, @NonNull boolean live, @NonNull String url, @NonNull String subtitle, @NonNull Map<String, String> headers) {
        //如果要显示移动网络提示则不继续播放

        // 中止播放
        boolean showNetWarning = showNetWarning();
        if (showNetWarning) {
            setPlayState(PlayerType.StateType.STATE_START_ABORT);
            return false;
        }

        //读取播放进度
        if (mProgressManager != null) {
            mCurrentPosition = mProgressManager.getSavedProgress(mUrl);
        }

        // fix bug
        release();

        // update
        updateUrl(live, url, subtitle, headers);

        initPlayer();
        initCanvas();
        startPrepare(seekPosition, true);
        return true;
    }

    /**
     * 初始化播放器
     */
    protected void initPlayer() {
        //通过工厂模式创建对象
        mMediaPlayer = mPlayerFactory.createPlayer(getContext());
        mMediaPlayer.setOnVideoPlayerChangeListener(this);
        setInitOptions();
        mMediaPlayer.initPlayer(getContext(), mUrl);
        setOptions();
    }

    /**
     * 是否显示移动网络提示，可在Controller中配置
     */
    public boolean showNetWarning() {
        //播放本地数据源时不检测网络
        if (VideoHelper.instance().isLocalDataSource(mUrl, mAssetFileDescriptor)) {
            return false;
        }
        return mVideoController != null && mVideoController.showNetWarning();
    }


    /**
     * 初始化之前的配置项
     */
    protected void setInitOptions() {

    }

    /**
     * 初始化之后的配置项
     */
    protected void setOptions() {
        //设置是否循环播放
        mMediaPlayer.setLooping(mIsLooping);
    }

    /**
     * 初始化视频渲染View
     */
    protected void initCanvas() {
        if (mRenderView != null) {
            //从容器中移除渲染view
            mPlayerContainer.removeView(mRenderView.getView());
            //释放资源
            mRenderView.release();
        }
        //创建TextureView对象
        mRenderView = mRenderViewFactory.createRenderView(getContext().getApplicationContext());
        //绑定mMediaPlayer对象
        mRenderView.attachToPlayer(mMediaPlayer);
        //添加渲染view到Container布局中
        mPlayerContainer.addView(mRenderView.getView(), 0, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER));
    }

    /**
     * 开始准备播放（直接播放）
     *
     * @param reset
     */
    protected void startPrepare(@NonNull long seekPosition, @NonNull boolean reset) {
        MediaLogUtil.log("startPrepare => seekPosition = " + seekPosition + ", reset = " + reset);

        if (reset) {
            mMediaPlayer.reset();
            //重新设置option，media player reset之后，option会失效
            setOptions();
        }

        if (null == mUrl || mUrl.length() <= 0) {
            //更改播放器的播放状态
            setPlayState(PlayerType.StateType.STATE_PREPARE_START);
            // 播放状态
            setPlayState(PlayerType.StateType.STATE_ERROR_URL);
        }
        // 重置
        else if (reset) {
            //更改播放器的播放状态
            setPlayState(PlayerType.StateType.STATE_PREPARE_START);
            mMediaPlayer.prepareAsync(getContext(), mLive, mUrl, mHeaders);
            //准备开始播放
            if (seekPosition < 0) {
                seekPosition = 0;
            }
            mCurrentPosition = seekPosition;
            //更改播放器播放模式状态
            setWindowState(isFullScreen() ? PlayerType.WindowType.FULL : isTinyScreen() ? PlayerType.WindowType.TINY : PlayerType.WindowType.NORMAL);
        }
        // 重试
        else {
            //准备开始播放
            mMediaPlayer.prepareAsync(getContext(), mLive, mUrl, mHeaders);
        }
    }

    /**
     * 播放状态下开始播放
     */
    protected void startInPlaybackState() {
        mMediaPlayer.start();
        setPlayState(PlayerType.StateType.STATE_START);
    }

    /**
     * 暂停播放
     */
    @Override
    public void pause() {

        if (null == mUrl || mUrl.length() <= 0)
            return;

        if (isInPlaybackState() && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            setPlayState(PlayerType.StateType.STATE_PAUSED);
            mPlayerContainer.setKeepScreenOn(false);
        }
    }

    /**
     * 继续播放
     */
    public void resume() {

        if (null == mUrl || mUrl.length() <= 0)
            return;

        if (isInPlaybackState() && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            setPlayState(PlayerType.StateType.STATE_START);
            mPlayerContainer.setKeepScreenOn(true);
        }
    }

    /**
     * 释放播放器
     */
    public void release() {
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
                config.mBuriedPointEvent.playerOutProgress(mUrl, duration, mCurrentPosition);
            }
            //释放播放器
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            //释放renderView
            if (mRenderView != null) {
                mPlayerContainer.removeView(mRenderView.getView());
                mRenderView.release();
                mRenderView = null;
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
            mPlayerContainer.setKeepScreenOn(false);
            //保存播放进度
            saveProgress();
            //重置播放进度
            mCurrentPosition = 0;
            //切换转态
            setPlayState(PlayerType.StateType.STATE_CLEAN);
            setPlayState(PlayerType.StateType.STATE_INIT);
        }
    }

    /**
     * 保存播放进度
     */
    protected void saveProgress() {
        if (mProgressManager != null && mCurrentPosition > 0) {
            MediaLogUtil.log("saveProgress: " + mCurrentPosition);
            mProgressManager.saveProgress(mUrl, mCurrentPosition);
        }
    }

    /**
     * 是否处于播放状态
     */
    protected boolean isInPlaybackState() {

        if (null == mMediaPlayer)
            return false;

        int state = getPlayState();
        return mMediaPlayer != null
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
    public long getDuration() {
        if (isInPlaybackState()) {
            long duration = mMediaPlayer.getDuration();
            if (duration < 0) {
                duration = 0;
            }
            return duration;
        }
        return 0;
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getPosition() {
        if (isInPlaybackState()) {
            long position = mMediaPlayer.getCurrentPosition();
            if (position < 0) {
                position = 0;
            }
            return position;
        }
        return 0;
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
            mMediaPlayer.seekTo(seek);
        }
    }

    /**
     * 是否处于播放状态
     */
    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    /**
     * 获取当前缓冲百分比
     */
    @Override
    public int getBufferedPercentage() {
        return mMediaPlayer != null ? mMediaPlayer.getBufferedPercentage() : 0;
    }

    /**
     * 设置静音
     */
    @Override
    public void setMute(boolean isMute) {
        if (mMediaPlayer != null) {
            this.mIsMute = isMute;
            float volume = isMute ? 0.0f : 1.0f;
            mMediaPlayer.setVolume(volume, volume);
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
                    if (mPlayerContainer.getWindowVisibility() != VISIBLE) {
                        pause();
                    }
                }
                break;
            //            // play-begin
//            case PlayerType.MediaType.MEDIA_INFO_VIDEO_RENDERING_START: // 视频开始渲染
////            case PlayerType.MediaType.MEDIA_INFO_AUDIO_RENDERING_START: // 视频开始渲染
//                if (position <= 10) {
//                    setPlayState(PlayerType.StateType.STATE_START);
//                    if (mPlayerContainer.getWindowVisibility() != VISIBLE) {
//                        pause();
//                    }
//                }
//                break;
            case PlayerType.MediaType.MEDIA_INFO_VIDEO_SEEK_RENDERING_START: // 视频开始渲染
//            case PlayerType.MediaType.MEDIA_INFO_AUDIO_SEEK_RENDERING_START: // 视频开始渲染

//                if (position > 0) {
                setPlayState(PlayerType.StateType.STATE_START);
                if (mPlayerContainer.getWindowVisibility() != VISIBLE) {
                    pause();
                }
//                }
                break;
            case PlayerType.MediaType.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                if (mRenderView != null)
                    mRenderView.setVideoRotation(extra);
                break;
        }
    }

    /**
     * 视频播放出错回调
     */
    @Override
    public void onError(@PlayerType.ErrorType.Value int type, String error) {
        MediaLogUtil.log("onError => type = " + type + ", error = " + error);

        mPlayerContainer.setKeepScreenOn(false);
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
        mPlayerContainer.setKeepScreenOn(false);
        mCurrentPosition = 0;
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
        setPlayState(PlayerType.StateType.STATE_PREPARE_END);
        if (mCurrentPosition > 0) {
            seekTo(mCurrentPosition);
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
        return mMediaPlayer != null ? mMediaPlayer.getTcpSpeed() : 0;
    }

    /**
     * 设置播放速度
     */
    @Override
    public void setSpeed(float speed) {
        if (isInPlaybackState()) {
            mMediaPlayer.setSpeed(speed);
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
            return mMediaPlayer.getSpeed();
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
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(v1, v2);
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
        if (mMediaPlayer != null) {
            mMediaPlayer.setLooping(looping);
        }
    }

    /**
     * 自定义播放核心，继承{@link PlayerFactory}实现自己的播放核心
     */
    public void setPlayerFactory(PlayerFactory<P> playerFactory) {
        if (playerFactory == null) {
            throw new VideoException(VideoException.CODE_NOT_PLAYER_FACTORY, "PlayerFactory can not be null!");
        }
        mPlayerFactory = playerFactory;
    }

    /**
     * 自定义RenderView，继承{@link SurfaceFactory}实现自己的RenderView
     */
    public void setRenderViewFactory(SurfaceFactory renderViewFactory) {
        if (renderViewFactory == null) {
            throw new VideoException(VideoException.CODE_NOT_RENDER_FACTORY, "RenderViewFactory can not be null!");
        }
        mRenderViewFactory = renderViewFactory;
    }

    /**
     * 进入全屏
     */
    @Override
    public void startFullScreen() {
        if (mIsFullScreen) {
            return;
        }
        ViewGroup decorView = VideoHelper.instance().getDecorView(getContext().getApplicationContext(), mVideoController);
        if (decorView == null) {
            return;
        }
        mIsFullScreen = true;
        //隐藏NavigationBar和StatusBar
        VideoHelper.instance().hideSysBar(decorView, getContext().getApplicationContext(), mVideoController);
        //从当前FrameLayout中移除播放器视图
        this.removeView(mPlayerContainer);
        //将播放器视图添加到DecorView中即实现了全屏
        decorView.addView(mPlayerContainer);
        setWindowState(PlayerType.WindowType.FULL);
    }

    /**
     * 退出全屏
     */
    @Override
    public void stopFullScreen() {
        if (!mIsFullScreen) {
            return;
        }
        ViewGroup decorView = VideoHelper.instance().getDecorView(getContext().getApplicationContext(), mVideoController);
        if (decorView == null) {
            return;
        }
        mIsFullScreen = false;
        //显示NavigationBar和StatusBar
        VideoHelper.instance().showSysBar(decorView, getContext().getApplicationContext(), mVideoController);

        //把播放器视图从DecorView中移除并添加到当前FrameLayout中即退出了全屏
        decorView.removeView(mPlayerContainer);
        this.addView(mPlayerContainer);
        setWindowState(PlayerType.WindowType.NORMAL);
    }


    /**
     * 判断是否处于全屏状态
     */
    @Override
    public boolean isFullScreen() {
        return mIsFullScreen;
    }

    /**
     * 开启小屏
     */
    public void startTinyScreen() {
        if (mIsTinyScreen) {
            return;
        }
        ViewGroup contentView = VideoHelper.instance().getContentView(getContext().getApplicationContext(), mVideoController);
        if (contentView == null) {
            return;
        }
        this.removeView(mPlayerContainer);
        int width = mTinyScreenSize[0];
        if (width <= 0) {
            width = PlayerUtils.getScreenWidth(getContext(), false) / 2;
        }
        int height = mTinyScreenSize[1];
        if (height <= 0) {
            height = width * 9 / 16;
        }
        LayoutParams params = new LayoutParams(width, height);
        params.gravity = Gravity.BOTTOM | Gravity.END;
        contentView.addView(mPlayerContainer, params);
        mIsTinyScreen = true;
        setWindowState(PlayerType.WindowType.TINY);
    }

    /**
     * 退出小屏
     */
    public void stopTinyScreen() {
        if (!mIsTinyScreen) {
            return;
        }
        ViewGroup contentView = VideoHelper.instance().getContentView(getContext().getApplicationContext(), mVideoController);
        if (contentView == null) {
            return;
        }
        contentView.removeView(mPlayerContainer);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mPlayerContainer, params);
        mIsTinyScreen = false;
        setWindowState(PlayerType.WindowType.NORMAL);
    }

    /**
     * 是否是小窗口模式
     *
     * @return 是否是小窗口模式
     */
    public boolean isTinyScreen() {
        return mIsTinyScreen;
    }

    public ControllerLayout getVideoController() {
        return mVideoController;
    }

    @Override
    public void onVideoSizeChanged(int videoWidth, int videoHeight) {
        mVideoSize[0] = videoWidth;
        mVideoSize[1] = videoHeight;
        if (mRenderView != null) {
            mRenderView.setScaleType(mCurrentScreenScaleType);
            mRenderView.setVideoSize(videoWidth, videoHeight);
        }
    }

    /**
     * 设置视频比例
     */
    @Override
    public void setScreenScaleType(@PlayerType.ScaleType.Value int screenScaleType) {
        mCurrentScreenScaleType = screenScaleType;
        if (mRenderView != null) {
            mRenderView.setScaleType(screenScaleType);
        }
    }

    /**
     * 设置镜像旋转，暂不支持SurfaceView
     */
    @Override
    public void setMirrorRotation(boolean enable) {
        if (mRenderView != null) {
            mRenderView.getView().setScaleX(enable ? -1 : 1);
        }
    }

    /**
     * 截图，暂不支持SurfaceView
     */
    @Override
    public Bitmap doScreenShot() {
        if (mRenderView != null) {
            return mRenderView.doScreenShot();
        }
        return null;
    }

    public View getRenderView() {
        return mRenderView.getView();
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
        if (mRenderView != null) {
            mRenderView.setVideoRotation((int) rotation);
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
        if (mVideoController != null) {
            mVideoController.setPlayState(state);
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
        if (mVideoController != null) {
            mVideoController.setWindowState(windowState);
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
        return mVideoController != null && mVideoController.onBackPressed();
    }


    /*-----------------------------暴露api方法--------------------------------------**/
    /*-----------------------------暴露api方法--------------------------------------**/


    public void setVideoBuilder(VideoBuilder videoBuilder) {
        if (mPlayerContainer == null || videoBuilder == null) {
            return;
        }
        //设置视频播放器的背景色
        mPlayerContainer.setBackgroundColor(videoBuilder.mColor);
        //设置小屏的宽高
        if (videoBuilder.mTinyScreenSize != null && videoBuilder.mTinyScreenSize.length > 0) {
            mTinyScreenSize = videoBuilder.mTinyScreenSize;
        }
        //一开始播放就seek到预先设置好的位置
        if (videoBuilder.mCurrentPosition > 0) {
            this.mCurrentPosition = videoBuilder.mCurrentPosition;
        }
    }

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        MediaLogUtil.log("dispatchKeyEvent => " + event.getKeyCode());
//        if (null != mVideoController) {
//            mVideoController.dispatchKeyEvent(event);
//        }
//        return super.dispatchKeyEvent(event);
//    }

    /*************************/

    private final void updateUrl(@NonNull boolean live, @NonNull String url, @NonNull String subtitle, @NonNull Map<String, String> headers) {
        mAssetFileDescriptor = null;
        mUrl = url;
        mLive = live;
        mHeaders = headers;

        PlayerConfig config = PlayerConfigManager.getInstance().getConfig();
        if (config != null && config.mBuriedPointEvent != null) {
            //相当于进入了视频页面
            config.mBuriedPointEvent.playerIn(url);
        }
    }

    private final void clearUrl() {
        mAssetFileDescriptor = null;
        mUrl = null;
        mLive = false;
        mHeaders = null;
    }
}
