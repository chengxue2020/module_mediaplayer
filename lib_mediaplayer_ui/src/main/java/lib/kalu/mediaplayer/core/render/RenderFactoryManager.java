package lib.kalu.mediaplayer.core.render;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;

@Keep
public final class RenderFactoryManager {

    public static RenderFactory getFactory(@PlayerType.RenderType int type) {
        if (type == PlayerType.RenderType.SURFACE) {
            return SurfaceFactory.build();
        } else {
            return TextureFactory.build();
        }
    }

    public static ImplRender getRender(@NonNull Context context, @PlayerType.RenderType int type) {
        if (type == PlayerType.RenderType.SURFACE) {
            return SurfaceFactory.build().createRender(context);
        } else {
            return TextureFactory.build().createRender(context);
        }
    }
}
