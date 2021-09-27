package lib.kalu.mediaplayer.cache.ram.sourcestorage;

import lib.kalu.mediaplayer.cache.ram.SourceInfo;

/**
 * Storage for {@link SourceInfo}.
 */
public interface SourceInfoStorage {

    SourceInfo get(String url);

    void put(String url, SourceInfo sourceInfo);

    void release();
}
