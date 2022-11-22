package lib.kalu.mediaplayer.core.render;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.config.ConfigType;

@Keep
public final class RenderFactoryManager {

    public static RenderFactory getFactory(@ConfigType.RenderType int type) {
        if (type == ConfigType.RenderType.SURFACE_VIEW) {
            return SurfaceFactory.build();
        } else {
            return TextureFactory.build();
        }
    }

    public static RenderApi getRender(@NonNull Context context, @ConfigType.RenderType int type) {
        if (type == ConfigType.RenderType.SURFACE_VIEW) {
            return SurfaceFactory.build().createRender(context);
        } else {
            return TextureFactory.build().createRender(context);
        }
    }
}
