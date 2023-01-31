package lib.kalu.mediaplayer.core.video.kernel.exo;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.video.KernelEvent;
import lib.kalu.mediaplayer.core.video.KernelFactory;

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
