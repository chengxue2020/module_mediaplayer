package lib.kalu.mediaplayer.core.kernel.video.vlc;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.video.KernelEvent;
import lib.kalu.mediaplayer.core.kernel.video.KernelFactory;

@Keep
public class VideoVlcPlayerFactory implements KernelFactory<VideoVlcPlayer> {

    private VideoVlcPlayerFactory() {
    }

    public static VideoVlcPlayerFactory build() {
        return new VideoVlcPlayerFactory();
    }

    @Override
    public VideoVlcPlayer createKernel(@NonNull Context context, @NonNull KernelEvent event) {
        return new VideoVlcPlayer(event);
    }
}
