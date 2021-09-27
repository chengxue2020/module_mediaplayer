package lib.kalu.mediaplayer.context;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import lib.kalu.mediaplayer.cache.config.CacheConfig;
import lib.kalu.mediaplayer.cache.config.CacheConfigManager;
import lib.kalu.mediaplayer.cache.config.CacheType;
import lib.kalu.mediaplayer.kernel.video.utils.PlayerFactoryUtils;
import lib.kalu.mediaplayer.keycode.KeycodeImplSimulator;
import lib.kalu.mediaplayer.ui.config.PlayerConfig;
import lib.kalu.mediaplayer.ui.config.PlayerConfigManager;
import lib.kalu.mediaplayer.ui.config.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

@Keep
public class MediaplayerContentProvider extends ContentProvider {

    public static WeakReference<Context> weakReference = null;

    public static final Context getContextWeakReference() {
        try {
            return weakReference.get();
        } catch (Exception e) {
            LogUtil.log(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext().getApplicationContext();
        weakReference = new WeakReference<>(context);

        // init
        PlayerConfig build = PlayerConfig.newBuilder()
                //设置视频全局埋点事件
//                .setBuriedPointEvent(new BuriedPointEventImpl())
                //调试的时候请打开日志，方便排错
                .setLogEnabled(false)
                // loading
                //设置exo
                .setPlayerFactory(PlayerFactoryUtils.getPlayer(PlayerType.PlatformType.EXO))
                //创建SurfaceView
                //.setRenderViewFactory(SurfaceViewFactory.create())
                .setKeycodeImpl(new KeycodeImplSimulator())
                .build();
        PlayerConfigManager.getInstance().setConfig(build);

        // init
        CacheConfig config = new CacheConfig.Build()
                .setIsEffective(true)
                .setType(CacheType.ROM)
                .setCacheMax(1000)
                .setLog(false)
                .build();
        CacheConfigManager.getInstance().setConfig(config);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
