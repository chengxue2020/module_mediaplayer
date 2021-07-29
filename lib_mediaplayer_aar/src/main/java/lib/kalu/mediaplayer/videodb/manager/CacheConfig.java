package lib.kalu.mediaplayer.videodb.manager;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import android.content.Context;

import androidx.annotation.IntDef;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import lib.kalu.mediaplayer.videokernel.contentprovider.ContextProviderMediaplayer;

/**
 * @description: 配置类
 * @date: 2021-05-12 14:40
 */
@Keep
public class CacheConfig {

    private final Context mContext = ContextProviderMediaplayer.mContext;

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
    private String mSalt = "media_salt";
    /**
     * 0，表示内存缓存
     * 1，表示磁盘缓存
     * 2，表示内存缓存+磁盘缓存
     */
    private int type = Cache.ALL;
    /**
     * 是否开启日志
     */
    private boolean isLog = false;

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
        return type;
    }

    public boolean isLog() {
        return isLog;
    }

    public Context getContext() {
        return mContext;
    }

    private CacheConfig(@NonNull Build build) {
        if (null == build)
            return;
        this.mIsEffective = build.mIsEffective;
        this.mCacheMax = build.mCacheMax;
        this.mSalt = build.mSalt;
        this.type = build.type;
        this.isLog = build.isLog;
    }

    @Keep
    public interface Cache {
        int ONLY_MEMERY = 0;
        int ONLY_DISK = 1;
        int ALL = 2;
    }

    @Retention(SOURCE)
    @Target({ElementType.PARAMETER})
    @IntDef(value = {Cache.ALL, Cache.ONLY_DISK, Cache.ONLY_MEMERY})
    public @interface CacheType {
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
        private String mSalt = "yc_video";
        /**
         * 0，表示内存缓存
         * 1，表示磁盘缓存
         * 2，表示内存缓存+磁盘缓存
         */
        private int type = Cache.ALL;
        /**
         * 是否开启日志
         */
        private boolean isLog = false;

        public Build setIsEffective(boolean mIsEffective) {
            this.mIsEffective = mIsEffective;
            return this;
        }

        public Build setCacheMax(int value) {
            if (value <= 0) {
                value = 1000;
            }
            this.mCacheMax = value;
            return this;
        }

        public Build setSalt(String salt) {
            //设置盐处理
            if (salt != null && salt.length() > 0) {
                this.mSalt = salt;
            }
            return this;
        }

        public Build setType(@CacheType int type) {
            this.type = type;
            return this;
        }

        public Build setLog(boolean log) {
            isLog = log;
            return this;
        }

        public CacheConfig build() {
            return new CacheConfig(this);
        }
    }
}
