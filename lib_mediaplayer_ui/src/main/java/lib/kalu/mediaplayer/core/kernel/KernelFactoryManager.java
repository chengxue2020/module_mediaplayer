package lib.kalu.mediaplayer.core.kernel;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.exo.ExoFactory;
import lib.kalu.mediaplayer.core.kernel.ijk.IjkFactory;
import lib.kalu.mediaplayer.core.kernel.android.AndroidFactory;
import lib.kalu.mediaplayer.config.config.ConfigType;
import lib.kalu.mediaplayer.core.kernel.vlc.VlcFactory;

/**
 * @description: 工具类
 * @date: 2021-05-12 14:41
 */
@Keep
public final class KernelFactoryManager {

    public static KernelFactory getFactory(@ConfigType.KernelType int type) {
        // ijk
        if (type == ConfigType.KernelType.IJK) {
            return IjkFactory.build();
        }
        // exo
        else if (type == ConfigType.KernelType.EXO) {
            return ExoFactory.build();
        }
        // vlc
        else if (type == ConfigType.KernelType.VLC) {
            return VlcFactory.build();
        }
        // android
        else {
            return AndroidFactory.build();
        }
    }

    public static KernelApi getKernel(@NonNull Context context, @ConfigType.KernelType.Value int type, @NonNull KernelEvent event) {
        // ijk
        if (type == ConfigType.KernelType.IJK) {
            return getFactory(type).createKernel(context, event);
        }
        // exo
        else if (type == ConfigType.KernelType.EXO) {
            return getFactory(type).createKernel(context, event);
        }
        // vlc
        else if (type == ConfigType.KernelType.VLC) {
            return getFactory(type).createKernel(context, event);
        }
        // android
        else {
            return getFactory(type).createKernel(context, event);
        }
    }
}
