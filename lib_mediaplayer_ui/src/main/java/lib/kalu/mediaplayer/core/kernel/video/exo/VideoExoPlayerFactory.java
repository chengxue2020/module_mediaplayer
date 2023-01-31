package lib.kalu.mediaplayer.core.kernel.video.exo;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.video.KernelEvent;
import lib.kalu.mediaplayer.core.kernel.video.KernelFactory;

@Keep
public final class VideoExoPlayerFactory implements KernelFactory<VideoExoPlayer> {

    private VideoExoPlayerFactory() {
    }

    public static VideoExoPlayerFactory build() {
        return new VideoExoPlayerFactory();
    }

    @Override
    public VideoExoPlayer createKernel(@NonNull Context context, @NonNull KernelEvent event) {
        return new VideoExoPlayer(event);
    }
}
