package lib.kalu.mediaplayer.kernel.video.platfrom.exo;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSource;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.DefaultHlsExtractorFactory;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import lib.kalu.mediaplayer.cache.CacheConfig;
import lib.kalu.mediaplayer.cache.CacheType;
import lib.kalu.mediaplayer.util.MediaLogUtil;

/**
 * @description: exo视频播放器帮助类
 * @date: 2021-05-12 09:36
 */
public final class ExoMediaSourceHelper {

    private final WeakHashMap<String, SimpleCache> mWHM = new WeakHashMap<>();

    private ExoMediaSourceHelper() {
    }

    private static final class Holder {
        private final static ExoMediaSourceHelper mInstance = new ExoMediaSourceHelper();
    }

    public static ExoMediaSourceHelper getInstance() {
        return Holder.mInstance;
    }

    /**
     * @param url     视频url
     * @param headers 视频headers
     * @return
     */
    public MediaSource getMediaSource(@NonNull Context context, @NonNull boolean live, @NonNull String url, @Nullable Map<String, String> headers, @NonNull CacheConfig config) {
        Uri contentUri = Uri.parse(url);
        MediaLogUtil.log("getMediaSource => scheme = " + contentUri.getScheme() + ", live = " + live + ", url = " + url);
        // rtmp
        if ("rtmp".equals(contentUri.getScheme())) {
            RtmpDataSource.Factory factory = new RtmpDataSource.Factory();
            return new ProgressiveMediaSource.Factory(factory).createMediaSource(MediaItem.fromUri(contentUri));
        }
        // rtsp
        else if ("rtsp".equals(contentUri.getScheme())) {
            RtspMediaSource.Factory factory = new RtspMediaSource.Factory();
            return factory.createMediaSource(MediaItem.fromUri(contentUri));
        }
        // other
        else {
            // http
            DefaultHttpDataSource.Factory http = new DefaultHttpDataSource.Factory();
            http.setUserAgent("(Linux;Android " + Build.VERSION.RELEASE + ") " + ExoPlayerLibraryInfo.VERSION_SLASHY);
            http.setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS);
            http.setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS);
            http.setAllowCrossProtocolRedirects(true);
            http.setKeepPostFor302Redirects(true);

            // head
            refreshHeaders(http, headers);

            // 本地缓存
            if (!live && null != context && null != config && config.getCacheType() == CacheType.DEFAULT) {
                MediaLogUtil.log("getMediaSource => 策略, 本地缓存");

                // cache
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
                if (null != context && null != config && config.getCacheType() == CacheType.DEFAULT) {
                    if (!mWHM.containsKey(dir)) {
                        File file = new File(context.getExternalCacheDir(), dir);
                        LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(size * 1024 * 1024);
                        StandaloneDatabaseProvider provider = new StandaloneDatabaseProvider(context);
                        SimpleCache simpleCache = new SimpleCache(file, evictor, provider);
                        mWHM.put(dir, simpleCache);
                    }
                    factory.setCache(mWHM.get(dir));
                }

                factory.setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
                factory.setUpstreamDataSourceFactory(http);
                return createMediaSource(url, factory);
            }
            // 默认
            else {
                MediaLogUtil.log("getMediaSource => 默认, 不缓存");
                DefaultDataSource.Factory factory = new DefaultDataSource.Factory(context, http);
                return createMediaSource(url, factory);
            }
        }
    }

    private final MediaSource createMediaSource(@NonNull String url, @NonNull DataSource.Factory factory) {

        int contentType;
        if (url.toLowerCase().contains(".mpd")) {
            contentType = C.TYPE_DASH;
        } else if (url.toLowerCase().contains(".m3u8")) {
            contentType = C.TYPE_HLS;
        } else if (url.toLowerCase().matches(".*\\.ism(l)?(/manifest(\\(.+\\))?)?")) {
            contentType = C.TYPE_SS;
        } else {
            contentType = C.TYPE_OTHER;
        }

        switch (contentType) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(factory).createMediaSource(MediaItem.fromUri(url));
            case C.TYPE_SS:
                return new SsMediaSource.Factory(factory).createMediaSource(MediaItem.fromUri(url));
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(factory).createMediaSource(MediaItem.fromUri(url));
            default:
                DefaultExtractorsFactory extractors = new DefaultExtractorsFactory();
                extractors.setConstantBitrateSeekingEnabled(true);
                return new ProgressiveMediaSource.Factory(factory, extractors).createMediaSource(MediaItem.fromUri(url));
        }
    }

    private void refreshHeaders(@NonNull HttpDataSource.Factory factory, @NonNull Map<String, String> map) {

        if (null == map || map.size() <= 0)
            return;

        String userAgent = null;
        HashMap<String, String> mapFormat = new HashMap<>();

        for (String temp : map.keySet()) {
            if (null == temp || temp.length() <= 0)
                continue;
            String value = mapFormat.get(temp);
            if (null == value || value.length() <= 0)
                continue;
            if ("User-Agent".equals(temp)) {
                userAgent = value;
            } else {
                mapFormat.put(temp, value);
            }
        }

        //如果发现用户通过header传递了UA，则强行将HttpDataSourceFactory里面的userAgent字段替换成用户的
        if (null != userAgent) {
            try {
                Field userAgentField = factory.getClass().getDeclaredField("userAgent");
                userAgentField.setAccessible(true);
                userAgentField.set(factory, userAgent);
            } catch (Exception e) {
            }
        }

        // add
        factory.setDefaultRequestProperties(mapFormat);
    }
}
