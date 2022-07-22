package lib.kalu.mediaplayer.core.kernel.video.vlc;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import org.checkerframework.checker.units.qual.K;

import lib.kalu.mediaplayer.core.kernel.KernelEvent;
import lib.kalu.mediaplayer.core.kernel.KernelFactory;

@Keep
public class VlcFactory extends KernelFactory<VlcMediaPlayer> {

    private VlcFactory() {
    }

//    private static class Holder {
//        static final AndroidMediaPlayer mP = new AndroidMediaPlayer();
//    }

    public static VlcFactory build() {
        return new VlcFactory();
    }

    @Override
    public VlcMediaPlayer createKernel(@NonNull Context context, @NonNull KernelEvent event) {
        return new VlcMediaPlayer(event);
    }
}
