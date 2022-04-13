package lib.kalu.mediaplayer.core.controller.impl;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.Dimension;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import lib.kalu.mediaplayer.widget.MediaProgressBar;
import lib.kalu.mediaplayer.widget.subtitle.widget.SimpleSubtitleView;

public interface ImplController {

    /**
     * 初始化
     */
    void init();

    /**
     * 控制器意外销毁，比如手动退出，意外崩溃等等
     */
    void destroy();

    /**
     * 设置控制器布局文件，子类必须实现
     */
    int initLayout();

    /*******************/

    /**
     * 显示播放控制菜单
     */
    void hide();

    /**
     * 隐藏控制视图
     */
    void show();

    /*******************/

    /**
     * 开始控制视图自动隐藏倒计时
     */
    void startFadeOut();

    /**
     * 取消控制视图自动隐藏倒计时
     */
    void stopFadeOut();

    /**
     * 控制视图是否处于显示状态
     */
    boolean isShowing();

    /**
     * 设置锁定状态
     *
     * @param locked 是否锁定
     */
    void setLocked(boolean locked);

    /**
     * 是否处于锁定状态
     */
    boolean isLocked();

    /**
     * 开始刷新进度
     */
    void startProgress();

    /**
     * 停止刷新进度
     */
    void stopProgress();

    /**
     * 是否需要适配刘海
     */
    boolean hasCutout();

    /**
     * 获取刘海的高度
     */
    int getCutoutHeight();


//    /************************/
//
//    @Nullable
//    default TextView findComponentErrorText() {
//        return null;
//    }
//
//    @Nullable
//    default ImageView findComponentErrorImage() {
//        return null;
//    }
//
//    @Nullable
//    default ImageView findPrepareBackground() {
//        return null;
//    }
//
//    @Nullable
//    default View findPrepareProgress() {
//        return null;
//    }
//
//    @Nullable
//    default View findCenterProgress() {
//        return null;
//    }
//
//    @Nullable
//    default SimpleSubtitleView findSubtitle() {
//        return null;
//    }
//
//    @Nullable
//    default TextView findPrepareTip() {
//        return null;
//    }
//
//    default void setComponentPrepareBackgroundColor(@ColorInt int color) {
//        ImageView view = findPrepareBackground();
//        if (null == view)
//            return;
//        view.setImageDrawable(null);
//        view.setBackgroundColor(color);
//    }
//
//    default void setComponentPrepareBackgroundResource(@DrawableRes int resId) {
//        ImageView view = findPrepareBackground();
//        if (null == view)
//            return;
//        view.setBackgroundColor(Color.TRANSPARENT);
//        view.setImageResource(resId);
//    }
//
//    default void setComponentPrepareBackgroundDrawable(@NonNull Drawable drawable) {
//        ImageView view = findPrepareBackground();
//        if (null == view)
//            return;
//        view.setBackgroundColor(Color.TRANSPARENT);
//        view.setImageDrawable(drawable);
//    }
//
//    @SuppressLint("UseCompatLoadingForDrawables")
//    default void setComponentPrepareProgressBarDimens(@DimenRes int resId) {
//        View view = findPrepareProgress();
//        if (null == view)
//            return;
//        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//        if (null == layoutParams)
//            return;
//        try {
//            Resources resources = view.getResources();
//            int offset = resources.getDimensionPixelOffset(resId);
//            layoutParams.width = offset;
//            layoutParams.height = offset;
//            view.setLayoutParams(layoutParams);
//        } catch (Exception e) {
//        }
//    }
//
//
//    @SuppressLint("UseCompatLoadingForDrawables")
//    default void setComponentPrepareProgressBarCount(@NonNull int count) {
//        View view = findPrepareProgress();
//        if (null == view || !(view instanceof MediaProgressBar))
//            return;
//        try {
//            ((MediaProgressBar) view).setCount(count);
//        } catch (Exception e) {
//        }
//    }
//
//    @SuppressLint("UseCompatLoadingForDrawables")
//    default void setComponentPrepareProgressBarRate(@NonNull float rate) {
//        View view = findPrepareProgress();
//        if (null == view || !(view instanceof MediaProgressBar))
//            return;
//        try {
//            ((MediaProgressBar) view).setRate(rate);
//        } catch (Exception e) {
//        }
//    }
//
//    @SuppressLint("UseCompatLoadingForDrawables")
//    default void setComponentPrepareProgressBarRadius(@DimenRes int resId) {
//        View view = findPrepareProgress();
//        if (null == view || !(view instanceof MediaProgressBar))
//            return;
//        try {
//            ((MediaProgressBar) view).setRadius(resId);
//        } catch (Exception e) {
//        }
//    }
//
////    @SuppressLint("UseCompatLoadingForDrawables")
////    default void setComponentPrepareProgressBarIndeterminateDrawable(@DrawableRes int resId) {
////        View view = findPrepareProgress();
////        if (null == view || (view instanceof ProgressBar))
////            return;
////        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
////        if (null == layoutParams)
////            return;
////        try {
////            Resources resources = view.getResources();
////            Drawable drawable = resources.getDrawable(resId);
////            layoutParams.width = drawable.getIntrinsicWidth();
////            layoutParams.height = drawable.getIntrinsicHeight();
////            view.setLayoutParams(layoutParams);
////            ((ProgressBar) view).setIndeterminateDrawable(drawable);
////        } catch (Exception e) {
////        }
////    }
//
//    default void setComponentPrepareText(@NonNull String text) {
//
//        if (null == text || text.length() <= 0)
//            return;
//
//        TextView textView = findPrepareTip();
//        if (null == textView)
//            return;
//
//        try {
//            textView.setText(text);
//        } catch (Exception e) {
//        }
//    }
//
//    default void setComponentPrepareText(@StringRes int res) {
//
//        TextView textView = findPrepareTip();
//        if (null == textView)
//            return;
//
//        try {
//            textView.setText(res);
//        } catch (Exception e) {
//        }
//    }
//
//    default void setComponentPrepareTextColor(@ColorRes int id) {
//
//        TextView textView = findPrepareTip();
//        if (null == textView)
//            return;
//
//        try {
//            int color = textView.getResources().getColor(id);
//            textView.setTextColor(color);
//        } catch (Exception e) {
//        }
//    }
//
//    default void setComponentPrepareTextSize(@DimenRes int dimen) {
//
//        TextView textView = findPrepareTip();
//        if (null == textView)
//            return;
//
//        try {
//            float dimension = textView.getResources().getDimension(dimen);
//            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimension);
//        } catch (Exception e) {
//        }
//    }
//
//    /********************  ComponentError  **********************/
//
//    default void setComponentErrorImage(@DrawableRes int res) {
//
//        ImageView imageView = findComponentErrorImage();
//        if (null == imageView)
//            return;
//
//        try {
//            imageView.setImageResource(res);
//        } catch (Exception e) {
//        }
//    }
//
//    default void setComponentErrorText(@NonNull String msg) {
//
//        TextView textView = findComponentErrorText();
//        if (null == textView)
//            return;
//
//        try {
//            textView.setText(msg);
//        } catch (Exception e) {
//        }
//    }
//
//    default void setComponentErrorText(@StringRes int res) {
//
//        TextView textView = findComponentErrorText();
//        if (null == textView)
//            return;
//
//        try {
//            textView.setText(res);
//        } catch (Exception e) {
//        }
//    }
//
//    default void setComponentErrorTextSize(@DimenRes int dimen) {
//
//        TextView textView = findComponentErrorText();
//        if (null == textView)
//            return;
//
//        try {
//            int offset = textView.getResources().getDimensionPixelOffset(dimen);
//            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, offset);
//        } catch (Exception e) {
//        }
//    }
//
//    default void setComponentErrorTextColor(@ColorRes int id) {
//
//        TextView textView = findComponentErrorText();
//        if (null == textView)
//            return;
//
//        try {
//            int color = textView.getResources().getColor(id);
//            textView.setTextColor(color);
//        } catch (Exception e) {
//        }
//    }
//
//    /********************  ComponentError  **********************/
//
//    /********************  ComponentCenter  **********************/
//
//    @SuppressLint("UseCompatLoadingForDrawables")
//    default void setComponentCenterProgressBarDimens(@DimenRes int resId) {
//        View view = findCenterProgress();
//        if (null == view)
//            return;
//        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//        if (null == layoutParams)
//            return;
//        try {
//            Resources resources = view.getResources();
//            int offset = resources.getDimensionPixelOffset(resId);
//            layoutParams.width = offset;
//            layoutParams.height = offset;
//            view.setLayoutParams(layoutParams);
//        } catch (Exception e) {
//        }
//    }
//
//    @SuppressLint("UseCompatLoadingForDrawables")
//    default void setComponentCenterProgressBarCount(@NonNull int count) {
//        View view = findCenterProgress();
//        if (null == view || !(view instanceof MediaProgressBar))
//            return;
//        try {
//            ((MediaProgressBar) view).setCount(count);
//        } catch (Exception e) {
//        }
//    }
//
//    @SuppressLint("UseCompatLoadingForDrawables")
//    default void setComponentCenterProgressBarRate(@NonNull float rate) {
//        View view = findCenterProgress();
//        if (null == view || !(view instanceof MediaProgressBar))
//            return;
//        try {
//            ((MediaProgressBar) view).setRate(rate);
//        } catch (Exception e) {
//        }
//    }
//
//    @SuppressLint("UseCompatLoadingForDrawables")
//    default void setComponentCenterProgressBarRadius(@DimenRes int resId) {
//        View view = findCenterProgress();
//        if (null == view || !(view instanceof MediaProgressBar))
//            return;
//        try {
//            ((MediaProgressBar) view).setRadius(resId);
//        } catch (Exception e) {
//        }
//    }
//
//    /********************  ComponentCenter  **********************/
//
//
//    /********************  ComponentCenter  **********************/
//
//    default void setComponentSubtitlePath(@NonNull String subtitlePath) {
//
//        View view = findSubtitle();
//        if (null == view || !(view instanceof SimpleSubtitleView))
//            return;
//        try {
//            ((SimpleSubtitleView) view).setSubtitlePath(subtitlePath);
//        } catch (Exception e) {
//        }
//    }
//
//    default void setImageResource(@NonNull View view, @IdRes int id, @DrawableRes int res) {
//        try {
//            ImageView temp = view.findViewById(id);
//            temp.setImageResource(res);
//        } catch (Exception e) {
//        }
//    }
//
//    default void setBackgroundColor(@NonNull View view, @IdRes int id, @ColorInt int color) {
//        try {
//            View temp = view.findViewById(id);
//            temp.setBackgroundColor(color);
//        } catch (Exception e) {
//        }
//    }
}
