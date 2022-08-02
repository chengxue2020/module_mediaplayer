package lib.kalu.mediaplayer.core.controller.component;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
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
import lib.kalu.mediaplayer.core.controller.base.ControllerWrapper;
import lib.kalu.mediaplayer.config.player.PlayerConfig;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.controller.impl.ComponentApi;
import lib.kalu.mediaplayer.util.MediaLogUtil;
import lib.kalu.mediaplayer.util.PlayerUtils;

/**
 * description: 底部控制栏视图
 * created by kalu on 2021/11/23
 */
@Keep
public class ComponentBottom extends RelativeLayout implements ComponentApi {

    protected ControllerWrapper mControllerWrapper;

    public ComponentBottom(@NonNull Context context) {
        super(context);
        init();
    }

    public ComponentBottom(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ComponentBottom(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // step1
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_bottom, this, true);

        // step2, 5.1以下系统SeekBar高度需要设置成WRAP_CONTENT
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            ProgressBar progressBar = findViewById(R.id.module_mediaplayer_component_bottom_pb);
            progressBar.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        // step3
        View play = findViewById(R.id.module_mediaplayer_component_bottom_play);
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
        View back = findViewById(R.id.module_mediaplayer_component_bottom_back);
        if (null != back) {
            back.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "back", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // step5
        SeekBar sb = findViewById(R.id.module_mediaplayer_component_bottom_sb);
        if (null != sb) {
            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    MediaLogUtil.log("ComponentBottom => onStartTrackingTouch => mControllerWrapper = " + mControllerWrapper);
//        // sb开始拖动
//        SeekBar sb = findViewById(R.id.module_mediaplayer_component_bottom_sb);
//        if (null != sb) {
//            sb.setTag(1);
//        }
//        // pb
//        ProgressBar pb = findViewById(R.id.module_mediaplayer_component_bottom_pb);
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
                    MediaLogUtil.log("ComponentBottom => onStopTrackingTouch => mControllerWrapper = " + mControllerWrapper);
//        // sb结束拖动
//        SeekBar sb = findViewById(R.id.module_mediaplayer_component_bottom_sb);
//        if (null != sb) {
//            sb.setTag(null);
//        }
//        // pb
//        ProgressBar pb = findViewById(R.id.module_mediaplayer_component_bottom_pb);
//        if (null != pb) {
//            pb.setTag(null);
//        }
                    if (null != mControllerWrapper) {
                        mControllerWrapper.resume();
                    }
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    MediaLogUtil.log("ComponentBottom => onProgressChanged => progress = " + progress + ", fromUser = " + fromUser + ", mControllerWrapper = " + mControllerWrapper);
                    if (!fromUser)
                        return;
                    if (null == mControllerWrapper)
                        return;
                    long max = mControllerWrapper.getMax();
                    boolean looping = mControllerWrapper.isLooping();
                    mControllerWrapper.seekTo(true, progress, max, looping);
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
        ProgressBar pb = findViewById(R.id.module_mediaplayer_component_bottom_pb);
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
                MediaLogUtil.log("ComponentBottom[show] => playState = " + playState);
                bringToFront();
                setVisibility(View.VISIBLE);
                break;
            case PlayerType.StateType.STATE_LOADING_START:
                MediaLogUtil.log("ComponentBottom[gone] => playState = " + playState);
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
    public void setProgress(@NonNull long position, @NonNull long duration) {

        int visibility = getVisibility();
//        MediaLogUtil.log("ComponentBottom => setProgress => duration = " + duration + ", position = " + position + ", visibility = " + visibility);
        if (visibility != View.VISIBLE)
            return;

        // pb
        ProgressBar pb = findViewById(R.id.module_mediaplayer_component_bottom_pb);
        if (null != pb && pb.getVisibility() == View.VISIBLE) {
            pb.setMax((int) duration);
            pb.setProgress((int) position);
            pb.setSecondaryProgress((int) position);
        }

        // sb
        SeekBar sb = findViewById(R.id.module_mediaplayer_component_bottom_sb);
        if (null != sb && sb.getVisibility() == View.VISIBLE) {
            sb.setMax((int) duration);
            sb.setProgress((int) position);
            sb.setSecondaryProgress((int) position);
        }

        // time
        TextView time = findViewById(R.id.module_mediaplayer_component_bottom_time);
        if (null != time) {

            // 1
            // ms => s
            long c = position / 1000;
            long c1 = c / 60;
            long c2 = c % 60;
            StringBuilder stringBuilder = new StringBuilder();
            if (c1 < 10) {
                stringBuilder.append("0");
            }
            stringBuilder.append(c1);
            stringBuilder.append(":");
            if (c2 < 10) {
                stringBuilder.append("0");
            }
            stringBuilder.append(c2);
            stringBuilder.append("/");

            // 2
            // ms => s
            long d = duration / 1000;
            long d1 = d / 60;
            long d2 = d % 60;
            if (d1 < 10) {
                stringBuilder.append("0");
            }
            stringBuilder.append(d1);
            stringBuilder.append(":");
            if (d2 < 10) {
                stringBuilder.append("0");
            }
            stringBuilder.append(d2);
            String s = stringBuilder.toString();
            time.setText(s);
        }
    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        onVisibilityChanged(!isLocked, null);
    }

    /**
     * 横竖屏切换
     */
    private void toggleFullScreen() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        mControllerWrapper.toggleFullScreen(activity);
    }
}
