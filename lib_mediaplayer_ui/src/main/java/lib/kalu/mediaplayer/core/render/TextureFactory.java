package lib.kalu.mediaplayer.core.render;

import android.content.Context;

public class TextureFactory implements RenderFactory {

    private TextureFactory() {
    }

    public static TextureFactory build() {
        return new TextureFactory();
    }

    @Override
    public ImplRender createRender(Context context) {
        return new RenderTextureView(context);
    }
}
