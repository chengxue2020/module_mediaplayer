package lib.kalu.mediaplayer.core.controller.impl;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import lib.kalu.mediaplayer.core.controller.base.ControllerWrapper;
import lib.kalu.mediaplayer.util.MPLogUtil;


@Keep
public interface ComponentApi {

    /**
     * 这个是绑定视图操作
     *
     * @param controllerWrapper 自定义控制器包装类
     */
    void attach(@NonNull ControllerWrapper controllerWrapper);

    /**
     * 获取该自定义视图view对象
     *
     * @return 视图view对象
     */
    View getView();

    /**
     * 视图显示发生变化监听
     *
     * @param isVisible 是否可见
     * @param anim      动画
     */
    void onVisibilityChanged(boolean isVisible, Animation anim);

    /**
     * 播放状态
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
     *
     * @param playState 播放状态，主要是指播放器的各种状态
     */
    void onPlayStateChanged(@NonNull int playState);

    /**
     * 播放模式
     * 普通模式，小窗口模式，正常模式三种其中一种
     * MODE_NORMAL              普通模式
     * MODE_FULL_SCREEN         全屏模式
     * MODE_TINY_WINDOW         小屏模式
     *
     * @param playerState 播放模式
     */
    void onWindowStateChanged(int playerState);

    /**
     * 锁屏状态监听
     *
     * @param isLocked 是否锁屏
     */
    void onLockStateChanged(boolean isLocked);

    default void restart(@NonNull ControllerWrapper wrapper) {
        if (null == wrapper)
            return;
        wrapper.restart();
    }

//    default void finish(@NonNull Context context, @NonNull ControllerWrapper wrapper) {
//        if (null == wrapper)
//            return;
//        boolean full = wrapper.isFullScreen();
//        if (!full)
//            return;
//        Activity activity = PlayerUtils.scanForActivity(context);
//        if (null == activity || activity.isFinishing())
//            return;
//        wrapper.stopFullScreen();
//        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//    }

    /*************/

    default void setImageResource(@NonNull View layout, @IdRes int id, @DrawableRes int value) {
        try {
            ImageView view = layout.findViewById(id);
            view.setImageResource(value);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    default void setTextColor(@NonNull View layout, @IdRes int id, @ColorInt int value) {
        try {
            TextView view = layout.findViewById(id);
            view.setTextColor(value);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    default void setTextSize(@NonNull View layout, @IdRes int id, @DimenRes int value) {
        try {
            TextView view = layout.findViewById(id);
            int offset = layout.getResources().getDimensionPixelOffset(value);
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, offset);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    default void setText(@NonNull View layout, @IdRes int id, @StringRes int value) {
        try {
            TextView view = layout.findViewById(id);
            view.setText(value);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    default void setText(@NonNull View layout, @IdRes int id, @NonNull String value) {
        try {
            TextView view = layout.findViewById(id);
            view.setText(value);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    default void setCompoundDrawablesWithIntrinsicBounds(@NonNull View layout, @IdRes int id, @DrawableRes int left, @DrawableRes int top, @DrawableRes int right, @DrawableRes int bottom) {
        try {
            TextView view = layout.findViewById(id);
            view.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    default void setDimens(@NonNull View layout, @IdRes int id, @DimenRes int value) {
        try {
            View view = layout.findViewById(id);
            ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
            int offset = layout.getResources().getDimensionPixelOffset(value);
            layoutParams.width = offset;
            layoutParams.height = offset;
            view.setLayoutParams(layoutParams);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    default void setBackgroundColor(@NonNull View layout, @IdRes int id, @ColorInt int value) {
        try {
            View view = layout.findViewById(id);
            view.setBackgroundColor(value);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    default void setBackgroundResource(@NonNull View layout, @IdRes int id, @DrawableRes int resid) {
        try {
            View view = layout.findViewById(id);
            view.setBackgroundResource(resid);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    /******************/

    default void show() {
    }

    default void gone() {
    }

    /******************/

    default void seekForwardDown(boolean enable) {
    }

    default void seekForwardUp(boolean enable) {
    }

    default void seekRewindDown(boolean enable) {
    }

    default void seekRewindUp(boolean enable) {
    }

    default void onSeekProgressUpdate(@NonNull int position, @NonNull int duration) {
    }

    default void onSeekTimeUpdate(@NonNull int position, @NonNull int duration) {
    }

    default void onSeekPlaying(@NonNull int position) {
    }

    default void onUpdateTimeMillis(@NonNull long seek, @NonNull long position, @NonNull long duration) {
    }
}
