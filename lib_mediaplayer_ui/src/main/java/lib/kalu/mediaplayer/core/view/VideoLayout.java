package lib.kalu.mediaplayer.core.view;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.core.kernel.KernelEvent;
import lib.kalu.mediaplayer.core.kernel.KernelFactory;
import lib.kalu.mediaplayer.core.kernel.KernelFactoryManager;
import lib.kalu.mediaplayer.core.kernel.KernelApi;
import lib.kalu.mediaplayer.core.render.RenderApi;
import lib.kalu.mediaplayer.core.render.RenderFactoryManager;
import lib.kalu.mediaplayer.listener.OnChangeListener;
import lib.kalu.mediaplayer.config.player.PlayerConfig;
import lib.kalu.mediaplayer.config.player.PlayerConfigManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.controller.base.ControllerLayout;
import lib.kalu.mediaplayer.util.BaseToast;
import lib.kalu.mediaplayer.util.PlayerUtils;
import lib.kalu.mediaplayer.util.MediaLogUtil;

/**
 * @description: 播放器具体实现类
 */
@Keep
public class VideoLayout extends RelativeLayout implements PlayerApi, Handler.Callback {

//    private String mUrl = null;
//    protected Map<String, String> mHeaders = null;

    // 状态
    @PlayerType.StateType.Value
    private int mStateType = PlayerType.StateType.STATE_INIT;
    // 解码
    protected KernelApi mKernel;
    // 渲染
    protected RenderApi mRender;

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
    protected List<OnChangeListener> mOnStateChangeListeners;

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

//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        init();
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        release();
//    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
//        String url = getUrl();
//        if (visibility == View.VISIBLE) {
//            if (null != url && url.length() > 0) {
//                repeat();
//            }
//        } else {
//            if (null != url && url.length() > 0) {
//                stop();
//            }
//        }
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
    public void start(@NonNull long seek, @NonNull long maxLength, @NonNull int maxNum, @NonNull String url) {
        try {

            stop();
            setPlayState(PlayerType.StateType.STATE_LOADING_START);
            create();

            //如果要显示移动网络提示则不继续播放
            boolean showNetWarning = showNetWarning();
            if (showNetWarning) {
                setPlayState(PlayerType.StateType.STATE_START_ABORT);
            }
            if (null != mKernel) {
                mKernel.stop();
                mKernel.create(getContext(), seek, maxLength, maxNum, url);
            }
            setKeepScreenOn(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        String url = getUrl();
        MediaLogUtil.log("K_ => repeat => url = " + url);
        if (null != url && url.length() > 0) {
            long seek = getSeek();
            long maxLength = getMaxLength();
            int maxNum = getMaxNum();
            start(seek, maxLength, maxNum, url);
        }
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
                && state != PlayerType.StateType.STATE_LOADING_START
                && state != PlayerType.StateType.STATE_START_ABORT
                && state != PlayerType.StateType.STATE_BUFFERING_START;
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
        if (null == mKernel || !isInPlaybackState())
            return 0L;
        long duration = mKernel.getDuration();
        if (duration < 0) {
            duration = 0L;
        }
        return duration;
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getPosition() {
        if (null == mKernel || !isInPlaybackState())
            return 0L;
        long position = mKernel.getPosition();
        if (position < 0) {
            position = 0L;
        }
        return position;
    }

    @Override
    public long getSeek() {
        try {
            return mKernel.getSeek();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    @Override
    public long getMaxLength() {
        try {
            return mKernel.getMaxLength();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    @Override
    public int getMaxNum() {
        try {
            return mKernel.getMaxNum();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void setMaxNum(int num) {
        try {
            mKernel.setMaxNum(num);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getUrl() {
        try {
            return mKernel.getUrl();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void toggleMusic(@NonNull Context context, @NonNull String music) {
        try {
            mKernel.toggleMusic(context, music);
        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    public void seekTo(@NonNull boolean force, @NonNull long seek, @NonNull long maxLength, @NonNull int maxNum) {
        if (seek < 0)
            return;

        MediaLogUtil.log("onEvent => seekTo => " + seek + ", seek = " + seek + ", maxLength = " + maxLength + ", maxNum = " + maxNum + ", force = " + force);
        // must
        if (force) {
            mKernel.update(seek, maxLength, maxNum);
            if (seek == 0) {
                mKernel.start();
            } else {
                mKernel.seekTo(seek);
            }
        }
        // sample
        else {
            boolean state = isInPlaybackState();
            MediaLogUtil.log("onEvent => seekTo => state = " + state);
            if (state) {
                mKernel.seekTo(seek);
            }
        }
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

//    /**
//     * 视频缓冲完毕，准备开始播放时回调
//     */
//    @Override
//    public void onPrepared(@NonNull long seek, @NonNull long duration) {
//        MediaLogUtil.log("onPrepared => seek = " + seek + ", duration = " + duration);
//
//
////        Object tag = getTag(R.id.module_mediaplayer_id_state_code);
////        if (null == tag) {
////            tag = 1;
////        }
////        MediaLogUtil.log("ComponentLoading => onPrepared => mCurrentPlayerState = " + tag.toString());
////        setPlayState(PlayerType.StateType.STATE_LOADING_COMPLETE);
//
//        // 快进
//        if (seek > 0) {
//            seekTo(seek);
//        }
//    }

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
    public void setKernel(@PlayerType.KernelType.Value int type) {

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

    /**
     * 设置视频比例
     */
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
     * 截图，暂不支持SurfaceView
     */
    @Override
    public Bitmap doScreenShot() {
//        if (mRender != null) {
//            return mRender.doScreenShot();
//        }
        return null;
    }

//    public View getRenderView() {
//        return mRender.getView();
//    }

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
     * 获取当前的播放状态
     */
    @PlayerType.StateType.Value
    public int getPlayState() {
        return mStateType;
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
        mStateType = state;

//        if (state == PlayerType.StateType.STATE_START) {
//            mHandler.sendEmptyMessageDelayed(0x2022, 0);
//        } else if (state == PlayerType.StateType.STATE_END) {
//            mHandler.sendEmptyMessageDelayed(0x2023, 0);
//        } else if (state == PlayerType.StateType.STATE_INIT) {
//            mHandler.sendEmptyMessageDelayed(0x2023, 0);
//        }

        ControllerLayout layout = getControlLayout();
        if (null != layout) {
            layout.setPlayState(state);
        }
        if (mOnStateChangeListeners != null) {
            for (OnChangeListener l : PlayerUtils.getSnapshot(mOnStateChangeListeners)) {
                if (l != null) {
                    l.onChange(state);
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
            for (OnChangeListener l : PlayerUtils.getSnapshot(mOnStateChangeListeners)) {
                if (l != null) {
                    l.onWindow(windowState);
                }
            }
        }
    }

    /**
     * 添加一个播放状态监听器，播放状态发生变化时将会调用。
     */
    public void addOnStateChangeListener(@NonNull OnChangeListener listener) {
        if (mOnStateChangeListeners == null) {
            mOnStateChangeListeners = new ArrayList<>();
        }
        mOnStateChangeListeners.add(listener);
    }

    /**
     * 移除某个播放状态监听
     */
    public void removeOnStateChangeListener(@NonNull OnChangeListener listener) {
        if (mOnStateChangeListeners != null) {
            mOnStateChangeListeners.remove(listener);
        }
    }

    /**
     * 设置一个播放状态监听器，播放状态发生变化时将会调用，
     * 如果你想同时设置多个监听器，推荐 {@link #addOnStateChangeListener(OnChangeListener)}。
     */
    public void setOnMediaStateListener(@NonNull OnChangeListener listener) {
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
        setPlayState(mStateType);
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
            e.printStackTrace();
        }
    }

    @Override
    public void goneReal() {
        try {
            ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_video);
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = viewGroup.getChildAt(i);
                child.setVisibility(View.INVISIBLE);
            }
            viewGroup.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void release() {

        // control
//        clearControllerLayout();
//        try {
//            ControllerLayout layout = getControlLayout();
//            layout.destroy();
//        } catch (Exception e) {
//        }

        // handler
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
//            mHandler = null;
        }

        // render
//        clearRender();

        // kernel
        try {
            mKernel.pause();
            mKernel.releaseDecoder();
            mKernel = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (!isInit()) {
//            PlayerConfig config = PlayerConfigManager.getInstance().getConfig();
//            if (config != null && config.mBuriedPointEvent != null) {
//                //退出视频播放
//                config.mBuriedPointEvent.playerDestroy(mUrl);
//
//                //计算退出视频时候的进度
//                long duration = getDuration();
//                long position = getPosition();
//                float progress = (position * 1.0f) / (duration * 1.0f);
//                config.mBuriedPointEvent.playerOutProgress(mUrl, progress);
//                config.mBuriedPointEvent.playerOutProgress(mUrl, duration, position);
//            }
//
//            //释放Assets资源
//            if (mAssetFileDescriptor != null) {
//                try {
//                    mAssetFileDescriptor.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            //关闭屏幕常亮
//            setKeepScreenOn(false);
//            //保存播放进度
//            saveProgress();
//            //重置播放进度
////            mCurrentPosition = 0;
//            //切换转态
//            setPlayState(PlayerType.StateType.STATE_CLEAN);
//            setPlayState(PlayerType.StateType.STATE_INIT);
//        }
    }

    @Override
    public void releaseKernel() {

    }

    @Override
    public void releaseRender() {
        ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_video);
        if (null != viewGroup) {
            viewGroup.removeAllViews();
        }
        if (null != mRender) {
            mRender.releaseReal();
            mRender = null;
        }
    }

    @Override
    public void startLoop() {
        MediaLogUtil.log("onEvent => startLoop = " + mHandler);
        clearLoop();
        if (null != mHandler) {
            Message message = Message.obtain();
            message.what = 0x92001;
            long millis = System.currentTimeMillis();
            message.obj = millis;
            mHandler.sendMessage(message);
        }
    }

    @Override
    public void clearLoop() {
        MediaLogUtil.log("onEvent => clearLoop = " + mHandler);
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void playEnd() {
        goneReal();
        setKeepScreenOn(false);
        setPlayState(PlayerType.StateType.STATE_END);
    }

    @Override
    public void create() {

        // 1.销毁
        release();

        // 2.创建
        if (null == mKernel) {
            mKernel = KernelFactoryManager.getKernel(getContext(), PlayerConfigManager.getInstance().getConfig().mKernel, new KernelEvent() {
                @Override
                public void onEvent(int kernel, int event) {


                    MediaLogUtil.log("onEvent => kernel = " + kernel + ", event = " + event);

                    switch (event) {
//            // loading-start
//            case PlayerType.EventType.EVENT_BUFFERING_START:
//                setPlayState(PlayerType.StateType.STATE_BUFFERING_STOP);
//                break;
//            // loading-end
//            case PlayerType.EventType.EVENT_BUFFERING_END:
//            case PlayerType.EventType.EVENT_VIDEO_RENDERING_START:
//                setPlayState(PlayerType.StateType.STATE_LOADING_STOP);
//                break;
                        // seekTo 会调用
                        case PlayerType.EventType.EVENT_OPEN_INPUT:
                            setPlayState(PlayerType.StateType.STATE_START);

                            // step1
                            goneReal();

//                            View layout = getVideoLayout();
//                            if (null != layout && layout.getWindowVisibility() != VISIBLE) {
//                                pause();
//                            }
//                        if (position == 0) {
//                            setPlayState(PlayerType.StateType.STATE_BUFFERING_STOP);
//                        }
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
//                    case PlayerType.EventType.EVENT_VIDEO_ROTATION_CHANGED:
//                        if (mRender != null) {
//                            if (rotation != -1) {
//                                mRender.setVideoRotation(rotation);
//                            }
//                        }
//                        break;
                        // 初始化开始 => loading start
                        case PlayerType.EventType.EVENT_INIT_START:
                            setPlayState(PlayerType.StateType.STATE_LOADING_START);
                            break;
                        // 初始化完成 => loading close
                        case PlayerType.EventType.EVENT_INIT_COMPILE:
                            setPlayState(PlayerType.StateType.STATE_LOADING_STOP);

                            break;
                        // 播放开始-快进
                        case PlayerType.EventType.EVENT_VIDEO_SEEK_COMPLETE:

                            // step1
                            setPlayState(PlayerType.StateType.STATE_LOADING_STOP);

                            // step2
                            showReal();

                            // step3
                            startLoop();

                            break;
                        // 播放开始
                        case PlayerType.EventType.EVENT_VIDEO_START:
//                        case PlayerType.EventType.EVENT_VIDEO_SEEK_RENDERING_START: // 视频开始渲染
//            case PlayerType.MediaType.MEDIA_INFO_AUDIO_SEEK_RENDERING_START: // 视频开始渲染
                            setPlayState(PlayerType.StateType.STATE_START);

                            // step1
                            showReal();

                            // step2
                            startLoop();

//                            View layout1 = getVideoLayout();
//                            if (null != layout1 && layout1.getWindowVisibility() != VISIBLE) {
//                                pause();
//                            }

//                        if (position > 0) {
//                            setPlayState(PlayerType.StateType.STATE_BUFFERING_STOP);
//                        }

                            if (PlayerConfigManager.getInstance().getConfig() != null && PlayerConfigManager.getInstance().getConfig().mBuriedPointEvent != null) {
                                //相当于进入了视频页面
                                String url = getUrl();
                                PlayerConfigManager.getInstance().getConfig().mBuriedPointEvent.playerIn(url);
                            }

                            break;
                        // 播放结束
                        case PlayerType.EventType.EVENT_PLAYER_END:

                            // step2
                            clearLoop();

                            // 埋点
                            if (PlayerConfigManager.getInstance().getConfig() != null && PlayerConfigManager.getInstance().getConfig().mBuriedPointEvent != null) {
                                String url = getUrl();
                                PlayerConfigManager.getInstance().getConfig().mBuriedPointEvent.playerCompletion(url);
                            }

                            int maxNum = getMaxNum();
                            // loop
                            if (maxNum > 0) {
                                // step1
                                setPlayState(PlayerType.StateType.STATE_LOADING_START);
                                goneReal();

                                // step2
                                seekTo(true, 0, 0, maxNum);
                            }
                            // sample
                            else {
                                // step1
                                playEnd();
                            }

                            break;
                        // 播放错误
                        case PlayerType.EventType.EVENT_ERROR_URL:
                        case PlayerType.EventType.EVENT_ERROR_PARSE:
                        case PlayerType.EventType.EVENT_ERROR_RETRY:
                        case PlayerType.EventType.EVENT_ERROR_SOURCE:
                        case PlayerType.EventType.EVENT_ERROR_UNEXPECTED:

                            boolean connected = PlayerUtils.isConnected(getContext());
                            setKeepScreenOn(false);
                            setPlayState(connected ? PlayerType.StateType.STATE_ERROR : PlayerType.StateType.STATE_ERROR_NET);

                            // 埋点
                            if (PlayerConfigManager.getInstance().getConfig() != null && PlayerConfigManager.getInstance().getConfig().mBuriedPointEvent != null) {
                                String url = getUrl();
                                PlayerConfigManager.getInstance().getConfig().mBuriedPointEvent.onError(url, connected);
                            }

                            break;
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
            mKernel.createDecoder(getContext());
            mKernel.setLooping(mIsLooping);
        }
        if (null == mRender) {
            mRender = RenderFactoryManager.getRender(getContext(), PlayerConfigManager.getInstance().getConfig().mRender);
            mRender.setKernel(mKernel);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            mRender.getReal().setLayoutParams(params);
            ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_video);
            if (null != viewGroup) {
                viewGroup.removeAllViews();
                viewGroup.addView(mRender.getReal(), 0);
            }
            mRender.ss();
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

    private Handler mHandler = new Handler(this);

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        MediaLogUtil.log("onEvent => onMessage => what = " + msg.what);
        if (null != msg && msg.what == 0x92001) {
            long seek = getSeek();
            long maxLength = getMaxLength();
            int maxNum = getMaxNum();
            long start = (long) msg.obj;
            long millis = System.currentTimeMillis();
            MediaLogUtil.log("onEvent => onMessage => millis = " + millis + ", start = " + start + ", seek = " + seek + ", maxLength = " + maxLength + ", maxNum = " + maxNum);

            // end
            if (maxLength > 0 && (millis - start > maxLength)) {

                // loop
                if (maxNum == -1 || maxNum > 0) {

                    if (maxNum > 0) {
                        int news = maxNum - 1;
                        setMaxNum(news);
                    }

                    // step1
                    setPlayState(PlayerType.StateType.STATE_LOADING_START);
                    goneReal();

                    // step2
                    seekTo(true, seek, maxLength, maxNum);
                }
                // stop
                else {
                    playEnd();
                    release();
                }
            }
            // next
            else {
                if (null != mHandler) {
                    Message message = Message.obtain();
                    message.what = 0x92001;
                    message.obj = msg.obj;
                    mHandler.sendMessageDelayed(message, 100);
                }
                if (mOnStateChangeListeners != null) {
                    long position = getPosition();
                    long duration = getDuration();
                    for (OnChangeListener l : PlayerUtils.getSnapshot(mOnStateChangeListeners)) {
                        if (l != null) {
                            l.onProgress(position, duration);
                        }
                    }
                }
            }
        }
        return false;
    }
}
