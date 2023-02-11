package lib.kalu.mediaplayer.core.player.api;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.controller.base.ControllerLayout;
import lib.kalu.mediaplayer.core.controller.component.ComponentSeek;
import lib.kalu.mediaplayer.util.MPLogUtil;

/**
 * revise: 播放器基础属性获取和设置属性接口
 */
public interface PlayerApi extends PlayerApiBase, PlayerApiKernel, PlayerApiDevice, PlayerApiComponent, PlayerApiCache, PlayerApiRender {

    default boolean dispatchEvent(@NonNull KeyEvent event) {

        boolean isFull = isFull();
        boolean isFloat = isFloat();
        MPLogUtil.log("PlayerApi => dispatchEvent => isFloat = " + isFloat + ", isFull = " + isFull);

        // full
        if (isFull) {
            // volume_up
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
                return false;
            }
            // volume_down
            else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                return false;
            }
            // volume_mute
            else if (isFull() && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_MUTE) {
                return false;
            }
            // voice_assist
            else if (isFull() && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_VOICE_ASSIST) {
                return false;
            }
            // stopFull
            else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                MPLogUtil.log("PlayerApi => dispatchEvent => stopFull =>");
                stopFull();
            }
            // center
            else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                MPLogUtil.log("PlayerApi => dispatchEvent => toggle =>");
                toggle();
            }
            // component
            else {
                try {
                    ControllerLayout layout = getControlLayout();
                    if (null != layout) {
                        int count = layout.getChildCount();
                        for (int i = 0; i < count; i++) {
                            View child = layout.getChildAt(i);
                            if (null == child)
                                continue;
                            if (child instanceof ComponentSeek) {
                                child.dispatchKeyEvent(event);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                }
            } return true;
        }
        // float
        else if (isFloat) {
            // stopFloat
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                MPLogUtil.log("PlayerApi => dispatchEvent => stopFloat =>");
                stopFloat();
                return true;
            }
        } return false;
    }

    default void checkOnWindowVisibilityChanged(int visibility) {

        boolean hideStop = isHideStop();
        boolean hideRelease = isHideRelease();
        MPLogUtil.log("PlayerApi => checkOnWindowVisibilityChanged => hideStop = " + hideStop + ", hideRelease = " + hideRelease + ", this = " + this);
        if (!hideStop && !hideRelease) return;

        String url = getUrl();
        MPLogUtil.log("PlayerApi => checkOnWindowVisibilityChanged => url = " + url + ", this = " + this);
        if (null == url || url.length() <= 0) return;

        boolean playing = isPlaying();
        MPLogUtil.log("PlayerApi => checkOnWindowVisibilityChanged => playing = " + playing + ", this = " + this);

        // show
        if (visibility == View.VISIBLE) {
            if (hideRelease) {
                restart();
            } else {
                resume(false);
            }
        }
        // hide
        else {
            if (hideRelease) {
                release();
            } else {
                pause(true);
            }
        }
    }

    default void checkOnDetachedFromWindow() {

        boolean hideStop = isHideStop();
        boolean hideRelease = isHideRelease();
        MPLogUtil.log("PlayerApi => checkOnDetachedFromWindow => hideStop = " + hideStop + ", hideRelease = " + hideRelease + ", this = " + this);
        if (!hideStop && !hideRelease) return;

        String url = getUrl();
        MPLogUtil.log("PlayerApi => checkOnDetachedFromWindow => url = " + url + ", this = " + this);
        if (null == url || url.length() <= 0) return;

        boolean playing = isPlaying();
        MPLogUtil.log("PlayerApi => checkOnDetachedFromWindow => playing = " + playing + ", this = " + this);
        if (!playing) return;

        if (hideRelease) {
            release();
        } else {
            pause(false);
        }
    }

    default void checkOnAttachedToWindow() {

        boolean hideStop = isHideStop();
        boolean hideRelease = isHideRelease();
        MPLogUtil.log("PlayerApi => checkOnAttachedToWindow => hideStop = " + hideStop + ", hideRelease = " + hideRelease + ", this = " + this);
        if (!hideStop && !hideRelease) return;

        String url = getUrl();
        MPLogUtil.log("PlayerApi => checkOnAttachedToWindow => url = " + url + ", this = " + this);
        if (null == url || url.length() <= 0) return;

        boolean playing = isPlaying();
        MPLogUtil.log("PlayerApi => checkOnAttachedToWindow => playing = " + playing + ", this = " + this);
        if (playing) return;

        if (hideRelease) {
            restart();
        } else {
            resume(false);
        }
    }

    default void onSaveBundle() {
        try {
            ViewGroup layout = getLayout();
            if (null == layout) return;
            String url = getUrl();
            if (null == url || url.length() <= 0) return;
            Context context = layout.getContext();
            long position = getPosition();
            long duration = getDuration();
            saveBundle(context, url, position, duration);
        } catch (Exception e) {
        }
    }

    default void initAttribute(@NonNull AttributeSet attrs) {

        ViewGroup layout = getLayout();
        if (null == layout) return;

        Context context = layout.getContext();
        layout.setBackgroundColor(Color.parseColor("#000000"));
        LayoutInflater.from(context).inflate(R.layout.module_mediaplayer_player, layout, true);
//        layout.setFocusable(false);
        setScaleType(PlayerType.ScaleType.SCREEN_SCALE_MATCH_PARENT);
//        BaseToast.init(context.getApplicationContext());

        //读取xml中的配置，并综合全局配置
        TypedArray typed = null;
        try {
            typed = context.obtainStyledAttributes(attrs, R.styleable.VideoPlayer);
            int v = typed.getInt(R.styleable.VideoPlayer_screenScaleType, -110);
            if (v != -110) {
                setScaleType(v);
            }
        } catch (Exception e) {
        }
        if (null != typed) {
            typed.recycle();
        }
    }

    @Override
    default ControllerLayout getControlLayout() {
        boolean isFull = isFull();
        return getControlLayout(isFull);
    }
}
