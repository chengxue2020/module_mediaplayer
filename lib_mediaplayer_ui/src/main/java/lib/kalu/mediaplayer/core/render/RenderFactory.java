package lib.kalu.mediaplayer.core.render;

import android.content.Context;

public interface RenderFactory {
    RenderApi createRender(Context context);
}
