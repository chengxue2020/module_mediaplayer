package lib.kalu.mediaplayer.core.kernel.video.android;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.video.KernelEvent;
import lib.kalu.mediaplayer.core.kernel.video.KernelFactory;

@Keep
public class VideoAndroidPlayerFactory implements KernelFactory<VideoAndroidPlayer> {

    private VideoAndroidPlayerFactory() {
    }

//    private static class Holder {
//        static final AndroidMediaPlayer mP = new AndroidMediaPlayer();
//    }

    public static VideoAndroidPlayerFactory build() {
        return new VideoAndroidPlayerFactory();
    }

    @Override
    public VideoAndroidPlayer createKernel(@NonNull Context context, @NonNull KernelEvent event) {
        return new VideoAndroidPlayer(event);
//        return Holder.mP;
    }
}
