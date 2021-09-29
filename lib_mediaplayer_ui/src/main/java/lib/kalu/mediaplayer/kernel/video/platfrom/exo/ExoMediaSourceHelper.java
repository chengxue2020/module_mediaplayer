package lib.kalu.mediaplayer.kernel.video.platfrom.exo;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

import lib.kalu.mediaplayer.cache.config.CacheConfig;
import lib.kalu.mediaplayer.cache.config.CacheType;

/**
 * @description: exo视频播放器帮助类
 * @date: 2021-05-12 09:36
 */
public final class ExoMediaSourceHelper {

    private static ExoMediaSourceHelper sInstance;
    private final String mUserAgent;
    private HttpDataSource.Factory mHttpDataSourceFactory;
    private Cache mCache;

    private ExoMediaSourceHelper(@NonNull Context context) {
        mUserAgent = Util.getUserAgent(context, context.getApplicationInfo().name);
    }

    public static ExoMediaSourceHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (ExoMediaSourceHelper.class) {
                if (sInstance == null) {
                    sInstance = new ExoMediaSourceHelper(context);
                }
            }
        }
        return sInstance;
    }

    /**
     * @param uri     视频url
     * @param headers 视频headers
     * @return
     */
    public MediaSource getMediaSource(@NonNull Context context, @NonNull String uri, @Nullable Map<String, String> headers, @NonNull CacheConfig config) {
        Uri contentUri = Uri.parse(uri);
        if ("rtmp".equals(contentUri.getScheme())) {
            RtmpDataSource.Factory rtmpDataSourceFactory = new RtmpDataSource.Factory();
            return new ProgressiveMediaSource.Factory(rtmpDataSourceFactory).createMediaSource(MediaItem.fromUri(contentUri));
        }
        int contentType = inferContentType(uri);
        DataSource.Factory factory;
        if (null != context && null != config && config.getCacheType() > CacheType.RAM) {
            Toast.makeText(context, "磁盘缓存", Toast.LENGTH_SHORT).show();
            factory = getCacheDataSourceFactory(context, config);
        } else {
            Toast.makeText(context, "内存缓存", Toast.LENGTH_SHORT).show();
            factory = new DefaultDataSourceFactory(context, getHttpDataSourceFactory());
        }
        if (mHttpDataSourceFactory != null) {
            setHeaders(headers);
        }
        switch (contentType) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(factory).createMediaSource(MediaItem.fromUri(contentUri));
            case C.TYPE_SS:
                return new SsMediaSource.Factory(factory).createMediaSource(MediaItem.fromUri(contentUri));
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(factory).createMediaSource(MediaItem.fromUri(contentUri));
            default:
            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(factory).createMediaSource(MediaItem.fromUri(contentUri));
        }
    }

    private int inferContentType(String fileName) {
        fileName = fileName.toLowerCase();
        if (fileName.contains(".mpd")) {
            return C.TYPE_DASH;
        } else if (fileName.contains(".m3u8")) {
            return C.TYPE_HLS;
        } else if (fileName.matches(".*\\.ism(l)?(/manifest(\\(.+\\))?)?")) {
            return C.TYPE_SS;
        } else {
            return C.TYPE_OTHER;
        }
    }

    private DataSource.Factory getCacheDataSourceFactory(@NonNull Context context, @NonNull CacheConfig config) {

        int size;
        String dir;
        if (null != config) {
            size = config.getCacheMaxMB();
            dir = config.getCacheDir();
        } else {
            size = 1024;
            dir = "temp";
        }

        CacheDataSource.Factory factory = new CacheDataSource.Factory();

        // 缓存策略：磁盘
        if (null == mCache && null != context && null != config && config.getCacheType() > CacheType.RAM) {
            // 缓存目录
            File file = new File(context.getExternalCacheDir(), dir);
            // 缓存大小，默认1024M，使用LRU算法实现
            LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(size * 1024 * 1024);
            ExoDatabaseProvider provider = new ExoDatabaseProvider(context);
            mCache = new SimpleCache(file, evictor, provider);
        }

        if (null != mCache) {
            factory.setCache(mCache);
        }
        factory.setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
        factory.setUpstreamDataSourceFactory(getHttpDataSourceFactory());
        return factory;

//        return new CacheDataSourceFactory(
//                mCache,
//                getDataSourceFactory(context),
//                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);

    }


    /**
     * Returns a new HttpDataSource factory.
     *
     * @return A new HttpDataSource factory.
     */
    private DataSource.Factory getHttpDataSourceFactory() {
        if (mHttpDataSourceFactory == null) {
            mHttpDataSourceFactory = new DefaultHttpDataSourceFactory(
                    mUserAgent,
                    null,
                    DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                    DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                    //http->https重定向支持
                    true);
        }
        return mHttpDataSourceFactory;
    }

    private void setHeaders(Map<String, String> headers) {
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                String key = header.getKey();
                String value = header.getValue();
                //如果发现用户通过header传递了UA，则强行将HttpDataSourceFactory里面的userAgent字段替换成用户的
                if (TextUtils.equals(key, "User-Agent")) {
                    if (!TextUtils.isEmpty(value)) {
                        try {
                            Field userAgentField = mHttpDataSourceFactory.getClass().getDeclaredField("userAgent");
                            userAgentField.setAccessible(true);
                            userAgentField.set(mHttpDataSourceFactory, value);
                        } catch (Exception e) {
                            //ignore
                        }
                    }
                } else {
                    mHttpDataSourceFactory.getDefaultRequestProperties().set(key, value);
                }
            }
        }
    }
}
