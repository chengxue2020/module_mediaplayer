package lib.kalu.mediaplayer.core.kernel.exo;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSource;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
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

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.util.MPLogUtil;

public final class ExoMediaSourceHelper {

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
    public MediaSource createMediaSource(@NonNull Context context,
                                         @NonNull CharSequence url,
                                         @Nullable Map<String, String> headers,
                                         @PlayerType.CacheType int cacheType,
                                         @NonNull int cacheMax,
                                         @NonNull String cacheDir,
                                         @NonNull String caheSate
    ) {
        Uri contentUri = Uri.parse(url.toString());
        MPLogUtil.log("getMediaSource => scheme = " + contentUri.getScheme() + ", url = " + url);
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

            // okhttp
//            OkHttpClient.Builder builder = new OkHttpClient.Builder();
//            OkHttpClient client = builder.build();
//            OkHttpDataSource.Factory http = new OkHttpDataSource.Factory(client);
//            http.setUserAgent("(Linux;Android " + Build.VERSION.RELEASE + ") " + ExoPlayerLibraryInfo.VERSION_SLASHY);

            // http
            DefaultHttpDataSource.Factory httpFactory = new DefaultHttpDataSource.Factory();
            httpFactory.setUserAgent("(Linux;Android " + Build.VERSION.RELEASE + ") " + ExoPlayerLibraryInfo.VERSION_SLASHY);
            httpFactory.setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS);
            httpFactory.setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS);
            httpFactory.setAllowCrossProtocolRedirects(true);
            httpFactory.setKeepPostFor302Redirects(true);

            // head
            refreshHeaders(httpFactory, headers);

            // 本地缓存
            if (null != context && cacheType == PlayerType.CacheType.DEFAULT) {
                MPLogUtil.log("getMediaSource => 策略, 本地缓存");

                CacheDataSource.Factory cacheFactory = new CacheDataSource.Factory();
                SimpleCache cache = ExoSimpleCache.getSimpleCache(context, cacheMax, cacheDir, caheSate);
                cacheFactory.setCache(cache);

                cacheFactory.setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
                cacheFactory.setUpstreamDataSourceFactory(httpFactory);
                return createMediaSource(url, cacheFactory);
            }
            // 默认
            else {
                MPLogUtil.log("getMediaSource => 默认, 不缓存");
                DefaultDataSource.Factory factory = new DefaultDataSource.Factory(context, httpFactory);
                return createMediaSource(url, factory);
            }
        }
    }

    private MediaSource createMediaSource(@NonNull CharSequence url, @NonNull DataSource.Factory factory) {

        int contentType;
        if (url.toString().toLowerCase().contains(".mpd")) {
            contentType = C.CONTENT_TYPE_DASH;
        } else if (url.toString().toLowerCase().contains(".m3u8")) {
            contentType = C.CONTENT_TYPE_HLS;
        } else if (url.toString().toLowerCase().matches(".*\\.ism(l)?(/manifest(\\(.+\\))?)?")) {
            contentType = C.CONTENT_TYPE_SS;
        } else {
            contentType = C.CONTENT_TYPE_OTHER;
        }

        // 字幕
//        MediaItem.SubtitleConfiguration.Builder subtitle = new MediaItem.SubtitleConfiguration.Builder(srtUri);
//        subtitle.setMimeType(MimeTypes.APPLICATION_SUBRIP);
//        subtitle.setLanguage("en");
//        subtitle.setSelectionFlags(C.SELECTION_FLAG_AUTOSELECT);

//        MediaLogUtil.log("SRT => srtUri = " + srtUri);
        MediaItem.Builder builder = new MediaItem.Builder();
        builder.setUri(Uri.parse(url.toString()));
//        builder.setSubtitleConfigurations(Arrays.asList(subtitle.build()));
        MediaItem mediaItem = builder.build();
//        MediaItem.Subtitle subtitle = new MediaItem.Subtitle(
//                srtUri,
//                MimeTypes.APPLICATION_SUBRIP,
//                "en",
//                C.SELECTION_FLAG_DEFAULT);
////                C.SELECTION_FLAG_AUTOSELECT);


        switch (contentType) {
            case C.CONTENT_TYPE_DASH:
                MPLogUtil.log("SRT => TYPE_DASH");
                return new DashMediaSource.Factory(factory).createMediaSource(mediaItem);
            case C.CONTENT_TYPE_SS:
                MPLogUtil.log("SRT => TYPE_SS");
                return new SsMediaSource.Factory(factory).createMediaSource(mediaItem);
            case C.CONTENT_TYPE_HLS:
                MPLogUtil.log("SRT => TYPE_HLS");
                return new HlsMediaSource.Factory(factory).createMediaSource(mediaItem);
            default:
                MPLogUtil.log("SRT => TYPE_DEFAULT");
//                return new DefaultMediaSourceFactory(factory).createMediaSource(mediaItem);
                DefaultExtractorsFactory extractors = new DefaultExtractorsFactory();
                extractors.setConstantBitrateSeekingEnabled(true);
                return new ProgressiveMediaSource.Factory(factory, extractors).createMediaSource(mediaItem);
        }

//        // 字幕
//        if (null != srtUri) {
//
//            MediaItem.Builder srtBuilder = new MediaItem.Builder().setUri(srtUri);
//            MediaItem.Subtitle subtitle = new MediaItem.Subtitle(srtUri,
//                    MimeTypes.TEXT_VTT,
//                    "en",
//                    C.SELECTION_FLAG_DEFAULT);
//            srtBuilder.setSubtitles(Arrays.asList(subtitle));
//
//            MediaItem srtItem = srtBuilder.build();
////            MediaSource srtSource = new DefaultMediaSourceFactory(factory).createMediaSource(srtItem);
//
////            MediaItem.SubtitleConfiguration.Builder builder = new MediaItem.SubtitleConfiguration.Builder(srtUri);
//////            builder.setMimeType(MimeTypes.APPLICATION_SUBRIP);
////            builder.setMimeType(MimeTypes.TEXT_VTT);
////            builder.setLanguage("en");
////            builder.setSelectionFlags(C.SELECTION_FLAG_DEFAULT);
////            MediaItem.SubtitleConfiguration subtitle = builder.build();
//            MediaSource textMediaSource = new SingleSampleMediaSource.Factory(factory).createMediaSource(subtitle, C.TIME_UNSET);
//////            textMediaSource.getMediaItem().mediaMetadata.subtitle.toString();
////            MediaLogUtil.log("SRT => " + subtitle);
//            return new MergingMediaSource(mediaSource, srtSource);
//        }
//        // 默认
//        else {
//            return mediaSource;
//        }
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
