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
import lib.kalu.mediaplayer.util.MPLogUtil;

/**
 * revise: 播放器基础属性获取和设置属性接口
 */
public interface PlayerApi extends PlayerApiBase, PlayerApiKernel, PlayerApiDevice, PlayerApiComponent, PlayerApiCache, PlayerApiRender {

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

    default boolean dispatchKeyEvent(@NonNull KeyEvent event) {

        boolean isFull = isFull();
        boolean isFloat = isFloat();

        // float
        if (isFloat) {
            // stopFloat
            if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                MPLogUtil.log("dispatchKeyEvent => stopFloat =>");
                stopFloat();
                return true;
            }
        }
        // full
        else if (isFull) {
            // center
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                MPLogUtil.log("dispatchKeyEvent => toggle =>");
                toggle();
                return true;
            }
            // VOLUME_UP
            else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
                return false;
            }
            // VOLUME_DOWN
            else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                return false;
            }
            // VOLUME_MUTE
            else if (isFull() && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_MUTE) {
                return false;
            }
            // VOICE_ASSIST
            else if (isFull() && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_VOICE_ASSIST) {
                return false;
            }
        }
        return false;
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
