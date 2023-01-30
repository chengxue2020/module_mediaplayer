package lib.kalu.mediaplayer.core.video.kernel.vlc;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.video.kernel.KernelEvent;
import lib.kalu.mediaplayer.core.video.kernel.KernelFactory;

@Keep
public class VlcFactory implements KernelFactory<VlcMediaPlayer> {

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
