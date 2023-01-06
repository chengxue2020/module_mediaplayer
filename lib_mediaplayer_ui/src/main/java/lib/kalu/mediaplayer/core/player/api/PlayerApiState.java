package lib.kalu.mediaplayer.core.player.api;

import java.util.List;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.controller.base.ControllerLayout;
import lib.kalu.mediaplayer.listener.OnChangeListener;

interface PlayerApiState extends PlayerApiBase {

    default void callPlayerState(@PlayerType.StateType.Value int playerState) {
        List<OnChangeListener> listener = getOnChangeListener();
        if (null == listener)
            return;
        ControllerLayout layout = getControlLayout();
        if (null == layout)
            return;
        layout.setPlayState(playerState);
        for (OnChangeListener l : listener) {
            if (null == l)
                continue;
            l.onChange(playerState);
        }
    }

    default void callWindowState(@PlayerType.WindowType.Value int windowState) {
        List<OnChangeListener> listener = getOnChangeListener();
        if (null == listener)
            return;
        ControllerLayout layout = getControlLayout();
        if (null == layout)
            return;
        layout.setWindowState(windowState);
        for (OnChangeListener l : listener) {
            if (null == l)
                continue;
            l.onWindow(windowState);
        }
    }

    default void showComponentError() {
        callPlayerState(PlayerType.StateType.STATE_ERROR_IGNORE);
    }

    default void hideComponentLoading() {
        callPlayerState(PlayerType.StateType.STATE_LOADING_STOP);
    }

    default void showComponentLoading() {
        callPlayerState(PlayerType.StateType.STATE_LOADING_START);
    }
}
