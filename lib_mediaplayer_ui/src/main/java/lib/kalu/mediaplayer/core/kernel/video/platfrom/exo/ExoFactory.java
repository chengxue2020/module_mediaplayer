package lib.kalu.mediaplayer.core.kernel.video.platfrom.exo;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.KernelFactory;

@Keep
public class ExoFactory extends KernelFactory<ExoMediaPlayer> {

    private ExoFactory() {
    }
//
//    private static class Holder {
//        static final ExoMediaPlayer mP = new ExoMediaPlayer();
//    }

    public static ExoFactory build() {
        return new ExoFactory();
    }

    @Override
    public ExoMediaPlayer createKernel(@NonNull Context context) {
        return new ExoMediaPlayer();
//        return Holder.mP;
    }
}
