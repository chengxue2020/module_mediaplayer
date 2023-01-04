package lib.kalu.mediaplayer.core.kernel.exo;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.MediaItem;
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
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.MimeTypes;

import java.util.Arrays;

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

    public MediaSource createMediaSource(@NonNull Context context,
                                         @NonNull String mediaUrl,
                                         @Nullable String subtitleUrl,
                                         @PlayerType.CacheType int cacheType,
                                         @NonNull int cacheMax,
                                         @NonNull String cacheDir) {

        MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => mediaUrl = " + mediaUrl);
        MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => subtitleUrl = " + subtitleUrl);
        MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => cacheType = " + cacheType);
        MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => cacheMax = " + cacheMax);
        MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => cacheDir = " + cacheDir);

        String scheme;
        Uri uri = Uri.parse(mediaUrl);
        try {
            scheme = uri.getScheme();
        } catch (Exception e) {
            scheme = null;
        }
        MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => scheme = " + scheme);

        // rtmp
        if (PlayerType.SchemeType.RTMP.equals(scheme)) {
            MediaItem mediaItem = MediaItem.fromUri(uri);
            return new ProgressiveMediaSource.Factory(new RtmpDataSource.Factory()).createMediaSource(mediaItem);
        }
        // rtsp
        else if (PlayerType.SchemeType.RTSP.equals(scheme)) {
            MediaItem mediaItem = MediaItem.fromUri(uri);
            return new RtspMediaSource.Factory().createMediaSource(mediaItem);
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
//            refreshHeaders(httpFactory, headers);

            DataSource.Factory dataSource;
            if (cacheType == PlayerType.CacheType.NONE) {
                dataSource = new DefaultDataSource.Factory(context, httpFactory);
            } else {
                CacheDataSource.Factory cacheFactory = new CacheDataSource.Factory();
                SimpleCache cache = ExoSimpleCache.getSimpleCache(context, cacheMax, cacheDir);
                cacheFactory.setCache(cache);
                cacheFactory.setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
                cacheFactory.setUpstreamDataSourceFactory(httpFactory);
                dataSource = cacheFactory;
            }

            MediaSource mediaSource = createMediaSource(mediaUrl, subtitleUrl, dataSource);
            return mediaSource;
        }
    }

    private MediaSource createMediaSource(
            @NonNull String mediaUrl,
            @Nullable String subtitleUrl,
            @NonNull DataSource.Factory dataSource) {

        // 1
        int contentType;
        try {
            String s = mediaUrl.toLowerCase();
            if (s.endsWith(PlayerType.SchemeType._MPD)) {
                contentType = C.CONTENT_TYPE_DASH;
            } else if (s.endsWith(PlayerType.SchemeType._M3U)) {
                contentType = C.CONTENT_TYPE_HLS;
            } else if (s.endsWith(PlayerType.SchemeType._M3U8)) {
                contentType = C.CONTENT_TYPE_HLS;
            } else if (s.matches(PlayerType.SchemeType._MATCHES)) {
                contentType = C.CONTENT_TYPE_SS;
            } else {
                contentType = C.CONTENT_TYPE_OTHER;
            }
        } catch (Exception e) {
            contentType = C.CONTENT_TYPE_OTHER;
        }
        MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => contentType = " + contentType);

        // 2
        MediaItem.Builder builder = new MediaItem.Builder();
        builder.setUri(Uri.parse(mediaUrl));
        if (null != subtitleUrl && subtitleUrl.length() > 0) {
            MediaItem.SubtitleConfiguration.Builder subtitle = new MediaItem.SubtitleConfiguration.Builder(Uri.parse(mediaUrl));
            subtitle.setMimeType(MimeTypes.APPLICATION_SUBRIP);
            subtitle.setLanguage("en");
            subtitle.setSelectionFlags(C.SELECTION_FLAG_AUTOSELECT); // C.SELECTION_FLAG_DEFAULT
            builder.setSubtitleConfigurations(Arrays.asList(subtitle.build()));

//            MediaItem.SubtitleConfiguration.Builder builder = new MediaItem.SubtitleConfiguration.Builder(srtUri);
//            builder.setMimeType(MimeTypes.APPLICATION_SUBRIP);
//            builder.setMimeType(MimeTypes.TEXT_VTT);
//            builder.setLanguage("en");
//            builder.setSelectionFlags(C.SELECTION_FLAG_DEFAULT);
//            MediaItem.SubtitleConfiguration subtitle = builder.build();
//            MediaSource textMediaSource = new SingleSampleMediaSource.Factory(factory).createMediaSource(subtitle, C.TIME_UNSET);
//            textMediaSource.getMediaItem().mediaMetadata.subtitle.toString();
//            MediaLogUtil.log("SRT => " + subtitle);
//            return new MergingMediaSource(mediaSource, srtSource);
        }
        MediaItem mediaItem = builder.build();

        // 3
        switch (contentType) {
            case C.CONTENT_TYPE_DASH:
                return new DashMediaSource.Factory(dataSource).createMediaSource(mediaItem);
            case C.CONTENT_TYPE_SS:
                return new SsMediaSource.Factory(dataSource).createMediaSource(mediaItem);
            case C.CONTENT_TYPE_HLS:
                return new HlsMediaSource.Factory(dataSource).createMediaSource(mediaItem);
            default:
                DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                extractorsFactory.setConstantBitrateSeekingEnabled(true);
                return new ProgressiveMediaSource.Factory(dataSource, extractorsFactory).createMediaSource(mediaItem);
        }
    }
}
