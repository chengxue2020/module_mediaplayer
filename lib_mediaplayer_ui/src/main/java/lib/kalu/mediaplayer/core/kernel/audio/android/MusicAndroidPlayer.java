package lib.kalu.mediaplayer.core.kernel.audio.android;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.exoplayer2.util.ExoLogUtil;
import lib.kalu.mediaplayer.core.kernel.audio.OnMusicPlayerChangeListener;
import lib.kalu.mediaplayer.core.kernel.audio.MusicKernelApi;

@Keep
public final class MusicAndroidPlayer implements MusicKernelApi {

    private android.media.MediaPlayer mAndroidPlayer;
    private MediaPlayer.OnInfoListener mOnInfoListener;
    private MediaPlayer.OnErrorListener mOnErrorListener;
    private MediaPlayer.OnCompletionListener mOnCompletionListener;
    //
    private OnMusicPlayerChangeListener mOnMusicPlayerChangeListener;

    public MusicAndroidPlayer() {
    }

    @Override
    public void setMusicListener(@NonNull OnMusicPlayerChangeListener listener) {
        mOnMusicPlayerChangeListener = listener;
    }

    @Override
    public void createDecoder(@NonNull Context context) {
        if (null != mAndroidPlayer) {
            release();
        }
        if (null == mAndroidPlayer) {
            mAndroidPlayer = new android.media.MediaPlayer();
            setVolume(1F);
            setLooping(false);
            setSeekParameters();
        }
    }

    @Override
    public void setDataSource(@NonNull Context context, @NonNull String musicUrl) {
        // 2
        createDecoder(context);
        // 3
        if (null != mAndroidPlayer) {
            ExoLogUtil.log("MusicAndroidPlayer => setDataSource => musicUrl = " + musicUrl + ", mAndroidPlayer = " + mAndroidPlayer);
            try {
                mAndroidPlayer.setDataSource(context, Uri.parse(musicUrl));
                mAndroidPlayer.prepare();
            } catch (Exception e) {
                release();
            }
        }
    }

    @Override
    public void start() {
        // 1
        removeListener();
        // 2
        addListener();
        // 3
        setVolume(1F);
        // 4
        if (null != mAndroidPlayer) {
            ExoLogUtil.log("MusicAndroidPlayer => start => mAndroidPlayer = " + mAndroidPlayer);
            mAndroidPlayer.start();
        }
    }

    @Override
    public void stop() {
        if (null != mAndroidPlayer) {
            ExoLogUtil.log("MusicAndroidPlayer => stop => mAndroidPlayer = " + mAndroidPlayer);
            mAndroidPlayer.stop();
        }
    }

    @Override
    public void pause() {
        if (null != mAndroidPlayer) {
            ExoLogUtil.log("MusicAndroidPlayer => pause => mAndroidPlayer = " + mAndroidPlayer);
            mAndroidPlayer.pause();
        }
    }

    @Override
    public void release() {
        removeListener();
        if (null != mAndroidPlayer) {
            ExoLogUtil.log("MusicAndroidPlayer => release => mAndroidPlayer = " + mAndroidPlayer);
            mAndroidPlayer.release();
            mAndroidPlayer = null;
        }
    }

    @Override
    public void addListener() {
        // 1
        mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (null != mOnMusicPlayerChangeListener) {
                    mOnMusicPlayerChangeListener.onComplete();
                }
            }
        };
        mAndroidPlayer.setOnCompletionListener(mOnCompletionListener);
        // 2
        mOnErrorListener = new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (null != mOnMusicPlayerChangeListener) {
                    mOnMusicPlayerChangeListener.onError();
                }
                return false;
            }
        };
        mAndroidPlayer.setOnErrorListener(mOnErrorListener);
//        // 3
//        mOnInfoListener = new MediaPlayer.OnInfoListener() {
//            @Override
//            public boolean onInfo(MediaPlayer mp, int what, int extra) {
//                // what：701 ---  加载中、702 --- 加载完成。
//                if (what == 702) {
//
//                }
//                return false;
//            }
//        };
        mAndroidPlayer.setOnInfoListener(mOnInfoListener);
    }

    @Override
    public void removeListener() {
        if (null != mOnMusicPlayerChangeListener) {
            setMusicListener(null);
            mOnMusicPlayerChangeListener = null;
        }
        if (null != mOnCompletionListener) {
            if (null != mAndroidPlayer) {
                mAndroidPlayer.setOnCompletionListener(null);
            }
            mOnCompletionListener = null;
        }
        if (null != mOnErrorListener) {
            if (null != mAndroidPlayer) {
                mAndroidPlayer.setOnErrorListener(null);
            }
            mOnErrorListener = null;
        }
    }

    @Override
    public void setLooping(boolean v) {
        if (null != mAndroidPlayer) {
            ExoLogUtil.log("MusicAndroidPlayer => setLooping => v = " + v + ", mAndroidPlayer = " + mAndroidPlayer);
            mAndroidPlayer.setLooping(v);
        }
    }

    @Override
    public void setVolume(float v) {
        if (null != mAndroidPlayer) {
            ExoLogUtil.log("MusicAndroidPlayer => setVolume => v = " + v + ", mAndroidPlayer = " + mAndroidPlayer);
            mAndroidPlayer.setVolume(v, v);
        }
    }

    @Override
    public boolean isPlaying() {
        ExoLogUtil.log("MusicAndroidPlayer => isPlaying => mAndroidPlayer = " + mAndroidPlayer);
        if (mAndroidPlayer == null)
            return false;
        return mAndroidPlayer.isPlaying();
    }

    @Override
    public void seekTo(long v) {
        if (null != mAndroidPlayer) {
            ExoLogUtil.log("MusicAndroidPlayer => seekTo => v = " + v + ", mAndroidPlayer = " + mAndroidPlayer);
            long duration = getDuration();
            ExoLogUtil.log("MusicAndroidPlayer => seekTo =>  duration = " + duration);
            if (v < duration) {
                mAndroidPlayer.seekTo((int) v);
            }
        }
    }

    @Override
    public long getDuration() {
        ExoLogUtil.log("MusicAndroidPlayer => getDuration =>  mAndroidPlayer = " + mAndroidPlayer);
        if (mAndroidPlayer == null)
            return 0L;
        return mAndroidPlayer.getDuration();
    }
}
