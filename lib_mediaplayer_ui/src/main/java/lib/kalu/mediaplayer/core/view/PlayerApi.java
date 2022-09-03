package lib.kalu.mediaplayer.core.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.BoolRes;
import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.controller.base.ControllerLayout;
import lib.kalu.mediaplayer.util.MediaLogUtil;

/**
 * revise: 播放器基础属性获取和设置属性接口
 */
public interface PlayerApi {

    default void saveBundle(@NonNull Context context, @NonNull String url, @NonNull long position, @NonNull long duration) {

        if (null == url || url.length() <= 0)
            return;
        try {
            JSONObject object = new JSONObject();
            object.putOpt("url", url);
            object.putOpt("position", position);
            object.putOpt("duration", duration);
            String s = object.toString();
            setCache(context, "save_bundle", s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    default void start(@NonNull String url) {
        start(0, 0, false, true, url);
    }

    default void start(@NonNull long seek, @NonNull String url) {
        start(seek, 0, false, true, url);
    }

    default void start(@NonNull long seek, @NonNull long max, @NonNull boolean loop, @NonNull String url) {
        start(seek, max, loop, true, url);
    }

    void start(@NonNull long seek, @NonNull long max, @NonNull boolean loop, @NonNull boolean autoRelease, @NonNull String url);

    void create();

    default void pause() {
        pause(false);
    }

    void toggle();

    void pause(boolean auto);

    void resume();

    void close();

    void repeat();

    long getDuration();

    long getPosition();

    boolean isLooping();

    boolean isAutoRelease();

    long getSeek();

    long getMax();

    String getUrl();

    default void toggleMusicExtra() {
        toggleMusicExtra(false);
    }

    void toggleMusicExtra(boolean auto);

    void toggleMusicDefault();

    void toggleMusicDefault(boolean musicPrepare);

    void toggleMusic();

    boolean hasMusicExtra();

    void updateMusic(@NonNull String musicPath, @NonNull boolean musicPlay, @NonNull boolean musicLoop, @NonNull boolean musicSeek);

    /**
     * 获取当前缓冲百分比
     *
     * @return 百分比
     */
    int getBufferedPercentage();

    default void seekTo(@NonNull long seek) {
        seekTo(false, seek, 0, false);
    }

    default void seekTo(@NonNull boolean force, @NonNull long seek) {
        seekTo(force, seek, 0, false);
    }

    void seekTo(@NonNull boolean force, @NonNull long seek, @NonNull long max, @NonNull boolean loop);

    boolean seekForward(@NonNull boolean callback);

    boolean seekRewind(boolean callback);

    /**
     * 是否处于播放状态
     *
     * @return 是否处于播放状态
     */
    boolean isPlaying();

    boolean isFull();

    void startFull();

    void stopFull();

    boolean isFloat();

    void startFloat();

    void stopFloat();

    boolean isMute();

    void setScaleType(@PlayerType.ScaleType.Value int scaleType);

    void setSpeed(float speed);

    float getSpeed();

    long getTcpSpeed();

    void setMirrorRotation(boolean enable);

    Bitmap doScreenShot();

    int[] getVideoSize();

    void setRotation(float rotation);

    ControllerLayout getControlLayout();

    void showReal();

    void goneReal();

    void checkReal();

    default void release() {
        release(false);
    }

    void release(@NonNull boolean onlyHandle);

    void startLoop();

    void clearLoop();

    /*********/

    void playEnd();

    /*********/

    void callPlayerState(@PlayerType.StateType.Value int playerState);

    void callWindowState(@PlayerType.WindowType.Value int windowState);

    /***********/

    default boolean setCache(Context context, String key, String value) {
        FileOutputStream out = null;
        BufferedWriter writer = null;

        boolean result;
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("user@");
            String string1 = key.toString();
            String toLowerCase = string1.toLowerCase();
            builder.append(toLowerCase);
            builder.append("@");
            String string = builder.toString();
            out = context.openFileOutput(string, 0);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(value);
            writer.flush();
            result = true;
        } catch (Exception var22) {
            result = false;
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (Exception var21) {
                }
            }

            if (null != writer) {
                try {
                    writer.close();
                } catch (Exception var20) {
                }
            }

        }

        return result;
    }

    default String getCache(Context context, String key) {
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();

        try {
            StringBuilder builder = new StringBuilder();
            builder.append("user@");
            String toLowerCase = key.toLowerCase();
            builder.append(toLowerCase);
            builder.append("@");
            String filename = builder.toString();
            String absolutePath = context.getFilesDir().getAbsolutePath();
            File file = new File(absolutePath + File.separator + filename);
            if (null != file && file.exists()) {
                in = context.openFileInput(filename);
                reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
            } else {
                content.append("");
            }
        } catch (Exception var23) {
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (Exception var22) {
                }
            }

            if (null != reader) {
                try {
                    reader.close();
                } catch (Exception var21) {
                }
            }

        }

        String value = content.toString();
        return value;
    }
}
