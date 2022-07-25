package lib.kalu.mediaplayer.core.render;

import android.content.Context;

public class SurfaceFactory implements RenderFactory {

    private SurfaceFactory() {
    }

    public static SurfaceFactory build() {
        return new SurfaceFactory();
    }

    @Override
    public RenderApi createRender(Context context) {
        return new RenderSurfaceView(context);
    }
}
