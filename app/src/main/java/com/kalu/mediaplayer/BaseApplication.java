package com.kalu.mediaplayer;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lib.kalu.mediaplayer.config.cache.CacheConfig;
import lib.kalu.mediaplayer.config.cache.CacheConfigManager;
import lib.kalu.mediaplayer.config.cache.CacheType;
import lib.kalu.mediaplayer.config.player.PlayerConfig;
import lib.kalu.mediaplayer.config.player.PlayerConfigManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.keycode.KeycodeSimulator;
import lib.kalu.mediaplayer.util.MediaLogUtil;

public class BaseApplication extends MultiDexApplication {

    private static BaseApplication instance;

    public static synchronized BaseApplication getInstance() {
        if (null == instance) {
            instance = new BaseApplication();
        }
        return instance;
    }

    public BaseApplication() {
    }

    /**
     * 这个最先执行
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    /**
     * 程序启动的时候执行
     */
    @Override
    public void onCreate() {
        Log.d("Application", "onCreate");
        super.onCreate();
        instance = this;

        MediaLogUtil.setIsLog(true);
        // init
        PlayerConfig build = PlayerConfig.newBuilder()
                //设置视频全局埋点事件
                .setBuriedPointEvent(new BuriedPointEventImpl())
                //调试的时候请打开日志，方便排错
                .setLogEnabled(true)
                .setKernel(PlayerType.KernelType.IJK)
                .setRender(PlayerType.RenderType.SURFACE_VIEW)
                .setKeycode(new KeycodeSimulator())
                .build();
        PlayerConfigManager.getInstance().setConfig(build);

        // init
        CacheConfig config = new CacheConfig.Build()
                .setIsEffective(true)
                .setCacheType(CacheType.DEFAULT)
                .setCacheMaxMB(1024)
                .setCacheDir("temp")
                .setLog(true)
                .build();
        CacheConfigManager.getInstance().setConfig(getApplicationContext(), config);
    }

    /**
     * 程序终止的时候执行
     */
    @Override
    public void onTerminate() {
        Log.d("Application", "onTerminate");
        super.onTerminate();
    }

    /**
     * 低内存的时候执行
     */
    @Override
    public void onLowMemory() {
        Log.d("Application", "onLowMemory");
        super.onLowMemory();
    }

    /**
     * HOME键退出应用程序
     * 程序在内存清理的时候执行
     */
    @Override
    public void onTrimMemory(int level) {
        Log.d("Application", "onTrimMemory");
        super.onTrimMemory(level);
    }

    /**
     * onConfigurationChanged
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d("Application", "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }


    public static String getModelFilePath(Context context, String modelName) {
        copyFileIfNeed(context, modelName);
        return context.getFilesDir().getAbsolutePath() + File.separator + modelName;
    }

    /**
     * 拷贝asset下的文件到context.getFilesDir()目录下
     */
    private static void copyFileIfNeed(Context context, String modelName) {
        InputStream is = null;
        OutputStream os = null;
        try {
            // 默认存储在data/data/<application name>/file目录下
            File modelFile = new File(context.getFilesDir(), modelName);
            is = context.getAssets().open(modelName);
            if (modelFile.length() == is.available()) {
                return;
            }
            os = new FileOutputStream(modelFile);
            byte[] buffer = new byte[1024];
            int length = is.read(buffer);
            while (length > 0) {
                os.write(buffer, 0, length);
                length = is.read(buffer);
            }
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}