package lib.kalu.mediaplayer.core.kernel;

import androidx.annotation.Keep;
import lib.kalu.mediaplayer.config.config.ConfigType;

@Keep
public interface KernelEvent {

    default void onEvent(@ConfigType.KernelType.Value int kernel, @ConfigType.EventType.Value int event) {
    }

    default void onChanged(@ConfigType.KernelType.Value int kernel, int width, int height, int rotation) {
    }
}
