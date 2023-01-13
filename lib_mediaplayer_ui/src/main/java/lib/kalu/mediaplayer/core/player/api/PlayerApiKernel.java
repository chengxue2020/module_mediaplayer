package lib.kalu.mediaplayer.core.player.api;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import java.util.List;

import lib.kalu.mediaplayer.config.buried.BuriedEvent;
import lib.kalu.mediaplayer.config.player.PlayerBuilder;
import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.core.controller.base.ControllerLayout;
import lib.kalu.mediaplayer.core.kernel.KernelApi;
import lib.kalu.mediaplayer.core.kernel.KernelEvent;
import lib.kalu.mediaplayer.core.kernel.KernelFactoryManager;
import lib.kalu.mediaplayer.core.render.RenderApi;
import lib.kalu.mediaplayer.listener.OnChangeListener;
import lib.kalu.mediaplayer.util.MPLogUtil;
import lib.kalu.mediaplayer.util.PlayerUtils;

public interface PlayerApiKernel extends
        PlayerApiRender,
        PlayerApiDevice {

    default void start(@NonNull String url) {
        StartBuilder.Builder builder = new StartBuilder.Builder();
        StartBuilder build = builder.build();
        start(build, url);
    }

    default void startSeek(@NonNull long seek, @NonNull String url) {
        StartBuilder.Builder builder = new StartBuilder.Builder();
        builder.setSeek(seek);
        StartBuilder build = builder.build();
        start(build, url);
    }

    default void startLive(@NonNull String url) {
        StartBuilder.Builder builder = new StartBuilder.Builder();
        builder.setLive(true);
        builder.setLoop(false);
        StartBuilder build = builder.build();
        start(build, url);
    }

    default void startLoop(@NonNull String url) {
        StartBuilder.Builder builder = new StartBuilder.Builder();
        builder.setLive(false);
        builder.setLoop(true);
        StartBuilder build = builder.build();
        start(build, url);
    }

    default void startLoop(@NonNull long seek, @NonNull long max, @NonNull String url) {
        StartBuilder.Builder builder = new StartBuilder.Builder();
        builder.setLive(false);
        builder.setLoop(true);
        builder.setSeek(seek);
        builder.setMax(max);
        StartBuilder build = builder.build();
        start(build, url);
    }

    default void start(@NonNull StartBuilder builder, @NonNull String url) {

        String s = builder.toString();
        MPLogUtil.log("PlayerApiKernel => start => url = " + url + ", data = " + s);
        if (null == url || url.length() <= 0)
            return;

        try {

            // log
            PlayerBuilder config = PlayerManager.getInstance().getConfig();
            int type = config.getKernel();
            MPLogUtil.setLogger(type, config.isLog());
            // step0
            callPlayerState(PlayerType.StateType.STATE_INIT);
            // step1
            callPlayerState(PlayerType.StateType.STATE_LOADING_START);
            // 1
            createRender();
            // 2
            createKernel(builder, config.isLog());
            // 3
            updateKernel(builder, url);
            // 4
            attachRender();
        } catch (Exception e) {
        }
    }

    /**********/

    default StartBuilder getStartBuilder() {
        try {
            String url = getUrl();
            if (null == url || url.length() <= 0)
                throw new Exception();
            StartBuilder.Builder builder = new StartBuilder.Builder();
            builder.setMax(getMax());
            builder.setSeek(getSeek());
            builder.setLoop(isLooping());
            builder.setLive(isLive());
            builder.setMute(isMute());
            builder.setInvisibleStop(isInvisibleStop());
            builder.setInvisibleIgnore(isInvisibleIgnore());
            builder.setInvisibleRelease(isInvisibleRelease());
            return builder.build();
        } catch (Exception e) {
            return null;
        }
    }

    default long getDuration() {
        try {
            KernelApi kernel = getKernel();
            long duration = kernel.getDuration();
            if (duration < 0L) {
                duration = 0L;
            }
            return duration;
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
            return 0L;
        }
    }

    default long getPosition() {
        try {
            KernelApi kernel = getKernel();
            long position = kernel.getPosition();
            if (position < 0L) {
                position = 0L;
            }
            return position;
        } catch (Exception e) {
            return 0L;
        }
    }

    default void setVolume(@FloatRange(from = 0f, to = 1f) float left, @FloatRange(from = 0f, to = 1f) float right) {
        try {
            KernelApi kernel = getKernel();
            kernel.setVolume(left, right);
        } catch (Exception e) {
        }
    }

    default void setMute(boolean enable) {
        try {
            KernelApi kernel = getKernel();
            kernel.setMute(enable);
        } catch (Exception e) {
        }
    }

    default void setLooping(boolean looping) {
        try {
            KernelApi kernel = getKernel();
            kernel.setLooping(looping);
        } catch (Exception e) {
        }
    }

    default void release() {
        release(false);
    }

    default void release(@NonNull boolean onlyHandle) {
//        MPLogUtil.log("onEvent => release => onlyHandle = " + onlyHandle + ", mKernel = " + mKernel);

        KernelApi kernel = getKernel();
        if (null == kernel)
            return;

        // step2
        pause(!onlyHandle);

        // step3
        if (onlyHandle)
            return;

        boolean isFloat = isFloat();
        if (isFloat) {
            stopFloat();
        }

        boolean isFull = isFull();
        if (isFull) {
            stopFull();
        }

        // step51
        clearRender();

        // step52
        releaseRender();

        // step7
        releaseKernel();
    }

    default void toggle() {
        toggle(false);
    }

    default void toggle(boolean cleanHandler) {
        try {
            boolean playing = isPlaying();
            if (playing) {
                pause(cleanHandler);
            } else {
                resume(true);
            }
        } catch (Exception e) {
        }
    }

    default void pause() {
        pause(false);
    }

    default void pause(boolean auto) {
        try {
            boolean playing = isPlaying();
            MPLogUtil.log("pauseME => auto = " + auto + ", playing = " + playing);
            if (!playing)
                return;
            pauseKernel(!auto);
        } catch (Exception e) {
        }
    }

    default void stop() {
        try {
            boolean playing = isPlaying();
            if (!playing)
                return;
            stopKernel(true);
        } catch (Exception e) {
        }
    }


    default void restart() {
        try {
            StartBuilder builder = getStartBuilder();
            MPLogUtil.log("PlayerApiKernel => restart => builder = " + builder);
            if (null == builder)
                return;
            callPlayerState(PlayerType.StateType.STATE_RESTAER);
            String url = getUrl();
            start(builder, url);
        } catch (Exception e) {
        }
    }

    default void resume() {
        MPLogUtil.log("PlayerApiKernel => resume =>");
        resume(true);
    }

    default void resume(boolean call) {
        try {
            MPLogUtil.log("PlayerApiKernel => resume => call = " + call);
            checkKernel();
            resumeKernel(call);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => resume => " + e.getMessage());
        }
    }

    default boolean isLooping() {
        try {
            KernelApi kernel = getKernel();
            return kernel.isLooping();
        } catch (Exception e) {
            return false;
        }
    }

    default boolean isInvisibleStop() {
        try {
            KernelApi kernel = getKernel();
            return kernel.isInvisibleStop();
        } catch (Exception e) {
            return false;
        }
    }

    default boolean isInvisibleIgnore() {
        try {
            KernelApi kernel = getKernel();
            return kernel.isInvisibleIgnore();
        } catch (Exception e) {
            return false;
        }
    }

    default boolean isInvisibleRelease() {
        try {
            KernelApi kernel = getKernel();
            return kernel.isInvisibleRelease();
        } catch (Exception e) {
            return false;
        }
    }

    default long getSeek() {
        try {
            KernelApi kernel = getKernel();
            return kernel.getSeek();
        } catch (Exception e) {
            return 0L;
        }
    }

    default long getMax() {
        try {
            KernelApi kernel = getKernel();
            return kernel.getMax();
        } catch (Exception e) {
            return 0L;
        }
    }

    default String getUrl() {
        try {
            KernelApi kernel = getKernel();
            return kernel.getUrl();
        } catch (Exception e) {
            return null;
        }
    }

    default int getBufferedPercentage() {
        try {
            KernelApi kernel = getKernel();
            return kernel.getBufferedPercentage();
        } catch (Exception e) {
            return 0;
        }
    }

    default void seekTo(@NonNull boolean force) {
        StartBuilder.Builder builder = new StartBuilder.Builder();
        builder.setMax(getMax());
        builder.setSeek(getSeek());
        builder.setLoop(isLooping());
        builder.setLive(isLive());
        builder.setMute(isMute());
        builder.setInvisibleStop(isInvisibleStop());
        builder.setInvisibleIgnore(isInvisibleIgnore());
        builder.setInvisibleRelease(isInvisibleRelease());
        StartBuilder build = builder.build();
        seekTo(force, build);
    }

    default void seekTo(@NonNull long seek) {
        seekTo(false, seek, getMax(), isLooping());
    }

    default void seekTo(@NonNull long seek, @NonNull long max) {
        seekTo(false, seek, max, isLooping());
    }

    default void seekTo(@NonNull boolean force, @NonNull long seek) {
        seekTo(force, seek, getMax(), isLooping());
    }

    default void seekTo(@NonNull boolean force, @NonNull long seek, @NonNull long max, @NonNull boolean loop) {
        StartBuilder.Builder builder = new StartBuilder.Builder();
        builder.setMax(max);
        builder.setSeek(seek);
        builder.setLoop(loop);
        builder.setLive(isLive());
        builder.setInvisibleStop(isInvisibleStop());
        builder.setInvisibleIgnore(isInvisibleIgnore());
        builder.setInvisibleRelease(isInvisibleRelease());
        StartBuilder build = builder.build();
        seekTo(force, build);
    }

    default void seekTo(@NonNull boolean force, @NonNull StartBuilder builder) {

        try {
            // 1
            checkKernel();
            // 2
            if (force) {
                updateKernel(builder);
                pause(true);
            }
            // 3
            long seek = builder.getSeek();
            seekToKernel(seek);
        } catch (Exception e) {
        }
    }

    default boolean isLive() {
        try {
            KernelApi kernel = getKernel();
            return kernel.isLive();
        } catch (Exception e) {
            return false;
        }
    }

    default boolean isPlaying() {
        try {
            KernelApi kernel = getKernel();
            return kernel.isPlaying();
        } catch (Exception e) {
            return false;
        }
    }

    default boolean isMute() {
        try {
            KernelApi kernel = getKernel();
            return kernel.isMute();
        } catch (Exception e) {
            return false;
        }
    }

    default void setSpeed(float speed) {
        try {
            KernelApi kernel = getKernel();
            kernel.setSpeed(speed);
        } catch (Exception e) {
        }
    }

    default float getSpeed() {
        try {
            KernelApi kernel = getKernel();
            return kernel.getSpeed();
        } catch (Exception e) {
            return 1F;
        }
    }

    /*********************/

    default void checkKernel() throws Exception {
        KernelApi kernel = getKernel();
        if (null == kernel)
            throw new Exception("kernel is null");
    }

    default void resumeKernel(@NonNull boolean call) {
        try {
            // 1
            checkKernel();
            // 2
            KernelApi kernel = getKernel();
            MPLogUtil.log("PlayerApiKernel => resumeKernel => call = " + call + ", kernel = " + kernel);
            kernel.start();
            setScreenKeep(true);
            if (call) {
                callPlayerState(PlayerType.StateType.STATE_RESUME);
                callPlayerState(PlayerType.StateType.STATE_KERNEL_RESUME);
                callPlayerState(PlayerType.StateType.STATE_LOADING_STOP);
            } else {
                callPlayerState(PlayerType.StateType.STATE_RESUME_IGNORE);
            }
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => resumeKernel => " + e.getMessage());
        }
    }

    default void stopKernel(@NonNull boolean call) {
        try {
            // 1
            checkKernel();
            // 2
            KernelApi kernel = getKernel();
            MPLogUtil.log("PlayerApiKernel => stopKernel => kernel = " + kernel);
            kernel.stop();
            setScreenKeep(false);
            if (!call)
                return;
//            callPlayerState(PlayerType.StateType.STATE_LOADING_STOP);
            callPlayerState(PlayerType.StateType.STATE_KERNEL_STOP);
            callPlayerState(PlayerType.StateType.STATE_CLOSE);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => stopKernel => " + e.getMessage());
        }
    }

    default void pauseKernel(@NonNull boolean call) {
        try {
            // 1
            checkKernel();
            // 2
            KernelApi kernel = getKernel();
            MPLogUtil.log("PlayerApiKernel => pauseKernel => kernel = " + kernel);
            kernel.pause();
            setScreenKeep(false);
            callPlayerState(call ? PlayerType.StateType.STATE_PAUSE : PlayerType.StateType.STATE_PAUSE_IGNORE);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => pauseKernel => " + e.getMessage());
        }
    }

    default void releaseKernel() {
        try {
            // 1
            checkKernel();
            // 2
            KernelApi kernel = getKernel();
            MPLogUtil.log("PlayerApiKernel => releaseKernel => kernel = " + kernel);
            kernel.releaseDecoder();
            kernel = null;
            setScreenKeep(false);
            MPLogUtil.log("PlayerApiKernel => releaseKernel => succ");
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => releaseKernel => " + e.getMessage());
        }
    }

    default void seekToKernel(long milliSeconds) {
        try {
            // 1
            checkKernel();
            // 2
            KernelApi kernel = getKernel();
            MPLogUtil.log("PlayerApiKernel => seekToKernel => milliSeconds = " + milliSeconds + ", kernel = " + kernel);
            kernel.seekTo(milliSeconds);
            setScreenKeep(true);
            if (milliSeconds <= 0)
                return;
            callPlayerState(PlayerType.StateType.STATE_LOADING_START);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => seekToKernel => " + e.getMessage());
        }
    }

    default void updateKernel(@NonNull StartBuilder builder) {
        try {
            // 1
            checkKernel();
            // 2
            KernelApi kernel = getKernel();
            MPLogUtil.log("PlayerApiKernel => updateKernel => kernel = " + kernel);
            long seek = builder.getSeek();
            long max = builder.getMax();
            boolean loop = builder.isLoop();
            kernel.update(seek, max, loop);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => updateKernel => " + e.getMessage());
        }
    }

    default void updateKernel(@NonNull StartBuilder bundle, @NonNull String playUrl) {
        try {
            // 1
            checkKernel();
            // 2
            KernelApi kernel = getKernel();
            MPLogUtil.log("PlayerApiKernel => updateKernel => kernel = " + kernel);
            ViewGroup layout = getLayout();
            Context context = layout.getContext();
            kernel.update(context, bundle, playUrl);
            setScreenKeep(true);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => updateKernel => " + e.getMessage());
        }
    }

    /***************************/

    default void enableExternalMusic(boolean enable, boolean release) {
        enableExternalMusic(enable, release, false);
    }

    default void enableExternalMusic(boolean enable, boolean release, boolean auto) {
        try {
            KernelApi kernel = getKernel();
            if (auto) {
                kernel.setExternalMusicAuto(true);
            }
            kernel.enableExternalMusic(enable, release);
        } catch (Exception e) {
        }
    }

    default boolean isExternalMusicAuto() {
        try {
            KernelApi kernel = getKernel();
            boolean auto = kernel.isExternalMusicAuto();
            MPLogUtil.log("isExternalMusicAuto => auto = " + auto);
            if (auto) {
                String path = kernel.getExternalMusicPath();
                return null != path && path.length() > 0;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    default boolean isExternalMusicLoop() {
        try {
            KernelApi kernel = getKernel();
            return kernel.isExternalMusicLoop();
        } catch (Exception e) {
            return false;
        }
    }

    default boolean isExternalMusicPrepared() {
        try {
            KernelApi kernel = getKernel();
            return kernel.isExternalMusicPrepared();
        } catch (Exception e) {
            return false;
        }
    }

    default void setExternalMusic(@NonNull StartBuilder bundle) {
        try {
            KernelApi kernel = getKernel();
            String url = bundle.getExternalMusicUrl();
            kernel.setExternalMusicPath(url);
            boolean loop = bundle.isExternalMusicLoop();
            kernel.setExternalMusicLoop(loop);
        } catch (Exception e) {
        }
    }

    default void playEnd() {
        hideReal();
        setScreenKeep(false);
        callPlayerState(PlayerType.StateType.STATE_END);
    }

    /***************************/

    default void setKernel(@PlayerType.KernelType.Value int v) {
        try {
            PlayerBuilder config = PlayerManager.getInstance().getConfig();
            PlayerBuilder.Builder builder = config.newBuilder();
            builder.setKernel(v);
            PlayerManager.getInstance().setConfig(config);
        } catch (Exception e) {
        }
    }

    default void createKernel(@NonNull StartBuilder builder, @NonNull boolean logger) {

        try {
            // 1
            releaseKernel();
            // 2
            ViewGroup layout = getLayout();
            Context context = layout.getContext();
            int type = PlayerManager.getInstance().getConfig().getKernel();
            KernelApi kernel = KernelFactoryManager.getKernel(context, type, new KernelEvent() {

                @Override
                public void onUpdateTimeMillis(@NonNull boolean isLooping, @NonNull long max, @NonNull long seek, @NonNull long position, @NonNull long duration) {

                    boolean replay;
                    if (max > 0) {
                        long playTime = (position - seek);
                        if (playTime > max) {
                            replay = true;
                        } else {
                            replay = false;
                        }
                    } else {
                        replay = false;
                    }
//                    MPLogUtil.log("PlayerApiKernel => onUpdateTimeMillis => replay = " + replay + ", isLooping = " + isLooping + ", max = " + max + ", seek = " + seek + ", position = " + position + ", duration = " + duration);

                    // end
                    if (replay) {

                        // loop
                        if (isLooping) {

                            // step1
                            hideReal();

                            // step2
                            // pause(true, true);

                            // step3
                            seekTo(true);
                        }
                        // stop
                        else {

                            // step1
                            pause(true);

                            // step2
                            playEnd();
                        }
                    }
                    // next
                    else {

                        ControllerLayout controlLayout = getControlLayout();
                        if (null != controlLayout) {
                            controlLayout.onUpdateTimeMillis(seek, position, duration);
                        }

                        List<OnChangeListener> listener = getOnChangeListener();
                        if (null != listener) {
                            for (OnChangeListener l : listener) {
                                if (null == l)
                                    continue;
                                l.onProgress(position, duration);
                            }
                        }
                    }
                }

                @Override
                public void onEvent(int kernel, int event) {

                    MPLogUtil.log("PlayerApiKernel => onEvent = " + kernel + ", event = " + event);

                    switch (event) {
                        // 网络拉流开始
                        case PlayerType.EventType.EVENT_OPEN_INPUT:
                            hideReal();
                            break;
                        // 初始化开始 => loading start
                        case PlayerType.EventType.EVENT_LOADING_START:
                            callPlayerState(PlayerType.StateType.STATE_LOADING_START);
                            break;
                        // 初始化完成 => loading stop
                        case PlayerType.EventType.EVENT_LOADING_STOP:
                            callPlayerState(PlayerType.StateType.STATE_LOADING_STOP);
                            break;
                        // 缓冲开始
                        case PlayerType.EventType.EVENT_BUFFERING_START:
                            callPlayerState(PlayerType.StateType.STATE_BUFFERING_START);
                            break;
                        // 缓冲结束
                        case PlayerType.EventType.EVENT_BUFFERING_STOP:
                            callPlayerState(PlayerType.StateType.STATE_BUFFERING_STOP);
                            break;
                        // 播放开始-快进
                        case PlayerType.EventType.EVENT_VIDEO_START_SEEK:
                            // step1
                            callPlayerState(PlayerType.StateType.STATE_LOADING_STOP);
                            callPlayerState(PlayerType.StateType.STATE_START_SEEK);
                            // step2
                            showReal();
                            // step4
                            resume(false);
                            // step5
                            boolean musicAuto = isExternalMusicAuto();
                            if (musicAuto) {
                                boolean musicLoop = isExternalMusicLoop();
                                boolean prepared = isExternalMusicPrepared();
                                if (musicLoop || !prepared) {
                                    enableExternalMusic(true, true);
                                } else {
                                    enableExternalMusic(false, true);
                                }
                            } else {
                                boolean prepared = isExternalMusicPrepared();
                                enableExternalMusic(false, prepared);
                            }

                            break;
                        // 播放开始
                        case PlayerType.EventType.EVENT_VIDEO_START:
//                        case PlayerType.EventType.EVENT_VIDEO_SEEK_RENDERING_START: // 视频开始渲染
//            case PlayerType.MediaType.MEDIA_INFO_AUDIO_SEEK_RENDERING_START: // 视频开始渲染


                            callPlayerState(PlayerType.StateType.STATE_START);

                            // step2
                            showReal();
                            // step3
                            checkReal();
                            // step4
                            boolean musicAuto1 = isExternalMusicAuto();
                            if (musicAuto1) {
                                boolean musicLoop = isExternalMusicLoop();
                                boolean prepared = isExternalMusicPrepared();
                                if (musicLoop || !prepared) {
                                    enableExternalMusic(true, true);
                                } else {
                                    enableExternalMusic(false, true);
                                }
                            } else {
                                boolean prepared = isExternalMusicPrepared();
                                enableExternalMusic(false, prepared);
                            }

                            // 埋点
                            try {
                                BuriedEvent buriedEvent = PlayerManager.getInstance().getConfig().getBuriedEvent();
                                buriedEvent.playerIn(getUrl());
                            } catch (Exception e) {
                            }

                            break;
                        // 播放结束
                        case PlayerType.EventType.EVENT_ERROR_URL:
                        case PlayerType.EventType.EVENT_ERROR_RETRY:
                        case PlayerType.EventType.EVENT_ERROR_SOURCE:
                        case PlayerType.EventType.EVENT_ERROR_PARSE:
                        case PlayerType.EventType.EVENT_ERROR_NET:

                            boolean connected = PlayerUtils.isConnected(context);
                            setScreenKeep(false);
                            callPlayerState(connected ? PlayerType.StateType.STATE_ERROR : PlayerType.StateType.STATE_ERROR_NET);

                            // step2
                            pause(true);

                            // 埋点
                            try {
                                BuriedEvent buriedEvent = PlayerManager.getInstance().getConfig().getBuriedEvent();
                                buriedEvent.playerError(getUrl(), connected);
                            } catch (Exception e) {
                            }
                            break;
                        case PlayerType.EventType.EVENT_VIDEO_END:

                            callPlayerState(PlayerType.StateType.STATE_END);

                            // step2
                            pause(true);

                            // 埋点
                            try {
                                BuriedEvent buriedEvent = PlayerManager.getInstance().getConfig().getBuriedEvent();
                                buriedEvent.playerCompletion(getUrl());
                            } catch (Exception e) {
                            }

                            boolean looping = isLooping();
                            // loop
                            if (looping) {

                                // step1
                                callPlayerState(PlayerType.StateType.STATE_LOADING_START);
                                hideReal();

                                // step2
                                pause(true);

                                // step3
                                seekTo(true, builder);
                            }
                            // sample
                            else {
                                // step1
                                pause(true);

                                // step2
                                playEnd();
                            }

                            break;
                        // 播放错误
                    }
                }

                @Override
                public void onChanged(int kernel, int width, int height, int rotation) {
                    int scaleType = PlayerManager.getInstance().getConfig().getScaleType();
                    setScaleType(scaleType);
//                    setVideoSize(width, height);
                    setVideoRotation(rotation);
                }
            });
            MPLogUtil.log("PlayerApiKernel => createKernel => kernel = " + kernel);
            // 4
            setKernel(kernel);
            // 5
            createDecoder(builder, logger);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => createKernel => " + e.getMessage());
        }
    }

    default void attachRender() {
        try {
            RenderApi render = getRender();
            KernelApi kernel = getKernel();
            render.setKernel(kernel);
            MPLogUtil.log("PlayerApiKernel => attachRender => render = " + render + ", kernel = " + kernel);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => attachRender => " + e.getMessage(), e);
        }
    }

    default void createDecoder(@NonNull StartBuilder builder, @NonNull boolean logger) {
        try {
            ViewGroup layout = getLayout();
            Context context = layout.getContext();
            KernelApi kernel = getKernel();
            kernel.createDecoder(context, builder.isMute(), logger);
        } catch (Exception e) {
        }
    }

    KernelApi getKernel();

    void setKernel(@NonNull KernelApi kernel);
}
