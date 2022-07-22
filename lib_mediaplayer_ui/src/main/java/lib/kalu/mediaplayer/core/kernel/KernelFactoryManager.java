package lib.kalu.mediaplayer.core.kernel;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.video.exo.ExoFactory;
import lib.kalu.mediaplayer.core.kernel.video.ijk.IjkFactory;
import lib.kalu.mediaplayer.core.kernel.video.android.AndroidFactory;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.vlc.VlcFactory;

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
            return IjkFactory.build().createKernel(context, event);
        }
        // exo
        else if (type == PlayerType.KernelType.EXO) {
            return ExoFactory.build().createKernel(context, event);
        }
        // vlc
        else if (type == PlayerType.KernelType.VLC) {
            return VlcFactory.build().createKernel(context, event);
        }
        // android
        else {
            return AndroidFactory.build().createKernel(context, event);
        }
    }
}
