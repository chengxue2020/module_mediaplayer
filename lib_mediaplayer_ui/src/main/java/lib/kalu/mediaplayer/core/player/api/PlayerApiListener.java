
package lib.kalu.mediaplayer.core.player.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import java.util.List;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.buried.BuriedEvent;
import lib.kalu.mediaplayer.config.player.PlayerBuilder;
import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.core.kernel.video.KernelApi;
import lib.kalu.mediaplayer.core.kernel.video.KernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.KernelFactoryManager;
import lib.kalu.mediaplayer.core.render.RenderApi;
import lib.kalu.mediaplayer.listener.OnPlayerChangeListener;
import lib.kalu.mediaplayer.util.MPLogUtil;
import lib.kalu.mediaplayer.util.PlayerUtils;

public interface PlayerApiListener extends PlayerApiBase {

    default void callProgressListener(long position, long duration) {
        List<OnPlayerChangeListener> listener = getPlayerChangeListener();
        if (null != listener) {
            for (OnPlayerChangeListener l : listener) {
                if (null == l) continue;
                l.onProgress(position, duration);
            }
        }
    }
}
