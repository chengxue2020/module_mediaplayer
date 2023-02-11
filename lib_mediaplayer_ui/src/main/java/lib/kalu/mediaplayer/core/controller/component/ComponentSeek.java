package lib.kalu.mediaplayer.core.controller.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.controller.base.ControllerLayout;
import lib.kalu.mediaplayer.core.controller.base.ControllerWrapper;
import lib.kalu.mediaplayer.core.controller.impl.ComponentApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

/**
 * description: 底部控制栏视图
 * created by kalu on 2021/11/23
 */
@Keep
public class ComponentSeek extends RelativeLayout implements ComponentApi {

    private long mTimeMillis = 0L;
    private boolean mTouch = false;
    protected boolean mShowBottomPB = false;
    protected ControllerWrapper mControllerWrapper;

    public ComponentSeek(@NonNull Context context) {
        super(context);
        init();
    }

    public ComponentSeek(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ComponentSeek(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return dispatchEvent(event) || super.dispatchKeyEvent(event);
    }

    private boolean dispatchEvent(@NonNull KeyEvent event) {

        // seekForward
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            boolean live = false;
            if (null != mControllerWrapper) {
                live = mControllerWrapper.isLive();
            }
            MPLogUtil.log("ComponentSeek => dispatchKeyEvent => seekForwardDown => live = " + live);
            seekForwardDown(!live);
            return true;
        }
        // seekForward
        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            boolean live = false;
            if (null != mControllerWrapper) {
                live = mControllerWrapper.isLive();
            }
            MPLogUtil.log("ComponentSeek => dispatchKeyEvent => seekForwardUp => live = " + live);
            seekForwardUp(!live);
            return true;
        }
        // seekRewind
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            boolean live = false;
            if (null != mControllerWrapper) {
                live = mControllerWrapper.isLive();
            }
            MPLogUtil.log("ComponentSeek => dispatchKeyEvent => seekRewindDown => live = " + live);
            seekRewindDown(!live);
            return true;
        }
        // seekRewind
        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            boolean live = false;
            if (null != mControllerWrapper) {
                live = mControllerWrapper.isLive();
            }
            MPLogUtil.log("ComponentSeek => dispatchKeyEvent => seekRewindUp => live = " + live);
            seekRewindUp(!live);
            return true;
        }
        return false;
    }

    public void enableBottomPB(boolean enable) {
        this.mShowBottomPB = enable;
    }

    private void init() {
        // step1
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_seek, this, true);

        // step2, 5.1以下系统SeekBar高度需要设置成WRAP_CONTENT
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
//            ProgressBar progressBar = findViewById(R.id.module_mediaplayer_component_seek_pb);
//            progressBar.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        }

        // step3
        SeekBar sb = findViewById(R.id.module_mediaplayer_component_seek_sb);
        if (null != sb) {
            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        }
    }

    @Override
    public void attach(@NonNull ControllerWrapper controllerWrapper) {
        mControllerWrapper = controllerWrapper;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {
    }

    @Override
    public void onPlayStateChanged(int playState) {
        boolean isLive = mControllerWrapper.isLive();
        boolean isFull = mControllerWrapper.isFull();
        switch (playState) {
            case PlayerType.StateType.STATE_LOADING_STOP:
                MPLogUtil.log("ComponentSeek22[show] => playState = " + playState + ", isLive = " + isLive + ", isFull = " + isFull);
                if (!isLive && isFull) {
                    show();
                }
                break;
            case PlayerType.StateType.STATE_COMPONENT_SEEK_SHOW:
                MPLogUtil.log("ComponentSeek22[show] => playState = " + playState + ", isLive = " + isLive + ", isFull = " + isFull);
                show();
                break;
            case PlayerType.StateType.STATE_LOADING_START:
                MPLogUtil.log("ComponentSeek22[gone] => playState = " + playState + ", isLive = " + isLive + ", isFull = " + isFull);
                gone();
                break;
        }
    }

    @Override
    public void onWindowStateChanged(int windowState) {
        boolean isLive = mControllerWrapper.isLive();
        boolean isFull = mControllerWrapper.isFull();
        switch (windowState) {
            case PlayerType.WindowType.FULL:
                MPLogUtil.log("ComponentSeek22[show] => onWindowStateChanged => windowState = " + windowState + ", isLive = " + isLive + ", isFull = " + isFull);
                if (!isLive && isFull) {
                    show();
                }
                break;
            default:
                MPLogUtil.log("ComponentSeek22[gone] => onWindowStateChanged => windowState = " + windowState + ", isLive = " + isLive + ", isFull = " + isFull);
                gone();
                break;
        }
    }

    @Override
    public void show() {
        MPLogUtil.log("ComponentSeek => show =>");
        mTimeMillis = System.currentTimeMillis();
        bringToFront();
        if (mShowBottomPB) {
            findViewById(R.id.module_mediaplayer_component_seek_pb).setVisibility(View.GONE);
        }
        findViewById(R.id.module_mediaplayer_component_seek_bg).setVisibility(View.VISIBLE);
        findViewById(R.id.module_mediaplayer_component_seek_sb).setVisibility(View.VISIBLE);
        findViewById(R.id.module_mediaplayer_component_seek_position).setVisibility(View.VISIBLE);
        findViewById(R.id.module_mediaplayer_component_seek_max).setVisibility(View.VISIBLE);
    }

    @Override
    public void gone() {
        MPLogUtil.log("ComponentSeek => gone =>");
        mTimeMillis = 0L;
        boolean isFull = mControllerWrapper.isFull();
        if (mShowBottomPB) {
            findViewById(R.id.module_mediaplayer_component_seek_pb).setVisibility(isFull ? View.VISIBLE : View.GONE);
        }
        findViewById(R.id.module_mediaplayer_component_seek_bg).setVisibility(View.GONE);
        findViewById(R.id.module_mediaplayer_component_seek_sb).setVisibility(View.GONE);
        findViewById(R.id.module_mediaplayer_component_seek_position).setVisibility(View.GONE);
        findViewById(R.id.module_mediaplayer_component_seek_max).setVisibility(View.GONE);
    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        onVisibilityChanged(!isLocked, null);
    }

    /****************************************/

    @Override
    public void onUpdateTimeMillis(@NonNull long seek, @NonNull long position, @NonNull long duration) {
        if (mTouch) return;
        if (position <= 0 || duration <= 0) return;
        SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
        if (null == seekBar) return;
        int v = seekBar.getVisibility();
        if (v != View.VISIBLE) return;
        long timeMillis = System.currentTimeMillis();
        long i = timeMillis - mTimeMillis;
        if (i > 10000) { // 10s
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
        if (mShowBottomPB) {
            ProgressBar progress = findViewById(R.id.module_mediaplayer_component_seek_pb);
            if (null == progress) {
                progress.setProgress(position);
                progress.setSecondaryProgress(position);
                if (duration > 0) {
                    progress.setMax(duration);
                }
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

        MPLogUtil.log("ComponentSeek => refreshText => position = " + position + ", strPosition = " + strPosition + ", duration = " + duration + ", strDuration = " + strDuration);

        TextView viewMax = findViewById(R.id.module_mediaplayer_component_seek_max);
        viewMax.setText(strDuration);
        TextView viewPosition = findViewById(R.id.module_mediaplayer_component_seek_position);
        viewPosition.setText(strPosition);
    }

    @Override
    public void onSeekPlaying(@NonNull int position) {
        if (null != mControllerWrapper) {
            mControllerWrapper.seekTo(true, position);
        }
    }
}
