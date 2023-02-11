package lib.kalu.mediaplayer.core.player.api;

import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.controller.base.ControllerLayout;
import lib.kalu.mediaplayer.core.controller.component.ComponentSeek;

public interface PlayerApiComponent extends PlayerApiBase {

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

    default void dispatchEventComponent(@NonNull KeyEvent event) {
        try {
            ControllerLayout layout = getControlLayout();
            if (null != layout) {
                int count = layout.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = layout.getChildAt(i);
                    if (null == child) continue;
                    if (child instanceof ComponentSeek) {
                        child.dispatchKeyEvent(event);
                        break;
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    void clearComponent();
}
