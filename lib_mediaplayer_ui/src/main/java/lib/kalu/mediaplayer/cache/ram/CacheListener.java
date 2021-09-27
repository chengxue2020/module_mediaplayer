package lib.kalu.mediaplayer.cache.ram;

import java.io.File;

/**
 * Listener for cache availability.
 *
 */
public interface CacheListener {

    void onCacheAvailable(File cacheFile, String url, int percentsAvailable);
}
