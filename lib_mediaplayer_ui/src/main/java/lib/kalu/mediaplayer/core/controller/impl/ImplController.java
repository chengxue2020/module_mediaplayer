/*
Copyright 2017 yangchong211（github.com/yangchong211）

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package lib.kalu.mediaplayer.core.controller.impl;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * <pre>
 *     @author yangchong
 *     blog  : https://github.com/yangchong211
 *     time  : 2017/11/21
 *     desc  : 视频控制器接口
 *     revise: 定义一些设置视图属性接口
 * </pre>
 */
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


    /************************/

    @Nullable
    default ImageView findPrepareBackground() {
        return null;
    }

    @Nullable
    default ProgressBar findPrepareProgress() {
        return null;
    }

    @Nullable
    default TextView findPrepareTip() {
        return null;
    }

    default void setPrepareBackgroundColor(@ColorInt int color) {
        ImageView view = findPrepareBackground();
        if (null == view)
            return;
        view.setImageDrawable(null);
        view.setBackgroundColor(color);
    }

    default void setPrepareBackgroundResource(@DrawableRes int resId) {
        ImageView view = findPrepareBackground();
        if (null == view)
            return;
        view.setBackgroundColor(Color.TRANSPARENT);
        view.setImageResource(resId);
    }

    default void setPrepareBackgroundDrawable(@NonNull Drawable drawable) {
        ImageView view = findPrepareBackground();
        if (null == view)
            return;
        view.setBackgroundColor(Color.TRANSPARENT);
        view.setImageDrawable(drawable);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    default void setPrepareProgressBarIndeterminateDrawable(@DrawableRes int resId) {
        ProgressBar progressBar = findPrepareProgress();
        if (null == progressBar)
            return;
        ViewGroup.LayoutParams layoutParams = progressBar.getLayoutParams();
        if (null == layoutParams)
            return;
////                float v1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, width, resources.getDisplayMetrics());
////                float v2 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, height, resources.getDisplayMetrics());
////                layoutParams.width = (int) v1;
////                layoutParams.height = (int) v2;
        try {
            Resources resources = progressBar.getResources();
            Drawable drawable = resources.getDrawable(resId);
            progressBar.setIndeterminateDrawable(drawable);
            layoutParams.width = drawable.getIntrinsicWidth();
            layoutParams.height = drawable.getIntrinsicHeight();
            progressBar.setLayoutParams(layoutParams);
        } catch (Exception e) {
        }
//        layoutParams.width = resources.getDimensionPixelOffset(width);
//        layoutParams.height = resources.getDimensionPixelOffset(height);
    }

    default void setPrepareTipText(@NonNull String text) {

        if (null == text || text.length() <= 0)
            return;

        TextView textView = findPrepareTip();
        if (null == textView)
            return;

        try {
            textView.setText(text);
        } catch (Exception e) {
        }
    }

    default void setPrepareTipColor(@ColorRes int id) {

        TextView textView = findPrepareTip();
        if (null == textView)
            return;

        try {
            int color = textView.getResources().getColor(id);
            textView.setTextColor(color);
        } catch (Exception e) {
        }
    }

    default void setPrepareTipSize(@DimenRes int dimen) {

        TextView textView = findPrepareTip();
        if (null == textView)
            return;

        try {
            float dimension = textView.getResources().getDimension(dimen);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimension);
        } catch (Exception e) {
        }
    }

    /************************/
}
