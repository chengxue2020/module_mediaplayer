package lib.kalu.mediaplayer.core.kernel;

import androidx.annotation.Keep;
import lib.kalu.mediaplayer.config.player.PlayerType;

@Keep
public interface KernelEvent {

    default void onEvent(@PlayerType.KernelType.Value int kernel, @PlayerType.EventType.Value int event) {
    }

    default void onChanged(@PlayerType.KernelType.Value int kernel, int width, int height, int rotation) {
    }
}
