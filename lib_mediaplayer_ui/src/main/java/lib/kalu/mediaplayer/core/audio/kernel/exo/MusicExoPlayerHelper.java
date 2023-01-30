package lib.kalu.mediaplayer.core.audio.kernel.exo;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;

public final class MusicExoPlayerHelper {

    private MusicExoPlayerHelper() {
    }

    private static final class Holder {
        private final static MusicExoPlayerHelper mInstance = new MusicExoPlayerHelper();
    }

    public static MusicExoPlayerHelper getInstance() {
        return Holder.mInstance;
    }

    public MediaSource createMediaSource(@NonNull Context context, @NonNull String musicUrl) {

//        String scheme;
//        Uri uri = Uri.parse(musicUrl);
//        try {
//            scheme = uri.getScheme();
//        } catch (Exception e) {
//            scheme = null;
//        }
//        MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => scheme = " + scheme);

        MediaSource mediaSource = create(context, musicUrl);
        return mediaSource;
    }

    private MediaSource create(
            @NonNull Context context,
            @NonNull String musicUrl) {

        // 2
        MediaItem.Builder builder = new MediaItem.Builder();
        builder.setUri(Uri.parse(musicUrl));

        // 3
        DefaultHttpDataSource.Factory httpFactory = new DefaultHttpDataSource.Factory();
        httpFactory.setUserAgent("(Linux;Android " + Build.VERSION.RELEASE + ") " + ExoPlayerLibraryInfo.VERSION_SLASHY);
        httpFactory.setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS);
        httpFactory.setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS);
        httpFactory.setAllowCrossProtocolRedirects(true);
        httpFactory.setKeepPostFor302Redirects(true);

        // 4
        DefaultDataSource.Factory dataSource = new DefaultDataSource.Factory(context, httpFactory);

        // 5
        MediaItem mediaItem = builder.build();
        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        extractorsFactory.setConstantBitrateSeekingEnabled(true);
        return new ProgressiveMediaSource.Factory(dataSource, extractorsFactory).createMediaSource(mediaItem);
    }
}
