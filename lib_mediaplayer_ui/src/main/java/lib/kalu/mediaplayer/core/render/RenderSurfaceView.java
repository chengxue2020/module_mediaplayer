package lib.kalu.mediaplayer.core.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.core.kernel.video.KernelApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

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
public class RenderSurfaceView extends SurfaceView implements RenderApi {

    @Nullable
    private Handler mHandler;
    @Nullable
    private KernelApi mKernel;
    @Nullable
    private Surface mSurface;
    @Nullable
    private SurfaceHolder.Callback mSurfaceHolderCallback;

    public RenderSurfaceView(Context context) {
        super(context);
        init();
    }

    @Override
    public void init() {
        MPLogUtil.log("RenderSurfaceView => init => " + this);
        setFocusable(false);
//        mMeasureHelper = new MeasureHelper();
        SurfaceHolder holder = this.getHolder();
        //holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //类型必须设置成SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS
//        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolderCallback = new SurfaceHolder.Callback() {
            /**
             * 创建的时候调用该方法
             * @param holder                        holder
             */
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                MPLogUtil.log("RenderSurfaceView => surfaceCreated => " + this);
                if (mKernel != null) {
                    mSurface = holder.getSurface();
                    mKernel.setSurface(mSurface);
                }
                if (null == mHandler) {
                    mHandler = new Handler(Looper.myLooper()) {
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            super.handleMessage(msg);
                            if (null != mKernel && null != msg && msg.what == 9899) {
                                mKernel.onUpdateTimeMillis();
                                mHandler.sendEmptyMessageDelayed(9899, 100);
                            }
                        }
                    };
                    mHandler.sendEmptyMessageDelayed(9899, 100);
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
                MPLogUtil.log("RenderSurfaceView => surfaceChanged => " + this);
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
                MPLogUtil.log("RenderSurfaceView => surfaceDestroyed => " + this);
                if (null != mHandler) {
                    mHandler.removeMessages(9899);
                    mHandler.removeCallbacksAndMessages(null);
                    mHandler = null;
                }
            }
        };
        holder.addCallback(mSurfaceHolderCallback);
//        setZOrderOnTop(true);
//        setZOrderMediaOverlay(true);
    }

    @Override
    public void releaseReal() {
        MPLogUtil.log("RenderSurfaceView => releaseReal => " + this);
        if (null != mHandler) {
            mHandler.removeMessages(9899);
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mSurface != null) {
            mSurface.release();
        }
        if (mSurfaceHolderCallback != null) {
            getHolder().removeCallback(mSurfaceHolderCallback);
            mSurfaceHolderCallback = null;
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
    public String screenshot() {
        Context context = getContext();
        Bitmap bitmap = getDrawingCache();
        return saveBitmap(context, bitmap);
    }

    @Override
    public void clearCanvas() {
//        try {
//            Canvas canvas = mSurface.lockCanvas(null);
//            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//            mSurface.unlockCanvasAndPost(canvas);
//        } catch (Exception e) {
//        }
    }

    @Override
    public void updateCanvas() {
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
}
