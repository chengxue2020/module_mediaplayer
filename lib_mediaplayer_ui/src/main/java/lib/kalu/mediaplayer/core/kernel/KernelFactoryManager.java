package lib.kalu.mediaplayer.core.kernel;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.impl.ImplKernel;
import lib.kalu.mediaplayer.core.kernel.video.platfrom.exo.ExoFactory;
import lib.kalu.mediaplayer.core.kernel.video.platfrom.ijk.IjkFactory;
import lib.kalu.mediaplayer.core.kernel.video.platfrom.android.AndroidFactory;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.platfrom.vlc.VlcFactory;

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

    public static ImplKernel getKernel(@NonNull Context context, @PlayerType.KernelType.Value int type) {
        Toast.makeText(context, type + "", Toast.LENGTH_SHORT).show();
        // ijk
        if (type == PlayerType.KernelType.IJK) {
            return IjkFactory.build().createKernel(context);
        }
        // exo
        else if (type == PlayerType.KernelType.EXO) {
            return ExoFactory.build().createKernel(context);
        }
        // vlc
        else if (type == PlayerType.KernelType.VLC) {
            return VlcFactory.build().createKernel(context);
        }
        // android
        else {
            return AndroidFactory.build().createKernel(context);
        }
    }
}
