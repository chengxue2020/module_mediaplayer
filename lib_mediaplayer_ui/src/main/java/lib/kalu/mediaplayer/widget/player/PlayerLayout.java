package lib.kalu.mediaplayer.widget.player;

import android.content.Context;
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

    private final void init() {
        try {
            int childCount = getChildCount();
            if (childCount > 0)
                throw new Exception("childCount warning: " + childCount);
            PlayerView playerView = new PlayerView(getContext());
            playerView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            addView(playerView);
        } catch (Exception e) {
            MPLogUtil.log("PlayerLayout => init => " + e.getMessage());
        }
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
            MPLogUtil.log("PlayerLayout => onSaveInstanceState => " + e.getMessage());
        }
        return super.onSaveInstanceState();
    }

    /**********/

    private final ViewGroup findDecorView(View view) {
        try {
            View parent = (View) view.getParent();
            if (null == parent) {
                return (ViewGroup) view;
            } else {
                return findDecorView(parent);
            }
        } catch (Exception e) {
            return (ViewGroup) view;
        }
    }

    private final PlayerView getPlayerView() {
        try {
            int childCount = getChildCount();
            MPLogUtil.log("PlayerLayout => getPlayerView => childCount = " + childCount);
            // sample
            if (childCount == 1) {
                return (PlayerView) getChildAt(0);
            }
            // not
            else {
                ViewGroup decorView = findDecorView(this);
                MPLogUtil.log("PlayerLayout => getPlayerView => decorView = " + decorView);
                if (null == decorView)
                    throw new Exception("decorView error: null");
                int decorChildCount = decorView.getChildCount();
                MPLogUtil.log("PlayerLayout => getPlayerView => decorChildCount = " + decorChildCount);
                for (int i = 0; i < decorChildCount; i++) {
                    View childAt = decorView.getChildAt(i);
                    MPLogUtil.log("PlayerLayout => getPlayerView => childAt = " + childAt);
                    if (null == childAt)
                        continue;
                    if (childAt.getId() == R.id.module_mediaplayer_root) {
                        return (PlayerView) childAt;
                    }
                }
            }
            throw new Exception("not find");
        } catch (Exception e) {
            MPLogUtil.log("PlayerLayout => getPlayerView => " + e.getMessage());
            return null;
        }
    }

    private final StartBuilder getStartBuilder() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.getStartBuilder();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => getStartBuilder => " + e.getMessage());
            return null;
        }
    }

    /**********/

    protected void checkOnWindowVisibilityChanged(int visibility) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.checkOnWindowVisibilityChanged(visibility);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => checkOnWindowVisibilityChanged => " + e.getMessage());
        }
    }

    protected void checkOnDetachedFromWindow() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.checkOnDetachedFromWindow();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => checkOnDetachedFromWindow => " + e.getMessage());
        }
    }

    protected void checkOnAttachedToWindow() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.checkOnAttachedToWindow();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => checkOnAttachedToWindow => " + e.getMessage());
        }
    }

    /**********/

    public final boolean isFull() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.isFull();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => isFull => " + e.getMessage());
            return false;
        }
    }

    public final boolean isFloat() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.isFloat();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => isFloat => " + e.getMessage());
            return false;
        }
    }

    public final void startFull() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.startFull();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => startFull => " + e.getMessage());
        }
    }

    public final void stopFull() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.stopFull();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => stopFull => " + e.getMessage());
        }
    }

    public final void startFloat() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.startFloat();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => startFloat => " + e.getMessage());
        }
    }

    public final void stopFloat() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.stopFloat();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => stopFloat => " + e.getMessage());
        }
    }

    public long getPosition() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.getPosition();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => getPosition => " + e.getMessage());
            return 0;
        }
    }

    public long getDuration() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.getDuration();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => getDuration => " + e.getMessage());
            return 0;
        }
    }

    public final void setScaleType(@PlayerType.ScaleType.Value int scaleType) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setScaleType(scaleType);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => setScaleType => " + e.getMessage());
        }
    }

    public final void addComponent(@NonNull ComponentApi componentApi) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.addComponent(componentApi);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => addComponent => " + e.getMessage());
        }
    }

    public final <T extends android.view.View> T findComponent(java.lang.Class<?> cls) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.findComponent(cls);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => findComponent => " + e.getMessage());
            return null;
        }
    }

    public final boolean showComponent(java.lang.Class<?> cls) {
        try {
            View component = findComponent(cls);
            if (null == component)
                throw new Exception("component error: null");
            component.setVisibility(View.VISIBLE);
            return true;
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => showComponent => " + e.getMessage());
            return false;
        }
    }

    public final boolean hideComponent(java.lang.Class<?> cls) {
        try {
            View component = findComponent(cls);
            if (null == component)
                throw new Exception("component error: null");
            component.setVisibility(View.GONE);
            return true;
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => hideComponent => " + e.getMessage());
            return false;
        }
    }

    public final void setPlayerChangeListener(@NonNull OnPlayerChangeListener listener) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setPlayerChangeListener(listener);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => setPlayerChangeListener => " + e.getMessage());
        }
    }

    public final void toggle() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.toggle();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => toggle => " + e.getMessage());
        }
    }

    public final void resume() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.resume();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => resume => " + e.getMessage());
        }
    }

    public final void resume(boolean ignore) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.resume(ignore);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => resume => " + e.getMessage());
        }
    }

    public final void pause() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.pause();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => pause => " + e.getMessage());
        }
    }

    public final void pause(boolean ignore) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.pause(ignore);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => pause => " + e.getMessage());
        }
    }

    public final void release() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.release();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => release => " + e.getMessage());
        }
    }

    public final void stop() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.stop();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => stop => " + e.getMessage());
        }
    }

    public final boolean isPlaying() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.isPlaying();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => isPlaying => " + e.getMessage());
            return false;
        }
    }

    public final String getUrl() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.getUrl();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => getUrl => " + e.getMessage());
            return null;
        }
    }

    public void start(@NonNull String playerUrl) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.start(playerUrl);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => start => " + e.getMessage());
        }
    }

    public void start(@NonNull StartBuilder data, @NonNull String playerUrl) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.start(data, playerUrl);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => start => " + e.getMessage());
        }
    }

    /**********/
    public final void stopExternalMusic(@NonNull boolean release) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.stopExternalMusic(release);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => stopExternalMusic => " + e.getMessage());
        }
    }

    public final void startExternalMusic(@NonNull Context context) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.startExternalMusic(context);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => startExternalMusic => " + e.getMessage());
        }
    }

    public final void startExternalMusic(@NonNull Context context, @Nullable StartBuilder bundle) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.startExternalMusic(context, bundle);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => startExternalMusic => " + e.getMessage());
        }
    }

    public final void setVolume(@NonNull float left, @NonNull float right) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setVolume(left, right);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => setVolume => " + e.getMessage());
        }
    }

    public final void setMute(@NonNull boolean enable) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setMute(enable);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => setMute => " + e.getMessage());
        }
    }

    public final long getSeek() {
        try {
            StartBuilder startBuilder = getStartBuilder();
            if (null == startBuilder)
                throw new Exception("startBuilder error: null");
            return startBuilder.getSeek();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => getSeek => " + e.getMessage());
            return 0;
        }
    }

    public final long getMax() {
        try {
            StartBuilder startBuilder = getStartBuilder();
            if (null == startBuilder)
                throw new Exception("startBuilder error: null");
            return startBuilder.getMax();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => getMax => " + e.getMessage());
            return 0;
        }
    }

    public final void seekTo(@NonNull boolean force, @NonNull long seek, @NonNull long max, @NonNull boolean loop) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.seekTo(force, seek, max, loop);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBase => seekTo => " + e.getMessage());
        }
    }
}