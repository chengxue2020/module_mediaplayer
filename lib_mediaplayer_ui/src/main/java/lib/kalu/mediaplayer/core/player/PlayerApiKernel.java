package lib.kalu.mediaplayer.core.player;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.buried.BuriedEvent;
import lib.kalu.mediaplayer.config.player.PlayerBuilder;
import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.core.kernel.video.KernelApi;
import lib.kalu.mediaplayer.core.kernel.video.KernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.KernelFactoryManager;
import lib.kalu.mediaplayer.core.render.RenderApi;
import lib.kalu.mediaplayer.util.MPLogUtil;
import lib.kalu.mediaplayer.util.NetworkUtil;

public interface PlayerApiKernel extends PlayerApiListener, PlayerApiComponent, PlayerApiRender, PlayerApiDevice, PlayerApiExternalMusic {

    default void start(@NonNull String url) {
        StartBuilder.Builder builder = new StartBuilder.Builder();
        StartBuilder build = builder.build();
        start(build, url);
    }

    default void start(@NonNull StartBuilder builder, @NonNull String url) {

        MPLogUtil.log("PlayerApiKernel => start => url = " + url);
        if (null == url || url.length() <= 0)
            return;

        callPlayerEvent(PlayerType.StateType.STATE_INIT);
        int delay = builder.getDelay();
        MPLogUtil.log("PlayerApiKernel => start => delay = " + delay);

        // 1
        PlayerBuilder config = PlayerManager.getInstance().getConfig();
        MPLogUtil.setLogger(config);
        // 5
        createKernel(builder, config);

        if (delay <= 0) {
            try {
                // 3
                callPlayerEvent(PlayerType.StateType.STATE_LOADING_START);
                // 4
                createRender();
                // 6
                updateKernel(builder, url);
                // 7
                attachRender();
                // 8
                updatePlayerData(builder, url);
                // 9
                updateExternalMusicData(builder);
            } catch (Exception e) {
                MPLogUtil.log("PlayerApiKernel => start => " + e.getMessage());
            }
        } else {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 0
                        checkKernel();
                        // 3
                        callPlayerEvent(PlayerType.StateType.STATE_LOADING_START);
                        // 4
                        createRender();
                        // 6
                        updateKernel(builder, url);
                        // 7
                        attachRender();
                        // 8
                        updatePlayerData(builder, url);
                        // 9
                        updateExternalMusicData(builder);
                    } catch (Exception e) {
                        MPLogUtil.log("PlayerApiKernel => start => " + e.getMessage());
                    }
                }
            }, delay);
        }
    }

    default void updatePlayerData(@NonNull StartBuilder bundle, @NonNull String playUrl) {
        try {
            boolean windowVisibilityChangedRelease = bundle.isWindowVisibilityChangedRelease();
            boolean loop = bundle.isLoop();
            MPLogUtil.log("PlayerApiKernel => updatePlayerData => loop = " + loop + ", windowVisibilityChangedRelease = " + windowVisibilityChangedRelease + ", playUrl = " + playUrl);
            ((View) this).setTag(R.id.module_mediaplayer_id_player_url, playUrl);
            ((View) this).setTag(R.id.module_mediaplayer_id_player_looping, loop);
            ((View) this).setTag(R.id.module_mediaplayer_id_player_window_visibility_changed_release, windowVisibilityChangedRelease);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => updatePlayerData => " + e.getMessage(), e);
        }
    }

    default StartBuilder getStartBuilder() {
        try {
            String url = getUrl();
            if (null == url || url.length() <= 0) throw new Exception();
            StartBuilder.Builder builder = new StartBuilder.Builder();
            builder.setMax(getMax());
            builder.setSeek(getSeek());
            builder.setLoop(isLooping());
            builder.setLive(isLive());
            builder.setMute(isMute());
            builder.setExternalMusicUrl(getExternalMusicPath());
            builder.setExternalMusicLooping(isExternalMusicLooping());
            builder.setExternalMusicSeek(isExternalMusicSeek());
            builder.setExternalMusicPlayWhenReady(isExternalMusicPlayWhenReady());
            builder.setWindowVisibilityChangedRelease(isWindowVisibilityChangedRelease());
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
            MPLogUtil.log("PlayerApiKernel => getDuration => duration = " + duration);
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
            MPLogUtil.log("PlayerApiKernel => getPosition => position = " + position);
            return position;
        } catch (Exception e) {
            return 0L;
        }
    }

    default void setVolume(@FloatRange(from = 0f, to = 1f) float left, @FloatRange(from = 0f, to = 1f) float right) {
        try {
            MPLogUtil.log("PlayerApiKernel => setVolume => left = " + left + ", right = " + right);
            KernelApi kernel = getKernel();
            kernel.setVolume(left, right);
        } catch (Exception e) {
        }
    }

    default void setMute(boolean enable) {
        try {
            MPLogUtil.log("PlayerApiKernel => setMute => enable = " + enable);
            KernelApi kernel = getKernel();
            kernel.setMute(enable);
        } catch (Exception e) {
        }
    }

    default void setLooping(boolean looping) {
        try {
            MPLogUtil.log("PlayerApiKernel => setLooping => looping = " + looping);
            KernelApi kernel = getKernel();
            kernel.setLooping(looping);
        } catch (Exception e) {
        }
    }

    default void release() {
        try {
            // 1
            checkKernel();
            MPLogUtil.log("PlayerApiKernel => release =>");
//            //2
//            stopFloat();
//            // 3
//            stopFull();
            // 4
            clearRender();
            // 5
            releaseRender();
            // 6
            releaseKernel();
        } catch (Exception e) {
        }
    }

    default void toggle() {
        toggle(false);
    }

    default void toggle(boolean ignore) {
        try {
            MPLogUtil.log("PlayerApiKernel => toggle => ignore = " + ignore);
            boolean playing = isPlaying();
            if (playing) {
                pause(ignore);
            } else {
                resume();
            }
        } catch (Exception e) {
        }
    }

    default void pause() {
        pause(false);
    }

    default void pause(boolean ignore) {
        try {
            boolean playing = isPlaying();
            MPLogUtil.log("PlayerApiKernel => pause => ignore = " + ignore + ", playing = " + playing);
            if (playing) {
                pauseKernel(ignore);
            } else {
                callPlayerEvent(PlayerType.StateType.STATE_LOADING_STOP);
            }
        } catch (Exception e) {
        }
    }

    default void stop() {
        try {
            boolean playing = isPlaying();
            if (!playing) return;
            stopKernel(true);
        } catch (Exception e) {
        }
    }


    default void restart() {
        try {
            StartBuilder builder = getStartBuilder();
            MPLogUtil.log("PlayerApiKernel => restart => builder = " + builder);
            if (null == builder) return;
            callPlayerEvent(PlayerType.StateType.STATE_RESTAER);
            String url = getUrl();
            start(builder, url);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => restart => " + e.getMessage());
        }
    }

    default void resume() {
        resume(false);
    }

    default void resume(boolean ignore) {
        try {
            MPLogUtil.log("PlayerApiKernel => resume => ignore = " + ignore);
            checkKernel();
            // 1
            resumeExternalMusic();
            // 2
            resumeKernel(ignore);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => resume => " + e.getMessage());
        }
    }

    default boolean isLooping() {
        try {
            KernelApi kernel = getKernel();
            return kernel.isLooping();
        } catch (Exception e) {
            return (boolean) ((View) this).getTag(R.id.module_mediaplayer_id_player_looping);
        }
    }

    default boolean isWindowVisibilityChangedRelease() {
        try {
            return (Boolean) ((View) this).getTag(R.id.module_mediaplayer_id_player_window_visibility_changed_release);
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
            return (String) ((View) this).getTag(R.id.module_mediaplayer_id_player_url);
        } catch (Exception e) {
            return null;
        }
    }

    default void seekTo(@NonNull boolean force) {
        StartBuilder.Builder builder = new StartBuilder.Builder();
        builder.setMax(getMax());
        builder.setSeek(getSeek());
        builder.setLoop(isLooping());
        builder.setLive(isLive());
        builder.setMute(isMute());
        builder.setWindowVisibilityChangedRelease(isWindowVisibilityChangedRelease());
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
        builder.setWindowVisibilityChangedRelease(isWindowVisibilityChangedRelease());
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
            }
            // 3
            long seek = builder.getSeek();
            seekToKernel(seek, false);
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
        if (null == kernel) throw new Exception("check kernel is null");
    }

    default void resumeKernel(@NonNull boolean ignore) {
        try {
            // 1
            checkKernel();
            // 2
            KernelApi kernel = getKernel();
            MPLogUtil.log("PlayerApiKernel => resumeKernel => ignore = " + ignore + ", kernel = " + kernel);
            kernel.start();
            setScreenKeep(true);
            if (ignore) {
                callPlayerEvent(PlayerType.StateType.STATE_RESUME_IGNORE);
            } else {
                callPlayerEvent(PlayerType.StateType.STATE_RESUME);
                callPlayerEvent(PlayerType.StateType.STATE_KERNEL_RESUME);
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
            if (!call) return;
//            callPlayerEvent(PlayerType.StateType.STATE_LOADING_STOP);
            callPlayerEvent(PlayerType.StateType.STATE_KERNEL_STOP);
            callPlayerEvent(PlayerType.StateType.STATE_CLOSE);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => stopKernel => " + e.getMessage());
        }
    }

    default void pauseKernel(@NonNull boolean ignore) {
        try {
            // 1
            checkKernel();
            // 2
            pauseExternalMusic();
            // 3
            KernelApi kernel = getKernel();
            MPLogUtil.log("PlayerApiKernel => pauseKernel => kernel = " + kernel);
            kernel.pause();
            setScreenKeep(false);
            callPlayerEvent(ignore ? PlayerType.StateType.STATE_PAUSE_IGNORE : PlayerType.StateType.STATE_PAUSE);
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
            setKernel(null);
            setScreenKeep(false);
            MPLogUtil.log("PlayerApiKernel => releaseKernel => succ");
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => releaseKernel => " + e.getMessage());
        }
    }

    default void seekToKernel(long milliSeconds, boolean seekHelp) {
        try {
            // 1
            checkKernel();
            // 2
            KernelApi kernel = getKernel();
            MPLogUtil.log("PlayerApiKernel => seekToKernel => milliSeconds = " + milliSeconds + ", kernel = " + kernel);
            kernel.seekTo(milliSeconds, seekHelp);
            setScreenKeep(true);
            if (milliSeconds <= 0) return;
            callPlayerEvent(PlayerType.StateType.STATE_LOADING_START);
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
            kernel.update(getBaseContext(), bundle, playUrl);
            setScreenKeep(true);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => updateKernel => " + e.getMessage());
        }
    }

    default void playEnd() {
        hideReal();
        setScreenKeep(false);
        callPlayerEvent(PlayerType.StateType.STATE_END);
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

    default void createKernel(@NonNull StartBuilder builder, @NonNull PlayerBuilder config) {

        try {
            // 1
            releaseKernel();
            // 2
            int type = PlayerManager.getInstance().getConfig().getKernel();
            KernelApi kernel = KernelFactoryManager.getKernel((PlayerApi) this, type, new KernelApiEvent() {

                @Override
                public void onUpdateTimeMillis(@NonNull boolean isLooping, @NonNull long max, @NonNull long seek, @NonNull long position, @NonNull long duration) {

                    boolean reset;
                    if (max > 0) {
                        long playTime = (position - seek);
                        if (playTime > max) {
                            reset = true;
                        } else {
                            reset = false;
                        }
                    } else {
                        reset = false;
                    }

                    // end
                    if (reset) {
                        // loop
                        if (isLooping) {
                            // 1
                            hideReal();
                            // 2
                            boolean seekHelp = config.isSeekHelp();
                            if (seekHelp) {
                                seekToKernel(1, true);
                            } else {
                                seekTo(true);
                            }
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

                        // 1
                        callUpdateTimeMillis(seek, position, duration);
                        // 2
                        callProgressListener(position, duration);
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
                            callPlayerEvent(PlayerType.StateType.STATE_LOADING_START);
                            break;
                        // 初始化完成 => loading stop
                        case PlayerType.EventType.EVENT_LOADING_STOP:
                            callPlayerEvent(PlayerType.StateType.STATE_LOADING_STOP);
                            break;
                        // 缓冲开始
                        case PlayerType.EventType.EVENT_BUFFERING_START:
                            pauseExternalMusic();
                            callPlayerEvent(PlayerType.StateType.STATE_BUFFERING_START);
                            break;
                        // 缓冲结束
                        case PlayerType.EventType.EVENT_BUFFERING_STOP:
                            resumeExternalMusic();
                            callPlayerEvent(PlayerType.StateType.STATE_BUFFERING_STOP);
                            break;
                        // 播放开始-快进
                        case PlayerType.EventType.EVENT_VIDEO_START_SEEK:
                            // step1
                            callPlayerEvent(PlayerType.StateType.STATE_LOADING_STOP);
                            callPlayerEvent(PlayerType.StateType.STATE_START_SEEK);
                            // step2
                            showReal();
                            // step4
                            resume(true);
                            // step5
                            checkExternalMusic(getBaseContext());

                            break;
                        // 播放开始
                        case PlayerType.EventType.EVENT_VIDEO_START:
//                        case PlayerType.EventType.EVENT_VIDEO_SEEK_RENDERING_START: // 视频开始渲染
//            case PlayerType.MediaType.MEDIA_INFO_AUDIO_SEEK_RENDERING_START: // 视频开始渲染


                            callPlayerEvent(PlayerType.StateType.STATE_START);

                            // step2
                            showReal();
                            // step3
                            checkReal();
                            // step4
                            checkExternalMusic(getBaseContext());

                            // 埋点
                            try {
                                BuriedEvent buriedEvent = PlayerManager.getInstance().getConfig().getBuriedEvent();
                                buriedEvent.playerIn(getUrl());
                            } catch (Exception e) {
                            }

                            break;
                        // 播放错误
                        case PlayerType.EventType.EVENT_ERROR_URL:
                        case PlayerType.EventType.EVENT_ERROR_RETRY:
                        case PlayerType.EventType.EVENT_ERROR_SOURCE:
                        case PlayerType.EventType.EVENT_ERROR_PARSE:
                        case PlayerType.EventType.EVENT_ERROR_NET:

                            boolean connected = NetworkUtil.isConnected(getBaseContext());
                            setScreenKeep(false);
                            callPlayerEvent(connected ? PlayerType.StateType.STATE_ERROR : PlayerType.StateType.STATE_ERROR_NET);

                            // step2
                            pause(true);

                            // 埋点
                            try {
                                BuriedEvent buriedEvent = PlayerManager.getInstance().getConfig().getBuriedEvent();
                                buriedEvent.playerError(getUrl(), connected);
                            } catch (Exception e) {
                            }
                            break;
                        // 播放结束
                        case PlayerType.EventType.EVENT_VIDEO_END:

                            // 埋点
                            try {
                                BuriedEvent buriedEvent = PlayerManager.getInstance().getConfig().getBuriedEvent();
                                buriedEvent.playerCompletion(getUrl());
                            } catch (Exception e) {
                            }

                            boolean looping = isLooping();
                            MPLogUtil.log("PlayerApiKernel => onEvent = 播放结束 => looping = " + looping);
                            // loop
                            if (looping) {

                                callPlayerEvent(PlayerType.StateType.STATE_END);
                                // step1
                                callPlayerEvent(PlayerType.StateType.STATE_LOADING_START);
                                hideReal();

                                // step3
                                seekTo(true, builder);
                            }
                            // sample
                            else {
                                // step2
                                playEnd();
                            }

                            break;
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
            MPLogUtil.log("PlayerApiKernel => createKernel => kernel = " + getKernel());
            // 5
            createDecoder(builder, config);
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

    default void createDecoder(@NonNull StartBuilder builder, @NonNull PlayerBuilder config) {
        try {
            // 1
            checkKernel();
            // 2
            KernelApi kernel = getKernel();
            MPLogUtil.log("PlayerApiKernel => createDecoder => kernel = " + kernel);
            boolean log = config.isLog();
            int seekParameters = config.getExoSeekParameters();
            kernel.createDecoder(getBaseContext(), log, seekParameters);
            MPLogUtil.log("PlayerApiKernel => createDecoder => succ");
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => createDecoder => " + e.getMessage(), e);
        }
    }
}
