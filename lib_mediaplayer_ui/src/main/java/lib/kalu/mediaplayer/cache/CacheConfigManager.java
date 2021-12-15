package lib.kalu.mediaplayer.cache;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.util.MediaLogUtil;

/**
 * @description: 音视频播放记录本地缓存 最开始使用greenDao，二级缓存耗时100毫秒左右 磁盘+内存+key缓存+读写优化，耗时大概2到5毫秒左右
 * @date: 2021-05-12 14:41
 */
@Keep
public class CacheConfigManager {

    /**
     * 配置类
     */
    private CacheConfig cacheConfig = null;

    private static class Holder {
        private static final CacheConfigManager INSTANCE = new CacheConfigManager();
    }

    public static CacheConfigManager getInstance() {
        return Holder.INSTANCE;
    }

    public void setConfig(@NonNull Context context, @NonNull CacheConfig config) {
        this.cacheConfig = config;
        MediaLogUtil.setIsLog(cacheConfig.isLog());
        MediaLogUtil.log("CacheConfigManager-----init初始化-");
    }

    public @NonNull
    CacheConfig getCacheConfig() {
        return cacheConfig;
    }
}
