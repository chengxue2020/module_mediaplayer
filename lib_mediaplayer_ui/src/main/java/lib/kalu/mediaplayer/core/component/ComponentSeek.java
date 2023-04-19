package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.util.MPLogUtil;

@Keep
public final class ComponentSeek extends RelativeLayout implements ComponentApi {

    private boolean mTouch = false;

    public ComponentSeek(@NonNull Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_seek, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
//            ProgressBar progressBar = findViewById(R.id.module_mediaplayer_component_seek_pb);
//            progressBar.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        }

        try {
            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    MPLogUtil.log("ComponentSeek => onProgressChanged => fromUser = " + fromUser + ", progress = " + progress);
                    if (fromUser) {
                        onSeekPlaying(progress);
                    } else if (!mTouch) {
                        onSeekProgressUpdate(progress, 0);
                    }
                }
            });
        } catch (Exception e) {
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return dispatchEvent(event) || super.dispatchKeyEvent(event);
    }

    private boolean dispatchEvent(@NonNull KeyEvent event) {

        // seekForward
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            boolean live = false;
            if (null != getPlayerApi()) {
                live = getPlayerApi().isLive();
            }
            MPLogUtil.log("ComponentSeek => dispatchKeyEvent => seekForwardDown => live = " + live);
            seekForwardDown(!live);
            return true;
        }
        // seekForward
        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            boolean live = false;
            if (null != getPlayerApi()) {
                live = getPlayerApi().isLive();
            }
            MPLogUtil.log("ComponentSeek => dispatchKeyEvent => seekForwardUp => live = " + live);
            seekForwardUp(!live);
            return true;
        }
        // seekRewind
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            boolean live = false;
            if (null != getPlayerApi()) {
                live = getPlayerApi().isLive();
            }
            MPLogUtil.log("ComponentSeek => dispatchKeyEvent => seekRewindDown => live = " + live);
            seekRewindDown(!live);
            return true;
        }
        // seekRewind
        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            boolean live = false;
            if (null != getPlayerApi()) {
                live = getPlayerApi().isLive();
            }
            MPLogUtil.log("ComponentSeek => dispatchKeyEvent => seekRewindUp => live = " + live);
            seekRewindUp(!live);
            return true;
        }
        return false;
    }

    @Override
    public void callPlayerEvent(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_START:
                MPLogUtil.log("ComponentSeek22[show] => callPlayerEvent => playState = " + playState);
                show();
                break;
            case PlayerType.StateType.STATE_INIT:
            case PlayerType.StateType.STATE_ERROR:
            case PlayerType.StateType.STATE_ERROR_IGNORE:
                MPLogUtil.log("ComponentSeek22[gone] => callPlayerEvent => playState = " + playState);
                gone();
                break;
        }
    }

    @Override
    public void callWindowEvent(int windowState) {
        switch (windowState) {
            case PlayerType.WindowType.FULL:
                MPLogUtil.log("ComponentSeek22[show] => callWindowEvent => windowState = " + windowState);
                try {
                    findViewById(R.id.module_mediaplayer_component_seek_pb).setVisibility(View.VISIBLE);
                } catch (Exception e) {
                }
                break;
            default:
                MPLogUtil.log("ComponentSeek22[gone] => callWindowEvent => windowState = " + windowState);
                try {
                    findViewById(R.id.module_mediaplayer_component_seek_pb).setVisibility(View.GONE);
                } catch (Exception e) {
                }
                break;
        }
    }

    @Override
    public void show() {
        MPLogUtil.log("ComponentSeek => show =>");
        try {
            boolean full = getPlayerApi().isFull();
            if (!full)
                throw new Exception();
            bringToFront();
            findViewById(R.id.module_mediaplayer_component_seek_bg).setVisibility(View.VISIBLE);
            findViewById(R.id.module_mediaplayer_component_seek_sb).setVisibility(View.VISIBLE);
            findViewById(R.id.module_mediaplayer_component_seek_position).setVisibility(View.VISIBLE);
            findViewById(R.id.module_mediaplayer_component_seek_max).setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }
    }

    @Override
    public void gone() {
        MPLogUtil.log("ComponentSeek => gone =>");
        try {
            findViewById(R.id.module_mediaplayer_component_seek_bg).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_seek_sb).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_seek_position).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_seek_max).setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }

    @Override
    public void onUpdateTimeMillis(@NonNull long seek, @NonNull long position, @NonNull long duration) {
        if (mTouch)
            return;
        if (position <= 0 || duration <= 0) return;
        SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
        if (null == seekBar)
            return;
        int v = seekBar.getVisibility();
        if (v != View.VISIBLE)
            return;
        long oldTimeMillis;
        long newTimeMillis = System.currentTimeMillis();
        try {
            Object tag = seekBar.getTag(R.id.module_mediaplayer_component_seek_sb);
            if (null == tag)
                throw new Exception();
            oldTimeMillis = (long) tag;
        } catch (Exception e) {
            oldTimeMillis = newTimeMillis;
            seekBar.setTag(R.id.module_mediaplayer_component_seek_sb, newTimeMillis);
        }
        long i = newTimeMillis - oldTimeMillis;
        if (i > 10000) { // 10s
            seekBar.setTag(R.id.module_mediaplayer_component_seek_sb, null);
            gone();
        } else {
            onSeekProgressUpdate((int) position, (int) duration);
            onSeekTimeUpdate((int) position, (int) duration);
        }
    }

    @Override
    public void seekForwardDown(boolean enable) {
        if (!enable) return;
        SeekBar seek = findViewById(R.id.module_mediaplayer_component_seek_sb);
        if (null == seek) return;
        int max = seek.getMax();
        int progress = seek.getProgress();
        if (progress >= max) return;
        mTouch = true;
        int next = progress + Math.abs(max) / 200;
        if (next > max) {
            next = max;
        }
        show();
        onSeekProgressUpdate(next, 0);
    }

    @Override
    public void seekForwardUp(boolean enable) {
        if (!enable) return;
        SeekBar seek = findViewById(R.id.module_mediaplayer_component_seek_sb);
        if (null == seek) return;
        int max = seek.getMax();
        int progress = seek.getProgress();
        if (max <= 0 || progress <= 0 && progress > max) return;
        mTouch = false;
        onSeekPlaying(progress);
    }

    @Override
    public void seekRewindDown(boolean enable) {
        if (!enable) return;
        SeekBar seek = findViewById(R.id.module_mediaplayer_component_seek_sb);
        if (null == seek) return;
        int max = seek.getMax();
        int progress = seek.getProgress();
        if (progress >= max) return;
        mTouch = true;
        int next = progress - Math.abs(max) / 200;
        if (next < 0) {
            next = 0;
        }
        show();
        onSeekProgressUpdate(next, 0);
    }

    @Override
    public void seekRewindUp(boolean enable) {
        if (!enable) return;
        SeekBar seek = findViewById(R.id.module_mediaplayer_component_seek_sb);
        if (null == seek) return;
        int max = seek.getMax();
        int progress = seek.getProgress();
        if (max <= 0 || progress <= 0 && progress > max) return;
        mTouch = false;
        onSeekPlaying(progress);
    }

    @Override
    public void onSeekProgressUpdate(@NonNull int position, @NonNull int duration) {
        // 1
        SeekBar seek = findViewById(R.id.module_mediaplayer_component_seek_sb);
        if (null != seek) {
            int v = seek.getVisibility();
            if (v == View.VISIBLE) {
                seek.setProgress(position);
                seek.setSecondaryProgress(position);
                if (duration > 0) {
                    seek.setMax(duration);
                }
            }
        }
        // 2
        ProgressBar progress = findViewById(R.id.module_mediaplayer_component_seek_pb);
        if (null == progress) {
            progress.setProgress(position);
            progress.setSecondaryProgress(position);
            if (duration > 0) {
                progress.setMax(duration);
            }
        }
    }

    @Override
    public void onSeekTimeUpdate(@NonNull int position, @NonNull int duration) {
        // ms => s
        long c = position / 1000;
        long c1 = c / 60;
        long c2 = c % 60;
        StringBuilder builderPosition = new StringBuilder();
        if (c1 < 10) {
            builderPosition.append("0");
        }
        builderPosition.append(c1);
        builderPosition.append(":");
        if (c2 < 10) {
            builderPosition.append("0");
        }
        builderPosition.append(c2);
        String strPosition = builderPosition.toString();

        // ms => s
        StringBuilder builderDuration = new StringBuilder();
        long d = duration / 1000;
        long d1 = d / 60;
        long d2 = d % 60;
        if (d1 < 10) {
            builderDuration.append("0");
        }
        builderDuration.append(d1);
        builderDuration.append(":");
        if (d2 < 10) {
            builderDuration.append("0");
        }
        builderDuration.append(d2);
        String strDuration = builderDuration.toString();

        TextView viewMax = findViewById(R.id.module_mediaplayer_component_seek_max);
        viewMax.setText(strDuration);
        TextView viewPosition = findViewById(R.id.module_mediaplayer_component_seek_position);
        viewPosition.setText(strPosition);
    }

    @Override
    public void onSeekPlaying(@NonNull int position) {
        if (null != getPlayerApi()) {
            getPlayerApi().seekTo(true, position);
        }
    }
}
