package lib.kalu.mediaplayer.core.controller.impl;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

@Keep
public interface ImplControllerAction {

    default void addComponent(@NonNull ImplComponent... components) {
        if (null == components)
            return;
        for (ImplComponent component : components) {
            addComponent(component);
        }
    }

    void addComponent(@NonNull ImplComponent component);
}
