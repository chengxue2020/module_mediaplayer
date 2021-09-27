package lib.kalu.mediaplayer.cache.config;

import androidx.annotation.IntRange;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * @description: 配置类
 * @date: 2021-05-12 14:40
 */
@Keep
public class CacheConfig implements Serializable {

    /**
     * 是否生效
     */
    private boolean mIsEffective = true;
    /**
     * 内存缓存最大值
     */
    private int mCacheMax;
    /**
     * 对视频链接加盐字符串
     * 处理md5加密的盐
     */
    private String mSalt = "salt_cache_config";
    /**
     * 0，表示内存缓存
     * 1，表示磁盘缓存
     * 2，表示内存缓存+磁盘缓存
     */
    private int mType = CacheType.ALL;
    /**
     * 是否开启日志
     */
    private boolean mIsLog = false;

    public boolean isEffective() {
        return mIsEffective;
    }

    public int getCacheMax() {
        return mCacheMax;
    }

    public String getSalt() {
        return mSalt;
    }

    public int getType() {
        return mType;
    }

    public boolean isLog() {
        return mIsLog;
    }

    private CacheConfig(@NonNull Build build) {
        if (null == build)
            return;
        this.mIsEffective = build.mIsEffective;
        this.mCacheMax = build.mCacheMax;
        this.mSalt = build.mSalt;
        this.mType = build.mType;
        this.mIsLog = build.mIsLog;
    }

    @Keep
    public static final class Build {

        public Build() {
        }

        /**
         * 是否生效
         */
        private boolean mIsEffective = true;
        /**
         * 内存缓存最大值
         */
        private int mCacheMax;
        /**
         * 对视频链接加盐字符串
         * 处理md5加密的盐
         */
        private String mSalt = "salt_cache";
        /**
         * 0，表示内存缓存
         * 1，表示磁盘缓存
         * 2，表示内存缓存+磁盘缓存
         */
        private int mType = CacheType.ALL;
        /**
         * 是否开启日志
         */
        private boolean mIsLog = false;

        public Build setIsEffective(boolean mIsEffective) {
            this.mIsEffective = mIsEffective;
            return this;
        }

        public Build setCacheMax(@IntRange(from = 1, to = 2048) int value) {
            if (value <= 0) {
                value = 1000;
            }
            this.mCacheMax = value;
            return this;
        }

        public Build setSalt(@NonNull String salt) {
            //设置盐处理
            if (salt != null && salt.length() > 0) {
                this.mSalt = salt;
            }
            return this;
        }

        public Build setType(@CacheType.Value int type) {
            this.mType = type;
            return this;
        }

        public Build setLog(boolean log) {
            this.mIsLog = log;
            return this;
        }

        public CacheConfig build() {
            return new CacheConfig(this);
        }
    }
}
