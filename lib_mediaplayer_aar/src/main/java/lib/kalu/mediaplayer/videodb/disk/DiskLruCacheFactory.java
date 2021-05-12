package lib.kalu.mediaplayer.videodb.disk;

import java.io.File;

import lib.kalu.mediaplayer.videodb.model.SafeKeyGenerator;

public class DiskLruCacheFactory implements InterDiskFactory {

    private final long diskCacheSize;
    private final CacheDirectoryGetter cacheDirectoryGetter;

    /**
     * 接口被UI线程调用以获取缓存文件夹。
     */
    public interface CacheDirectoryGetter {
        File getCacheDirectory();
    }

    public DiskLruCacheFactory(final String diskCacheFolder, long diskCacheSize) {
        this(new CacheDirectoryGetter() {
            @Override
            public File getCacheDirectory() {
                return new File(diskCacheFolder);
            }
        }, diskCacheSize);
    }

    public DiskLruCacheFactory(final String diskCacheFolder, final String diskCacheName, long diskCacheSize) {
        this(new CacheDirectoryGetter() {
            @Override
            public File getCacheDirectory() {
                return new File(diskCacheFolder, diskCacheName);
            }
        }, diskCacheSize);
    }

    /**
     * When using this constructor {@link CacheDirectoryGetter#getCacheDirectory()} will be called out
     * of UI thread, allowing to do I/O access without performance impacts.
     *
     * @param cacheDirectoryGetter                          接口被UI线程调用以获取缓存文件夹
     * @param diskCacheSize                                 LRU磁盘缓存所需的最大字节大小
     */
    @SuppressWarnings("WeakerAccess")
    public DiskLruCacheFactory(CacheDirectoryGetter cacheDirectoryGetter, long diskCacheSize) {
        this.diskCacheSize = diskCacheSize;
        this.cacheDirectoryGetter = cacheDirectoryGetter;
    }

    @Override
    public InterDiskCache build() {
        File cacheDir = cacheDirectoryGetter.getCacheDirectory();
        if (cacheDir == null) {
            return null;
        }
        if (!cacheDir.mkdirs() && (!cacheDir.exists() || !cacheDir.isDirectory())) {
            return null;
        }
        return DiskLruCacheWrapper.create(cacheDir, new SafeKeyGenerator());
    }
}

