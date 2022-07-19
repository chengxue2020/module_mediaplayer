package lib.kalu.mediaplayer.core.kernel.video.platfrom.vlc;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

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
    public VlcMediaPlayer createKernel(@NonNull Context context) {
        return new VlcMediaPlayer();
//        return Holder.mP;
    }
}
