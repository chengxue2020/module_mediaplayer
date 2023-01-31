package lib.kalu.mediaplayer.config.start;

import androidx.annotation.Keep;

@Keep
public final class StartBuilder {

    private long max;
    private long seek;
    private boolean live;
    private boolean loop;
    private boolean mute;

    private boolean invisibleStop; // 不可见， 停止
    private boolean invisibleIgnore; // 不可见忽略, 什么也不做
    private boolean invisibleRelease; // 不可见生命周期自动销毁

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

    public boolean isInvisibleIgnore() {
        return invisibleIgnore;
    }

    public boolean isInvisibleStop() {
        return invisibleStop;
    }

    public boolean isInvisibleRelease() {
        return invisibleRelease;
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
        this.invisibleStop = builder.invisibleStop;
        this.invisibleIgnore = builder.invisibleIgnore;
        this.invisibleRelease = builder.invisibleRelease;
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
                ", invisibleStop=" + invisibleStop +
                ", invisibleIgnore=" + invisibleIgnore +
                ", invisibleRelease=" + invisibleRelease +
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
        builder.invisibleStop = invisibleStop;
        builder.invisibleIgnore = invisibleIgnore;
        builder.invisibleRelease = invisibleRelease;
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

        private boolean invisibleStop = false; // 不可见, 暂停
        private boolean invisibleIgnore = false; // 不可见忽略, 什么也不做
        private boolean invisibleRelease = true; // 不可见生命周期自动销毁

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

        public Builder setInvisibleStop(boolean v) {
            this.invisibleStop = v;
            return this;
        }

        public Builder setInvisibleIgnore(boolean v) {
            this.invisibleIgnore = v;
            return this;
        }

        public Builder setInvisibleRelease(boolean v) {
            this.invisibleRelease = v;
            return this;
        }

        public StartBuilder build() {
            return new StartBuilder(this);
        }
    }
}
