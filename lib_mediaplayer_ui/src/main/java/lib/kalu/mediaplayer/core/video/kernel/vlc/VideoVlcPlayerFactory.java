package lib.kalu.mediaplayer.core.video.kernel.vlc;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.video.kernel.KernelEvent;
import lib.kalu.mediaplayer.core.video.kernel.KernelFactory;

@Keep
public class VideoVlcPlayerFactory implements KernelFactory<VideoVlcPlayer> {

    private VideoVlcPlayerFactory() {
    }

//    private static class Holder {
//        static final AndroidMediaPlayer mP = new AndroidMediaPlayer();
//    }

    public static VideoVlcPlayerFactory build() {
        return new VideoVlcPlayerFactory();
    }

    @Override
    public VideoVlcPlayer createKernel(@NonNull Context context, @NonNull KernelEvent event) {
        return new VideoVlcPlayer(event);
    }
}
