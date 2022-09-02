package lib.kalu.mediaplayer.core.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

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
import lib.kalu.mediaplayer.listener.OnFullChangeListener;
import lib.kalu.mediaplayer.util.ActivityUtils;
import lib.kalu.mediaplayer.util.BaseToast;
import lib.kalu.mediaplayer.util.PlayerUtils;
import lib.kalu.mediaplayer.util.MediaLogUtil;

/**
 * @description: 播放器具体实现类
 */
@Keep
public class VideoLayout extends RelativeLayout implements PlayerApi, Handler.Callback {

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
        MediaLogUtil.log("onLife => onAttachedToWindow => this = " + this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        boolean autoRelease = isAutoRelease();
        MediaLogUtil.log("onLife => onDetachedFromWindow => autoRelease = " + autoRelease + ", this = " + this);
        release(!autoRelease);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        MediaLogUtil.log("onLife => onWindowVisibilityChanged => visibility = " + visibility + ", this = " + this);
        // visable
        if (visibility == View.VISIBLE) {
            startLoop();
            resume();
        }
        // not visable
        else {
            clearLoop();
            pause(true);
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
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_player, this, true);

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
        MediaLogUtil.log("onSaveInstanceState => url = " + url);
        if (null != url && url.length() > 0) {
            try {
                long position = getPosition();
                long duration = getDuration();
                MediaLogUtil.log("onSaveInstanceState => position = " + position + ", duration = " + duration + ", url = " + url);
                saveBundle(getContext(), url, position, duration);
            } catch (Exception e) {
                e.printStackTrace();
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
    public void start(@NonNull long seek, @NonNull long max, @NonNull boolean loop, @NonNull boolean autoRelease, @NonNull String url) {
        MediaLogUtil.log("VideoLayout => start => seek = " + seek + ", max = " + max + ", loop = " + loop + ", autoRelease = " + autoRelease + ", url = " + url);
        try {

            // step1
            callState(PlayerType.StateType.STATE_LOADING_START);

            // step2
            String temp = getUrl();
            if (null != temp && temp.length() > 0) {
                release();
            }

            // step3
            if (null == mKernel) {
                create();
            } else {
                pause(true);
            }

            // step4
            boolean showNetWarning = showNetWarning();
            if (showNetWarning) {
                callState(PlayerType.StateType.STATE_START_ABORT);
            }

            // step4
            mKernel.create(getContext(), seek, max, loop, autoRelease, url);
            setKeepScreenOn(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void create() {

        // step1
        release(true);

        // step2
        if (null == mKernel) {
            mKernel = KernelFactoryManager.getKernel(getContext(), PlayerConfigManager.getInstance().getConfig().mKernel, new KernelEvent() {
                @Override
                public void onEvent(int kernel, int event) {

                    MediaLogUtil.log("onEvent => onKernel = " + kernel + ", event = " + event);

                    switch (event) {
                        // 网络拉流开始
                        case PlayerType.EventType.EVENT_OPEN_INPUT:
                            // step1
                            callState(PlayerType.StateType.STATE_START);
                            // step2
                            goneReal();
                            break;
                        // 初始化开始 => loading start
                        case PlayerType.EventType.EVENT_LOADING_START:
                            callState(PlayerType.StateType.STATE_LOADING_START);
                            break;
                        // 初始化完成 => loading stop
                        case PlayerType.EventType.EVENT_LOADING_STOP:
                            callState(PlayerType.StateType.STATE_LOADING_STOP);
                            break;
                        // 播放开始-快进
                        case PlayerType.EventType.EVENT_VIDEO_START_SEEK:

                            // step1
                            callState(PlayerType.StateType.STATE_LOADING_STOP);
                            // step2
                            showReal();
                            // step3
                            startLoop();
                            // step4
                            resume();
                            // step5
                            boolean hasMusicExtra = hasMusicExtra();
                            if (hasMusicExtra) {
                                toggleMusicExtra(true);
                            } else {
                                toggleMusicDefault(true);
                            }

                            break;
                        // 播放开始
                        case PlayerType.EventType.EVENT_VIDEO_START:
//                        case PlayerType.EventType.EVENT_VIDEO_SEEK_RENDERING_START: // 视频开始渲染
//            case PlayerType.MediaType.MEDIA_INFO_AUDIO_SEEK_RENDERING_START: // 视频开始渲染

                            callState(PlayerType.StateType.STATE_START);

                            // step1
                            showReal();

                            // step2
                            startLoop();

                            // step3
                            checkReal();

                            // step4
                            boolean has = hasMusicExtra();
                            if (has) {
                                toggleMusicExtra(true);
                            } else {
                                toggleMusicDefault(true);
                            }

                            if (PlayerConfigManager.getInstance().getConfig() != null && PlayerConfigManager.getInstance().getConfig().mBuriedPointEvent != null) {
                                //相当于进入了视频页面
                                String url = getUrl();
                                PlayerConfigManager.getInstance().getConfig().mBuriedPointEvent.playerIn(url);
                            }

                            break;
                        // 播放结束
                        case PlayerType.EventType.EVENT_VIDEO_END:

                            // step2
                            clearLoop();

                            // 埋点
                            if (PlayerConfigManager.getInstance().getConfig() != null && PlayerConfigManager.getInstance().getConfig().mBuriedPointEvent != null) {
                                String url = getUrl();
                                PlayerConfigManager.getInstance().getConfig().mBuriedPointEvent.playerCompletion(url);
                            }

                            boolean looping = isLooping();
                            // loop
                            if (looping) {

                                // step1
                                callState(PlayerType.StateType.STATE_LOADING_START);
                                goneReal();

                                // step2
                                pause(true);

                                // step3
                                long seek = getSeek();
                                long max = getMax();
                                seekTo(true, seek, max, true);
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
                        case PlayerType.EventType.EVENT_ERROR_URL:
                        case PlayerType.EventType.EVENT_ERROR_PARSE:
                        case PlayerType.EventType.EVENT_ERROR_RETRY:
                        case PlayerType.EventType.EVENT_ERROR_SOURCE:

                            boolean connected = PlayerUtils.isConnected(getContext());
                            setKeepScreenOn(false);
                            callState(connected ? PlayerType.StateType.STATE_ERROR : PlayerType.StateType.STATE_ERROR_NET);

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
        }

        // step3
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
        }
    }

    @Override
    public void toggle() {
        boolean playing = isPlaying();
        if (playing) {
            pause(false);
        } else {
            resume();
        }
    }

    @Override
    public void pause(boolean auto) {
        boolean playing = isPlaying();
        if (!playing)
            return;
        // 自动不回调状态
        if (auto) {

        }
        // 用户手动，需要显示暂停图标
        else {
            callState(PlayerType.StateType.STATE_PAUSE);
        }
        setKeepScreenOn(false);
        mKernel.pause();
    }

    @Override
    public void close() {
        boolean playing = isPlaying();
        if (!playing)
            return;
        callState(PlayerType.StateType.STATE_CLOSE);
        setKeepScreenOn(false);
        mKernel.stop();
    }

    @Override
    public void resume() {
        String url = getUrl();
        MediaLogUtil.log("onEvent => resume => url = " + url + ", mHanlder = " + mHandler + ", mKernel = " + mKernel);
        if (null == url || url.length() <= 0)
            return;
        callState(PlayerType.StateType.STATE_RESUME);
        setKeepScreenOn(true);
        mKernel.start();
    }

    @Override
    public void repeat() {
        String url = getUrl();
        MediaLogUtil.log("onEvent => repeat => url = " + url);
        if (null == url || url.length() <= 0)
            return;
        long seek = getSeek();
        long max = getMax();
        boolean looping = isLooping();
        MediaLogUtil.log("onEvent => repeat => seek = " + seek + ", max = " + max + ", loop = " + looping);
        callState(PlayerType.StateType.STATE_REPEAT);
        seekTo(true, seek, max, looping);
    }

    @Override
    public long getDuration() {
        boolean playing = isPlaying();
        if (!playing)
            return 0L;
        try {
            long duration = mKernel.getDuration();
            if (duration < 0) {
                duration = 0L;
            }
            return duration;
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isAutoRelease() {
        try {
            return mKernel.isAutoRelease();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
    public long getMax() {
        try {
            return mKernel.getMax();
        } catch (Exception e) {
            e.printStackTrace();
            return -1L;
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
    public void toggleMusicExtra(boolean auto) {
        try {
            mKernel.toggleMusicExtra(auto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toggleMusicDefault(boolean musicPrepare) {
        try {
            mKernel.toggleMusicDafault(musicPrepare);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toggleMusicDefault() {
        try {
            mKernel.toggleMusicDafault();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toggleMusic() {
        try {
            mKernel.toggleMusic();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasMusicExtra() {
        try {
            return mKernel.hasMusicExtra();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void updateMusic(@NonNull String musicPath, @NonNull boolean musicPlay, @NonNull boolean musicLoop, @NonNull boolean musicSeek) {
        try {
            mKernel.updateMusic(musicPath, musicPlay, musicLoop, musicSeek);
        } catch (Exception e) {
            e.printStackTrace();
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
    public void seekTo(@NonNull boolean force, @NonNull long seek, @NonNull long max, @NonNull boolean loop) {

        if (seek < 0)
            return;

        if (seek == 0) {
            seek = 1;
        }

        String url = getUrl();
        MediaLogUtil.log("onEvent => seekTo => seek = " + seek + ", max = " + max + ", loop = " + loop + ", force = " + force + ", url = " + url + ", mKernel = " + mKernel);
        if (null == url || url.length() < 0)
            return;

        // step1
        callState(PlayerType.StateType.STATE_LOADING_START);
        // step2
        clearLoop();
        // step3
        if (force) {
            mKernel.update(seek, max, loop);
            pause(true);
        }
        // step4
        mKernel.seekTo(seek);
    }

    @Override
    public void seekForward(@NonNull boolean callback) {
        ControllerLayout layout = getControlLayout();
        if (null == layout)
            return;
        layout.seekForward(callback);
    }

    @Override
    public void seekRewind(boolean callback) {
        ControllerLayout layout = getControlLayout();
        if (null == layout)
            return;
        layout.seekRewind(callback);
    }

    @Override
    public boolean isMute() {
        try {
            return mKernel.isMute();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
    public void setVolume(float v1, float v2) {
        if (mKernel != null) {
            mKernel.setVolume(v1, v2);
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
     * 向Controller设置播放器状态，包含全屏状态和非全屏状态
     * 播放模式
     * 普通模式，小窗口模式，正常模式三种其中一种
     * MODE_NORMAL              普通模式
     * MODE_FULL_SCREEN         全屏模式
     * MODE_TINY_WINDOW         小屏模式
     */
    protected void setWindowState(@PlayerType.WindowType.Value int windowState) {

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
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(params);
        viewGroup.addView(layout);
        layout.setMediaPlayer(this);
        callState(PlayerType.StateType.STATE_INIT);
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
    public void checkReal() {
        int visibility = getWindowVisibility();
        MediaLogUtil.log("onLife => checkReal => visibility = " + visibility);
        if (visibility == View.VISIBLE)
            return;
        clearLoop();
        pause();
    }

    @Override
    public void release(@NonNull boolean onlyHandle) {
        MediaLogUtil.log("onEvent => release => onlyHandle = " + onlyHandle + ", mHandler = " + mHandler + ", mKernel = " + mKernel);
        // control
//        clearControllerLayout();
//        try {
//            ControllerLayout layout = getControlLayout();
//            layout.destroy();
//        } catch (Exception e) {
//        }

        // step1
        if (null != mHandler && null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }

        // step2
        pause(true);

        // step3
        if (onlyHandle)
            return;

        // step5
        try {
            mRender.releaseReal();
            mRender = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // step7
        try {
            mKernel.pause();
            mKernel.stop();
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
//            callState(PlayerType.StateType.STATE_CLEAN);
//            callState(PlayerType.StateType.STATE_INIT);
//        }
    }

    @Override
    public void startLoop() {
        clearLoop();
        MediaLogUtil.log("onEvent => startLoop => mhandler = " + mHandler);
        if (null == mHandler)
            return;
        String url = getUrl();
        if (null == url || url.length() <= 0)
            return;
        Message message = Message.obtain();
        message.what = 0x92001;
        long millis = System.currentTimeMillis();
        message.obj = millis;
        mHandler.sendMessage(message);
    }

    @Override
    public void clearLoop() {
        MediaLogUtil.log("onEvent => clearLoop => mHandler = " + mHandler);
        if (null == mHandler)
            return;
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void playEnd() {
        goneReal();
        setKeepScreenOn(false);
        callState(PlayerType.StateType.STATE_END);
    }

    @Override
    public void callState(int state) {
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
            // 1
            View real = getChildAt(0);
            removeAllViews();
            // 2
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            int index = decorView.getChildCount();
            decorView.addView(real, index);
            // 3
            setFocusable(true);
            requestFocus();
            // 4
            setWindowState(PlayerType.WindowType.FULL);
            // 5
            if (null != mOnFullChangeListener) {
                mOnFullChangeListener.onFull();
            }
        } catch (Exception e) {
            e.printStackTrace();
            setFocusable(false);
            clearFocus();
        }
    }

    @Override
    public void stopFull() {

        Context context = getContext();
        Activity activity = ActivityUtils.getActivity(context);
        Log.e("UIUIIUUIU", "stopFull => activity = " + activity);
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
            removeAllViews();
            addView(real, 0);
            // 3
            setFocusable(false);
            clearFocus();
            // 4
            setWindowState(PlayerType.WindowType.NORMAL);
            // 5
            if (null != mOnFullChangeListener) {
                mOnFullChangeListener.onNormal();
            }
        } catch (Exception e) {
            e.printStackTrace();
            setFocusable(false);
            clearFocus();
        }
    }

    /*****************************/

    @Override
    public boolean isFloat() {
        try {
            Activity activity = ActivityUtils.getActivity(getContext());
            View decorView = activity.getWindow().getDecorView();
            View v = decorView.findViewById(R.id.module_mediaplayer_root);
            return null != v;
        } catch (Exception e) {
            e.printStackTrace();
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
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            root.setBackgroundColor(Color.BLACK);
            root.setLayoutParams(layoutParams);
            // 3
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            int index = decorView.getChildCount();
            decorView.addView(real, index);
            // 4
            setFocusable(true);
            requestFocus();
            // 5
            setWindowState(PlayerType.WindowType.FLOAT);
        } catch (Exception e) {
            e.printStackTrace();
            clearFocus();
            setFocusable(false);
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
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            root.setBackground(null);
            root.setLayoutParams(layoutParams);
            // 2
            removeAllViews();
            addView(real, 0);
            // 3
            clearFocus();
            setFocusable(false);
            // 4
            setWindowState(PlayerType.WindowType.NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
            clearFocus();
            setFocusable(false);
        }
    }

    private Handler mHandler = new Handler(this);
//    private final WeakReference<Handler> mHandler = new WeakReference<>(new Handler(this));

    @Override
    public boolean handleMessage(@NonNull Message msg) {
//        MediaLogUtil.log("onEvent => onMessage => what = " + msg.what);
        if (null != msg && msg.what == 0x92001) {
            long seek = getSeek();
            long max = getMax();
            boolean looping = isLooping();
            long start = (long) msg.obj;
            long millis = System.currentTimeMillis();
//            MediaLogUtil.log("onEvent => onMessage => millis = " + millis + ", start = " + start + ", seek = " + seek + ", max = " + max + ", loop = " + looping + ", mKernel = " + mKernel);

            // end
            if (max > 0 && ((millis - start) > max)) {

                // loop
                if (looping) {

                    // step1
                    goneReal();

                    // step2
                    pause(true);

                    // step3
                    seekTo(true, seek, max, true);
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
                if (null != mHandler) {
                    Message message = Message.obtain();
                    message.what = 0x92001;
                    message.obj = msg.obj;
                    mHandler.sendMessageDelayed(message, 50);
                }

                long position = getPosition();
                long duration = getDuration();
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
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        // seekForward
        if (isFocusable() && isFull() && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            int count = event.getRepeatCount();
            MediaLogUtil.log("seekForward[false] => count = " + count);
            if (count > 0) {
                clearLoop();
                seekForward(false);
            }
            return true;
        }
        // seekForward
        else if (isFocusable() && isFull() && event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            int count = event.getRepeatCount();
            MediaLogUtil.log("seekForward[true] => count = " + count);
            startLoop();
            seekForward(true);
            return true;
        }
        // seekRewind
        else if (isFocusable() && isFull() && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            int count = event.getRepeatCount();
            MediaLogUtil.log("seekRewind[false] => count = " + count);
            if (count > 0) {
                clearLoop();
                seekRewind(false);
            }
            return true;
        }
        // seekRewind
        else if (isFocusable() && isFull() && event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            int count = event.getRepeatCount();
            MediaLogUtil.log("seekRewind[true] => count = " + count);
            startLoop();
            seekRewind(true);
            return true;
        }
        // stopFloat
        else if (isFocusable() && isFloat() && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            stopFloat();
            return true;
        }
        // stopFull
        else if (isFocusable() && isFull() && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            stopFull();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
