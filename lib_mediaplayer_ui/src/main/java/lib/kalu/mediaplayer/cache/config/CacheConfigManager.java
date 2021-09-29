package lib.kalu.mediaplayer.cache.config;

import android.content.Context;
import android.os.Debug;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import java.io.IOException;

import lib.kalu.mediaplayer.BuildConfig;
import lib.kalu.mediaplayer.cache.rom.cache.RamCache;
import lib.kalu.mediaplayer.cache.rom.disk.DiskFileUtils;
import lib.kalu.mediaplayer.cache.rom.disk.RomCache;
import lib.kalu.mediaplayer.cache.rom.model.VideoLocation;
import lib.kalu.mediaplayer.util.LogUtil;

/**
 * @description: 音视频播放记录本地缓存 最开始使用greenDao，二级缓存耗时100毫秒左右 磁盘+内存+key缓存+读写优化，耗时大概2到5毫秒左右
 * @date: 2021-05-12 14:41
 */
@Keep
public class CacheConfigManager {

    /**
     * 终极目标
     * 1.开发者可以自由切换缓存模式
     * 2.可以设置内存缓存最大值，设置磁盘缓存的路径
     * 3.能够有增删改查基础方法
     * 4.多线程下安全和脏数据避免
     * 5.代码体积小
     * 6.一键打印存取表结构日志
     * 7.如何一键将本地记录数据上传
     * 8.拓展性和封闭性
     * 9.性能，插入和获取数据，超1000条数据测试
     */

    /**
     * 内存缓存
     */
    private RamCache mRamCache;
    /**
     * 磁盘缓存
     */
    private RomCache mRomCache;
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
        LogUtil.setIsLog(cacheConfig.isLog());
        mRamCache = new RamCache();
        mRomCache = new RomCache(context);
        LogUtil.log("CacheConfigManager-----init初始化-");
    }

    public @NonNull
    CacheConfig getCacheConfig() {
        return cacheConfig;
    }

    /**
     * 存数据
     * url为什么要md5？思考一下……
     *
     * @param url      链接
     * @param location 视频数据
     */
    public synchronized void put(String url, VideoLocation location) {
        if (!cacheConfig.isEffective()) {
            return;
        }
        if (url == null || url.length() == 0 || location == null) {
            return;
        }

        long start = System.currentTimeMillis();

        // 缓存策略：内存+磁盘
        if (cacheConfig.getCacheType() == CacheType.ALL) {
            //存储到内存中
            mRamCache.put(url, location);
            //存储到磁盘中
            mRomCache.put(url, location);
        }
        // 缓存策略：内存
        else if (cacheConfig.getCacheType() == CacheType.RAM) {
            //存储到内存中
            mRamCache.put(url, location);
        }
        // 缓存策略：磁盘
        else if (cacheConfig.getCacheType() == CacheType.ROM) {
            //存储到磁盘中
            mRomCache.put(url, location);
        }
        // 默认
        else {
            //存储到内存中
            mRamCache.put(url, location);
        }
        long end = System.currentTimeMillis();
        LogUtil.log("CacheConfigManager-----put--存数据耗时-" + (end - start));
    }

    /**
     * 取数据
     *
     * @param url 链接
     * @return
     */
    public synchronized long get(String url) {
        if (!cacheConfig.isEffective()) {
            return 0;
        }
        if (url == null || url.length() == 0) {
            return 0;
        }
        /*
         * type
         * 0，表示内存缓存
         * 1，表示磁盘缓存
         * 2，表示内存缓存+磁盘缓存
         */
        long start = System.currentTimeMillis();
        long position;
        // 缓存策略：内存+磁盘
        if (cacheConfig.getCacheType() == CacheType.ALL) {
            //先从内存中找
            position = mRamCache.get(url);
            if (position < 0) {
                //内存找不到，则从磁盘中查找
                position = mRomCache.get(url);
            }
        }
        // 缓存策略：内存
        else if (cacheConfig.getCacheType() == CacheType.RAM) {
            //先从内存中找
            position = mRamCache.get(url);
        }
        // 缓存策略：磁盘
        else if (cacheConfig.getCacheType() == CacheType.ROM) {
            //从磁盘中查找
            position = mRomCache.get(url);
        }
        // 默认
        else {
            //先从内存中找
            position = mRamCache.get(url);
        }
        long end = System.currentTimeMillis();
        LogUtil.log("CacheConfigManager-----get--取数据耗时-" + (end - start) + "---进度-" + position);
        return position;
    }

    /**
     * 移除数据
     *
     * @param url 链接
     * @return
     */
    public synchronized boolean remove(String url) {
        if (!cacheConfig.isEffective()) {
            return false;
        }
        if (url == null || url.length() == 0) {
            return false;
        }
        /*
         * type
         * 0，表示内存缓存
         * 1，表示磁盘缓存
         * 2，表示内存缓存+磁盘缓存
         */
        if (cacheConfig.getCacheType() == CacheType.ROM) {
            return mRomCache.remove(url);
        } else if (cacheConfig.getCacheType() == CacheType.ALL) {
            boolean remove1 = mRamCache.remove(url);
            boolean remove2 = mRomCache.remove(url);
            return remove1 || remove2;
        } else if (cacheConfig.getCacheType() == CacheType.RAM) {
            return mRamCache.remove(url);
        } else {
            return mRamCache.remove(url);
        }
    }

    /**
     * 是否包含
     *
     * @param url 链接
     * @return
     */
    public synchronized boolean containsKey(String url) {
        if (!cacheConfig.isEffective()) {
            return false;
        }
        if (url == null || url.length() == 0) {
            return false;
        }
        /*
         * type
         * 0，表示内存缓存
         * 1，表示磁盘缓存
         * 2，表示内存缓存+磁盘缓存
         */
        boolean containsKey;
        if (cacheConfig.getCacheType() == CacheType.ROM) {
            containsKey = mRomCache.containsKey(url);
        } else if (cacheConfig.getCacheType() == CacheType.ALL) {
            containsKey = mRamCache.containsKey(url);
            if (!containsKey) {
                containsKey = mRomCache.containsKey(url);
                return containsKey;
            }
        } else if (cacheConfig.getCacheType() == CacheType.RAM) {
            containsKey = mRamCache.containsKey(url);
        } else {
            containsKey = mRamCache.containsKey(url);
        }
        return containsKey;
    }

    /**
     * 清楚所有数据
     *
     * @return 是否清楚完毕
     */
    public synchronized void clearAll() {
        if (!cacheConfig.isEffective()) {
            return;
        }
        /*
         * type
         * 0，表示内存缓存
         * 1，表示磁盘缓存
         * 2，表示内存缓存+磁盘缓存
         */
        if (cacheConfig.getCacheType() == CacheType.ROM) {
            mRomCache.clearAll();
        } else if (cacheConfig.getCacheType() == CacheType.ALL) {
            mRamCache.clearAll();
            mRomCache.clearAll();
        } else if (cacheConfig.getCacheType() == CacheType.RAM) {
            mRamCache.clearAll();
        } else {
            mRamCache.clearAll();
        }
    }

    /**
     * 获取当前应用使用的内存
     *
     * @return
     */
    public long getUseMemory() {
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        LogUtil.log("CacheConfigManager-----内存-" + totalMemory + "-----" + freeMemory);
        //long maxMemory = Runtime.getRuntime().maxMemory();
        long useMemory = totalMemory - freeMemory;
        LogUtil.log("CacheConfigManager-----获取当前应用使用的内存-" + useMemory);
        return useMemory;
    }

    /**
     * 设定内存的阈值
     *
     * @param proportion 比例
     * @return
     */
    public long setMemoryThreshold(int proportion) {
        if (proportion < 0 || proportion > 10) {
            proportion = 2;
        }
        long totalMemory = Runtime.getRuntime().totalMemory();
        long threshold = totalMemory / proportion;
        LogUtil.log("CacheConfigManager-----设定内存的阈值-" + threshold);
        return threshold;
    }

    /**
     * 获取Java内存快照文件
     *
     * @param context
     */
    public void dumpHprofData(Context context) {
        String dump = DiskFileUtils.getPath(context, "dump");
        LogUtil.log("CacheConfigManager-----获取Java内存快照文件-" + dump);
        try {
            Debug.dumpHprofData(dump);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
