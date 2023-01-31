package lib.kalu.mediaplayer.core.audio.kernel;

import androidx.annotation.Keep;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.audio.kernel.android.MusicAndroidPlayerFactory;
import lib.kalu.mediaplayer.core.audio.kernel.exo.MusicExoPlayerFactory;

@Keep
public final class MusicKernelFactoryManager {

    public static MusicKernelFactory getFactory(@PlayerType.KernelType int type) {
        if (type == PlayerType.KernelType.EXO) {
            return MusicExoPlayerFactory.build();
        } else {
            return MusicAndroidPlayerFactory.build();
        }
    }

    public static MusicKernelApi getKernel(@PlayerType.KernelType.Value int type) {
        return getFactory(type).createKernel();
    }
}
