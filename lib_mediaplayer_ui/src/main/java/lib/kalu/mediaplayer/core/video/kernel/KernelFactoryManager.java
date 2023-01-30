package lib.kalu.mediaplayer.core.video.kernel;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.video.kernel.exo.ExoFactory;
import lib.kalu.mediaplayer.core.video.kernel.ijk.IjkFactory;
import lib.kalu.mediaplayer.core.video.kernel.android.AndroidFactory;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.video.kernel.vlc.VlcFactory;

/**
 * @description: 工具类
 * @date: 2021-05-12 14:41
 */
@Keep
public final class KernelFactoryManager {

    public static KernelFactory getFactory(@PlayerType.KernelType int type) {
        // ijk
        if (type == PlayerType.KernelType.IJK) {
            return IjkFactory.build();
        }
        // exo
        else if (type == PlayerType.KernelType.EXO) {
            return ExoFactory.build();
        }
        // vlc
        else if (type == PlayerType.KernelType.VLC) {
            return VlcFactory.build();
        }
        // android
        else {
            return AndroidFactory.build();
        }
    }

    public static KernelApi getKernel(@NonNull Context context, @PlayerType.KernelType.Value int type, @NonNull KernelEvent event) {
        // ijk
        if (type == PlayerType.KernelType.IJK) {
            return getFactory(type).createKernel(context, event);
        }
        // exo
        else if (type == PlayerType.KernelType.EXO) {
            return getFactory(type).createKernel(context, event);
        }
        // vlc
        else if (type == PlayerType.KernelType.VLC) {
            return getFactory(type).createKernel(context, event);
        }
        // android
        else {
            return getFactory(type).createKernel(context, event);
        }
    }
}
