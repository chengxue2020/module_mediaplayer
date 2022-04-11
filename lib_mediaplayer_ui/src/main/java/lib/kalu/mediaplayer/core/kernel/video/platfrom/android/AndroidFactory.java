package lib.kalu.mediaplayer.core.kernel.video.platfrom.android;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.KernelFactory;

@Keep
public class AndroidFactory extends KernelFactory<AndroidMediaPlayer> {

    private AndroidFactory() {
    }

//    private static class Holder {
//        static final AndroidMediaPlayer mP = new AndroidMediaPlayer();
//    }

    public static AndroidFactory build() {
        return new AndroidFactory();
    }

    @Override
    public AndroidMediaPlayer createKernel(@NonNull Context context) {
        return new AndroidMediaPlayer();
//        return Holder.mP;
    }
}
