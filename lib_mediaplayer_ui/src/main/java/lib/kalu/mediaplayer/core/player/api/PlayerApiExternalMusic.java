package lib.kalu.mediaplayer.core.player.api;

import android.view.View;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.util.MPLogUtil;

public interface PlayerApiExternalMusic {

    default boolean isExternalMusicPlayWhenReady() {
        try {
            return (boolean) ((View) this).getTag(R.id.module_mediaplayer_id_player_external_music_play_when_ready);
        } catch (Exception e) {
            return false;
        }
    }

    default boolean isExternalMusicLooping() {
        try {
            return (boolean) ((View) this).getTag(R.id.module_mediaplayer_id_player_external_music_looping);
        } catch (Exception e) {
            return false;
        }
    }

    default boolean isExternalMusicEqualLength() {
        try {
            return (boolean) ((View) this).getTag(R.id.module_mediaplayer_id_player_external_music_equal_length);
        } catch (Exception e) {
            return false;
        }
    }

    default String getExternalMusicPath() {
        try {
            return (String) ((View) this).getTag(R.id.module_mediaplayer_id_player_external_music_url);
        } catch (Exception e) {
            return null;
        }
    }

    default void setExternalMusicEqualLength(boolean v) {
        try {
            ((View) this).setTag(R.id.module_mediaplayer_id_player_external_music_equal_length, v);
            MPLogUtil.log("PlayerApiKernel => setExternalMusicEqualLength => " + v);
        } catch (Exception e) {
        }
    }

    default void setExternalMusicUrl(String v) {
        try {
            ((View) this).setTag(R.id.module_mediaplayer_id_player_external_music_url, v);
            MPLogUtil.log("PlayerApiKernel => setExternalMusicUrl => " + v);
        } catch (Exception e) {
        }
    }

    default void setExternalMusicLooping(boolean v) {
        try {
            ((View) this).setTag(R.id.module_mediaplayer_id_player_external_music_looping, v);
            MPLogUtil.log("PlayerApiKernel => setExternalMusicLooping => " + v);
        } catch (Exception e) {
        }
    }

    default void setExternalMusicPlayWhenReady(boolean v) {
        try {
            ((View) this).setTag(R.id.module_mediaplayer_id_player_external_music_play_when_ready, v);
            MPLogUtil.log("PlayerApiKernel => setExternalMusicPlayWhenReady => " + v);
        } catch (Exception e) {
        }
    }

    default void setPlayerExternalMusicData(@NonNull StartBuilder bundle) {
        try {
            // 1
            String musicUrl = bundle.getExternalMusicUrl();
            setExternalMusicUrl(musicUrl);
            // 2
            boolean musicLoop = bundle.isExternalMusicLoop();
            setExternalMusicLooping(musicLoop);
            // 3
            boolean musicPlayWhenReady = bundle.isExternalMusicPlayWhenReady();
            setExternalMusicPlayWhenReady(musicPlayWhenReady);
        } catch (Exception e) {
        }
    }
}
