package lib.kalu.mediaplayer.core.video.player.api;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.LinkedList;
import java.util.List;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.controller.base.ControllerLayout;
import lib.kalu.mediaplayer.listener.OnChangeListener;
import lib.kalu.mediaplayer.util.ActivityUtils;
import lib.kalu.mediaplayer.util.MPLogUtil;

public interface PlayerApiBase {

    List<OnChangeListener> mListeners = new LinkedList<>();

    default List<OnChangeListener> getOnChangeListener() {
        return mListeners;
    }

    default void clearListener() {
        mListeners.clear();
    }

    default boolean removeOnChangeListener(@NonNull OnChangeListener l) {
        return mListeners.remove(l);
    }

    default void addOnChangeListener(@NonNull OnChangeListener l) {
        mListeners.add(l);
    }

    default void setOnChangeListener(@NonNull OnChangeListener l) {
        clearListener();
        addOnChangeListener(l);
    }

    default void callPlayerState(@PlayerType.StateType.Value int playerState) {
        List<OnChangeListener> listener = getOnChangeListener();
        MPLogUtil.log("PlayerApiBase => callPlayerState => listener = " + listener);
        if (null == listener)
            return;
        ControllerLayout layout = getControlLayout();
        MPLogUtil.log("PlayerApiBase => callPlayerState => layout = " + layout);
        if (null == layout)
            return;
        layout.setPlayState(playerState);
        for (OnChangeListener l : listener) {
            if (null == l)
                continue;
            l.onChange(playerState);
        }
    }

    default void callWindowState(@PlayerType.WindowType.Value int windowState) {
        List<OnChangeListener> listener = getOnChangeListener();
        if (null == listener)
            return;
        ControllerLayout layout = getControlLayout();
        if (null == layout)
            return;
        layout.setWindowState(windowState);
        for (OnChangeListener l : listener) {
            if (null == l)
                continue;
            l.onWindow(windowState);
        }
    }

    ControllerLayout getControlLayout() ;

    default ControllerLayout getControlLayout(boolean isFull) {

        ViewGroup layout = getLayout();
        MPLogUtil.log("PlayerApiBase => getControlLayout => isFull = " + isFull + ", layout = " + layout);
        if (null == layout) {
            return null;
        }

        ViewGroup group;
        if (isFull) {
            Context context = layout.getContext();
            Activity activity = ActivityUtils.getActivity(context);
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            int index = decorView.getChildCount();
            group = decorView.getChildAt(index - 1).findViewById(R.id.module_mediaplayer_control);
        } else {
            group = layout.findViewById(R.id.module_mediaplayer_control);
        }
        MPLogUtil.log("PlayerApiBase => getControlLayout => isFull = " + isFull + ", group = " + group);

        try {
            return (ControllerLayout) group.getChildAt(0);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => getControlLayout => " + e.getMessage(), e);
            return null;
        }
    }

    default void clearControllerLayout() {
        try {
            ViewGroup layout = getLayout();
            ViewGroup group = layout.findViewById(R.id.module_mediaplayer_control);
            MPLogUtil.log("PlayerApiBase => clearControllerLayout => group = " + group);
            group.removeAllViews();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => clearControllerLayout => " + e.getMessage(), e);
        }
    }

    default void setControllerLayout(@Nullable ControllerLayout controller) {
        try {
            // 0
            clearControllerLayout();
            // 1
            ViewGroup layout = getLayout();
            MPLogUtil.log("PlayerApiBase => setControllerLayout => layout = " + layout);
            if (null == layout)
                return;
            // 2
            ViewGroup group = layout.findViewById(R.id.module_mediaplayer_control);
            MPLogUtil.log("PlayerApiBase => setControllerLayout => group = " + group);
            if (null == group)
                return;
            // 3
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            controller.setLayoutParams(params);
            group.addView(controller, 0);
            controller.setMediaPlayer((PlayerApi) layout);
            // 4
//            callWindowState(PlayerType.WindowType.NORMAL);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => setControllerLayout => " + e.getMessage(), e);
        }
    }

    ViewGroup getLayout();
}
