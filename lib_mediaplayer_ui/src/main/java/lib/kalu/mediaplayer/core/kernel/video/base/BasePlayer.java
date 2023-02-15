package lib.kalu.mediaplayer.core.kernel.video.base;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.KernelApi;
import lib.kalu.mediaplayer.core.kernel.video.KernelApiEvent;
import lib.kalu.mediaplayer.core.player.api.PlayerApiExternalMusic;

public abstract class BasePlayer implements KernelApi {

    private KernelApiEvent eventApi;
    private PlayerApiExternalMusic musicApi;

    public BasePlayer(@NonNull PlayerApiExternalMusic musicApi, @NonNull KernelApiEvent eventApi) {
        setReadying(false);
        this.eventApi = eventApi;
        this.musicApi = musicApi;
    }

    @Override
    public void onUpdateTimeMillis() {
        if (null == eventApi)
            return;
        long position = getPosition();
        long duration = getDuration();
        if (position > 0 && duration > 0) {
            long seek = getSeek();
            long max = getMax();
            boolean looping = isLooping();
            eventApi.onUpdateTimeMillis(looping, max, seek, position, duration);
        }
    }

    @Override
    public boolean isExternalMusicPlayWhenReady() {
        try {
            return musicApi.isExternalMusicPlayWhenReady();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isExternalMusicLooping() {
        try {
            return musicApi.isExternalMusicLooping();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isExternalMusicEqualLength() {
        try {
            return musicApi.isExternalMusicEqualLength();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getExternalMusicPath() {
        try {
            return musicApi.getExternalMusicPath();
        } catch (Exception e) {
            return null;
        }
    }

    public final void setEvent(@NonNull KernelApiEvent eventApi) {
        this.eventApi = eventApi;
    }

    public final void onEvent(@PlayerType.KernelType.Value int kernel, @PlayerType.EventType.Value int event) {
        if (null == eventApi)
            return;
        eventApi.onEvent(kernel, event);
    }
}
