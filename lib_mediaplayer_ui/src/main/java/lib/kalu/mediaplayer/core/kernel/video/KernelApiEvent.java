package lib.kalu.mediaplayer.core.kernel.video;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;

@Keep
public interface KernelApiEvent {

    default void onUpdateTimeMillis(@NonNull boolean isLooping, @NonNull long max, @NonNull long seek, @NonNull long position, @NonNull long duration) {
    }

    default void onEvent(@PlayerType.KernelType.Value int kernel, @PlayerType.EventType.Value int event) {
    }

    default void onChanged(@PlayerType.KernelType.Value int kernel, int width, int height, int rotation) {
    }
}
