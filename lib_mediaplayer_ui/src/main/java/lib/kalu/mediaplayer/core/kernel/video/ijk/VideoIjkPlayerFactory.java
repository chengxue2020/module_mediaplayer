package lib.kalu.mediaplayer.core.kernel.video.ijk;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.video.KernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.KernelFactory;
import lib.kalu.mediaplayer.core.player.api.PlayerApi;
import lib.kalu.mediaplayer.core.player.api.PlayerApiExternalMusic;


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
    public VideoIjkPlayer createKernel(@NonNull PlayerApi playerApi, @NonNull KernelApiEvent event) {
        return new VideoIjkPlayer(playerApi, event);
    }
}
