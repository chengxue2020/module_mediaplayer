package lib.kalu.mediaplayer.core.kernel;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.impl.ImplKernel;
import lib.kalu.mediaplayer.core.kernel.video.platfrom.exo.ExoFactory;
import lib.kalu.mediaplayer.core.kernel.video.platfrom.ijk.IjkFactory;
import lib.kalu.mediaplayer.core.kernel.video.platfrom.android.AndroidFactory;
import lib.kalu.mediaplayer.config.PlayerType;

/**
 * @description: 工具类
 * @date: 2021-05-12 14:41
 */
@Keep
public final class KernelFactoryManager {

    public static KernelFactory getFactory(@PlayerType.PlatformType int type) {
        if (type == PlayerType.PlatformType.IJK) {
            return IjkFactory.build();
        } else if (type == PlayerType.PlatformType.EXO) {
            return ExoFactory.build();
        } else {
            return AndroidFactory.build();
        }
    }

    public static ImplKernel getKernel(@NonNull Context context, @PlayerType.PlatformType.Value int type) {
        if (type == PlayerType.PlatformType.IJK) {
            return IjkFactory.build().createKernel(context);
        } else if (type == PlayerType.PlatformType.EXO) {
            return ExoFactory.build().createKernel(context);
        } else {
            return AndroidFactory.build().createKernel(context);
        }
    }
}
