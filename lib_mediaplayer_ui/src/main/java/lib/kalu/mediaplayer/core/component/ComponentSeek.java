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
                if (playerApi.isLive())
                    throw new Exception("living error: true");
                seekForward(KeyEvent.ACTION_UP);
                if (playerApi.isPlaying())
                    throw new Exception("playing waining: true");
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
                if (playerApi.isLive())
                    throw new Exception("living error: true");
                seekForward(KeyEvent.ACTION_UP);
                if (playerApi.isPlaying())
                    throw new Exception("playing waining: true");
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
            case PlayerType.StateType.STATE_LOADING_STOP:
            case PlayerType.StateType.STATE_BUFFERING_STOP:
            case PlayerType.StateType.STATE_FAST_FORWARD_STOP:
            case PlayerType.StateType.STATE_FAST_REWIND_STOP:
                MPLogUtil.log("ComponentSeek => callPlayerEvent => gone => playState = " + playState);
                gone();
                break;
            case PlayerType.StateType.STATE_INIT:
            case PlayerType.StateType.STATE_ERROR:
            case PlayerType.StateType.STATE_ERROR_IGNORE:
            case PlayerType.StateType.STATE_END:
                onUpdateTimeMillis(0, 0, 0);
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
                throw new Exception("full error: false");
            setTag(R.id.module_mediaplayer_component_seek_sb, true);
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
            setTag(R.id.module_mediaplayer_component_seek_sb, false);
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
                throw new Exception("seekbar error: null");
            Object tag = getTag(R.id.module_mediaplayer_component_seek_sb);
            if (null != tag && ((boolean) tag))
                throw new Exception("seekbar warning: user current action down");
            onSeekUpdateProgress(position, duration, true);
        } catch (Exception e) {
            MPLogUtil.log("ComponentSeek => onUpdateTimeMillis => " + e.getMessage());
        }
    }

    @Override
    public void seekForward(int action) {
        try {
            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
            if (null == seekBar)
                throw new Exception("seekbar error: null");
            if (seekBar.getVisibility() != View.VISIBLE)
                throw new Exception("visabliity error: show");
            int max = seekBar.getMax();
            int progress = seekBar.getProgress();
            if (max <= 0)
                throw new Exception("max error: " + max);
            // action_down
            if (action == KeyEvent.ACTION_DOWN) {
                if (progress >= max)
                    throw new Exception("error: not progress>=max");
                int next = progress + Math.abs(max) / 200;
                if (next > max) {
                    next = max;
                }
                getPlayerApi().callPlayerEvent(PlayerType.StateType.STATE_FAST_FORWARD_START);
                onSeekUpdateProgress(next, max, true);
            }
            // action_up
            else if (action == KeyEvent.ACTION_UP) {
                if (progress >= max) {
                    progress = max;
                }
                getPlayerApi().callPlayerEvent(PlayerType.StateType.STATE_FAST_FORWARD_STOP);
                onSeekTo(progress);
            }
            // error
            else {
                getPlayerApi().callPlayerEvent(PlayerType.StateType.STATE_FAST_FORWARD_STOP);
                throw new Exception("error: not find");
            }

        } catch (Exception e) {
            MPLogUtil.log("ComponentSeek => seekForward => " + e.getMessage());
        }
    }


    @Override
    public void seekRewind(int action) {
        try {
            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
            if (null == seekBar)
                throw new Exception("seekbar error: null");
            if (seekBar.getVisibility() != View.VISIBLE)
                throw new Exception("visibility error: show");
            int max = seekBar.getMax();
            int progress = seekBar.getProgress();
            if (max <= 0)
                throw new Exception("error: max <= 0 || progress <= 0");
            // action_down
            if (action == KeyEvent.ACTION_DOWN) {
                if (progress <= 0)
                    throw new Exception("progress warning: " + progress);
                int next = progress - Math.abs(max) / 200;
                if (next < 0) {
                    next = 0;
                }
                getPlayerApi().callPlayerEvent(PlayerType.StateType.STATE_FAST_REWIND_START);
                onSeekUpdateProgress(next, max, true);
            }
            // action_up
            else if (action == KeyEvent.ACTION_UP) {
                if (progress < 0) {
                    progress = 0;
                }
                getPlayerApi().callPlayerEvent(PlayerType.StateType.STATE_FAST_REWIND_STOP);
                onSeekTo(progress);
            }
            // error
            else {
                getPlayerApi().callPlayerEvent(PlayerType.StateType.STATE_FAST_REWIND_STOP);
                throw new Exception("error: not find");
            }

        } catch (Exception e) {
            MPLogUtil.log("ComponentSeek => seekForward => " + e.getMessage());
        }
    }

    @Override
    public void onSeekUpdateProgress(@NonNull long position, @NonNull long duration, @NonNull boolean updateTime) {
        try {
            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
            if (null == seekBar)
                throw new Exception();
            seekBar.setProgress((int) position);
            seekBar.setSecondaryProgress((int) position);
            seekBar.setMax((int) duration);
        } catch (Exception e) {
        }

        try {
            if (!updateTime)
                throw new Exception();
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
        } catch (Exception e) {
            TextView viewMax = findViewById(R.id.module_mediaplayer_component_seek_max);
            viewMax.setText("00:00");
            TextView viewPosition = findViewById(R.id.module_mediaplayer_component_seek_position);
            viewPosition.setText("00:00");
        }
    }

    @Override
    public void onSeekTo(@NonNull int position) {
        try {
            PlayerApi playerApi = getPlayerApi();
            if (null == playerApi)
                throw new Exception("playerApi error null");
            playerApi.seekTo(true, position);
        } catch (Exception e) {
            MPLogUtil.log("ComponentSeek => onSeekTo => " + e.getMessage());
        }
    }
}
