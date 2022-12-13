package lib.kalu.mediaplayer.core.kernel.exo;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.database.StandaloneDatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;

public final class ExoSimpleCache {

    private static SimpleCache mSimpleCache;

    public static SimpleCache getSimpleCache(
            @NonNull Context context,
            @NonNull int cacheMax,
            @NonNull String cacheDir,
            @NonNull String caheSate) {

        if (null != mSimpleCache) {
            mSimpleCache.release();
        }

        // cache
        int size;
        if (cacheMax <= 0) {
            size = 1024;
        } else {
            size = cacheMax;
        }

        String dir;
        if (null == cacheDir || cacheDir.length() <= 0) {
            dir = "temp_" + caheSate;
        } else {
            dir = cacheDir + "_" + caheSate;
        }

        File file = new File(context.getExternalCacheDir(), dir);
        LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(size * 1024 * 1024);
        StandaloneDatabaseProvider provider = new StandaloneDatabaseProvider(context);
        mSimpleCache = new SimpleCache(file, evictor, provider);
        return mSimpleCache;
    }
}
