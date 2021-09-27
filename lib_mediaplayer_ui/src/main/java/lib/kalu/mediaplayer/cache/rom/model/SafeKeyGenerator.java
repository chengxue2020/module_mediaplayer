package lib.kalu.mediaplayer.cache.rom.model;

import lib.kalu.mediaplayer.cache.rom.cache.VideoLruCache;
import lib.kalu.mediaplayer.cache.rom.manager.CacheConfig;
import lib.kalu.mediaplayer.cache.rom.manager.CacheManager;
import lib.kalu.mediaplayer.common.util.LogUtil;
import lib.kalu.mediaplayer.common.util.Md5Util;

/**
 * <pre>
 *     @author yangchong
 *     email  : yangchong211@163.com
 *     time  : 2020/8/6
 *     desc  : url校验以及缓存
 *     revise: 内存+磁盘缓存需要md5两次，使用该类后只需要一次即可。
 * </pre>
 */
public class SafeKeyGenerator {

    private final VideoLruCache<Integer, String> loadIdToSafeHash = new VideoLruCache<>(1000);

    public SafeKeyGenerator() {

    }

    public String getSafeKey(String url) {
        if (url==null || url.length()==0){
            return null;
        }
        String safeKey;
        //获取链接的hash算法值
        int hashCode = url.hashCode();
        synchronized (loadIdToSafeHash) {
            safeKey = loadIdToSafeHash.get(hashCode);
            LogUtil.log("SafeKeyGenerator-----获取缓存key-"+safeKey);
        }
        if (safeKey == null || safeKey.length()==0) {
            CacheConfig cacheConfig = CacheManager.getInstance().getCacheConfig();
            safeKey = Md5Util.encryptMD5ToString(url, cacheConfig.getSalt());
            LogUtil.log("SafeKeyGenerator-----md5转化key-"+safeKey);
        }
        synchronized (loadIdToSafeHash) {
            loadIdToSafeHash.put(hashCode, safeKey);
            LogUtil.log("SafeKeyGenerator-----存储key-"+safeKey);
        }
        return safeKey;
    }
}
