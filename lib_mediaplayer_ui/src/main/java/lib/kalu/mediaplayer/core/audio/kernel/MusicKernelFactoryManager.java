package lib.kalu.mediaplayer.core.audio.kernel;

import androidx.annotation.Keep;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.audio.kernel.exo.MusicExoPlayerFactory;

@Keep
public final class MusicKernelFactoryManager {

    public static MusicKernelFactory getFactory(@PlayerType.KernelType int type) {
        // exo
        if (type == PlayerType.KernelType.EXO) {
            return MusicExoPlayerFactory.build();
        } else {
            return null;
        }
    }

    public static MusicKernelApi getKernel(@PlayerType.KernelType.Value int type) {
        // exo
        if (type == PlayerType.KernelType.EXO) {
            return getFactory(type).createKernel();
        }
        // android
        else {
            return null;
        }
    }
}
