package com.kalu.mediaplayer;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lib.kalu.mediaplayer.config.config.ConfigBuilder;
import lib.kalu.mediaplayer.config.config.ConfigManager;
import lib.kalu.mediaplayer.config.config.ConfigType;
import lib.kalu.mediaplayer.keycode.KeycodeSimulator;

public class Application extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        // config
        ConfigBuilder build = new ConfigBuilder.Builder()
                .setLog(true)
                .setKernel(ConfigType.KernelType.IJK)
                .setRender(ConfigType.RenderType.SURFACE_VIEW)
                .setKeycodeApi(new KeycodeSimulator())
                .setBuriedEvent(new BuriedPointEventImpl())
                .setCacheType(ConfigType.CacheType.DEFAULT)
                .setCacheMax(1024)
                .setCacheDir("temp")
                .build();
        ConfigManager.getInstance().setConfig(build);
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
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }
}