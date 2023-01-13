package lib.kalu.mediaplayer.core.controller.base;

import android.view.ViewGroup;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.core.controller.ControllerApi;
import lib.kalu.mediaplayer.core.kernel.KernelApi;
import lib.kalu.mediaplayer.core.player.api.PlayerApi;
import lib.kalu.mediaplayer.core.render.RenderApi;

@Keep
public class ControllerWrapper implements PlayerApi, ControllerApi {

    // 播放器
    private PlayerApi mPlayer;
    // 控制器
    private ControllerApi mController;

    public ControllerWrapper(@NonNull PlayerApi player, @NonNull ControllerApi controller) {
        this.mPlayer = player;
        this.mController = controller;
    }

    @Override
    public void setVolume(float v1, float v2) {
        try {
            this.mPlayer.setVolume(v1, v2);
        } catch (Exception e) {
        }
    }

    @Override
    public void setMute(boolean v) {
        try {
            this.mPlayer.setMute(v);
        } catch (Exception e) {
        }
    }

    @Override
    public void toggle(boolean cleanHandler) {
        try {
            mPlayer.toggle(cleanHandler);
        } catch (Exception e) {
        }
    }

    @Override
    public void stopKernel(@NonNull boolean call) {
        try {
            mPlayer.stopKernel(call);
        } catch (Exception e) {
        }
    }

    @Override
    public void pauseKernel(@NonNull boolean call) {
        try {
            mPlayer.pauseKernel(call);
        } catch (Exception e) {
        }
    }

    @Override
    public void pause(boolean auto) {
        try {
            mPlayer.pause(auto);
        } catch (Exception e) {
        }
    }

    @Override
    public void resume(boolean call) {
        try {
            mPlayer.resume(call);
        } catch (Exception e) {
        }
    }

    @Override
    public void stop() {
        try {
            mPlayer.stop();
        } catch (Exception e) {
        }
    }

    @Override
    public long getDuration() {
        try {
            return mPlayer.getDuration();
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public long getPosition() {
        try {
            return mPlayer.getPosition();
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public boolean isLooping() {
        try {
            return mPlayer.isLooping();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isInvisibleStop() {
        try {
            return mPlayer.isInvisibleStop();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isInvisibleIgnore() {
        try {
            return mPlayer.isInvisibleIgnore();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isInvisibleRelease() {
        try {
            return mPlayer.isInvisibleRelease();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public long getSeek() {
        try {
            return mPlayer.getSeek();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public long getMax() {
        try {
            return mPlayer.getMax();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public String getUrl() {
        try {
            return mPlayer.getUrl();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean isPlaying() {
        try {
            return mPlayer.isPlaying();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isFull() {
        try {
            return mPlayer.isFull();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void startFull() {
        try {
            mPlayer.startFull();
        } catch (Exception e) {
        }
    }

    @Override
    public void stopFull() {
        try {
            mPlayer.stopFull();
        } catch (Exception e) {
        }
    }

    @Override
    public boolean isFloat() {
        try {
            return mPlayer.isFloat();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void startFloat() {
        try {
            mPlayer.startFloat();
        } catch (Exception e) {
        }
    }

    @Override
    public void stopFloat() {
        try {
            mPlayer.stopFloat();
        } catch (Exception e) {
        }
    }

    @Override
    public int getBufferedPercentage() {
        try {
            return mPlayer.getBufferedPercentage();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void seekTo(@NonNull boolean force, @NonNull StartBuilder builder) {
        try {
            mPlayer.seekTo(force, builder);
        } catch (Exception e) {
        }
    }

    @Override
    public boolean isLive() {
        try {
            return mPlayer.isLive();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isMute() {
        try {
            return mPlayer.isMute();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void setScaleType(int scaleType) {
        try {
            mPlayer.setScaleType(scaleType);
        } catch (Exception e) {
        }
    }

    @Override
    public void setSpeed(float speed) {
        try {
            mPlayer.setSpeed(speed);
        } catch (Exception e) {
        }
    }

    @Override
    public float getSpeed() {
        try {
            return mPlayer.getSpeed();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void setMirrorRotation(boolean enable) {
        try {
            mPlayer.setMirrorRotation(enable);
        } catch (Exception e) {
        }
    }

    @Override
    public ViewGroup getLayout() {
        try {
            return mPlayer.getLayout();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ControllerLayout getControlLayout() {
        try {
            return (ControllerLayout) mController;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void showReal() {
        try {
            mPlayer.showReal();
        } catch (Exception e) {
        }
    }

    @Override
    public void hideReal() {
        try {
            mPlayer.hideReal();
        } catch (Exception e) {
        }
    }

    @Override
    public void checkReal() {
        try {
            mPlayer.checkReal();
        } catch (Exception e) {
        }
    }

    @Override
    public void releaseKernel() {
        try {
            mPlayer.releaseKernel();
        } catch (Exception e) {
        }
    }

    @Override
    public void releaseRender() {
        try {
            mPlayer.releaseRender();
        } catch (Exception e) {
        }
    }

    @Override
    public void clearRender() {

    }

    @Override
    public void release(@NonNull boolean onlyHandle) {
        try {
            mPlayer.release(onlyHandle);
        } catch (Exception e) {
        }
    }

    @Override
    public void playEnd() {
        try {
            mPlayer.playEnd();
        } catch (Exception e) {
        }
    }

    @Override
    public void setKernel(int v) {
        try {
            mPlayer.setKernel(v);
        } catch (Exception e) {
        }
    }

    @Override
    public KernelApi getKernel() {
        return null;
    }

    @Override
    public void setKernel(@NonNull KernelApi kernel) {

    }

    @Override
    public void setRender(int v) {
        try {
            mPlayer.setRender(v);
        } catch (Exception e) {
        }
    }

    @Override
    public void callPlayerState(int playerState) {
        try {
            mPlayer.callPlayerState(playerState);
        } catch (Exception e) {
        }
    }

    @Override
    public void callWindowState(int windowState) {
        try {
            mPlayer.callWindowState(windowState);
        } catch (Exception e) {
        }
    }

    @Override
    public void enableExternalMusic(boolean enable, boolean release) {
        try {
            mPlayer.enableExternalMusic(enable, release);
        } catch (Exception e) {
        }
    }

    @Override
    public void enableExternalMusic(boolean enable, boolean release, boolean auto) {
        try {
            mPlayer.enableExternalMusic(enable, release, auto);
        } catch (Exception e) {
        }
    }

    @Override
    public void setExternalMusic(@NonNull StartBuilder bundle) {
        try {
            mPlayer.setExternalMusic(bundle);
        } catch (Exception e) {
        }
    }

    @Override
    public boolean isExternalMusicAuto() {
        try {
            return mPlayer.isExternalMusicAuto();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isExternalMusicLoop() {
        try {
            return mPlayer.isExternalMusicLoop();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isExternalMusicPrepared() {
        try {
            return mPlayer.isExternalMusicPrepared();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public RenderApi getRender() {
        return null;
    }

    @Override
    public void setRender(@NonNull RenderApi render) {

    }

    @Override
    public void clearSuface() {
        try {
            mPlayer.clearSuface();
        } catch (Exception e) {
        }
    }

    @Override
    public void updateSuface() {
        try {
            mPlayer.updateSuface();
        } catch (Exception e) {
        }
    }

    @Override
    public String screenshot() {
        try {
            return mPlayer.screenshot();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void startFadeOut() {
        try {
            mController.startFadeOut();
        } catch (Exception e) {
        }
    }

    @Override
    public void stopFadeOut() {
        try {
            mController.stopFadeOut();
        } catch (Exception e) {
        }
    }

    @Override
    public boolean isShowing() {
        try {
            return mController.isShowing();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void setLocked(boolean locked) {
        try {
            mController.setLocked(locked);
        } catch (Exception e) {
        }
    }

    @Override
    public boolean isLocked() {
        try {
            return mController.isLocked();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void hide() {
        try {
            mController.hide();
        } catch (Exception e) {
        }
    }

    @Override
    public void show() {
        try {
            mController.show();
        } catch (Exception e) {
        }
    }

    @Override
    public boolean hasCutout() {
        try {
            return mController.hasCutout();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int getCutoutHeight() {
        try {
            return mController.getCutoutHeight();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void seekForwardDown(boolean enable) {
        try {
            mController.seekForwardDown(enable);
        } catch (Exception e) {
        }
    }

    @Override
    public void seekForwardUp(boolean enable) {
        try {
            mController.seekForwardUp(enable);
        } catch (Exception e) {
        }
    }

    @Override
    public void seekRewindDown(boolean enable) {
        try {
            mController.seekRewindDown(enable);
        } catch (Exception e) {
        }
    }

    @Override
    public void seekRewindUp(boolean enable) {
        try {
            mController.seekRewindUp(enable);
        } catch (Exception e) {
        }
    }

    @Override
    public void init() {
        try {
        } catch (Exception e) {
        }
    }

    @Override
    public void destroy() {
        try {
            mController.destroy();
        } catch (Exception e) {
        }
    }

    @Override
    public int initLayout() {
        return 0;
    }

    /**
     * 切换锁定状态
     */
    public void toggleLockState() {
        try {
            setLocked(!isLocked());
        } catch (Exception e) {
        }
    }


    /**
     * 切换显示/隐藏状态
     */
    public void toggleShowState() {
        try {
            if (isShowing()) {
                hide();
            } else {
                show();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void clearComponent() {

    }

    @Override
    public void setScreenKeep(boolean enable) {

    }
}
