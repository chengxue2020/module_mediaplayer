package lib.kalu.mediaplayer.config.start;

import androidx.annotation.Keep;

@Keep
public final class StartBuilder {

    private long max;
    private long seek;
    private boolean live;
    private boolean loop;
    private boolean mute;

    private boolean hidePause; // 不可见, pause

    // 外部背景音
    private String externalMusicUrl;
    private boolean externalMusicLoop;
    private boolean externalMusicPlayWhenReady;

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

    public boolean isHidePause() {
        return hidePause;
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

    public StartBuilder(StartBuilder.Builder builder) {
        this.max = builder.max;
        this.seek = builder.seek;
        this.mute = builder.mute;
        this.live = builder.live;
        this.loop = builder.loop;
        this.hidePause = builder.hidePause;
        this.externalMusicUrl = builder.externalMusicUrl;
        this.externalMusicLoop = builder.externalMusicLoop;
        this.externalMusicPlayWhenReady = builder.externalMusicPlayWhenReady;
    }

    @Override
    public String toString() {
        return "StartBuilder{" +
                "max=" + max +
                ", seek=" + seek +
                ", live=" + live +
                ", loop=" + loop +
                ", mute=" + mute +
                ", hidePause=" + hidePause +
                ", externalMusicUrl='" + externalMusicUrl + '\'' +
                ", externalMusicLoop=" + externalMusicLoop +
                ", externalMusicPlayWhenReady=" + externalMusicPlayWhenReady +
                '}';
    }

    public Builder newBuilder() {
        Builder builder = new Builder();
        builder.max = max;
        builder.seek = seek;
        builder.mute = mute;
        builder.live = live;
        builder.loop = loop;
        builder.hidePause = hidePause;
        builder.externalMusicUrl = externalMusicUrl;
        builder.externalMusicLoop = externalMusicLoop;
        builder.externalMusicPlayWhenReady = externalMusicPlayWhenReady;
        return builder;
    }

    @Keep
    public final static class Builder {

        private long max = 0;
        private long seek = 0;
        private boolean live = false;
        private boolean loop = false;
        private boolean mute = false;

        private boolean hidePause = false; // 不可见, pause

        private String externalMusicUrl = null;
        private boolean externalMusicLoop = false;
        private boolean externalMusicPlayWhenReady = false;


        public Builder() {
        }

        public Builder setExternalMusicPlayWhenReady(boolean v) {
            externalMusicPlayWhenReady = v;
            return this;
        }

        public Builder setExternalMusicLoop(boolean v) {
            externalMusicLoop = v;
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

        public Builder setHidePause(boolean v) {
            this.hidePause = v;
            return this;
        }

        public StartBuilder build() {
            return new StartBuilder(this);
        }
    }
}
