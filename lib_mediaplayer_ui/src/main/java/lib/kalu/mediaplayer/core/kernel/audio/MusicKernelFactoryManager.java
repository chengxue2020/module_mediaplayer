package lib.kalu.mediaplayer.core.kernel.audio;

import androidx.annotation.Keep;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.audio.android.MusicAndroidPlayerFactory;
import lib.kalu.mediaplayer.core.kernel.audio.exo.MusicExoPlayerFactory;
import lib.kalu.mediaplayer.core.kernel.audio.ijk.MusicIjkPlayerFactory;

@Keep
public final class MusicKernelFactoryManager {

    public static MusicKernelFactory getFactory(@PlayerType.KernelType int type) {
        if (type == PlayerType.KernelType.EXO) {
            return MusicExoPlayerFactory.build();
        } else if (type == PlayerType.KernelType.IJK) {
            return MusicIjkPlayerFactory.build();
        } else {
            return MusicAndroidPlayerFactory.build();
        }
    }

    public static MusicKernelApi getKernel(@PlayerType.KernelType.Value int type) {
        return getFactory(type).createKernel();
    }
}
