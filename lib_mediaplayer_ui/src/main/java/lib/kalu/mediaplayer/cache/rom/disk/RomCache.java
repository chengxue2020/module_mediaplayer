package lib.kalu.mediaplayer.cache.rom.disk;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.File;

import lib.kalu.mediaplayer.cache.rom.model.SafeKeyGenerator;
import lib.kalu.mediaplayer.cache.rom.model.VideoLocation;
import lib.kalu.mediaplayer.util.LogUtil;

/**
 * <pre>
 *     @author yangchong
 *     email  : yangchong211@163.com
 *     time  : 2020/8/6
 *     desc  : 磁盘缓存工具
 *     revise:
 * </pre>
 */
public class RomCache {

    private InterDiskCache interDiskCache;
    public final SafeKeyGenerator safeKeyGenerator;

    public RomCache(@NonNull Context context) {
        File path = DiskFileUtils.getFilePath(context);
        String pathString = path.getPath();
        LogUtil.log("SqlLiteCache-----pathString路径输出地址-"+pathString);
        this.safeKeyGenerator = new SafeKeyGenerator();
        interDiskCache = DiskLruCacheWrapper.get(path,safeKeyGenerator);
    }

    /**
     * 存数据
     * @param url                           链接
     * @param location                      视频数据
     */
    public synchronized void put(String url , VideoLocation location){
        if (location==null){
            return;
        }
        String safeKey = safeKeyGenerator.getSafeKey(url);
        location.setUrlMd5(safeKey);
        String json = location.toJson();
        LogUtil.log("SqlLiteCache-----put--json--"+json);
        interDiskCache.put(url,json);
    }

    /**
     * 取数据
     * @param url                           链接
     * @return
     */
    public synchronized long get(String url){
        String data = interDiskCache.get(url);
        if (data==null || data.length()==0){
            return -1;
        }
        LogUtil.log("SqlLiteCache-----get---"+data);
        VideoLocation location = VideoLocation.toObject(data);
        return location.getPosition();
    }

    /**
     * 移除数据
     * @param url                           链接
     * @return
     */
    public synchronized boolean remove(String url){
        return interDiskCache.remove(url);
    }

    /**
     * 是否包含
     * @param url                           链接
     * @return
     */
    public synchronized boolean containsKey(String url){
        return interDiskCache.containsKey(url);
    }

    /**
     * 清楚所有数据
     * @return                              是否清楚完毕
     */
    public synchronized void clearAll(){
        interDiskCache.clear();
    }


}
