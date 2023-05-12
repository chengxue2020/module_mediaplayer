package lib.kalu.mediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.Keep;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.core.component.ComponentComplete;
import lib.kalu.mediaplayer.core.component.ComponentError;
import lib.kalu.mediaplayer.core.component.ComponentInit;
import lib.kalu.mediaplayer.core.component.ComponentLoading;
import lib.kalu.mediaplayer.core.component.ComponentNet;
import lib.kalu.mediaplayer.core.component.ComponentPause;
import lib.kalu.mediaplayer.core.component.ComponentSeek;
import lib.kalu.mediaplayer.listener.OnPlayerChangeListener;
import lib.kalu.mediaplayer.util.MPLogUtil;
import lib.kalu.mediaplayer.widget.player.PlayerLayout;

/**
 * @description: 横屏全屏视频播放器
 * @date: 2021-05-25 10:37
 */
public final class TestActivity extends Activity {

    @Keep
    public static final int RESULT_CODE = 31001;
    @Keep
    public static final String INTENT_LIVE = "intent_live"; // live
    @Keep
    public static final String INTENT_MAX = "intent_max"; // max
    @Keep
    public static final String INTENT_SEEK = "intent_seek"; // 快进
    @Keep
    public static final String INTENT_DATA = "intent_data"; // 外部传入DATA
    @Keep
    public static final String INTENT_URL = "intent_url"; // 视频Url
    @Keep
    public static final String INTENT_SRT = "intent_srt"; // 字幕Url
    @Keep
    public static final String INTENT_TIME_BROWSING = "intent_time_browsing"; // 视频浏览时长
    @Keep
    public static final String INTENT_TIME_LENGTH = "intent_time_length"; // 视频总时长

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // back
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
            boolean full = playerLayout.isFull();
            Log.e("TestActivity", "dispatchKeyEvent => isFull = " + full);
            if (full) {
                stopFull();
                return true;
            } else {
                boolean floats = playerLayout.isFloat();
                Log.e("TestActivity", "dispatchKeyEvent => isFloat = " + floats);
                if (floats) {
                    stopFloat();
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.module_mediaplayer_test);

        initPlayer();
        startPlayer();

        // 跳转
        findViewById(R.id.module_mediaplayer_test_button0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, getUrl());
                startActivity(intent);
            }
        });
        // 换台
        findViewById(R.id.module_mediaplayer_test_button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlayer();
            }
        });
        // 全屏
        findViewById(R.id.module_mediaplayer_test_button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFull();
            }
        });
        // 浮动
        findViewById(R.id.module_mediaplayer_test_button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFloat();
            }
        });
        findViewById(R.id.module_mediaplayer_test_button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
                playerLayout.showComponent(ComponentNet.class);
            }
        });
        findViewById(R.id.module_mediaplayer_test_button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
                playerLayout.hideComponent(ComponentNet.class);
            }
        });
    }


    private void initPlayer() {
        // playerLayout
        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
        // loading
        ComponentLoading loading = new ComponentLoading(this);
        loading.setComponentText("加载中...");
        loading.setComponentBackgroundColorInt(Color.parseColor("#000000"));
        playerLayout.addComponent(loading);
        // complete
        ComponentComplete end = new ComponentComplete(this);
        playerLayout.addComponent(end);
        // error
        ComponentError error = new ComponentError(this);
        playerLayout.addComponent(error);
        // seek
        ComponentSeek bottom = new ComponentSeek(this);
        playerLayout.addComponent(bottom);
        // net
        ComponentNet speed = new ComponentNet(this);
        playerLayout.addComponent(speed);
        // init
        ComponentInit init = new ComponentInit(this);
        playerLayout.addComponent(init);
        // pause
        ComponentPause pause = new ComponentPause(this);
        playerLayout.addComponent(pause);

        playerLayout.setPlayerChangeListener(new OnPlayerChangeListener() {
            @Override
            public void onWindow(int playerState) {
                switch (playerState) {
                    case PlayerType.WindowType.NORMAL:
                        //普通模式
                        break;
                    case PlayerType.WindowType.FULL:
                        //全屏模式
                        break;
                    case PlayerType.WindowType.FLOAT:
                        //小屏模式
                        break;
                }
            }

            @Override
            public void onChange(int playState) {
                MPLogUtil.log("onPlayStateChanged => playState = " + playState);

                switch (playState) {
                    case PlayerType.StateType.STATE_INIT:
                        //播放未开始，初始化
                        break;
                    case PlayerType.StateType.STATE_START_ABORT:
                        //开始播放中止
                        break;
                    case PlayerType.StateType.STATE_LOADING_START:
                        //播放准备中
                        break;
                    case PlayerType.StateType.STATE_LOADING_STOP:
                        //播放准备就绪
                        break;
                    case PlayerType.StateType.STATE_ERROR:
                        //播放错误
                        break;
                    case PlayerType.StateType.STATE_BUFFERING_START:
                        //正在缓冲
                        break;
                    case PlayerType.StateType.STATE_START:
                        //正在播放
                        break;
                    case PlayerType.StateType.STATE_PAUSE:
                        //暂停播放
                        break;
                    case PlayerType.StateType.STATE_BUFFERING_STOP:
                        //暂停缓冲
                        break;
                    case PlayerType.StateType.STATE_END:
                        //播放完成
                        break;
                }
            }
        });
    }

    private String getUrl() {
        return getIntent().getStringExtra(INTENT_URL);
    }

    private void startPlayer() {

        String url = getUrl();
        if (null == url || url.length() == 0) {
            onBackPressed();
            return;
        }

        long seek = getIntent().getLongExtra(INTENT_SEEK, 0);
        long max = getIntent().getLongExtra(INTENT_MAX, 0);
        boolean live = getIntent().getBooleanExtra(INTENT_LIVE, false);
        MPLogUtil.log("TestActivity => onCreate => seek = " + seek + ", max = " + max + ", live = " + live + ", url = " + url);

        StartBuilder.Builder builder = new StartBuilder.Builder();
        builder.setSeek(seek);
        builder.setMax(max);
        builder.setLive(live);
        PlayerLayout videoLayout = findViewById(R.id.module_mediaplayer_test_video);
        videoLayout.start(builder.build(), url);
    }

    private void startFull() {
        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
        playerLayout.startFull();
    }

    private void stopFull() {
        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
        playerLayout.stopFull();
    }

    private void startFloat() {
        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
        playerLayout.startFloat();
    }

    private void stopFloat() {
        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
        playerLayout.stopFloat();
    }

    @Override
    public void finish() {

        PlayerLayout videoLayout = findViewById(R.id.module_mediaplayer_test_video);
        long browsing = videoLayout.getPosition() / 1000;
        if (browsing < 0) {
            browsing = 0;
        }
        long duration = videoLayout.getDuration() / 1000;
        if (duration < 0) {
            duration = 0;
        }
        String extra = getIntent().getStringExtra(INTENT_DATA);
        Intent intent = new Intent();
        intent.putExtra(INTENT_DATA, extra);
        intent.putExtra(INTENT_TIME_LENGTH, duration);
        intent.putExtra(INTENT_TIME_BROWSING, browsing);
        setResult(RESULT_CODE, intent);
        super.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PlayerLayout videoLayout = findViewById(R.id.module_mediaplayer_test_video);
        videoLayout.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        PlayerLayout videoLayout = findViewById(R.id.module_mediaplayer_test_video);
        videoLayout.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PlayerLayout videoLayout = findViewById(R.id.module_mediaplayer_test_video);
        videoLayout.release();
    }
}