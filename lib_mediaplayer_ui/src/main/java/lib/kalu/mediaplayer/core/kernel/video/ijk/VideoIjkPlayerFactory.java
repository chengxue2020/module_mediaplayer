package lib.kalu.mediaplayer.core.kernel.video.ijk;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.video.KernelEvent;
import lib.kalu.mediaplayer.core.kernel.video.KernelFactory;


@Keep
public class VideoIjkPlayerFactory implements KernelFactory<VideoIjkPlayer> {

    private VideoIjkPlayerFactory() {
    }

//    private static class Holder {
//        static final IjkMediaPlayer mP = new IjkMediaPlayer();
//    }

    public static VideoIjkPlayerFactory build() {
        return new VideoIjkPlayerFactory();
    }

    @Override
    public VideoIjkPlayer createKernel(@NonNull Context context, @NonNull KernelEvent event) {
//        return Holder.mP;
        return new VideoIjkPlayer(event);
    }
}
