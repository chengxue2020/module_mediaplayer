package lib.kalu.mediaplayer.listener;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;

@Keep
public interface OnFullChangeListener {

    void onFull();

    void onNormal();
}
