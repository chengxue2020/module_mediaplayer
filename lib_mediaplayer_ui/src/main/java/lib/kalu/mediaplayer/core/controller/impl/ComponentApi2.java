package lib.kalu.mediaplayer.core.controller.impl;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

@Keep
public interface ComponentApi2 {

    default void addComponent(@NonNull ComponentApi... components) {
        if (null == components)
            return;
        for (ComponentApi component : components) {
            addComponent(component);
        }
    }

    void addComponent(@NonNull ComponentApi component);
}
