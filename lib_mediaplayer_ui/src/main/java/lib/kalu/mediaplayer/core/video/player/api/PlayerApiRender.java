package lib.kalu.mediaplayer.core.video.player.api;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerBuilder;
import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.video.render.RenderApi;
import lib.kalu.mediaplayer.core.video.render.RenderFactoryManager;
import lib.kalu.mediaplayer.util.ActivityUtils;
import lib.kalu.mediaplayer.util.MPLogUtil;

public interface PlayerApiRender extends PlayerApiBase {

    default void clearSuface() {
        try {
            RenderApi render = getRender();
            render.clearCanvas();
        } catch (Exception e) {
        }
    }

    default void updateSuface() {
        try {
            RenderApi render = getRender();
            render.updateCanvas();
        } catch (Exception e) {
        }
    }

    default String screenshot() {
        try {
            RenderApi render = getRender();
            return render.screenshot();
        } catch (Exception e) {
            return null;
        }
    }

    default boolean isFull() {
        try {
            ViewGroup layout = getLayout();
            int count = layout.getChildCount();
            return count <= 0;
        }catch (Exception e){
            return false;
        }
    }

    default void startFull() {

        ViewGroup layout = getLayout();
        if (null == layout)
            return;

        Context context = layout.getContext();
        Activity activity = ActivityUtils.getActivity(context);
        if (null == activity)
            return;

        int count = layout.getChildCount();
        if (count <= 0)
            return;

        try {
            // 0
            layout.setFocusable(true);
            // 1
            View real = layout.getChildAt(0);
            layout.removeAllViews();
            // 2
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            int index = decorView.getChildCount();
            decorView.addView(real, index);
            // 3
            callWindowState(PlayerType.WindowType.FULL);
            // 4
//            if (null != mOnFullChangeListener) {
//                mOnFullChangeListener.onFull();
//            }
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    default void stopFull() {

        ViewGroup layout = getLayout();
        if (null == layout)
            return;

        Context context = layout.getContext();
        Activity activity = ActivityUtils.getActivity(context);
        if (null == activity)
            return;

        int count = layout.getChildCount();
        if (count > 0)
            return;

        try {
            // 0
            layout.setFocusable(false);
            // 1
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            // 2
            int index = decorView.getChildCount();
            View real = decorView.getChildAt(index - 1);
            decorView.removeView(real);
            // 2
            layout.removeAllViews();
            layout.addView(real, 0);
            // 4
            callWindowState(PlayerType.WindowType.NORMAL);
            // 5
//            if (null != mOnFullChangeListener) {
//                mOnFullChangeListener.onNormal();
//            }
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    default boolean isFloat() {
        try {

            ViewGroup layout = getLayout();
            if (null == layout)
                return false;

            int count = layout.getChildCount();
            if (count <= 0) {
                Context context = layout.getContext();
                Activity activity = ActivityUtils.getActivity(context);
                View decorView = activity.getWindow().getDecorView();
                View v = decorView.findViewById(R.id.module_mediaplayer_root);
                if (null != v) {
                    ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                    return !(layoutParams.width == ViewGroup.LayoutParams.MATCH_PARENT && layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT);
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
            return false;
        }
    }

    default void startFloat() {

        ViewGroup layout = getLayout();
        if (null == layout)
            return;

        Context context = layout.getContext();
        Activity activity = ActivityUtils.getActivity(context);
        if (null == activity)
            return;

        int count = layout.getChildCount();
        if (count <= 0)
            return;

        try {
            // 1
            ViewGroup real = (ViewGroup) layout.getChildAt(0);
            layout.removeAllViews();
            // 2
            ViewGroup root = real.findViewById(R.id.module_mediaplayer_root);
            int width = layout.getResources().getDimensionPixelOffset(R.dimen.module_mediaplayer_dimen_float_width);
            int height = width * 9 / 16;
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            root.setBackgroundColor(Color.BLACK);
            root.setLayoutParams(layoutParams);
            // 3
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            int index = decorView.getChildCount();
            decorView.addView(real, index);
            // 5
            callWindowState(PlayerType.WindowType.FLOAT);
        } catch (Exception e) {
        }
    }

    default void stopFloat() {

        ViewGroup layout = getLayout();
        if (null == layout)
            return;

        Context context = layout.getContext();
        Activity activity = ActivityUtils.getActivity(context);
        if (null == activity)
            return;

        int count = layout.getChildCount();
        if (count > 0)
            return;

        try {
            // 1
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            int index = decorView.getChildCount();
            View real = decorView.getChildAt(index - 1);
            decorView.removeView(real);
            // 2
            ViewGroup root = real.findViewById(R.id.module_mediaplayer_root);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            root.setBackground(null);
            root.setLayoutParams(layoutParams);
            // 2
            layout.removeAllViews();
            layout.addView(real, 0);
            // 4
            callWindowState(PlayerType.WindowType.NORMAL);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }

    default void setScaleType(@PlayerType.ScaleType.Value int scaleType) {
        try {
            // 1
            RenderApi render = getRender();
            render.setScaleType(scaleType);
            // 2
            PlayerManager.getInstance().getConfig().newBuilder().setScaleType(scaleType).build();
        } catch (Exception e) {
        }
    }

    default void setVideoSize(int width, int height) {
        try {
            RenderApi render = getRender();
            render.setVideoSize(width, height);
        } catch (Exception e) {
        }
    }

    default void setVideoRotation(int rotation) {
        try {
            if (rotation == -1)
                return;
            RenderApi render = getRender();
            render.setVideoRotation(rotation);
        } catch (Exception e) {
        }
    }

    /**
     * 设置镜像旋转，暂不支持SurfaceView
     *
     * @param enable
     */
    default void setMirrorRotation(boolean enable) {
        try {
            RenderApi render = getRender();
            render.setScaleX(enable ? -1 : 1);
        } catch (Exception e) {
        }
    }

    default void showReal() {
        try {
            ViewGroup layout = getLayout();
            ViewGroup viewGroup = layout.findViewById(R.id.module_mediaplayer_video);
            viewGroup.setVisibility(View.VISIBLE);
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = viewGroup.getChildAt(i);
                child.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
        }
    }

    default void hideReal() {
        try {
            ViewGroup layout = getLayout();
            ViewGroup viewGroup = layout.findViewById(R.id.module_mediaplayer_video);
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = viewGroup.getChildAt(i);
                child.setVisibility(View.INVISIBLE);
            }
            viewGroup.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
        }
    }

    default void releaseRender() {
        try {
            // 1
            RenderApi render = searchRender();
            MPLogUtil.log("PlayerApiRender => releaseRender => render = " + render);
            if (null != render) {
                render.releaseReal();
                MPLogUtil.log("PlayerApiRender => releaseRender => succ");
            } else {
                MPLogUtil.log("PlayerApiRender => releaseRender => fail");
            }
            // 2
            clearRender();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => releaseRender => " + e.getMessage());
        }
    }

    default void clearRender() {
        try {
            ViewGroup layout = getLayout();
            ViewGroup group = layout.findViewById(R.id.module_mediaplayer_video);
            MPLogUtil.log("PlayerApiRender => clearRender => group = " + group);
            group.removeAllViews();
            MPLogUtil.log("PlayerApiRender => clearRender => succ");
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => clearRender => " + e.getMessage());
        }
    }

    default RenderApi searchRender() {
        RenderApi render = null;
        try {
            ViewGroup layout = getLayout();
            ViewGroup group = layout.findViewById(R.id.module_mediaplayer_video);
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                View view = group.getChildAt(i);
                if (null == view)
                    continue;
                if (!(view instanceof RenderApi))
                    continue;
                render = (RenderApi) view;
                break;
            }
        } catch (Exception e) {
        }
        MPLogUtil.log("PlayerApiRender => searchRender => render = " + render);
        return render;
    }

    default void setRender(@PlayerType.RenderType int v) {
        try {
            PlayerBuilder config = PlayerManager.getInstance().getConfig();
            PlayerBuilder.Builder builder = config.newBuilder();
            builder.setRender(v);
            PlayerManager.getInstance().setConfig(config);
        } catch (Exception e) {
        }
    }

    default void createRender() {
        try {
            // 1
            ViewGroup layout = getLayout();
            Context context = layout.getContext();
            int type = PlayerManager.getInstance().getConfig().getRender();
            RenderApi render = RenderFactoryManager.getRender(context, type);
            MPLogUtil.log("PlayerApiRender => createRender => render = " + render);
            // 2
            releaseRender();
            // 2
            setRender(render);
            // 3
            updateRender();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => createRender => " + e.getMessage());
        }
    }

    default void updateRender() {
        try {
            // 1
            RenderApi render = getRender();
            MPLogUtil.log("PlayerApiRender => updateRender => render = " + render);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            render.setLayoutParams(params);
            // 2
            ViewGroup layout = getLayout();
            ViewGroup viewGroup = layout.findViewById(R.id.module_mediaplayer_video);
            viewGroup.addView((View) render, 0);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => updateRender => " + e.getMessage());
        }
    }

    RenderApi getRender();

    void setRender(@NonNull RenderApi render);

    void checkReal();
}
