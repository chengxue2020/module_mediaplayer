package lib.kalu.mediaplayer.videofloat;


import androidx.annotation.IntDef;
import androidx.annotation.Keep;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Keep
public class WindowScreen {
    public static final int WIDTH = 0;
    public static final int HEIGHT = 1;

    @IntDef({WIDTH, HEIGHT})
    @Retention(RetentionPolicy.SOURCE)
    @interface screenType {}
}
