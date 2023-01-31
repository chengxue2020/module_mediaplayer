package lib.kalu.mediaplayer.core.video.kernel;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.video.kernel.exo.VideoExoPlayerFactory;
import lib.kalu.mediaplayer.core.video.kernel.ijk.VideoIjkPlayerFactory;
import lib.kalu.mediaplayer.core.video.kernel.android.VideoAndroidPlayerFactory;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.video.kernel.vlc.VideoVlcPlayerFactory;

/**
 * @description: 工具类
 * @date: 2021-05-12 14:41
 */
@Keep
public final class KernelFactoryManager {

    public static KernelFactory getFactory(@PlayerType.KernelType int type) {
        // ijk
        if (type == PlayerType.KernelType.IJK) {
            return VideoIjkPlayerFactory.build();
        }
        // exo
        else if (type == PlayerType.KernelType.EXO) {
            return VideoExoPlayerFactory.build();
        }
        // vlc
        else if (type == PlayerType.KernelType.VLC) {
            return VideoVlcPlayerFactory.build();
        }
        // android
        else {
            return VideoAndroidPlayerFactory.build();
        }
    }

    public static KernelApi getKernel(@NonNull Context context, @PlayerType.KernelType.Value int type, @NonNull KernelEvent event) {
        return getFactory(type).createKernel(context, event);
    }
}
