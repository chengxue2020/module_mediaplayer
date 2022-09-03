package lib.kalu.mediaplayer.core.controller.component;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import lib.kalu.mediaplayer.core.controller.base.ControllerWrapper;
import lib.kalu.mediaplayer.core.controller.impl.ComponentApi;
import lib.kalu.mediaplayer.util.MediaLogUtil;

/**
 * description: 底部控制栏视图
 * created by kalu on 2021/11/23
 */
@Keep
public class ComponentSeek extends RelativeLayout implements ComponentApi {

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

    private void init() {
        // step1
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_seek, this, true);

        // step2, 5.1以下系统SeekBar高度需要设置成WRAP_CONTENT
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            ProgressBar progressBar = findViewById(R.id.module_mediaplayer_component_seek_pb);
            progressBar.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        // step3
        SeekBar sb = findViewById(R.id.module_mediaplayer_component_seek_sb);
        if (null != sb) {
            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
//                    if (null != mControllerWrapper) {
//                        mControllerWrapper.pause();
//                    }
//                    mControllerWrapper.stopFadeOut();
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
//                    if (null != mControllerWrapper) {
//                        mControllerWrapper.resume();
//                    }
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        seekPlayer(progress);
                    } else {
                        refreshPB(progress, 0);
                        long timestamp = getTimestamp();
                        long millis = System.currentTimeMillis();
                        if (millis - timestamp > 10000) {
                            gone();
                        }
                    }
                }
            });
        }
//        View viewFull = findViewById(R.id.module_mediaplayer_controller_bottom_full);
//        viewFull.setOnClickListener(this);
//        View viewPlayer = findViewById(R.id.module_mediaplayer_controller_bottom_play);
//        viewPlayer.setOnClickListener(this);
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
                MediaLogUtil.log("ComponentSeek22[show] => playState = " + playState + ", isLive = " + isLive + ", isFull = " + isFull);
                if (!isLive && isFull) {
                    refreshTimestamp(false);
                    bringToFront();
                    show();
                }
                break;
            case PlayerType.StateType.STATE_LOADING_START:
                MediaLogUtil.log("ComponentSeek22[gone] => playState = " + playState + ", isLive = " + isLive + ", isFull = " + isFull);
                refreshTimestamp(true);
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
                MediaLogUtil.log("ComponentSeek22[show] => onWindowStateChanged => windowState = " + windowState + ", isLive = " + isLive + ", isFull = " + isFull);
                if (!isLive && isFull) {
                    refreshTimestamp(false);
                    bringToFront();
                    show();
                }
                break;
            default:
                MediaLogUtil.log("ComponentSeek22[gone] => onWindowStateChanged => windowState = " + windowState + ", isLive = " + isLive + ", isFull = " + isFull);
                refreshTimestamp(true);
                gone();
                break;
        }
    }

    @Override
    public void show() {
        MediaLogUtil.log("ComponentSeek88 => show =>");
        findViewById(R.id.module_mediaplayer_component_seek_pb).setVisibility(View.GONE);
        findViewById(R.id.module_mediaplayer_component_seek_bg).setVisibility(View.VISIBLE);
        findViewById(R.id.module_mediaplayer_component_seek_sb).setVisibility(View.VISIBLE);
        findViewById(R.id.module_mediaplayer_component_seek_position).setVisibility(View.VISIBLE);
        findViewById(R.id.module_mediaplayer_component_seek_max).setVisibility(View.VISIBLE);
    }

    @Override
    public void gone() {
        MediaLogUtil.log("ComponentSeek88 => gone =>");
        boolean isFull = mControllerWrapper.isFull();
        findViewById(R.id.module_mediaplayer_component_seek_pb).setVisibility(isFull ? View.VISIBLE : View.GONE);
        findViewById(R.id.module_mediaplayer_component_seek_bg).setVisibility(View.GONE);
        findViewById(R.id.module_mediaplayer_component_seek_sb).setVisibility(View.GONE);
        findViewById(R.id.module_mediaplayer_component_seek_position).setVisibility(View.GONE);
        findViewById(R.id.module_mediaplayer_component_seek_max).setVisibility(View.GONE);
    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        onVisibilityChanged(!isLocked, null);
    }

    private void refreshTimestamp(boolean clean) {
        TextView textView = findViewById(R.id.module_mediaplayer_component_seek_timestamp);
        long millis = System.currentTimeMillis();
        textView.setText(clean ? "" : String.valueOf(millis));
    }

    private long getTimestamp() {
        try {
            TextView textView = findViewById(R.id.module_mediaplayer_component_seek_timestamp);
            CharSequence text = textView.getText();
            return Long.parseLong(String.valueOf(text));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /****************************************/

    @Override
    public void seekProgress(@NonNull boolean fromUser, @NonNull long position, @NonNull long duration) {

        MediaLogUtil.log("ComponentSeek => seekProgress => duration = " + duration + ", position = " + position);
        if (position <= 0 && duration <= 0)
            return;

        // sb
        refreshSB((int) position, (int) duration);
        // pb
        refreshPB((int) position, (int) duration);
        // text
        refreshText(position, duration);
    }

    @Override
    public boolean seekForward(boolean callback) {
        SeekBar sb = findViewById(R.id.module_mediaplayer_component_seek_sb);
        if (null != sb && sb.getVisibility() == View.VISIBLE) {
            int max = sb.getMax();
            int progress = sb.getProgress();
            if (progress < max) {
                int next = progress + Math.abs(max) / 200;
//                MediaLogUtil.log("ComponentSeek => seekForward =>  callback = " + callback + ", progress = " + progress + ", next = " + next + ", max = " + max);
                if (next > max) {
                    next = max;
                }
                refreshSB(next, 0);
                if (callback) {
                    seekPlayer(progress);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean seekRewind(boolean callback) {
        SeekBar sb = findViewById(R.id.module_mediaplayer_component_seek_sb);
//        MediaLogUtil.log("ComponentSeek => seekRewind => callback = " + callback);
        if (null != sb && sb.getVisibility() == View.VISIBLE) {
            int progress = sb.getProgress();
            if (progress > 0) {
                int max = sb.getMax();
                int next = progress - Math.abs(max) / 200;
                if (next < 0) {
                    next = 0;
                }
                refreshSB(next, 0);
                if (callback) {
                    seekPlayer(progress);
                }
                return true;
            }
        }
        return false;
    }

    private void refreshSB(int progress, int max) {
        SeekBar sb = findViewById(R.id.module_mediaplayer_component_seek_sb);
        if (null == sb)
            return;
        int visibility = sb.getVisibility();
        if (visibility != View.VISIBLE)
            return;
        sb.setProgress(progress);
        sb.setSecondaryProgress(progress);
        if (max > 0) {
            sb.setMax(max);
        }
    }

    private void refreshPB(int progress, int max) {
        ProgressBar pb = findViewById(R.id.module_mediaplayer_component_seek_pb);
        if (null == pb)
            return;
        pb.setProgress(progress);
        pb.setSecondaryProgress(progress);
        if (max > 0) {
            pb.setMax(max);
        }
    }

    private void refreshText(long position, long duration) {

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

        MediaLogUtil.log("ComponentSeek => refreshText => position = " + position + ", strPosition = " + strPosition + ", duration = " + duration + ", strDuration = " + strDuration);

        TextView viewMax = findViewById(R.id.module_mediaplayer_component_seek_max);
        viewMax.setText(strDuration);
        TextView viewPosition = findViewById(R.id.module_mediaplayer_component_seek_position);
        viewPosition.setText(strPosition);
    }

    private void seekPlayer(long progress) {
        refreshTimestamp(false);
        if (null != mControllerWrapper) {
            mControllerWrapper.seekTo(true, progress);
        }
    }
}
