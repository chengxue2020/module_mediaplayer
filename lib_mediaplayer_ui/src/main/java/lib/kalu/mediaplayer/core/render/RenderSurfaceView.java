package lib.kalu.mediaplayer.core.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.core.kernel.KernelApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

/**
 * <pre>
 *     desc  : 重写SurfaceView，适配视频的宽高和旋转
 * </pre>
 */
public class RenderSurfaceView extends SurfaceView implements RenderApi {

    /**
     * 优点：可以在一个独立的线程中进行绘制，不会影响主线程；使用双缓冲机制，播放视频时画面更流畅
     * 缺点：Surface不在View hierachy中，它的显示也不受View的属性控制，所以不能进行平移，缩放等变换，
     * 也不能放在其它ViewGroup中。SurfaceView 不能嵌套使用。
     * <p>
     * SurfaceView双缓冲
     * 1.SurfaceView在更新视图时用到了两张Canvas，一张frontCanvas和一张backCanvas。
     * 2.每次实际显示的是frontCanvas，backCanvas存储的是上一次更改前的视图，当使用lockCanvas（）获取画布时，
     * 得到的实际上是backCanvas而不是正在显示的frontCanvas，之后你在获取到的backCanvas上绘制新视图，
     * 再unlockCanvasAndPost（canvas）此视图，那么上传的这张canvas将替换原来的frontCanvas作为新的frontCanvas，
     * 原来的frontCanvas将切换到后台作为backCanvas。
     */

    @Nullable
    private KernelApi mKernel;
    @Nullable
    private Surface mSurface;

    public RenderSurfaceView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setFocusable(false);
        SurfaceHolder holder = this.getHolder();
        //holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //类型必须设置成SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS
//        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(callback);
//        setZOrderOnTop(true);
//        setZOrderMediaOverlay(true);
    }

    @Override
    public void releaseReal() {
        if (null != mSurface) {
            mSurface.release();
        }
        if (callback != null) {
            getHolder().addCallback(null);
            getHolder().removeCallback(callback);
        }
    }

    @Override
    public void setKernel(@NonNull KernelApi kernel) {
        this.mKernel = kernel;
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
    public View getReal() {
        return this;
    }

    @Override
    public Bitmap doScreenShot() {
        return getDrawingCache();
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

    /**
     * 释放资源
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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


    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        /**
         * 创建的时候调用该方法
         * @param holder                        holder
         */
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            MPLogUtil.log("RenderApi => surfaceCreated => " + this);
            if (mKernel != null) {
                mSurface = holder.getSurface();
                mKernel.setSurface(mSurface);
            }
        }

        /**
         * 视图改变的时候调用方法
         * @param holder
         * @param format
         * @param width
         * @param height
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            MPLogUtil.log("RenderApi => surfaceChanged => " + this);
//            if (mKernel != null) {
//                mSurface = holder.getSurface();
//                mKernel.setSurface(mSurface);
//            }
        }

        /**
         * 销毁的时候调用该方法
         * @param holder
         */
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    };
}
