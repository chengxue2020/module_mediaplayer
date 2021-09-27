package lib.kalu.mediaplayer.cache.rom;

import android.content.Context;

import lib.kalu.mediaplayer.cache.config.CacheConfig;
import lib.kalu.mediaplayer.cache.config.CacheConfigManager;
import lib.kalu.mediaplayer.cache.config.CacheType;

/**
 * <pre>
 *     @author yangchong
 *     email  : yangchong211@163.com
 *     time  : 2020/6/16
 *     desc  : 使用代码案例
 *     revise:
 * </pre>
 */
public class TestDemo {

    private void init(Context context) {

        CacheConfig build = new CacheConfig.Build()
                .setIsEffective(true)
                .setType(CacheType.ALL)
                .setCacheMax(1000)
                .setLog(false)
                .build();
        CacheConfigManager.getInstance().setConfig(build);

        //保存播放位置
//        VideoLocation location = new VideoLocation(url, currentPosition, duration);
//        LocationManager.getInstance().put(url,location);

        //获取播放位置
//        final long location = LocationManager.getInstance().get(url);

        //清除所有位置
//        LocationManager.getInstance().clearAll();
    }
}
