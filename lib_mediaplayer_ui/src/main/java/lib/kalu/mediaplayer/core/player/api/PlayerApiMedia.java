package lib.kalu.mediaplayer.core.player.api;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.start.StartBuilder;

interface PlayerApiMedia extends PlayerApiBase, PlayerApiExternal {

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

    void setVolume(float v1, float v2);

    void setMute(boolean v);

    void restart();

    void start(@NonNull StartBuilder builder, @NonNull String url);

    default void toggle() {
        toggle(false);
    }

    void toggle(boolean cleanHandler);

    default void pause() {
        pause(false);
    }

    void pause(boolean auto);

    void stop();


    default void resume() {
        resume(true);
    }

    void resume(boolean call);

    long getDuration();

    long getPosition();

    boolean isLooping();

    boolean isInvisibleStop();

    boolean isInvisibleIgnore();

    boolean isInvisibleRelease();

    long getSeek();

    long getMax();

    String getUrl();

    int getBufferedPercentage();

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

    void seekTo(@NonNull boolean force, @NonNull StartBuilder builder);

    boolean isLive();

    boolean isPlaying();

    boolean isMute();
}
