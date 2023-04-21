package lib.kalu.mediaplayer.core.kernel.audio.exo2;

import androidx.annotation.Keep;

import lib.kalu.mediaplayer.core.kernel.audio.MusicKernelFactory;

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
