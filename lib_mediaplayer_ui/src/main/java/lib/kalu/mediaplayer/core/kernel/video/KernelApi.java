package lib.kalu.mediaplayer.core.kernel.video;

import android.content.Context;
import android.view.Surface;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.util.MPLogUtil;


/**
 * @description: 播放器 - 抽象接口
 * @date: 2021-05-12 09:40
 */
@Keep
public interface KernelApi extends KernelApiBase,
        KernelApiExternalMusic,
        KernelApiEvent {

    void onUpdateTimeMillis();

    @NonNull
    <T extends Object> T getPlayer();

    void createDecoder(@NonNull Context context, @NonNull boolean logger, @NonNull int seekParameters);

    void releaseDecoder();

    void init(@NonNull Context context, @NonNull String url);

    default void setOptions() {
    }

    default void update(@NonNull Context context, @NonNull StartBuilder bundle, @NonNull String playUrl) {

        MPLogUtil.log("KernelApi => update => playUrl = " + playUrl);
        long seek = bundle.getSeek();
        MPLogUtil.log("KernelApi => update => seek = " + seek);
        setSeek(seek);
        long max = bundle.getMax();
        MPLogUtil.log("KernelApi => update => max = " + max);
        setMax(max);
        boolean mute = bundle.isMute();
        MPLogUtil.log("KernelApi => update => mute = " + mute);
        setMute(mute);
        boolean loop = bundle.isLoop();
        MPLogUtil.log("KernelApi => update => loop = " + loop);
        setLooping(loop);
        boolean live = bundle.isLive();
        MPLogUtil.log("KernelApi => update => live = " + live);
        setLive(live);

//        String musicUrl = bundle.getExternalMusicUrl();
//        MPLogUtil.log("KernelApi => update => musicUrl = " + musicUrl);
//        setExternalMusicPath(musicUrl);
//        boolean musicLoop = bundle.isExternalMusicLoop();
//        MPLogUtil.log("KernelApi => update => musicLoop = " + musicLoop);
//        setExternalMusicLooping(musicLoop);
//        boolean musicPlayWhenReady = bundle.isExternalMusicPlayWhenReady();
//        MPLogUtil.log("KernelApi => update => musicPlayWhenReady = " + musicPlayWhenReady);
//        setisExternalMusicPlayWhenReady(musicPlayWhenReady);

        init(context, playUrl);
    }

    default void update(@NonNull long seek, @NonNull long max, @NonNull boolean loop) {
        setSeek(seek);
        setMax(max);
        setLooping(loop);
    }

    void setSurface(@NonNull Surface surface, int w, int h);

    void setPlayWhenReady(boolean playWhenReady);
}