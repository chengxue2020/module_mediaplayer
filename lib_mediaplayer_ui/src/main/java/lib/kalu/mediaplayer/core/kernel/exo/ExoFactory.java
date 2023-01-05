package lib.kalu.mediaplayer.core.kernel.exo;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.KernelEvent;
import lib.kalu.mediaplayer.core.kernel.KernelFactory;

@Keep
public final class ExoFactory implements KernelFactory<ExoMediaPlayer> {

    private ExoFactory() {
    }

    public static ExoFactory build() {
        return new ExoFactory();
    }

    @Override
    public ExoMediaPlayer createKernel(@NonNull Context context, @NonNull KernelEvent event) {
        return new ExoMediaPlayer(event);
    }
}
