package lib.kalu.mediaplayer.core.view;

import android.content.Context;
import android.graphics.Bitmap;

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

import lib.kalu.mediaplayer.config.builder.BundleBuilder;
import lib.kalu.mediaplayer.config.builder.PlayerBuilder;
import lib.kalu.mediaplayer.config.player.PlayerConfigManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.controller.base.ControllerLayout;

/**
 * revise: 播放器基础属性获取和设置属性接口
 */
public interface PlayerApi {

    default void start(@NonNull String url) {
        BundleBuilder.Builder builder = new BundleBuilder.Builder();
        BundleBuilder build = builder.build();
        start(build, url);
    }

    default void startLive(@NonNull String url) {
        BundleBuilder.Builder builder = new BundleBuilder.Builder();
        builder.setLive(true);
        builder.setLoop(false);
        BundleBuilder build = builder.build();
        start(build, url);
    }

    default void startLoop(@NonNull String url) {
        BundleBuilder.Builder builder = new BundleBuilder.Builder();
        builder.setLive(false);
        builder.setLoop(true);
        BundleBuilder build = builder.build();
        start(build, url);
    }

    default void startLoop(@NonNull long seek, @NonNull long max, @NonNull String url) {
        BundleBuilder.Builder builder = new BundleBuilder.Builder();
        builder.setLive(false);
        builder.setLoop(true);
        builder.setSeek(seek);
        builder.setMax(max);
        BundleBuilder build = builder.build();
        start(build, url);
    }

    void start(@NonNull BundleBuilder builder, @NonNull String url);

    void create(@NonNull BundleBuilder builder);

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

    boolean isRelease();

    long getSeek();

    long getMax();

    String getUrl();

    int getBufferedPercentage();

    default void seekTo(@NonNull boolean force) {
        boolean live = isLive();
        boolean looping = isLooping();
        long max = getMax();
        long seek = getSeek();
        boolean release = isRelease();
        boolean mute = isMute();
        BundleBuilder.Builder builder = new BundleBuilder.Builder();
        builder.setMax(max);
        builder.setSeek(seek);
        builder.setLoop(looping);
        builder.setLive(live);
        builder.setMute(mute);
        builder.setRelease(release);
        BundleBuilder build = builder.build();
        seekTo(force, build);
    }

    default void seekTo(@NonNull long seek) {
        seekTo(false, seek, getMax(), isLooping());
    }

    default void seekTo(@NonNull long seek, @NonNull long max) {
        seekTo(false, seek, max, isLooping());
    }

    default void seekTo(@NonNull boolean force, @NonNull long seek) {
        seekTo(force, seek, getMax(), isLooping());
    }

    default void seekTo(@NonNull boolean force, @NonNull long seek, @NonNull long max, @NonNull boolean loop) {
        boolean live = isLive();
        boolean release = isRelease();
        BundleBuilder.Builder builder = new BundleBuilder.Builder();
        builder.setMax(max);
        builder.setSeek(seek);
        builder.setLoop(loop);
        builder.setLive(live);
        builder.setRelease(release);
        BundleBuilder build = builder.build();
        seekTo(force, build);
    }

    void seekTo(@NonNull boolean force, @NonNull BundleBuilder builder);

    boolean seekForward(@NonNull boolean callback);

    boolean seekRewind(boolean callback);

    boolean isLive();

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

    void startHanlder();

    void clearHanlder();

    /*********/

    void playEnd();

    /*********/

    void setKernel(@PlayerType.KernelType.Value int v);

    void setRender(@PlayerType.RenderType int v);

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

    /************/

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

    /*************************/

    void enableExternalMusic(boolean enable, boolean release);

    void enableExternalMusic(boolean enable, boolean release, boolean auto);

    void setExternalMusic(@NonNull BundleBuilder bundle);

    boolean isExternalMusicAuto();
}
