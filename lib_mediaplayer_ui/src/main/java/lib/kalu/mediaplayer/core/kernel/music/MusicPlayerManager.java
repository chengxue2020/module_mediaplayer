package lib.kalu.mediaplayer.core.kernel.music;


import android.media.MediaPlayer;
import android.net.Uri;

import androidx.annotation.NonNull;

public final class MusicPlayerManager {

    private static android.media.MediaPlayer mMediaPlayer = null;

    private static void check(@NonNull String path) {

        // 1
        if (null == mMediaPlayer) {
            mMediaPlayer = new android.media.MediaPlayer();
        }

        // 2
        try {
            mMediaPlayer.stop();
            mMediaPlayer.setLooping(false);
            mMediaPlayer.setVolume(1f, 1f);
            mMediaPlayer.setDataSource(Uri.parse(path).toString());
            mMediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setVolume(float v) {
        if (null == mMediaPlayer)
            return;
        mMediaPlayer.setVolume(v, v);
//        if (v <= 0) {
//            mMediaPlayer.stop();
//        }
    }

    public static void release() {
        if (null == mMediaPlayer)
            return;
        mMediaPlayer.setOnSeekCompleteListener(null);
        mMediaPlayer.setOnPreparedListener(null);
        mMediaPlayer.setLooping(false);
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    public static void start(@NonNull long msec, @NonNull String path, @NonNull android.media.MediaPlayer.OnPreparedListener listener) {

        if (null == path || path.length() < 0)
            return;

        // 1
        check(path);

        // 2
        mMediaPlayer.setVolume(1f, 1f);
        mMediaPlayer.start();
        if (msec > 0) {
            mMediaPlayer.seekTo((int) msec);
            mMediaPlayer.setOnPreparedListener(listener);
        } else {
            listener.onPrepared(null);
        }
    }

    public static void restart() {
        restart(0, null);
    }

    public static void restart(@NonNull long msec, @NonNull MediaPlayer.OnSeekCompleteListener listener) {
        if (null == mMediaPlayer)
            return;
        mMediaPlayer.setVolume(1f, 1f);
        mMediaPlayer.start();

        if (msec > 0 && null != listener) {
            mMediaPlayer.seekTo((int) msec);
            mMediaPlayer.setOnSeekCompleteListener(listener);
        }
    }
}
