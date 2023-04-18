package lib.kalu.mediaplayer.core.player;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.core.kernel.video.KernelApi;
import lib.kalu.mediaplayer.util.MPLogUtil;
 interface PlayerApiExternalMusic extends PlayerApiBase {

    default boolean isExternalMusicPlayWhenReady() {
        try {
            return (boolean) ((View) this).getTag(R.id.module_mediaplayer_id_player_external_music_play_when_ready);
        } catch (Exception e) {
            return false;
        }
    }

    default void setExternalMusicPlayWhenReady(boolean v) {
        try {
            ((View) this).setTag(R.id.module_mediaplayer_id_player_external_music_play_when_ready, v);
            MPLogUtil.log("PlayerApiExternalMusic => setExternalMusicPlayWhenReady => " + v);
        } catch (Exception e) {
        }
    }

    default void setExternalMusicLooping(boolean v) {
        try {
            ((View) this).setTag(R.id.module_mediaplayer_id_player_external_music_looping, v);
            MPLogUtil.log("PlayerApiExternalMusic => setExternalMusicLooping => " + v);
        } catch (Exception e) {
        }
    }

    default boolean isExternalMusicLooping() {
        try {
            return (boolean) ((View) this).getTag(R.id.module_mediaplayer_id_player_external_music_looping);
        } catch (Exception e) {
            return false;
        }
    }

    default boolean isExternalMusicSeek() {
        try {
            return (boolean) ((View) this).getTag(R.id.module_mediaplayer_id_player_external_music_seek);
        } catch (Exception e) {
            return true;
        }
    }

    default void setExternalMusicSeek(boolean v) {
        try {
            ((View) this).setTag(R.id.module_mediaplayer_id_player_external_music_seek, v);
            MPLogUtil.log("PlayerApiExternalMusic => setExternalMusicSeek => " + v);
        } catch (Exception e) {
        }
    }

    default String getExternalMusicPath() {
        try {
            return (String) ((View) this).getTag(R.id.module_mediaplayer_id_player_external_music_url);
        } catch (Exception e) {
            return null;
        }
    }

    default void setExternalMusicUrl(String v) {
        try {
            ((View) this).setTag(R.id.module_mediaplayer_id_player_external_music_url, v);
            MPLogUtil.log("PlayerApiExternalMusic => setExternalMusicUrl => " + v);
        } catch (Exception e) {
        }
    }


    default void updateExternalMusicData(@NonNull StartBuilder bundle) {
        if (null == bundle)
            return;
        String musicUrl = bundle.getExternalMusicUrl();
        MPLogUtil.log("PlayerApiExternalMusic => updateExternalMusicData => musicUrl = " + musicUrl);
        setExternalMusicUrl(musicUrl);
        boolean musicLoop = bundle.isExternalMusicLoop();
        MPLogUtil.log("PlayerApiExternalMusic => updateExternalMusicData => musicLoop = " + musicLoop);
        setExternalMusicLooping(musicLoop);
        boolean musicPlayWhenReady = bundle.isExternalMusicPlayWhenReady();
        MPLogUtil.log("PlayerApiExternalMusic => updateExternalMusicData => musicPlayWhenReady = " + musicPlayWhenReady);
        setExternalMusicPlayWhenReady(musicPlayWhenReady);
        boolean musicSeek = bundle.isExternalMusicSeek();
        setExternalMusicSeek(musicSeek);
        MPLogUtil.log("PlayerApiExternalMusic => updateExternalMusicData => musicSeek = " + musicSeek);
    }

//    default void cleanExternalMusicData() {
//        setExternalMusicUrl(null);
//        MPLogUtil.log("PlayerApiExternalMusic => cleanExternalMusicData => musicUrl = null");
//        setExternalMusicLooping(false);
//        MPLogUtil.log("PlayerApiExternalMusic => cleanExternalMusicData => musicLooping = false");
//        setExternalMusicPlayWhenReady(false);
//        MPLogUtil.log("PlayerApiExternalMusic => cleanExternalMusicData => musicPlayWhenReady = false");
//        setExternalMusicEqualLength(true);
//        MPLogUtil.log("PlayerApiExternalMusic => cleanExternalMusicData => musicEqualLength = true");
//    }

    default void stopExternalMusic(boolean release) {
        setExternalMusicPlayWhenReady(false);
        try {
            KernelApi kernel = getKernel();
            kernel.stopExternalMusic(release);
            MPLogUtil.log("PlayerApiExternalMusic => stopExternalMusic =>");
        } catch (Exception e) {
        }
    }

    default void startExternalMusic(@NonNull Context context) {
        startExternalMusic(context, null);
    }

    default void startExternalMusic(@NonNull Context context, @Nullable StartBuilder bundle) {
        updateExternalMusicData(bundle);
        setExternalMusicPlayWhenReady(true);
        try {
            KernelApi kernel = getKernel();
            kernel.startExternalMusic(context);
            MPLogUtil.log("PlayerApiExternalMusic => startExternalMusic =>");
        } catch (Exception e) {
        }
    }

    default void pauseExternalMusic() {
        try {
            KernelApi kernel = getKernel();
            kernel.pauseExternalMusic();
        } catch (Exception e) {
        }
    }

    default void resetExternalMusic() {
        try {
            KernelApi kernel = getKernel();
            kernel.resetExternalMusic();
        } catch (Exception e) {
        }
    }

    default void resumeExternalMusic() {
        try {
            KernelApi kernel = getKernel();
            kernel.resumeExternalMusic();
        } catch (Exception e) {
        }
    }

    default boolean isExternalMusicPlaying() {
        try {
            KernelApi kernel = getKernel();
            return kernel.isExternalMusicPlaying();
        } catch (Exception e) {
            return false;
        }
    }

    default void checkExternalMusic(@NonNull Context context) {

        // reset music player
        pauseExternalMusic();
        resetExternalMusic();

        String musicPath = getExternalMusicPath();
        MPLogUtil.log("PlayerApiExternalMusic => checkExternalMusic => musicPath = " + musicPath);
        if (null == musicPath || musicPath.length() <= 0)
            return;

        boolean playWhenReady = isExternalMusicPlayWhenReady();
        MPLogUtil.log("PlayerApiExternalMusic => checkExternalMusic => playWhenReady = " + playWhenReady);
        if (playWhenReady) {
            boolean musicLooping = isExternalMusicLooping();
            MPLogUtil.log("PlayerApiExternalMusic => checkExternalMusic => musicLooping = " + musicLooping);
            if (musicLooping) {
                startExternalMusic(context, null);
            } else {
                stopExternalMusic(false);
            }
        } else {
            stopExternalMusic(false);
        }
    }
}
