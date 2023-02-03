package lib.kalu.vlc.widget;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.interfaces.AbstractVLCEvent;

import lib.kalu.vlc.util.VlcLogUtil;

public final class VlcPlayer extends org.videolan.libvlc.MediaPlayer {

    public VlcPlayer(Context context) {
        super(new LibVLC(context));
        VlcLogUtil.log("VlcPlayer =>");
    }

    public void setLooping(boolean v) {
        try {
        } catch (Exception e) {
        }
    }

    public void setVolume(float v1, float v2) {
        try {
            super.setVolume((int) ((v1 + v2) * 100 / 2));
        } catch (Exception e) {
        }
    }

    public int getVolume() {
        try {
            return super.getVolume();
        } catch (Exception e) {
            return 1;
        }
    }

    public void setSurface(Surface surface) {
        try {
            getVLCVout().setVideoSurface(surface, null);
            VlcLogUtil.log("VlcPlayer => setSurface => surface = " + surface);
        } catch (Exception e) {
            VlcLogUtil.log("VlcPlayer => setSurface => surface = " + surface);
        }
    }

    public void setDisplay(SurfaceHolder sh) {
        try {
            getVLCVout().setVideoSurface(sh.getSurface(), sh);
            VlcLogUtil.log("VlcPlayer => setDisplay => sh = " + sh);
        } catch (Exception e) {
            VlcLogUtil.log("VlcPlayer => setDisplay => sh = " + sh);
        }
    }

    public void setDataSource(Uri uri) {
        setDataSource(uri, true);
    }

    public void setDataSource(String path) {
        setDataSource(Uri.parse(path), true);
    }

    public void setDataSource(Uri uri, boolean playWhenReady) {
        try {
            Media media = new Media(getLibVLC(), uri);
            if (!playWhenReady) {
                media.addOption(":video-paused");
            }
            setMedia(media);
            VlcLogUtil.log("VlcPlayer => setDataSource => uri = " + uri);
            setEventListener(new MediaPlayer.EventListener() {
                @Override
                public void onEvent(MediaPlayer.Event event) {
                    VlcLogUtil.log("VlcPlayer => onEvent => code = " + event.type);
                    // 解析开始
                    if (event.type == MediaPlayer.Event.MediaChanged) {
                        if (null != mL) {
                            mL.onStart();
                        }
                    }
                    // 首帧画面
                    else if (event.type == MediaPlayer.Event.Vout) {
                        if (null != mL) {
                            mL.onPlay();
                        }
                    }
                    // 播放完成
                    else if (event.type == MediaPlayer.Event.EndReached) {
                        if (null != mL) {
                            mL.onEnd();
                        }
                    }
                    // 错误
                    else if (event.type == MediaPlayer.Event.Stopped) {
                        if (null != mL) {
                            mL.onError();
                        }
                    }
                }
            });
        } catch (Exception e) {
        }
    }

    public void prepare() {
        try {
            super.play();
        } catch (Exception e) {
        }
    }

    public boolean isPlaying() {
        try {
            return super.isPlaying();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected synchronized void setEventListener(AbstractVLCEvent.Listener<Event> listener) {
        super.setEventListener(listener);
        VlcLogUtil.log("VlcPlayer => setEventListener => listener = " + listener);
    }

    private OnVlcInfoChangeListener mL;

    public void setOnVlcInfoChangeListener(OnVlcInfoChangeListener l) {
        mL = l;
    }
}
