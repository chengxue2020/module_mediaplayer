package lib.kalu.mediaplayer.core.video.controller.base;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.video.controller.impl.ComponentApi;
import lib.kalu.mediaplayer.core.video.controller.impl.GestureApi;
import lib.kalu.mediaplayer.util.PlayerUtils;
import lib.kalu.mediaplayer.util.MPLogUtil;


/**
 * description: 包含手势操作的VideoController
 * created by kalu on 2021/9/16
 */
public abstract class ControllerLayoutDispatchTouchEvent extends ControllerLayoutDispatchKeyEvent implements
        GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, View.OnTouchListener {

    private GestureDetector mGestureDetector;
    private AudioManager mAudioManager;
    private boolean mIsGestureEnabled = true;
    private int mStreamVolume;
    private float mBrightness;
    private int mSeekPosition;
    /**
     * 是否是第一次触摸
     */
    private boolean mFirstTouch;
    /**
     * 是否改变位置
     */
    private boolean mChangePosition;
    /**
     * 是否改变亮度
     */
    private boolean mChangeBrightness;
    /**
     * 是否改变音量
     */
    private boolean mChangeVolume;
    /**
     * 是否可以改变位置
     */
    private boolean mCanChangePosition = true;
    /**
     * 是否在竖屏模式下开始手势控制
     */
    private boolean mEnableInNormal;
    /**
     * 是否关闭了滑动手势
     */
    private boolean mCanSlide;
    /**
     * 播放状态
     */
    private int mCurPlayState;
    /**
     * 屏幕一半的距离
     */
    private int mHalfScreen;

    public ControllerLayoutDispatchTouchEvent(@NonNull Context context) {
        super(context);
    }

    public ControllerLayoutDispatchTouchEvent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ControllerLayoutDispatchTouchEvent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void init() {
        super.init();
        mHalfScreen = PlayerUtils.getScreenWidth(getContext(), true) / 2;
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector = new GestureDetector(getContext(), this);
        setOnTouchListener(this);
    }

    /**
     * 设置是否可以滑动调节进度，默认可以
     */
    public void setCanChangePosition(boolean canChangePosition) {
        mCanChangePosition = canChangePosition;
    }

    /**
     * 是否在竖屏模式下开始手势控制，默认关闭
     */
    public void setEnableInNormal(boolean enableInNormal) {
        mEnableInNormal = enableInNormal;
    }

    /**
     * 是否开启手势空控制，默认开启，关闭之后，双击播放暂停以及手势调节进度，音量，亮度功能将关闭
     */
    public void setGestureEnabled(boolean gestureEnabled) {
        mIsGestureEnabled = gestureEnabled;
    }

    /**
     * 调用此方法向控制器设置播放器模式
     *
     * @param windowState 播放模式
     */
    @Override
    public void setWindowState(@PlayerType.WindowType.Value int windowState) {
        super.setWindowState(windowState);
        if (windowState == PlayerType.WindowType.NORMAL) {
            mCanSlide = mEnableInNormal;
        } else if (windowState == PlayerType.WindowType.FULL) {
            mCanSlide = true;
        }
    }

    /**
     * 调用此方法向控制器设置播放状态
     *
     * @param playState 播放状态
     */
    @Override
    public void setPlayState(@PlayerType.StateType.Value int playState) {
        super.setPlayState(playState);
        mCurPlayState = playState;
    }

    private boolean isInPlaybackState() {
        return mControllerWrapper != null
                && mCurPlayState != PlayerType.StateType.STATE_ERROR
                && mCurPlayState != PlayerType.StateType.STATE_INIT
                && mCurPlayState != PlayerType.StateType.STATE_LOADING_START
                && mCurPlayState != PlayerType.StateType.STATE_LOADING_STOP
                && mCurPlayState != PlayerType.StateType.STATE_START_ABORT
                && mCurPlayState != PlayerType.StateType.STATE_BUFFERING_START;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!isEnabled()) {
            return super.onTouchEvent(event);
        } else {
            return mGestureDetector.onTouchEvent(event);
        }
    }

    /**
     * 手指按下的瞬间
     */
    @Override
    public boolean onDown(MotionEvent e) {
        if (!isInPlaybackState() //不处于播放状态
                || !mIsGestureEnabled //关闭了手势
                || PlayerUtils.isEdge(getContext(), e)) {
            //处于屏幕边沿
            return true;
        }
        mStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null) {
            mBrightness = 0;
        } else {
            mBrightness = activity.getWindow().getAttributes().screenBrightness;
        }
        mFirstTouch = true;
        mChangePosition = false;
        mChangeBrightness = false;
        mChangeVolume = false;
        return true;
    }

    /**
     * 单击
     */
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (isInPlaybackState()) {
            //切换显示/隐藏状态
            mControllerWrapper.toggleShowState();
        }
        return true;
    }

    /**
     * 双击
     */
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        //如果没有锁屏，
        if (!isLocked() && isInPlaybackState()) {
            //播放和暂停
            toggle();
        }
        return true;
    }

    /**
     * 在屏幕上滑动
     * 左右滑动，则是改变播放进度
     * 上下滑动，滑动左边改变音量；滑动右边改变亮度
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!isInPlaybackState() //不处于播放状态
                || !mIsGestureEnabled //关闭了手势
                || !mCanSlide //关闭了滑动手势
                || isLocked() //锁住了屏幕
                //处于屏幕边沿
                || PlayerUtils.isEdge(getContext(), e1)) {
            return true;
        }
        float deltaX = e1.getX() - e2.getX();
        float deltaY = e1.getY() - e2.getY();
        //如果是第一次触摸
        if (mFirstTouch) {
            //判断是左右滑动，还是上下滑动
            mChangePosition = Math.abs(distanceX) >= Math.abs(distanceY);
            if (!mChangePosition) {
                //上下滑动，滑动左边改变音量；滑动右边改变亮度
                //半屏宽度
                if (mHalfScreen == 0) {
                    mHalfScreen = PlayerUtils.getScreenWidth(getContext(), true) / 2;
                }
                if (e2.getX() > mHalfScreen) {
                    mChangeVolume = true;
                } else {
                    mChangeBrightness = true;
                }
            }

            //左右滑动，则是改变播放进度
            if (mChangePosition) {
                //根据用户设置是否可以滑动调节进度来决定最终是否可以滑动调节进度
                mChangePosition = mCanChangePosition;
            }

            if (mChangePosition || mChangeBrightness || mChangeVolume) {
                if (null != mComponents && mComponents.size() > 0) {
                    int size = mComponents.size();
                    for (int i = 0; i < size; i++) {
                        ComponentApi component = mComponents.get(i);
                        if (null == component)
                            continue;
                        if (component instanceof GestureApi) {
                            ((GestureApi) component).onStartSlide();
                        }
                    }
                }
            }
            mFirstTouch = false;
        }
        if (mChangePosition) {
            slideToChangePosition(deltaX);
        } else if (mChangeBrightness) {
            slideToChangeBrightness(deltaY);
        } else if (mChangeVolume) {
            slideToChangeVolume(deltaY);
        }
        return true;
    }

    protected void slideToChangePosition(float deltaX) {
        deltaX = -deltaX;
        int width = getMeasuredWidth();
        int duration = (int) mControllerWrapper.getDuration();
        int currentPosition = (int) mControllerWrapper.getPosition();
        int position = (int) (deltaX / width * 120000 + currentPosition);
        if (position > duration) position = duration;
        if (position < 0) position = 0;
        if (null != mComponents && mComponents.size() > 0) {
            int size = mComponents.size();
            for (int i = 0; i < size; i++) {
                ComponentApi component = mComponents.get(i);
                if (null == component)
                    continue;
                if (component instanceof GestureApi) {
                    ((GestureApi) component).onPositionChange(position, currentPosition, duration);
                }
            }
        }
        mSeekPosition = position;
    }

    protected void slideToChangeBrightness(float deltaY) {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (!PlayerUtils.isActivityLiving(activity)) {
            return;
        }
        Window window = activity.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        int height = getMeasuredHeight();
        if (mBrightness == -1.0f) mBrightness = 0.5f;
        float brightness = deltaY * 2 / height * 1.0f + mBrightness;
        if (brightness < 0) {
            brightness = 0f;
        }
        if (brightness > 1.0f) brightness = 1.0f;
        int percent = (int) (brightness * 100);
        attributes.screenBrightness = brightness;
        window.setAttributes(attributes);
        if (null != mComponents && mComponents.size() > 0) {
            int size = mComponents.size();
            for (int i = 0; i < size; i++) {
                ComponentApi component = mComponents.get(i);
                if (null == component)
                    continue;
                if (component instanceof GestureApi) {
                    ((GestureApi) component).onBrightnessChange(percent);
                }
            }
        }
    }

    protected void slideToChangeVolume(float deltaY) {
        int streamMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int height = getMeasuredHeight();
        float deltaV = deltaY * 2 / height * streamMaxVolume;
        float index = mStreamVolume + deltaV;
        if (index > streamMaxVolume) index = streamMaxVolume;
        if (index < 0) index = 0;
        int percent = (int) (index / streamMaxVolume * 100);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) index, 0);
        if (null != mComponents && mComponents.size() > 0) {
            int size = mComponents.size();
            for (int i = 0; i < size; i++) {
                ComponentApi component = mComponents.get(i);
                if (null == component)
                    continue;
                if (component instanceof GestureApi) {
                    ((GestureApi) component).onVolumeChange(percent);
                }
            }
        }
    }

    // 点击事件产生后，会直接调用dispatchTouchEvent分发方法
    /*public boolean dispatchTouchEvent(MotionEvent ev) {
        //代表是否消耗事件
        boolean consume = false;
        if (onInterceptTouchEvent(ev)) {
            //如果onInterceptTouchEvent()返回true则代表当前View拦截了点击事件
            //则该点击事件则会交给当前View进行处理
            //即调用onTouchEvent (）方法去处理点击事件
            consume = onTouchEvent (ev) ;
        } else {
            //如果onInterceptTouchEvent()返回false则代表当前View不拦截点击事件
            //则该点击事件则会继续传递给它的子元素
            //子元素的dispatchTouchEvent（）就会被调用，重复上述过程
            //直到点击事件被最终处理为止
            consume = child.dispatchTouchEvent (ev) ;
        }
        return consume;
    }*/

    /**
     * 拦截事件：只有ViewGroup才有这个
     *
     * @param ev event
     * @return 返回值
     * true： 当前ViewGroup（因为View中没有该方法，而没有child的VIew也不需要有拦截机制）
     * 希望该事件不再传递给其child，而是希望自己处理。
     * false：当前ViewGroup不准备拦截该事件，事件正常向下分发给其child。
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        MPLogUtil.log("事件----------事件拦截----------");
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 分发事件：使用对象	Activity、ViewGroup、View
     *
     * @param ev event
     * @return 返回值
     * true： 消费事件；事件不会往下传递；后续事件（Move、Up）会继续分发到该View
     * false：不消费事件；事件不会往下传递；将事件回传给父控件的onTouchEvent()处理；Activity例外：返回false=消费事件
     * 后续事件（Move、Up）会继续分发到该View(与onTouchEvent()区别）
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        MPLogUtil.log("事件----------事件分发----------");
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 触摸事件
     *
     * @param event event事件，主要处理up，down，cancel
     * @return 返回值
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // 禁用
        if (!isEnabled())
            return super.onTouchEvent(event);

        MPLogUtil.log("事件----------事件触摸----------");
        //滑动结束时事件处理
        if (!mGestureDetector.onTouchEvent(event)) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_UP:
                    //抬起View（与DOWN对应）
                    stopSlide();
                    if (mSeekPosition > 0) {
                        mControllerWrapper.seekTo(mSeekPosition);
                        mSeekPosition = 0;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    //非人为原因结束本次事件
                    stopSlide();
                    mSeekPosition = 0;
                    break;
                case MotionEvent.ACTION_HOVER_MOVE:
                    //滑动View
                    break;
                case MotionEvent.ACTION_DOWN:
                    //按下View（所有事件的开始）
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    private void stopSlide() {
        if (null != mComponents && mComponents.size() > 0) {
            int size = mComponents.size();
            for (int i = 0; i < size; i++) {
                ComponentApi component = mComponents.get(i);
                if (null == component)
                    continue;
                if (component instanceof GestureApi) {
                    //结束滑动
                    ((GestureApi) component).onStopSlide();
                }
            }
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }


    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }
}
