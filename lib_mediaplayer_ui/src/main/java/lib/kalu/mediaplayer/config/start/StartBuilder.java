package lib.kalu.mediaplayer.config.start;

import androidx.annotation.Keep;

@Keep
public final class StartBuilder {

    private int delay;
    private long max;
    private long seek;
    private boolean live;
    private boolean loop;
    private boolean mute;

    private boolean windowVisibilityChangedRelease; // 不可见, release

    // 外部背景音
    private String externalMusicUrl;
    private boolean externalMusicLoop;
    private boolean externalMusicPlayWhenReady;
    private boolean externalMusicSeek;

    public boolean isMute() {
        return mute;
    }

    public long getMax() {
        return max;
    }

    public long getSeek() {
        return seek;
    }

    public boolean isLive() {
        return live;
    }

    public boolean isLoop() {
        return loop;
    }

    public boolean isWindowVisibilityChangedRelease() {
        return windowVisibilityChangedRelease;
    }

    public String getExternalMusicUrl() {
        return externalMusicUrl;
    }

    public boolean isExternalMusicLoop() {
        return externalMusicLoop;
    }

    public boolean isExternalMusicPlayWhenReady() {
        return externalMusicPlayWhenReady;
    }

    public boolean isExternalMusicSeek() {
        return externalMusicSeek;
    }

    public int getDelay() {
        return delay;
    }

    public StartBuilder(StartBuilder.Builder builder) {
        this.delay = builder.delay;
        this.max = builder.max;
        this.seek = builder.seek;
        this.mute = builder.mute;
        this.live = builder.live;
        this.loop = builder.loop;
        this.windowVisibilityChangedRelease = builder.windowVisibilityChangedRelease;
        this.externalMusicUrl = builder.externalMusicUrl;
        this.externalMusicLoop = builder.externalMusicLoop;
        this.externalMusicPlayWhenReady = builder.externalMusicPlayWhenReady;
        this.externalMusicSeek = builder.externalMusicSeek;
    }

    @Override
    public String toString() {
        return "StartBuilder{" +
                "delay=" + delay +
                ", max=" + max +
                ", seek=" + seek +
                ", live=" + live +
                ", loop=" + loop +
                ", mute=" + mute +
                ", windowVisibilityChangedRelease=" + windowVisibilityChangedRelease +
                ", externalMusicUrl='" + externalMusicUrl + '\'' +
                ", externalMusicLoop=" + externalMusicLoop +
                ", externalMusicPlayWhenReady=" + externalMusicPlayWhenReady +
                ", externalMusicSeek=" + externalMusicSeek +
                '}';
    }

    public Builder newBuilder() {
        Builder builder = new Builder();
        builder.delay = delay;
        builder.max = max;
        builder.seek = seek;
        builder.mute = mute;
        builder.live = live;
        builder.loop = loop;
        builder.windowVisibilityChangedRelease = windowVisibilityChangedRelease;
        builder.externalMusicUrl = externalMusicUrl;
        builder.externalMusicLoop = externalMusicLoop;
        builder.externalMusicPlayWhenReady = externalMusicPlayWhenReady;
        builder.externalMusicSeek = externalMusicSeek;
        return builder;
    }

    @Keep
    public final static class Builder {

        private int delay = 0;
        private long max = 0;
        private long seek = 0;
        private boolean live = false;
        private boolean loop = false;
        private boolean mute = false;

        private boolean windowVisibilityChangedRelease = false; // 不可见, release

        private String externalMusicUrl = null;
        private boolean externalMusicLoop = false;
        private boolean externalMusicPlayWhenReady = false;
        private boolean externalMusicSeek = true;

        public Builder() {
        }

        public Builder setDelay(int v) {
            delay = v;
            return this;
        }

        public Builder setExternalMusicPlayWhenReady(boolean v) {
            externalMusicPlayWhenReady = v;
            return this;
        }

        public Builder setExternalMusicLooping(boolean v) {
            externalMusicLoop = v;
            return this;
        }

        public Builder setExternalMusicSeek(boolean v) {
            externalMusicSeek = v;
            return this;
        }

        public Builder setExternalMusicUrl(String v) {
            externalMusicUrl = v;
            return this;
        }

        public Builder setMute(boolean v) {
            mute = v;
            return this;
        }

        public Builder setMax(long v) {
            max = v;
            return this;
        }

        public Builder setSeek(long v) {
            seek = v;
            return this;
        }

        public Builder setLive(boolean v) {
            live = v;
            return this;
        }

        public Builder setLoop(boolean v) {
            loop = v;
            return this;
        }

        public Builder setWindowVisibilityChangedRelease(boolean v) {
            this.windowVisibilityChangedRelease = v;
            return this;
        }

        public StartBuilder build() {
            return new StartBuilder(this);
        }
    }
}
