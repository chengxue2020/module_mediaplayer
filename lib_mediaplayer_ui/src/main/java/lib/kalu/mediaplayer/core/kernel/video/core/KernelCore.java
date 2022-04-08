package lib.kalu.mediaplayer.core.kernel.video.core;

import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.core.kernel.impl.ImplKernel;
import lib.kalu.mediaplayer.core.kernel.video.listener.OnVideoPlayerChangeListener;

/**
 * @description:
 * @date: 2021-05-12 09:49
 */
public abstract class KernelCore implements ImplKernel {

    @Nullable
    private OnVideoPlayerChangeListener mOnVideoPlayerChangeListener = null;

    @Nullable
    @Override
    public void setOnVideoPlayerChangeListener(@Nullable OnVideoPlayerChangeListener onVideoPlayerChangeListener) {
        this.mOnVideoPlayerChangeListener = onVideoPlayerChangeListener;
    }

    @Nullable
    protected OnVideoPlayerChangeListener getVideoPlayerChangeListener() {
        return mOnVideoPlayerChangeListener;
    }
}
