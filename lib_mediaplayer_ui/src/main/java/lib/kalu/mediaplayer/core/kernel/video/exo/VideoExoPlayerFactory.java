package lib.kalu.mediaplayer.core.kernel.video.exo;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.video.KernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.KernelFactory;
import lib.kalu.mediaplayer.core.player.api.PlayerApi;
import lib.kalu.mediaplayer.core.player.api.PlayerApiExternalMusic;

@Keep
public final class VideoExoPlayerFactory implements KernelFactory<VideoExoPlayer> {

    private VideoExoPlayerFactory() {
    }

    public static VideoExoPlayerFactory build() {
        return new VideoExoPlayerFactory();
    }

    @Override
    public VideoExoPlayer createKernel(@NonNull PlayerApi playerApi, @NonNull KernelApiEvent event) {
        return new VideoExoPlayer(playerApi, event);
    }
}
