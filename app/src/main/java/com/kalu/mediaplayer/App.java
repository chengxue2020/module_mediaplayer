package com.kalu.mediaplayer;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lib.kalu.mediaplayer.config.player.PlayerBuilder;
import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.keycode.KeycodeSimulator;

public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        String version = FfmpegLibrary.getVersion();
//        Toast.makeText(getApplicationContext(), version, Toast.LENGTH_SHORT).show();

        // config
        PlayerBuilder build = new PlayerBuilder.Builder()
                .setLog(true)
                .setKernel(PlayerType.KernelType.IJK)
                .setRender(PlayerType.RenderType.TEXTURE_VIEW)
                .setKeycodeApi(new KeycodeSimulator())
                .setBuriedEvent(new Event())
                .setCacheType(PlayerType.CacheType.NONE)
                .setCacheMax(1024)
                .build();
        PlayerManager.getInstance().setConfig(build);
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