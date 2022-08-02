package lib.kalu.mediaplayer.widget.pip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import androidx.annotation.Keep;

import lib.kalu.mediaplayer.config.player.PlayerConfigManager;
import lib.kalu.mediaplayer.core.controller.ControllerFloat;
import lib.kalu.mediaplayer.core.view.VideoLayout;
import lib.kalu.mediaplayer.util.PlayerUtils;


/**
 * desc  : 悬浮播放
 */
@Keep
public class FloatVideoManager {

    //画中画
    public static final String PIP = "pip";
    private static FloatVideoManager instance;
    private VideoLayout mVideoPlayerLayout;
    private FloatVideoView mFloatView;
    private ControllerFloat mFloatController;
    private boolean mIsShowing;
    private int mPlayingPosition = -1;
    private Class mActClass;


    private FloatVideoManager(Context context) {
        mVideoPlayerLayout = new VideoLayout(context);
        PlayerConfigManager.instance().add(mVideoPlayerLayout, PIP);
        mFloatController = new ControllerFloat(context);
        mFloatView = new FloatVideoView(context, 0, 0);
    }

    public static FloatVideoManager getInstance(Context context) {
        if (instance == null) {
            synchronized (FloatVideoManager.class) {
                if (instance == null) {
                    instance = new FloatVideoManager(context);
                }
            }
        }
        return instance;
    }

    @SuppressLint("WrongConstant")
    public void startFloatWindow() {
        if (mIsShowing) {
            return;
        }
        PlayerUtils.removeViewFormParent(mVideoPlayerLayout);
        mVideoPlayerLayout.setControllerLayout(mFloatController);
//        mFloatController.setPlayState(mVideoPlayerLayout.getPlayState());
        mFloatController.setWindowState(mVideoPlayerLayout.getWindowState());
        mFloatView.addView(mVideoPlayerLayout);
        mFloatView.addToWindow();
        mIsShowing = true;
    }

    public void stopFloatWindow() {
        if (!mIsShowing) {
            return;
        }
        mFloatView.removeFromWindow();
        PlayerUtils.removeViewFormParent(mVideoPlayerLayout);
        mIsShowing = false;
    }

    public void setPlayingPosition(int position) {
        this.mPlayingPosition = position;
    }

    public int getPlayingPosition() {
        return mPlayingPosition;
    }

    public void pause() {
        if (mIsShowing) {
            return;
        }
        mVideoPlayerLayout.pause();
    }

    public void resume() {
        if (mIsShowing) {
            return;
        }
        mVideoPlayerLayout.resume();
    }

    public void reset() {
        if (mIsShowing) {
            return;
        }
        PlayerUtils.removeViewFormParent(mVideoPlayerLayout);
//        mVideoPlayerLayout.resetKernel();
        mVideoPlayerLayout.clearControllerLayout();
        mPlayingPosition = -1;
        mActClass = null;
    }

    public boolean onBackPress() {
        return !mIsShowing && mVideoPlayerLayout.onBackPressed();
    }

    public boolean isStartFloatWindow() {
        return mIsShowing;
    }

    /**
     * 显示悬浮窗
     */
    public void setFloatViewVisible() {
        if (mIsShowing) {
            mVideoPlayerLayout.resume();
            mFloatView.setVisibility(View.VISIBLE);
        }
    }

    public void setActClass(Class cls) {
        this.mActClass = cls;
    }

    public Class getActClass() {
        return mActClass;
    }

}
