package lib.kalu.mediaplayer.core.video.kernel.ijk;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.video.kernel.KernelEvent;
import lib.kalu.mediaplayer.core.video.kernel.KernelFactory;


@Keep
public class IjkFactory implements KernelFactory<IjkMediaPlayer> {

    private IjkFactory() {
    }

//    private static class Holder {
//        static final IjkMediaPlayer mP = new IjkMediaPlayer();
//    }

    public static IjkFactory build() {
        return new IjkFactory();
    }

    @Override
    public IjkMediaPlayer createKernel(@NonNull Context context, @NonNull KernelEvent event) {
//        return Holder.mP;
        return new IjkMediaPlayer(event);
    }
}
