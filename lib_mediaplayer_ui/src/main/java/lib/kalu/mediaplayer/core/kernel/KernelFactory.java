package lib.kalu.mediaplayer.core.kernel;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.impl.ImplKernel;

/**
 * @description: 1.继承{@link VideoPlayerImpl}扩展自己的播放器。
 * @date: 2021-05-12 14:42
 */
@Keep
public abstract class KernelFactory<T extends ImplKernel> {

    public abstract T createKernel(@NonNull Context context);
}