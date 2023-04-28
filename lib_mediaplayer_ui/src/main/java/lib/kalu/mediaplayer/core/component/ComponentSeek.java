package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.player.PlayerApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

@Keep
public final class ComponentSeek extends RelativeLayout implements ComponentApi {

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
//                    if (fromUser) {
//                        onSeekTo(progress);
//                    } else if (!mTouch) {
//                        onSeekProgressUpdate(progress, 0);
//                    }
                }
            });
        } catch (Exception e) {
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return dispatchKeyEventComponent(event) || super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchKeyEventComponent(KeyEvent event) {
        // seekForward => start
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            try {
                PlayerApi playerApi = getPlayerApi();
                if (null == playerApi)
                    throw new Exception("playerApi error: null");
                playerApi.callPlayerEvent(PlayerType.StateType.STATE_FAST_FORWARD_START);
                if (getPlayerApi().isLive())
                    throw new Exception("living error: true");
                seekForward(KeyEvent.ACTION_DOWN);
            } catch (Exception e) {
                MPLogUtil.log("ComponentSeek => dispatchKeyEventComponent => " + e.getMessage());
            }
            return true;
        }
        // seekRewind => start
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            try {
                PlayerApi playerApi = getPlayerApi();
                if (null == playerApi)
                    throw new Exception("playerApi error: null");
                playerApi.callPlayerEvent(PlayerType.StateType.STATE_FAST_REWIND_START);
                if (getPlayerApi().isLive())
                    throw new Exception("living error: true");
                seekRewind(KeyEvent.ACTION_DOWN);
            } catch (Exception e) {
                MPLogUtil.log("ComponentSeek => dispatchKeyEventComponent => " + e.getMessage());
            }
            return true;
        }
        // seekForward => complete
        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            try {
                PlayerApi playerApi = getPlayerApi();
                if (null == playerApi)
                    throw new Exception("playerApi error: null");
                playerApi.callPlayerEvent(PlayerType.StateType.STATE_FAST_FORWARD_STOP);
                if (playerApi.isLive())
                    throw new Exception("living error: true");
                seekForward(KeyEvent.ACTION_DOWN);
                if (playerApi.isPlaying())
                    throw new Exception("playing error: true");
                playerApi.resume();
            } catch (Exception e) {
                MPLogUtil.log("ComponentSeek => dispatchKeyEventComponent => " + e.getMessage());
            }
            return true;
        }
        // seekRewind => complete
        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            try {
                PlayerApi playerApi = getPlayerApi();
                if (null == playerApi)
                    throw new Exception("playerApi error: null");
                playerApi.callPlayerEvent(PlayerType.StateType.STATE_FAST_REWIND_STOP);
                if (playerApi.isLive())
                    throw new Exception("living error: true");
                seekForward(KeyEvent.ACTION_DOWN);
                if (playerApi.isPlaying())
                    throw new Exception("playing error: true");
                playerApi.resume();
            } catch (Exception e) {
                MPLogUtil.log("ComponentSeek => dispatchKeyEventComponent => " + e.getMessage());
            }
            return true;
        }
        return false;
    }

    @Override
    public void callPlayerEvent(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_FAST_FORWARD_START:
            case PlayerType.StateType.STATE_FAST_REWIND_START:
                MPLogUtil.log("ComponentSeek => callPlayerEvent => show => playState = " + playState);
                show();
                break;
            case PlayerType.StateType.STATE_INIT:
            case PlayerType.StateType.STATE_ERROR:
            case PlayerType.StateType.STATE_ERROR_IGNORE:
            case PlayerType.StateType.STATE_LOADING_STOP:
            case PlayerType.StateType.STATE_BUFFERING_STOP:
            case PlayerType.StateType.STATE_FAST_FORWARD_STOP:
            case PlayerType.StateType.STATE_FAST_REWIND_STOP:
                MPLogUtil.log("ComponentSeek => callPlayerEvent => gone => playState = " + playState);
                gone();
                break;
        }
    }

    @Override
    public void callWindowEvent(int windowState) {
        switch (windowState) {
            default:
                gone();
                break;
        }
    }

    @Override
    public void show() {
        try {
            PlayerApi playerApi = getPlayerApi();
            if (null == playerApi)
                throw new Exception("playerApi error: null");
            if (!playerApi.isFull())
                throw new Exception("full error: true");
            bringToFront();
            findViewById(R.id.module_mediaplayer_component_seek_bg).setVisibility(View.VISIBLE);
            findViewById(R.id.module_mediaplayer_component_seek_seekbar).setVisibility(View.VISIBLE);
        } catch (Exception e) {
            MPLogUtil.log("ComponentSeek => show => " + e.getMessage());
        }
    }

    @Override
    public void gone() {
        try {
            findViewById(R.id.module_mediaplayer_component_seek_bg).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_seek_seekbar).setVisibility(View.GONE);
        } catch (Exception e) {
            MPLogUtil.log("ComponentSeek => gone => " + e.getMessage());
        }
    }

    @Override
    public void onUpdateTimeMillis(@NonNull long seek, @NonNull long position, @NonNull long duration) {

        try {
            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
            if (null == seekBar)
                throw new Exception();
            if (position <= 0 || duration <= 0)
                throw new Exception();
            onSeekProgressUpdate((int) position, (int) duration);
            onSeekTimeUpdate((int) position, (int) duration);
        } catch (Exception e) {
        }
    }

    @Override
    public void seekForward(int action) {
        try {
            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
            if (null == seekBar)
                throw new Exception();
            if (seekBar.getVisibility() != View.VISIBLE)
                throw new Exception();
            int max = seekBar.getMax();
            int progress = seekBar.getProgress();
            // action_down
            if (action == KeyEvent.ACTION_DOWN) {
                if (progress >= max)
                    throw new Exception();
                int next = progress + Math.abs(max) / 200;
                if (next > max) {
                    next = max;
                }
                onSeekProgressUpdate(next, 0);
            }
            // action_up
            else if (action == KeyEvent.ACTION_UP) {
                if (max <= 0 || progress <= 0 && progress > max)
                    throw new Exception();
                onSeekTo(progress);
            }
            // error
            else {
                throw new Exception();
            }

        } catch (Exception e) {
        }
    }


    @Override
    public void seekRewind(int action) {
        try {
            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
            if (null == seekBar)
                throw new Exception();
            if (seekBar.getVisibility() != View.VISIBLE)
                throw new Exception();
            int max = seekBar.getMax();
            int progress = seekBar.getProgress();
            // action_down
            if (action == KeyEvent.ACTION_DOWN) {
                if (progress >= max)
                    throw new Exception();
                int next = progress - Math.abs(max) / 200;
                if (next < 0) {
                    next = 0;
                }
                onSeekProgressUpdate(next, 0);
            }
            // action_up
            else if (action == KeyEvent.ACTION_UP) {
                if (max <= 0 || progress <= 0 && progress > max)
                    throw new Exception();
                onSeekTo(progress);
            }
            // error
            else {
                throw new Exception();
            }

        } catch (Exception e) {
        }
    }

    @Override
    public void onSeekProgressUpdate(@NonNull int position, @NonNull int duration) {
        try {
            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
            if (null == seekBar)
                throw new Exception();
            if (seekBar.getVisibility() != View.VISIBLE)
                throw new Exception();
            seekBar.setProgress(position);
            seekBar.setSecondaryProgress(position);
            if (duration < 0)
                throw new Exception();
            seekBar.setMax(duration);
        } catch (Exception e) {
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
    public void onSeekTo(@NonNull int position) {
        try {
            MPLogUtil.log("ComponentSeek => onSeekTo => 1");
            PlayerApi playerApi = getPlayerApi();
            if (null == playerApi)
                throw new Exception("playerApi error null");
            playerApi.seekTo(true, position);
        } catch (Exception e) {
            MPLogUtil.log("ComponentSeek => onSeekTo => " + e.getMessage());
        }
    }
}
