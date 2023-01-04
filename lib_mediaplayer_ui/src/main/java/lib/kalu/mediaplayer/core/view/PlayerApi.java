package lib.kalu.mediaplayer.core.view;

import android.content.Context;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.core.controller.base.ControllerLayout;
import lib.kalu.mediaplayer.util.MPLogUtil;

/**
 * revise: 播放器基础属性获取和设置属性接口
 */
public interface PlayerApi extends PlayerApiBase, PlayerApiComponent, PlayerApiSuface, PlayerApiExternal {

    default void start(@NonNull String url) {
        StartBuilder.Builder builder = new StartBuilder.Builder();
        StartBuilder build = builder.build();
        start(build, url);
    }

    default void startSeek(@NonNull long seek, @NonNull String url) {
        StartBuilder.Builder builder = new StartBuilder.Builder();
        builder.setSeek(seek);
        StartBuilder build = builder.build();
        start(build, url);
    }

    default void startLive(@NonNull String url) {
        StartBuilder.Builder builder = new StartBuilder.Builder();
        builder.setLive(true);
        builder.setLoop(false);
        StartBuilder build = builder.build();
        start(build, url);
    }

    default void startLoop(@NonNull String url) {
        StartBuilder.Builder builder = new StartBuilder.Builder();
        builder.setLive(false);
        builder.setLoop(true);
        StartBuilder build = builder.build();
        start(build, url);
    }

    default void startLoop(@NonNull long seek, @NonNull long max, @NonNull String url) {
        StartBuilder.Builder builder = new StartBuilder.Builder();
        builder.setLive(false);
        builder.setLoop(true);
        builder.setSeek(seek);
        builder.setMax(max);
        StartBuilder build = builder.build();
        start(build, url);
    }

    /**********/

    void setVolume(float v1, float v2);

    void setMute(boolean v);

    void start(@NonNull StartBuilder builder, @NonNull String url);

    default void toggle() {
        toggle(false);
    }

    void toggle(boolean cleanHandler);

    /*********************/

    void stopKernel(@NonNull boolean call);

    void pauseKernel(@NonNull boolean call);

    void releaseKernel();

    default void pause() {
        pause(false);
    }

    void pause(boolean auto);

    /*********************/

    default void resume() {
        resume(true);
    }

    void resume(boolean call);

    void close();

    void repeat();

    long getDuration();

    long getPosition();

    boolean isLooping();

    boolean isInvisibleStop();

    boolean isInvisibleIgnore();

    boolean isInvisibleRelease();

    long getSeek();

    long getMax();

    String getUrl();

    int getBufferedPercentage();

    default void seekTo(@NonNull boolean force) {
        StartBuilder.Builder builder = new StartBuilder.Builder();
        builder.setMax(getMax());
        builder.setSeek(getSeek());
        builder.setLoop(isLooping());
        builder.setLive(isLive());
        builder.setMute(isMute());
        builder.setInvisibleStop(isInvisibleStop());
        builder.setInvisibleIgnore(isInvisibleIgnore());
        builder.setInvisibleRelease(isInvisibleRelease());
        StartBuilder build = builder.build();
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
        StartBuilder.Builder builder = new StartBuilder.Builder();
        builder.setMax(max);
        builder.setSeek(seek);
        builder.setLoop(loop);
        builder.setLive(isLive());
        builder.setInvisibleStop(isInvisibleStop());
        builder.setInvisibleIgnore(isInvisibleIgnore());
        builder.setInvisibleRelease(isInvisibleRelease());
        StartBuilder build = builder.build();
        seekTo(force, build);
    }

    void seekTo(@NonNull boolean force, @NonNull StartBuilder builder);

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

    String getTcpSpeed();

    void setMirrorRotation(boolean enable);

    int[] getVideoSize();

    void setRotation(float rotation);

    ControllerLayout getControlLayout();

    void showReal();

    void hideReal();

    void checkReal();

    void addRender();

    void delRender();

    void releaseRender();

    default void release() {
        release(false);
    }

    void release(@NonNull boolean onlyHandle);

//    void startTimer();

//    void clearTimer();

    /*********/

    void playEnd();

    /*********/

    void setKernel(@PlayerType.KernelType.Value int v);

    void setRender(@PlayerType.RenderType int v);

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
            MPLogUtil.log(e.getMessage(), e);
        }
    }
}
