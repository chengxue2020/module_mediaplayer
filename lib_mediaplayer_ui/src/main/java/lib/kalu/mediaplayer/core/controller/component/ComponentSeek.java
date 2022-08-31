package lib.kalu.mediaplayer.core.controller.component;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
        View play = findViewById(R.id.module_mediaplayer_component_seek_play);
        if (null != play) {
            play.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mControllerWrapper) {
                        boolean playing = mControllerWrapper.isPlaying();
                        if (playing) {
                            mControllerWrapper.pause();
                        } else {
                            mControllerWrapper.resume();
                        }
                    }
                }
            });
        }

        // step4
        View back = findViewById(R.id.module_mediaplayer_component_seek_back);
        if (null != back) {
            back.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "back", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // step5
        SeekBar sb = findViewById(R.id.module_mediaplayer_component_seek_sb);
        if (null != sb) {
            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    MediaLogUtil.log("ComponentSeek => onStartTrackingTouch => mControllerWrapper = " + mControllerWrapper);
//        // sb开始拖动
//        SeekBar sb = findViewById(R.id.module_mediaplayer_component_seek_sb);
//        if (null != sb) {
//            sb.setTag(1);
//        }
//        // pb
//        ProgressBar pb = findViewById(R.id.module_mediaplayer_component_seek_pb);
//        if (null != pb) {
//            pb.setTag(1);
//        }
                    if (null != mControllerWrapper) {
                        mControllerWrapper.pause();
                    }
                    mControllerWrapper.stopFadeOut();
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    MediaLogUtil.log("ComponentSeek => onStopTrackingTouch => mControllerWrapper = " + mControllerWrapper);
//        // sb结束拖动
//        SeekBar sb = findViewById(R.id.module_mediaplayer_component_seek_sb);
//        if (null != sb) {
//            sb.setTag(null);
//        }
//        // pb
//        ProgressBar pb = findViewById(R.id.module_mediaplayer_component_seek_pb);
//        if (null != pb) {
//            pb.setTag(null);
//        }
                    if (null != mControllerWrapper) {
                        mControllerWrapper.resume();
                    }
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    MediaLogUtil.log("ComponentSeek => onProgressChanged => progress = " + progress + ", fromUser = " + fromUser + ", mControllerWrapper = " + mControllerWrapper);
                    onSeekChanged(progress, fromUser);
                }
            });
        }
//        View viewFull = findViewById(R.id.module_mediaplayer_controller_bottom_full);
//        viewFull.setOnClickListener(this);
//        View viewPlayer = findViewById(R.id.module_mediaplayer_controller_bottom_play);
//        viewPlayer.setOnClickListener(this);
    }

    /**
     * 是否显示底部进度条，默认显示
     */
    public void showBottomProgress(boolean show) {
        ProgressBar pb = findViewById(R.id.module_mediaplayer_component_seek_pb);
        if (null == pb)
            return;
        pb.setVisibility(show ? View.VISIBLE : View.GONE);
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
//        View view = findViewById(R.id.module_mediaplayer_controller_bottom_progress);
//        View viewRoot = findViewById(R.id.module_mediaplayer_controller_bottom_root);
//        if (isVisible) {
//            viewRoot.setVisibility(VISIBLE);
//            if (anim != null) {
//                viewRoot.startAnimation(anim);
//            }
//            if (mIsShowBottomProgress) {
//                view.setVisibility(GONE);
//            }
//        } else {
//            viewRoot.setVisibility(GONE);
//            if (anim != null) {
//                viewRoot.startAnimation(anim);
//            }
//            if (mIsShowBottomProgress) {
//                view.setVisibility(VISIBLE);
//                AlphaAnimation animation = new AlphaAnimation(0f, 1f);
//                animation.setDuration(300);
//                view.startAnimation(animation);
//            }
//        }
    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_LOADING_STOP:
                MediaLogUtil.log("ComponentSeek[show] => playState = " + playState);
                bringToFront();
                setVisibility(View.VISIBLE);
                break;
            case PlayerType.StateType.STATE_LOADING_START:
                MediaLogUtil.log("ComponentSeek[gone] => playState = " + playState);
                setVisibility(View.GONE);
                break;
        }

//        View viewPlayer = findViewById(R.id.module_mediaplayer_controller_bottom_play);
//        View viewRoot = findViewById(R.id.module_mediaplayer_controller_bottom_root);
//        ProgressBar progressBar = findViewById(R.id.module_mediaplayer_controller_bottom_progress);
//        switch (playState) {
//            case PlayerType.StateType.STATE_INIT:
//            case PlayerType.StateType.STATE_BUFFERING_START:
//                setVisibility(GONE);
//                progressBar.setProgress(0);
//                progressBar.setSecondaryProgress(0);
//                SeekBar seekBar = findViewById(R.id.module_mediaplayer_controller_bottom_seek);
//                seekBar.setProgress(0);
//                seekBar.setSecondaryProgress(0);
//                break;
//            case PlayerType.StateType.STATE_START_ABORT:
//            case PlayerType.StateType.STATE_LOADING_START:
//            case PlayerType.StateType.STATE_LOADING_STOP:
//            case PlayerType.StateType.STATE_ERROR:
//            case PlayerType.StateType.STATE_ONCE_LIVE:
//                setVisibility(GONE);
//                break;
//            case PlayerType.StateType.STATE_START:
//                viewPlayer.setSelected(true);
//                if (mIsShowBottomProgress) {
//                    if (mControllerWrapper.isShowing()) {
//                        progressBar.setVisibility(GONE);
//                        viewRoot.setVisibility(VISIBLE);
//                    } else {
//                        viewRoot.setVisibility(GONE);
//                        progressBar.setVisibility(VISIBLE);
//                    }
//                } else {
//                    viewRoot.setVisibility(GONE);
//                }
//                setVisibility(VISIBLE);
//                //开始刷新进度
//                mControllerWrapper.startProgress();
//                break;
//            case PlayerType.StateType.STATE_PAUSE:
//                viewPlayer.setSelected(false);
//                break;
//            case PlayerType.StateType.STATE_BUFFERING_STOP:
//            case PlayerType.StateType.STATE_END:
//                viewPlayer.setSelected(mControllerWrapper.isPlaying());
//                break;
//    }
    }

    @Override
    public void onWindowStateChanged(int playerState) {
//        View viewFull = findViewById(R.id.module_mediaplayer_controller_bottom_full);
//        View viewRoot = findViewById(R.id.module_mediaplayer_controller_bottom_root);
//        ProgressBar progressBar = findViewById(R.id.module_mediaplayer_controller_bottom_progress);
//        switch (playerState) {
//            case PlayerType.WindowType.NORMAL:
//                viewFull.setSelected(false);
//                break;
//            case PlayerType.WindowType.FULL:
//                viewFull.setSelected(true);
//                break;
//        }
//
//        Activity activity = PlayerUtils.scanForActivity(getContext());
//        if (activity != null && mControllerWrapper.hasCutout()) {
//            int orientation = activity.getRequestedOrientation();
//            int cutoutHeight = mControllerWrapper.getCutoutHeight();
//            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//                viewRoot.setPadding(0, 0, 0, 0);
//                progressBar.setPadding(0, 0, 0, 0);
//            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//                viewRoot.setPadding(cutoutHeight, 0, 0, 0);
//                progressBar.setPadding(cutoutHeight, 0, 0, 0);
//            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
//                viewRoot.setPadding(0, 0, cutoutHeight, 0);
//                progressBar.setPadding(0, 0, cutoutHeight, 0);
//            }
//        }
    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        onVisibilityChanged(!isLocked, null);
    }


    /****************************************/

    @Override
    public void seekProgress(@NonNull boolean fromUser, @NonNull long position, @NonNull long duration) {

        MediaLogUtil.log("ComponentSeek => seekProgress => duration = " + duration + ", position = " + position);
        if (position <= 0 && duration <= 0)
            return;

        int visibility = getVisibility();
        if (visibility != View.VISIBLE)
            return;

        // pb
        ProgressBar pb = findViewById(R.id.module_mediaplayer_component_seek_pb);
        if (null != pb && pb.getVisibility() == View.VISIBLE) {
            refreshProgress((int) position, (int) duration);
        }

        // sb
        SeekBar sb = findViewById(R.id.module_mediaplayer_component_seek_sb);
        if (null != sb && sb.getVisibility() == View.VISIBLE) {
            refreshSeek((int) position, (int) duration);
        }

        // time
        TextView viewMax = findViewById(R.id.module_mediaplayer_component_seek_max);
        TextView viewPosition = findViewById(R.id.module_mediaplayer_component_seek_position);
        // 1
        // ms => s
        long c = position / 1000;
        long c1 = c / 60;
        long c2 = c % 60;
        StringBuilder builder1 = new StringBuilder();
        if (c1 < 10) {
            builder1.append("0");
        }
        builder1.append(c1);
        builder1.append(":");
        if (c2 < 10) {
            builder1.append("0");
        }
        builder1.append(c2);
        String s1 = builder1.toString();
        viewPosition.setText(s1);

        // 2
        // ms => s
        StringBuilder builder2 = new StringBuilder();
        long d = duration / 1000;
        long d1 = d / 60;
        long d2 = d % 60;
        if (d1 < 10) {
            builder2.append("0");
        }
        builder2.append(d1);
        builder2.append(":");
        if (d2 < 10) {
            builder2.append("0");
        }
        builder2.append(d2);
        String s2 = builder2.toString();
        viewMax.setText(s2);
        MediaLogUtil.log("ComponentSeek => seekProgress => s1 = " + s1 + ", s2 = " + s2);
    }

    @Override
    public void seekForward(boolean callback) {
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
                refreshSeek(next, 0);
                if (callback) {
                    onSeekChanged(progress, true);
                }
            }
        }
    }

    @Override
    public void seekRewind(boolean callback) {
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
                refreshSeek(next, 0);
                if (callback) {
                    onSeekChanged(progress, true);
                }
            }
        }
    }

    /****************************************/

//    private final Handler mHandler = new Handler(this);

//    @Override
//    public boolean handleMessage(@NonNull Message msg) {
//        if (msg.what == 0x123456) {
//            setVisibility(View.GONE);
//        }
//        return false;
//    }

    private boolean isOK = true;

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }

    private void autoGone() {
//        mHandler.removeCallbacksAndMessages(null);
//        mHandler.sendEmptyMessageDelayed(0x123456, 10000);
//        postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (isOK) {
//                    setVisibility(View.GONE);
//                }
//            }
//        }, 10000);
    }

    private void onSeekChanged(int progress, boolean fromUser) {
        if (!fromUser)
            return;
        if (null == mControllerWrapper)
            return;
        long max = mControllerWrapper.getMax();
        boolean looping = mControllerWrapper.isLooping();
        mControllerWrapper.seekTo(true, progress, max, looping);
    }

    private void refreshSeek(int progress, int max) {
        SeekBar sb = findViewById(R.id.module_mediaplayer_component_seek_sb);
        if (null == sb)
            return;
        sb.setProgress(progress);
        sb.setSecondaryProgress(progress);
        if (max > 0) {
            sb.setMax(max);
        }
    }

    private void refreshProgress(int progress, int max) {
        ProgressBar pb = findViewById(R.id.module_mediaplayer_component_seek_pb);
        if (null == pb)
            return;
        pb.setProgress(progress);
        pb.setSecondaryProgress(progress);
        if (max > 0) {
            pb.setMax(max);
        }
    }
}
