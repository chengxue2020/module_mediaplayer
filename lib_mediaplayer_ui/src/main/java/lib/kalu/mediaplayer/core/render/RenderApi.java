package lib.kalu.mediaplayer.core.render;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.view.Surface;
import android.view.View;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.KernelApi;

@Keep
public interface RenderApi {

    /**
     * 释放资源
     */
    void releaseReal();

    /**
     * 获取真实的RenderView
     *
     * @return view
     */
    View getReal();

    /**
     * 关联AbstractPlayer
     *
     * @param player player
     */
    void setKernel(@NonNull KernelApi player);

    /**
     * 设置视频宽高
     *
     * @param videoWidth  宽
     * @param videoHeight 高
     */
    void setVideoSize(int videoWidth, int videoHeight);

    /**
     * 设置视频旋转角度
     *
     * @param degree 角度值
     */
    void setVideoRotation(int degree);

    /**
     * 设置screen scale type
     *
     * @param scaleType 类型
     */
    void setScaleType(@PlayerType.ScaleType.Value int scaleType);

//    @PlayerType.ScaleType.Value
//    int getScaleType();

    /**
     * 截图
     *
     * @return bitmap
     */
    Bitmap doScreenShot();

    void clearSurface();
}