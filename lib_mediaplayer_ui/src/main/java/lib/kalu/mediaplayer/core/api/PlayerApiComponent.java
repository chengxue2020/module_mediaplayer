package lib.kalu.mediaplayer.core.api;

import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import java.util.LinkedList;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

public interface PlayerApiComponent extends PlayerApiBase {

    default void showComponentError() {
        callPlayerEvent(PlayerType.StateType.STATE_ERROR_IGNORE);
    }

    default void showComponentSeek() {
        callPlayerEvent(PlayerType.StateType.STATE_COMPONENT_SEEK_SHOW);
    }

    default void hideComponentLoading() {
        callPlayerEvent(PlayerType.StateType.STATE_LOADING_STOP);
    }

    default void showComponentLoading() {
        callPlayerEvent(PlayerType.StateType.STATE_LOADING_START);
    }

    default void clearAllComponent() {
        try {
            ViewGroup viewGroup = getBaseControlViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ComponentApi))
                    continue;
                ((ComponentApi) childAt).attachPlayerApi(null);
            }
            viewGroup.removeAllViews();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiComponent => clearAllComponent => " + e.getMessage());
        }
    }

    default void clearComponent(java.lang.Class<?> cls) {
        try {
            LinkedList<View> views = new LinkedList<>();
            ViewGroup viewGroup = getBaseControlViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ComponentApi))
                    continue;
                if (childAt.getClass() == cls) {
                    ((ComponentApi) childAt).attachPlayerApi(null);
                    views.add(childAt);
                }
            }
            for (View v : views) {
                viewGroup.removeView(v);
            }
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiComponent => clearComponent => " + e.getMessage());
        }
    }

    default void addComponent(ComponentApi componentApi) {
        try {
            ViewGroup viewGroup = getBaseControlViewGroup();
            componentApi.attachPlayerApi((PlayerApi) this);
            ((View) componentApi).setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            viewGroup.addView((View) componentApi);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiComponent => addComponent => " + e.getMessage());
        }
    }

    default <T extends android.view.View> T findComponent(java.lang.Class<?> cls) {
        try {
            ViewGroup viewGroup = getBaseControlViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ComponentApi))
                    continue;
                if (childAt.getClass() == cls) {
                    return (T) childAt;
                }
            }
            throw new Exception("not find");
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiComponent => callWindowEvent => " + e.getMessage());
            return null;
        }
    }


    default void dispatchEventComponent(@NonNull KeyEvent event) {
        try {
            ViewGroup viewGroup = getBaseControlViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ComponentApi))
                    continue;
                childAt.dispatchKeyEvent(event);
            }
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiComponent => dispatchEventComponent => " + e.getMessage());
        }
    }

    default void callUpdateTimeMillis(long seek, long position, long duration) {
        try {
            ViewGroup viewGroup = getBaseControlViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ComponentApi))
                    continue;
                ((ComponentApi) childAt).onUpdateTimeMillis(seek, position, duration);
            }
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiComponent => callUpdateTimeMillis => " + e.getMessage());
        }
    }
}
