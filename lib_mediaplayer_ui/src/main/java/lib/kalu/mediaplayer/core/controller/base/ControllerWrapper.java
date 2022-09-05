package lib.kalu.mediaplayer.core.controller.base;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.view.View;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import java.util.Map;

import lib.kalu.mediaplayer.config.builder.BundleBuilder;
import lib.kalu.mediaplayer.core.controller.ControllerApi;
import lib.kalu.mediaplayer.core.view.PlayerApi;

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
    public void start(@NonNull BundleBuilder builder, @NonNull String url) {
        mPlayer.start(builder, url);
    }

    @Override
    public void create(@NonNull BundleBuilder builder) {
        mPlayer.create(builder);
    }

    @Override
    public void toggle() {
        mPlayer.toggle();
    }

    @Override
    public void pause(boolean auto) {
        mPlayer.pause(auto);
    }

    @Override
    public void resume() {
        mPlayer.resume();
    }

    @Override
    public void close() {
        mPlayer.close();
    }

    @Override
    public void repeat() {
        mPlayer.repeat();
    }

    @Override
    public long getDuration() {
        return mPlayer.getDuration();
    }

    @Override
    public long getPosition() {
        return mPlayer.getPosition();
    }

    @Override
    public boolean isLooping() {
        return mPlayer.isLooping();
    }

    @Override
    public boolean isRelease() {
        return mPlayer.isRelease();
    }

    @Override
    public long getSeek() {
        return mPlayer.getSeek();
    }

    @Override
    public long getMax() {
        return mPlayer.getMax();
    }

    @Override
    public String getUrl() {
        return mPlayer.getUrl();
    }

    @Override
    public void toggleMusicExtra(boolean auto) {
        mPlayer.toggleMusicExtra(auto);
    }

    @Override
    public void toggleMusicDefault() {
        mPlayer.toggleMusicDefault();
    }

    @Override
    public void toggleMusicDefault(boolean musicPrepare) {
        mPlayer.toggleMusicDefault(musicPrepare);
    }

    @Override
    public void toggleMusic() {
        mPlayer.toggleMusic();
    }

    @Override
    public boolean hasMusicExtra() {
        return false;
    }

    @Override
    public void updateMusic(@NonNull String musicPath, @NonNull boolean musicPlay, @NonNull boolean musicLoop, @NonNull boolean musicSeek) {
        mPlayer.updateMusic(musicPath, musicPlay, musicLoop, musicSeek);
    }

    @Override
    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    @Override
    public boolean isFull() {
        return mPlayer.isFull();
    }

    @Override
    public void startFull() {
        mPlayer.startFull();
    }

    @Override
    public void stopFull() {
        mPlayer.stopFull();
    }

    @Override
    public boolean isFloat() {
        return mPlayer.isFloat();
    }

    @Override
    public void startFloat() {
        mPlayer.startFloat();
    }

    @Override
    public void stopFloat() {
        mPlayer.stopFloat();
    }

    @Override
    public int getBufferedPercentage() {
        return mPlayer.getBufferedPercentage();
    }

    @Override
    public void seekTo(@NonNull boolean force, @NonNull BundleBuilder builder) {
        mPlayer.seekTo(force, builder);
    }

    @Override
    public boolean seekForward(@NonNull boolean callback) {
        return mPlayer.seekForward(callback);
    }

    @Override
    public boolean seekRewind(boolean callback) {
        return mPlayer.seekRewind(callback);
    }

    @Override
    public boolean isLive() {
        return mPlayer.isLive();
    }

    @Override
    public boolean isMute() {
        return mPlayer.isMute();
    }

    @Override
    public void setScaleType(int scaleType) {
        mPlayer.setScaleType(scaleType);
    }

    @Override
    public void setSpeed(float speed) {
        mPlayer.setSpeed(speed);
    }

    @Override
    public float getSpeed() {
        return mPlayer.getSpeed();
    }

    @Override
    public long getTcpSpeed() {
        return mPlayer.getTcpSpeed();
    }

    @Override
    public void setMirrorRotation(boolean enable) {
        mPlayer.setMirrorRotation(enable);
    }

    @Override
    public Bitmap doScreenShot() {
        return mPlayer.doScreenShot();
    }

    @Override
    public int[] getVideoSize() {
        return mPlayer.getVideoSize();
    }

    @Override
    public void setRotation(float rotation) {
        mPlayer.setRotation(rotation);
    }

    @Override
    public ControllerLayout getControlLayout() {
        return null;
    }

    @Override
    public void showReal() {
        mPlayer.showReal();
    }

    @Override
    public void goneReal() {
        mPlayer.goneReal();
    }

    @Override
    public void checkReal() {
        mPlayer.checkReal();
    }

    @Override
    public void release(@NonNull boolean onlyHandle) {
        mPlayer.release(onlyHandle);
    }

    @Override
    public void startHanlder() {
        mPlayer.startHanlder();
    }

    @Override
    public void clearHanlder() {
        mPlayer.clearHanlder();
    }

    @Override
    public void playEnd() {
        mPlayer.playEnd();
    }

    @Override
    public void callPlayerState(int playerState) {
        mPlayer.callPlayerState(playerState);
    }

    @Override
    public void callWindowState(int windowState) {
        mPlayer.callWindowState(windowState);
    }

//    /**
//     * 横竖屏切换，会旋转屏幕
//     */
//    public void toggleFullScreen(Activity activity) {
//        if (activity == null || activity.isFinishing())
//            return;
//        if (isFullScreen()) {
//            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            stopFullScreen();
//        } else {
//            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            startFullScreen();
//        }
//    }
//
//    /**
//     * 横竖屏切换，不会旋转屏幕
//     */
//    public void toggleFullScreen() {
//        if (isFullScreen()) {
//            stopFullScreen();
//        } else {
//            startFullScreen();
//        }
//    }

//    /**
//     * 横竖屏切换，根据适配宽高决定是否旋转屏幕
//     */
//    public void toggleFullScreenByVideoSize(Activity activity) {
//        if (activity == null || activity.isFinishing())
//            return;
//        int[] size = getVideoSize();
//        int width = size[0];
//        int height = size[1];
//        if (isFullScreen()) {
//            stopFullScreen();
//            if (width > height) {
//                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            }
//        } else {
//            startFullScreen();
//            if (width > height) {
//                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            }
//        }
//    }

    @Override
    public void startFadeOut() {
        mController.startFadeOut();
    }

    @Override
    public void stopFadeOut() {
        mController.stopFadeOut();
    }

    @Override
    public boolean isShowing() {
        return mController.isShowing();
    }

    @Override
    public void setLocked(boolean locked) {
        mController.setLocked(locked);
    }

    @Override
    public boolean isLocked() {
        return mController.isLocked();
    }

    @Override
    public void hide() {
        mController.hide();
    }

    @Override
    public void show() {
        mController.show();
    }

    @Override
    public boolean hasCutout() {
        return mController.hasCutout();
    }

    @Override
    public int getCutoutHeight() {
        return mController.getCutoutHeight();
    }

    @Override
    public void seekProgress(@NonNull boolean fromUser, @NonNull long position, @NonNull long duration) {
        mController.seekProgress(fromUser, position, duration);
    }

    @Override
    public void init() {
    }

    @Override
    public void destroy() {
        mController.destroy();
    }

    @Override
    public int initLayout() {
        return 0;
    }

    /**
     * 切换锁定状态
     */
    public void toggleLockState() {
        setLocked(!isLocked());
    }


    /**
     * 切换显示/隐藏状态
     */
    public void toggleShowState() {
        if (isShowing()) {
            hide();
        } else {
            show();
        }
    }
}
