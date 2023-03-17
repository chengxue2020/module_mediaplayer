package lib.kalu.mediaplayer.core.kernel.video.base;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.KernelApi;
import lib.kalu.mediaplayer.core.kernel.video.KernelApiEvent;
import lib.kalu.mediaplayer.core.api.PlayerApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

public abstract class BasePlayer implements KernelApi {

    private KernelApiEvent eventApi;
    private PlayerApi musicApi;

    public BasePlayer(@NonNull PlayerApi musicApi, @NonNull KernelApiEvent eventApi) {
        setReadying(false);
        this.musicApi = musicApi;
        this.eventApi = eventApi;
    }

    @Override
    public void onUpdateTimeMillis() {
        MPLogUtil.log("BasePlayer => onUpdateTimeMillis => eventApi = "+eventApi);
        if (null == eventApi)
            return;
        long position = getPosition();
        long duration = getDuration();
        MPLogUtil.log("BasePlayer => onUpdateTimeMillis => position = "+position+", duration = "+duration);
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
    public boolean isExternalMusicSeek() {
        try {
            return musicApi.isExternalMusicSeek();
        } catch (Exception e) {
            return true;
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
        if (null == eventApi || null == eventApi)
            return;
        eventApi.onEvent(kernel, event);
    }
}
