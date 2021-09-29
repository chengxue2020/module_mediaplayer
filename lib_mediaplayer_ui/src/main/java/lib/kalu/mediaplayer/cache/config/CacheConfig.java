package lib.kalu.mediaplayer.cache.config;

import androidx.annotation.IntRange;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.android.exoplayer2.upstream.cache.Cache;

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
    private int mCacheMaxMB;
    /**
     * dir
     */
    private String mCacheDir = "temp";
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
    private @CacheType
    int mCacheType = CacheType.RAM;
    /**
     * 是否开启日志
     */
    private boolean mIsLog = false;

    public boolean isEffective() {
        return mIsEffective;
    }

    public int getCacheMaxMB() {
        return mCacheMaxMB;
    }

    public String getCacheDir() {
        return mCacheDir;
    }

    public String getSalt() {
        return mSalt;
    }

    public @CacheType
    int getCacheType() {
        return mCacheType;
    }

    public boolean isLog() {
        return mIsLog;
    }

    private CacheConfig(@NonNull Build build) {
        if (null == build)
            return;
        this.mIsEffective = build.mIsEffective;
        this.mCacheMaxMB = build.mCacheMaxMB;
        this.mCacheDir = build.mCacheDir;
        this.mSalt = build.mSalt;
        this.mCacheType = build.mCacheType;
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
         * 对视频链接加盐字符串
         * 处理md5加密的盐
         */
        private String mSalt = "salt_cache";
        /**
         * 0，表示内存缓存
         * 1，表示磁盘缓存
         * 2，表示内存缓存+磁盘缓存
         */
        private int mCacheType = CacheType.RAM;
        /**
         * 是否开启日志
         */
        private boolean mIsLog = false;

        /**
         * dir
         */
        private String mCacheDir = "temp";
        /**
         * MB
         */
        private int mCacheMaxMB = 1024;

        public Build setIsEffective(boolean mIsEffective) {
            this.mIsEffective = mIsEffective;
            return this;
        }

        public Build setCacheDir(String dir) {
            if (null == dir || dir.length() == 0) {
                dir = "temp";
            }
            this.mCacheDir = dir;
            return this;
        }

        public Build setCacheMaxMB(@IntRange(from = 1024, to = 4096) int value) {
            if (value <= 0) {
                value = 1024;
            }
            this.mCacheMaxMB = value;
            return this;
        }

        public Build setSalt(@NonNull String salt) {
            //设置盐处理
            if (salt != null && salt.length() > 0) {
                this.mSalt = salt;
            }
            return this;
        }

        public Build setCacheType(@CacheType.Value int type) {
            this.mCacheType = type;
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
