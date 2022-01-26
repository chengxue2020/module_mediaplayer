package lib.kalu.mediaplayer.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;

@Keep
public class MediaProgressBar2 extends ProgressBar {

    public MediaProgressBar2(@NonNull Context context) {
        super(context);
        init();
    }

    public MediaProgressBar2(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private final void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Drawable drawable = getResources().getDrawable(R.drawable.module_mediaplayer_shape_loading);
            setIndeterminateDrawable(drawable);
        }
    }
}
