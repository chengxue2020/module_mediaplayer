package lib.kalu.mediaplayer.core.player.api;

import androidx.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.core.controller.base.ControllerLayout;
import lib.kalu.mediaplayer.listener.OnChangeListener;

public interface PlayerApiBase {

    List<OnChangeListener> mListeners = new LinkedList<>();

    default List<OnChangeListener> getOnChangeListener() {
        return mListeners;
    }

    default void clearListener() {
        mListeners.clear();
    }

    default boolean removeOnChangeListener(@NonNull OnChangeListener l) {
        return mListeners.remove(l);
    }

    default void addOnChangeListener(@NonNull OnChangeListener l) {
        mListeners.add(l);
    }

    default void setOnChangeListener(@NonNull OnChangeListener l) {
        clearListener();
        addOnChangeListener(l);
    }

    ControllerLayout getControlLayout();

    default void createRender() {
    }

    default void createKernel(@NonNull StartBuilder builder, @NonNull boolean logger) {
    }

    default void attachKernelRender() {
    }
}
