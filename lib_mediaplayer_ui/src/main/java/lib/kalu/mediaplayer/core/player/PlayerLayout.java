package lib.kalu.mediaplayer.core.player;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.listener.OnPlayerChangeListener;
import lib.kalu.mediaplayer.util.MPLogUtil;

@Keep
public class PlayerLayout extends RelativeLayout {
    public PlayerLayout(Context context) {
        super(context);
        init();
    }

    public PlayerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PlayerLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        try {
            return getPlayerView().dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
        } catch (Exception e) {
            return super.dispatchKeyEvent(event);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        try {
            getPlayerView().checkOnDetachedFromWindow();
            super.onDetachedFromWindow();
        } catch (Exception e) {
        }
    }

    @Override
    protected void onAttachedToWindow() {
        try {
            getPlayerView().checkOnAttachedToWindow();
            super.onAttachedToWindow();
        } catch (Exception e) {
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        try {
            getPlayerView().checkOnWindowVisibilityChanged(visibility);
            super.onWindowVisibilityChanged(visibility);
        } catch (Exception e) {
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        try {
            getPlayerView().onSaveBundle();
        } catch (Exception e) {
        }
        return super.onSaveInstanceState();
    }

    private final void init() {
        PlayerView playerView = new PlayerView(getContext());
        playerView.setId(R.id.module_mediaplayer_root);
        playerView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        addView(playerView);
    }

    private final Activity getWrapperActivity(Context context) {
        if (context == null) {
            return null;
        }
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return getWrapperActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    private final PlayerView getPlayerView() {
        try {
            PlayerView playerView = null;
            int childCount = getChildCount();
            // sample
            if (childCount == 1) {
                playerView = (PlayerView) getChildAt(0);
            }
            // not
            else {
                Activity activity = getWrapperActivity(getContext());
                if (null == activity)
                    throw new Exception("activity is null");
                ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
                int length = decorView.getChildCount();
                for (int i = 0; i < length; i++) {
                    View childAt = decorView.getChildAt(i);
                    if (null == childAt)
                        continue;
                    if (childAt.getId() == R.id.module_mediaplayer_root) {
                        playerView = (PlayerView) childAt;
                        break;
                    }
                }
            }
            if (null == playerView)
                throw new Exception("not find playerView from decorView");
            return playerView;
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => getPlayerView => " + e.getMessage());
            return null;
        }
    }

    /**********/

    public final boolean isFull() {
        try {
            return getPlayerView().isFull();
        } catch (Exception e) {
            return false;
        }
    }

    public final boolean isFloat() {
        try {
            return getPlayerView().isFloat();
        } catch (Exception e) {
            return false;
        }
    }

    public final void startFull() {
        try {
            getPlayerView().startFull();
        } catch (Exception e) {
        }
    }

    public final void stopFull() {
        try {
            getPlayerView().stopFull();
        } catch (Exception e) {
        }
    }

    public final void startFloat() {
        try {
            getPlayerView().startFloat();
        } catch (Exception e) {
        }
    }

    public final void stopFloat() {
        try {
            getPlayerView().stopFloat();
        } catch (Exception e) {
        }
    }

    public long getPosition() {
        try {
            return getPlayerView().getPosition();
        } catch (Exception e) {
            return 0;
        }
    }

    public long getDuration() {
        try {
            return getPlayerView().getDuration();
        } catch (Exception e) {
            return 0;
        }
    }

    public final void setScaleType(@PlayerType.ScaleType.Value int scaleType) {
        try {
            getPlayerView().setScaleType(scaleType);
        } catch (Exception e) {
        }
    }

    public final void addComponent(@NonNull ComponentApi componentApi) {
        try {
            getPlayerView().addComponent(componentApi);
        } catch (Exception e) {
        }
    }

    public final <T extends android.view.View> T findComponent(java.lang.Class<?> cls) {
        try {
            return getPlayerView().findComponent(cls);
        } catch (Exception e) {
            return null;
        }
    }

    public final void showComponentSeek() {
        try {
            getPlayerView().showComponentSeek();
        } catch (Exception e) {
        }
    }

    public final void setPlayerChangeListener(@NonNull OnPlayerChangeListener listener) {
        try {
            getPlayerView().setPlayerChangeListener(listener);
        } catch (Exception e) {
        }
    }

    public final void resume() {
        try {
            getPlayerView().resume();
        } catch (Exception e) {
        }
    }

    public final void pause() {
        try {
            getPlayerView().pause();
        } catch (Exception e) {
        }
    }

    public final void release() {
        try {
            getPlayerView().release();
        } catch (Exception e) {
        }
    }

    public final void stop() {
        try {
            getPlayerView().stop();
        } catch (Exception e) {
        }
    }

    public final String getUrl() {
        try {
            return getPlayerView().getUrl();
        } catch (Exception e) {
            return null;
        }
    }

    public final void start(@NonNull String playerUrl) {
        try {
            getPlayerView().start(playerUrl);
        } catch (Exception e) {
        }
    }

    public final void start(@NonNull StartBuilder data, @NonNull String playerUrl) {
        try {
            getPlayerView().start(data, playerUrl);
        } catch (Exception e) {
        }
    }
}
