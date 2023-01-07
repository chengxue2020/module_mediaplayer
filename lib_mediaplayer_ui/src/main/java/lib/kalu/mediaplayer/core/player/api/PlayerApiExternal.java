package lib.kalu.mediaplayer.core.player.api;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.start.StartBuilder;

public interface PlayerApiExternal {

    default void enableExternalMusic(boolean enable, boolean release) {
        enableExternalMusic(enable, release, false);
    }

    void enableExternalMusic(boolean enable, boolean release, boolean auto);

    void setExternalMusic(@NonNull StartBuilder bundle);

    boolean isExternalMusicAuto();

    boolean isExternalMusicLoop();

    boolean isExternalMusicPrepared();
}
