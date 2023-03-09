package lib.kalu.mediaplayer.core.component;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import lib.kalu.mediaplayer.core.player.api.PlayerApi;
import lib.kalu.mediaplayer.util.MPLogUtil;


@Keep
public interface ComponentApi {

    PlayerApi[] mPlayerApi = new PlayerApi[1];

    default void attachPlayerApi(@NonNull PlayerApi api) {
        mPlayerApi[0] = null;
        mPlayerApi[0] = api;
    }

    default PlayerApi getPlayerApi() {
        return mPlayerApi[0];
    }

    default void callPlayerEvent(@NonNull int playState) {
    }

    default void callWindowEvent(int state) {
    }

    /*************/

    default void setImageResource(@NonNull View layout, @IdRes int id, @DrawableRes int value) {
        try {
            ImageView view = layout.findViewById(id);
            view.setImageResource(value);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    default void setTextColor(@NonNull View layout, @IdRes int id, @ColorInt int value) {
        try {
            TextView view = layout.findViewById(id);
            view.setTextColor(value);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    default void setTextSize(@NonNull View layout, @IdRes int id, @DimenRes int value) {
        try {
            TextView view = layout.findViewById(id);
            int offset = layout.getResources().getDimensionPixelOffset(value);
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, offset);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    default void setText(@NonNull View layout, @IdRes int id, @StringRes int value) {
        try {
            TextView view = layout.findViewById(id);
            view.setText(value);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    default void setText(@NonNull View layout, @IdRes int id, @NonNull String value) {
        try {
            TextView view = layout.findViewById(id);
            view.setText(value);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    default void setCompoundDrawablesWithIntrinsicBounds(@NonNull View layout, @IdRes int id, @DrawableRes int left, @DrawableRes int top, @DrawableRes int right, @DrawableRes int bottom) {
        try {
            TextView view = layout.findViewById(id);
            view.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    default void setDimens(@NonNull View layout, @IdRes int id, @DimenRes int value) {
        try {
            View view = layout.findViewById(id);
            ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
            int offset = layout.getResources().getDimensionPixelOffset(value);
            layoutParams.width = offset;
            layoutParams.height = offset;
            view.setLayoutParams(layoutParams);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    default void setBackgroundColor(@NonNull View layout, @IdRes int id, @ColorInt int value) {
        try {
            View view = layout.findViewById(id);
            view.setBackgroundColor(value);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    default void setBackgroundResource(@NonNull View layout, @IdRes int id, @DrawableRes int resid) {
        try {
            View view = layout.findViewById(id);
            view.setBackgroundResource(resid);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    /******************/

    default void show() {
    }

    default void gone() {
    }

    /******************/

    default void seekForwardDown(boolean enable) {
    }

    default void seekForwardUp(boolean enable) {
    }

    default void seekRewindDown(boolean enable) {
    }

    default void seekRewindUp(boolean enable) {
    }

    default void onSeekProgressUpdate(@NonNull int position, @NonNull int duration) {
    }

    default void onSeekTimeUpdate(@NonNull int position, @NonNull int duration) {
    }

    default void onSeekPlaying(@NonNull int position) {
    }

    default void onUpdateTimeMillis(@NonNull long seek, @NonNull long position, @NonNull long duration) {
    }
}
