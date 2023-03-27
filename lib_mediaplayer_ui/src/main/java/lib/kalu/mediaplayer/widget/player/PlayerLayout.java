package lib.kalu.mediaplayer.widget.player;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    }

    public PlayerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PlayerLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        PlayerView playerView = new PlayerView(getContext());
        playerView.setId(R.id.module_mediaplayer_root);
        playerView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        addView(playerView);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        try {
            return getPlayerView().dispatchEvent(event) || super.dispatchKeyEvent(event);
        } catch (Exception e) {
            return super.dispatchKeyEvent(event);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        checkOnDetachedFromWindow();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        checkOnAttachedToWindow();
        super.onAttachedToWindow();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        checkOnWindowVisibilityChanged(visibility);
        super.onWindowVisibilityChanged(visibility);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        try {
            getPlayerView().onSaveBundle();
        } catch (Exception e) {
        }
        return super.onSaveInstanceState();
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

    protected void checkOnWindowVisibilityChanged(int visibility) {
        try {
            getPlayerView().checkOnWindowVisibilityChanged(visibility);
        } catch (Exception e) {
        }
    }

    protected void checkOnDetachedFromWindow() {
        try {
            getPlayerView().checkOnDetachedFromWindow();
        } catch (Exception e) {
        }
    }

    protected void checkOnAttachedToWindow() {
        try {
            getPlayerView().checkOnAttachedToWindow();
        } catch (Exception e) {
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

    public final void toggle() {
        try {
            getPlayerView().toggle();
        } catch (Exception e) {
        }
    }

    public final void resume() {
        try {
            getPlayerView().resume();
        } catch (Exception e) {
        }
    }

    public final void resume(boolean ignore) {
        try {
            getPlayerView().resume(ignore);
        } catch (Exception e) {
        }
    }

    public final void pause() {
        try {
            getPlayerView().pause();
        } catch (Exception e) {
        }
    }

    public final void pause(boolean ignore) {
        try {
            getPlayerView().pause(ignore);
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

    public final boolean isPlaying() {
        try {
            return getPlayerView().isPlaying();
        } catch (Exception e) {
            return false;
        }
    }

    public final String getUrl() {
        try {
            return getPlayerView().getUrl();
        } catch (Exception e) {
            return null;
        }
    }

    public void start(@NonNull String playerUrl) {
        try {
            getPlayerView().start(playerUrl);
        } catch (Exception e) {
        }
    }

    public void start(@NonNull StartBuilder data, @NonNull String playerUrl) {
        try {
            getPlayerView().start(data, playerUrl);
        } catch (Exception e) {
        }
    }

    /**********/
    public final void stopExternalMusic(@NonNull boolean release) {
        try {
            getPlayerView().stopExternalMusic(release);
        } catch (Exception e) {
        }
    }

    public final void startExternalMusic(@NonNull Context context) {
        try {
            getPlayerView().startExternalMusic(context);
        } catch (Exception e) {
        }
    }

    public final void startExternalMusic(@NonNull Context context, @Nullable StartBuilder bundle) {
        try {
            getPlayerView().startExternalMusic(context, bundle);
        } catch (Exception e) {
        }
    }

    public final void setVolume(@NonNull float left, @NonNull float right) {
        try {
            getPlayerView().setVolume(left, right);
        } catch (Exception e) {
        }
    }

    public final void setMute(@NonNull boolean enable) {
        try {
            getPlayerView().setMute(enable);
        } catch (Exception e) {
        }
    }

    public final StartBuilder getStartBuilder() {
        try {
            return getPlayerView().getStartBuilder();
        } catch (Exception e) {
            return null;
        }
    }

    public final long getSeek() {
        try {
            return getStartBuilder().getSeek();
        } catch (Exception e) {
            return 0;
        }
    }

    public final long getMax() {
        try {
            return getStartBuilder().getMax();
        } catch (Exception e) {
            return 0;
        }
    }

    public final void seekTo(@NonNull boolean force, @NonNull long seek, @NonNull long max, @NonNull boolean loop) {
        try {
            getPlayerView().seekTo(force, seek, max, loop);
        } catch (Exception e) {
        }
    }
}
