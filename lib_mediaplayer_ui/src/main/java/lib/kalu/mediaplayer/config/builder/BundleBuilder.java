package lib.kalu.mediaplayer.config.builder;

import androidx.annotation.Keep;

@Keep
public final class BundleBuilder {

    private long max;
    private long seek;
    private boolean live;
    private boolean loop;
    private boolean release;
    private boolean mute;

    // 外部背景音
    private String externalMusicUrl = null;
    private boolean externalMusicLoop = false;
    private boolean externalMusicSeek = false;

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

    public boolean isRelease() {
        return release;
    }

    public String getExternalMusicUrl() {
        return externalMusicUrl;
    }

    public boolean isExternalMusicLoop() {
        return externalMusicLoop;
    }

    public boolean isExternalMusicSeek() {
        return externalMusicSeek;
    }

    @Override
    public String toString() {
        return "BundleBuilder{" +
                "max=" + max +
                ", seek=" + seek +
                ", live=" + live +
                ", loop=" + loop +
                ", release=" + release +
                ", mute=" + mute +
                ", externalMusicUrl='" + externalMusicUrl + '\'' +
                ", externalMusicLoop=" + externalMusicLoop +
                ", externalMusicSeek=" + externalMusicSeek +
                '}';
    }

    public BundleBuilder(BundleBuilder.Builder builder) {
        this.max = builder.max;
        this.seek = builder.seek;
        this.mute = builder.mute;
        this.live = builder.live;
        this.loop = builder.loop;
        this.release = builder.release;
        this.externalMusicUrl = builder.externalMusicUrl;
        this.externalMusicLoop = builder.externalMusicLoop;
        this.externalMusicSeek = builder.externalMusicSeek;
    }

    @Keep
    public final static class Builder {

        private long max = 0;
        private long seek = 0;
        private boolean live = false;
        private boolean loop = false;
        private boolean release = true;
        private boolean mute = false;

        private String externalMusicUrl = null;
        private boolean externalMusicLoop = false;
        private boolean externalMusicSeek = false;

        public Builder() {
        }

        public Builder setExternalMusicLoop(boolean v) {
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

        public Builder setRelease(boolean v) {
            release = v;
            return this;
        }

        public BundleBuilder build() {
            return new BundleBuilder(this);
        }
    }
}
