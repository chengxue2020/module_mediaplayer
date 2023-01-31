package lib.kalu.mediaplayer.core.audio.kernel.android;

import androidx.annotation.Keep;

import lib.kalu.mediaplayer.core.audio.kernel.MusicKernelFactory;

@Keep
public final class MusicAndroidPlayerFactory implements MusicKernelFactory<MusicAndroidPlayer> {

    private MusicAndroidPlayerFactory() {
    }

    public static MusicAndroidPlayerFactory build() {
        return new MusicAndroidPlayerFactory();
    }

    @Override
    public MusicAndroidPlayer createKernel() {
        return new MusicAndroidPlayer();
    }
}
