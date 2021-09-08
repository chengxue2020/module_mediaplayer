package lib.kalu.mediaplayer.videocache.sourcestorage;

import lib.kalu.mediaplayer.videocache.SourceInfo;

/**
 * Storage for {@link SourceInfo}.
 */
public interface SourceInfoStorage {

    SourceInfo get(String url);

    void put(String url, SourceInfo sourceInfo);

    void release();
}
