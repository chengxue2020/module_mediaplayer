package lib.kalu.mediaplayer.core.kernel.video;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.player.api.PlayerApi;
import lib.kalu.mediaplayer.core.player.api.PlayerApiBase;
import lib.kalu.mediaplayer.core.player.api.PlayerApiExternalMusic;

/**
 * @description: 1.继承{@link VideoPlayerImpl}扩展自己的播放器。
 * @date: 2021-05-12 14:42
 */
@Keep
public interface KernelFactory<T extends KernelApi> {
    T createKernel(@NonNull PlayerApiExternalMusic playerApi, @NonNull KernelApiEvent event);
}