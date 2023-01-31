package lib.kalu.mediaplayer.core.audio.kernel;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.audio.MusicPlayerManager;
import lib.kalu.mediaplayer.core.audio.OnMusicPlayerChangeListener;


@Keep
public interface MusicKernelApi {

    void setMusicListener(@NonNull OnMusicPlayerChangeListener listener);

    void createDecoder(@NonNull Context context);

    void setDataSource(@NonNull Context context, @NonNull String musicUrl);

    void start();

    void stop();

    void pause();

    void release();

    void addListener();

    void removeListener();

    void setLooping(boolean v);

    void setVolume(float v);

    boolean isPlaying();

    void seekTo(long v);

    long getDuration();

    default void setSeekParameters() {
    }
}