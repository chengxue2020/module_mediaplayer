package lib.kalu.mediaplayer.core.audio.kernel.exo;

import androidx.annotation.Keep;

import lib.kalu.mediaplayer.core.audio.kernel.MusicKernelFactory;

@Keep
public final class MusicExoPlayerFactory implements MusicKernelFactory<MusicExoPlayer> {

    private MusicExoPlayerFactory() {
    }

    public static MusicExoPlayerFactory build() {
        return new MusicExoPlayerFactory();
    }

    @Override
    public MusicExoPlayer createKernel() {
        return new MusicExoPlayer();
    }
}
