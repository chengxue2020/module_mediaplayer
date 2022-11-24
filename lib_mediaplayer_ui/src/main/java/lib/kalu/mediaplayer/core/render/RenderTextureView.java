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
package lib.kalu.mediaplayer.core.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.core.kernel.KernelApi;
import lib.kalu.mediaplayer.util.MPLogUtil;


/**
 * <pre>
 *     desc  : 重写TextureView，适配视频的宽高和旋转
 *     revise: 1.继承View，具有view的特性，比如移动，旋转，缩放，动画等变化。支持截图
 *             8.必须在硬件加速的窗口中使用，占用内存比SurfaceView高，在5.0以前在主线程渲染，5.0以后有单独的渲染线程。
 * </pre>
 */
public class RenderTextureView extends TextureView implements RenderApi {

    private SurfaceTexture mSurfaceTexture;
    @Nullable
    private KernelApi mKernel;
    private Surface mSurface;

    public RenderTextureView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setFocusable(false);
        setSurfaceTextureListener(listener);
    }

    /**
     * 释放资源
     */
    @Override
    public void releaseReal() {
        if (mSurface != null) {
            mSurface.release();
        }
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
        }
    }

    /**
     * 获取真实的RenderView
     *
     * @return view
     */
    @Override
    public View getReal() {
        return this;
    }

    @Override
    public void setKernel(@NonNull KernelApi player) {
        this.mKernel = player;
    }

    @Override
    public void setVideoSize(int videoWidth, int videoHeight) {

    }

    @Override
    public void setVideoRotation(int degree) {

    }

    @Override
    public void setScaleType(int scaleType) {

    }

    @Override
    public Bitmap doScreenShot() {
        return getBitmap();
    }

    @Override
    public void clearSurface() {
        try {
            Canvas canvas = mSurface.lockCanvas(null);
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mSurface.unlockCanvasAndPost(canvas);
        }catch (Exception e){
        }
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int[] measuredSize = mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec);
//        setMeasuredDimension(measuredSize[0], measuredSize[1]);
//    }

    /**
     * 记得一定要重新写这个方法，如果角度发生了变化，就重新绘制布局
     * 设置视频旋转角度
     *
     * @param rotation 角度
     */
    @Override
    public void setRotation(float rotation) {
        if (rotation != getRotation()) {
            super.setRotation(rotation);
            requestLayout();
        }
    }

    private SurfaceTextureListener listener = new SurfaceTextureListener() {
        /**
         * SurfaceTexture准备就绪
         * @param surfaceTexture            surface
         * @param width                     WIDTH
         * @param height                    HEIGHT
         */
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            MPLogUtil.log("RenderApi => onSurfaceTextureAvailable => " + this);
            if (mSurfaceTexture != null) {
                setSurfaceTexture(mSurfaceTexture);
            } else {
                mSurfaceTexture = surfaceTexture;
                mSurface = new Surface(surfaceTexture);
                if (mKernel != null) {
                    mKernel.setSurface(mSurface);
                }
            }
        }

        /**
         * SurfaceTexture缓冲大小变化
         * @param surface                   surface
         * @param width                     WIDTH
         * @param height                    HEIGHT
         */
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            MPLogUtil.log("RenderApi => onSurfaceTextureSizeChanged => " + this);
        }

        /**
         * SurfaceTexture即将被销毁
         * @param surface                   surface
         */
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        /**
         * SurfaceTexture通过updateImage更新
         * @param surface                   surface
         */
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            MPLogUtil.log("RenderApi => onSurfaceTextureUpdated => " + this);
        }
    };
}