package lib.kalu.mediaplayer.core.view;

import android.content.Context;
import android.graphics.Bitmap;
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
        start(false, 0, 0, false, true, url);
    }

    default void start(@NonNull boolean release, @NonNull String url) {
        start(release, 0, 0, false, true, url);
    }

    default void start(@NonNull long seek, @NonNull String url) {
        start(false, seek, 0, false, true, url);
    }

    default void start(@NonNull long seek, @NonNull long max, @NonNull boolean loop, @NonNull String url) {
        start(false, seek, max, loop, true, url);
    }

    default void start(@NonNull long seek, @NonNull long max, @NonNull boolean loop, @NonNull boolean autoRelease, @NonNull String url) {
        start(false, seek, max, loop, autoRelease, url);
    }

    void start(@NonNull boolean release, @NonNull long seek, @NonNull long max, @NonNull boolean loop, @NonNull boolean autoRelease, @NonNull String url);

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

    void stopMusic();

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

    /**
     * 是否处于播放状态
     *
     * @return 是否处于播放状态
     */
    boolean isPlaying();

    void startFullScreen();

    void stopFullScreen();

    boolean isFullScreen();

    boolean isMute();

    void setScaleType(@PlayerType.ScaleType.Value int scaleType);

    void setSpeed(float speed);

    float getSpeed();

    long getTcpSpeed();

    void setMirrorRotation(boolean enable);

    Bitmap doScreenShot();

    int[] getVideoSize();

    void setRotation(float rotation);

    void startTinyScreen();

    void stopTinyScreen();

    boolean isTinyScreen();

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

    /**
     * 向Controller设置播放状态，用于控制Controller的ui展示
     * 这里使用注解限定符，不要使用1，2这种直观数字，不方便知道意思
     * 播放状态，主要是指播放器的各种状态
     * -1               播放错误
     * 0                播放未开始
     * 1                播放准备中
     * 2                播放准备就绪
     * 3                正在播放
     * 4                暂停播放
     * 5                正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够后恢复播放)
     * 6                暂停缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停
     * 7                播放完成
     * 8                开始播放中止
     */
    void callState(@PlayerType.StateType.Value int state);


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
