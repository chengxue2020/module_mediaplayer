package lib.kalu.mediaplayer.config.builder;

import androidx.annotation.IntRange;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import java.io.Serializable;

import lib.kalu.mediaplayer.config.cache.CacheType;

/**
 * @description: 配置类
 * @date: 2021-05-12 14:40
 */
@Keep
public final class CacheBuilder implements Serializable {

    private boolean enable;
    private int max;
    private String dir;
    private String salt;
    @CacheType
    private int type;
    private boolean log;

    public boolean isEnable() {
        return enable;
    }

    public int getMax() {
        return max;
    }

    public String getDir() {
        return dir;
    }

    public String getSalt() {
        return salt;
    }

    public int getType() {
        return type;
    }

    public boolean isLog() {
        return log;
    }

    private CacheBuilder(@NonNull Build build) {
        if (null == build)
            return;
        this.enable = build.enable;
        this.max = build.max;
        this.dir = build.dir;
        this.salt = build.salt;
        this.type = build.type;
        this.log = build.log;
    }

    @Keep
    public static final class Build {

        public Build() {
        }

        private boolean enable = true;
        private int max = 1024;
        private String dir = "temp";
        private String salt = "salt_cache_config";
        @CacheType
        private int type = CacheType.DEFAULT;
        private boolean log = false;

        public Build setEnable(boolean v) {
            this.enable = v;
            return this;
        }

        public Build setDir(String dir) {
            if (null == dir || dir.length() == 0) {
                dir = "temp";
            }
            this.dir = dir;
            return this;
        }

        public Build setMax(@IntRange(from = 1024, to = 4096) int value) {
            if (value <= 0) {
                value = 1024;
            }
            this.max = value;
            return this;
        }

        public Build setSalt(@NonNull String salt) {
            //设置盐处理
            if (salt != null && salt.length() > 0) {
                this.salt = salt;
            }
            return this;
        }

        public Build setType(@CacheType.Value int type) {
            this.type = type;
            return this;
        }

        public Build setLog(boolean log) {
            this.log = log;
            return this;
        }

        public CacheBuilder build() {
            return new CacheBuilder(this);
        }
    }
}
