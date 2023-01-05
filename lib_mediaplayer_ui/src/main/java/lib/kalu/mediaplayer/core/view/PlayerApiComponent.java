package lib.kalu.mediaplayer.core.view;

import lib.kalu.mediaplayer.config.player.PlayerType;

interface PlayerApiComponent extends PlayerApiState {

    default void showComponentError() {
        callPlayerState(PlayerType.StateType.STATE_ERROR_IGNORE);
    }

    default void showComponentSeek() {
        callPlayerState(PlayerType.StateType.STATE_COMPONENT_SEEK_SHOW);
    }

    default void hideComponentLoading() {
        callPlayerState(PlayerType.StateType.STATE_LOADING_STOP);
    }

    default void showComponentLoading() {
        callPlayerState(PlayerType.StateType.STATE_LOADING_START);
    }
}
