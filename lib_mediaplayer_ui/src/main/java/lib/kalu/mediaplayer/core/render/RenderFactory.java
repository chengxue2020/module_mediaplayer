package lib.kalu.mediaplayer.core.render;

import android.content.Context;

public interface RenderFactory {
    ImplRender createRender(Context context);
}
