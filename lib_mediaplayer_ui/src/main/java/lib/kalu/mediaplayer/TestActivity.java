package lib.kalu.mediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import lib.kalu.mediaplayer.core.component.ComponentSeek;
import lib.kalu.mediaplayer.core.component.ComponentSpeed;
import lib.kalu.mediaplayer.core.player.PlayerLayout;
import lib.kalu.mediaplayer.listener.OnPlayerChangeListener;
import lib.kalu.mediaplayer.util.MPLogUtil;

/**
 * @description: 横屏全屏视频播放器
 * @date: 2021-05-25 10:37
 */
public final class TestActivity extends Activity {

    @Keep
    public static final int RESULT_CODE = 31001;
    @Keep
    public static final String INTENT_PREPARE_IMAGE_RESOURCE = "intent_prepare_image_resource"; // loading image
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
    }


    private void initPlayer() {
        // control
        PlayerLayout videoLayout = findViewById(R.id.module_mediaplayer_test_video);
        videoLayout.setScaleType(PlayerType.ScaleType.SCREEN_SCALE_MATCH_PARENT);

        // 加载ui
        ComponentLoading loading = new ComponentLoading(this);
        loading.setMessage("加载中...");
        int resId = getIntent().getIntExtra(INTENT_PREPARE_IMAGE_RESOURCE, 0);
        loading.setBackgroundResource(resId);
        videoLayout.addComponent(loading);

        // 结束ui
        ComponentComplete end = new ComponentComplete(this);
//        loading.setMessage("加载中...");
        videoLayout.addComponent(end);

        // 错误ui
        ComponentError error = new ComponentError(this);
        error.setMessage("发生错误");
        videoLayout.addComponent(error);

        // 进度条ui
        ComponentSeek bottom = new ComponentSeek(this);
        videoLayout.addComponent(bottom);

        // 网速ui
        ComponentSpeed speed = new ComponentSpeed(this);
        videoLayout.addComponent(speed);

        // 初始化ui
        ComponentInit init = new ComponentInit(this);
        videoLayout.addComponent(init);

        videoLayout.setPlayerChangeListener(new OnPlayerChangeListener() {
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

            /**
             * 播放状态
             * -1               播放错误
             * 0                播放未开始
             * 1                播放准备中
             * 2                播放准备就绪
             * 3                正在播放
             * 4                暂停播放
             * 5                正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够后恢复播放)
             * 6                暂停缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停
             * 7                播放完成
             * 8                开始播放中止
             * @param playState                         播放状态，主要是指播放器的各种状态
             */
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

//                        Toast.makeText(getApplicationContext(), "start", Toast.LENGTH_SHORT).show();
//                        VideoLayout videoLayout = findViewById(R.id.module_mediaplayer_video);
//                        ControllerLayout controller = videoLayout.getControlLayout();
//                        SimpleSubtitleView subtitle = controller.findSubtitle();
//                        subtitle.bindToMediaPlayer(videoLayout);
//                        subtitle.start();

                        //播放准备就绪
                        break;
                    case PlayerType.StateType.STATE_ERROR:
                        //播放错误
                        break;
                    case PlayerType.StateType.STATE_BUFFERING_START:
                        //正在缓冲
                        break;
                    case PlayerType.StateType.STATE_START:

//                        SimpleSubtitleView subtitleView = findViewById(R.id.module_mediaplayer_subtitle);
                        // 绑定MediaPlayer
//                        subtitleView.bindToMediaPlayer(mp);
                        // 设置字幕
//                        String subtitle = getIntent().getStringExtra(INTENT_SRT);
//                        subtitleView.setSubtitlePath(subtitle);

//                        Toast.makeText(getApplicationContext(), "start", Toast.LENGTH_SHORT).show();
//                        VideoLayout videoLayout = findViewById(R.id.module_mediaplayer_video);
//                        ControllerLayout controller = videoLayout.getVideoController();
//                        SimpleSubtitleView subtitle = controller.findSubtitle();
//                        subtitle.start();

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

    private void startPlayer() {

        String url = getIntent().getStringExtra(INTENT_URL);
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
        builder.setDelay(100);
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

//    @Override
//    protected void onResume() {
//        super.onResume();
//        VideoLayout videoLayout = findViewById(R.id.module_mediaplayer_test);
//        videoLayout.resume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        VideoLayout videoLayout = findViewById(R.id.module_mediaplayer_test);
//        videoLayout.pause();
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        try {
//            android.os.Process.killProcess(android.os.Process.myPid());
//        } catch (Exception e) {
//            System.exit(0);
//        }
    }
}