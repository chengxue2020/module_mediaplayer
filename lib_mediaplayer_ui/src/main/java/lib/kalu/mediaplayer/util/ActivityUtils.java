package lib.kalu.mediaplayer.util;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.Keep;
import androidx.appcompat.view.ContextThemeWrapper;

@Keep
public final class ActivityUtils {

    public static Activity getActivity(Context context) {
        if (context == null) {
            return null;
        }
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextThemeWrapper) {
            return getActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }
}
