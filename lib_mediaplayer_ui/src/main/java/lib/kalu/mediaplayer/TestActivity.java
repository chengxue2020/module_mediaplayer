package lib.kalu.mediaplayer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.Keep;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.util.Log;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.controller.ControllerEmpty;
import lib.kalu.mediaplayer.core.controller.component.ComponentEnd;
import lib.kalu.mediaplayer.core.controller.component.ComponentError;
import lib.kalu.mediaplayer.core.controller.component.ComponentLoading;
import lib.kalu.mediaplayer.core.controller.component.ComponentSeek;
import lib.kalu.mediaplayer.core.view.VideoLayout;
import lib.kalu.mediaplayer.listener.OnChangeListener;
import lib.kalu.mediaplayer.util.MediaLogUtil;

/**
 * @description: 横屏全屏视频播放器
 * @date: 2021-05-25 10:37
 */
public final class TestActivity extends AppCompatActivity {

    @Keep
    public static final int RESULT_CODE = 31001;
    @Keep
    public static final String INTENT_PREPARE_IMAGE_RESOURCE = "intent_prepare_image_resource"; // loading image
    @Keep
    public static final String INTENT_MAX = "intent_max"; // max
    @Keep
    public static final String INTENT_LOOP = "intent_loop"; // loop
    @Keep
    public static final String INTENT_SEEK = "intent_seek"; // 快进
    @Keep
    public static final String INTENT_DATA = "intent_data"; // 外部传入DATA
    @Keep
    public static final String INTENT_LIVE = "intent_live"; // 直播
    @Keep
    public static final String INTENT_URL = "intent_url"; // 视频Url
    @Keep
    public static final String INTENT_SRT = "intent_srt"; // 字幕Url
    @Keep
    public static final String INTENT_TIME_BROWSING = "intent_time_browsing"; // 视频浏览时长
    @Keep
    public static final String INTENT_TIME_LENGTH = "intent_time_length"; // 视频总时长

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.module_mediaplayer_test);

        String url = getIntent().getStringExtra(INTENT_URL);
        if (null == url || url.length() == 0) {
            onBackPressed();
            return;
        }

        ComponentLoading loading = new ComponentLoading(this);
        loading.setMessage("加载中...");
        loading.setMessageSize(20);

        ComponentEnd end = new ComponentEnd(this);
//        loading.setMessage("加载中...");
//        loading.setMessageSize(20);

        ComponentError error = new ComponentError(this);
        error.setMessage("发生错误");
        error.setMessageSize(20);

        ComponentSeek bottom = new ComponentSeek(this);

        // component
        ControllerEmpty controller = new ControllerEmpty(this);
        controller.addComponent(loading);
        controller.addComponent(error);
        controller.addComponent(end);
        controller.addComponent(bottom);

        // control
        VideoLayout videoLayout = findViewById(R.id.module_mediaplayer_test);
        videoLayout.setControllerLayout(controller);
        videoLayout.setScaleType(PlayerType.ScaleType.SCREEN_SCALE_MATCH_PARENT);

        // 设置视频播放链接地址
//        videoLayout.showNetWarning();
        // 全屏
//        videoLayout.startFullScreen();
        videoLayout.setOnChangeListener(new OnChangeListener() {
            /**
             * 播放模式
             * 普通模式，小窗口模式，正常模式三种其中一种
             * MODE_NORMAL              普通模式
             * MODE_FULL_SCREEN         全屏模式
             * MODE_TINY_WINDOW         小屏模式
             * @param playerState                       播放模式
             */
            @Override
            public void onWindow(int playerState) {
                switch (playerState) {
                    case PlayerType.WindowType.NORMAL:
                        onBackPressed();
                        //普通模式
                        break;
                    case PlayerType.WindowType.FULL:
                        //全屏模式
                        break;
                    case PlayerType.WindowType.TINY:
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
                MediaLogUtil.setIsLog(true);
                MediaLogUtil.log("onPlayStateChanged => playState = " + playState);

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

        // 开始播放
        long seek = getIntent().getLongExtra(INTENT_SEEK, 0);
        long max = getIntent().getLongExtra(INTENT_MAX, 0);
        boolean loop = getIntent().getBooleanExtra(INTENT_LOOP, false);
        MediaLogUtil.log("TestActivity => onCreate => seek = " + seek + ", max = " + max + ", url = " + url);
        videoLayout.start(seek, max, loop, url);
    }

    @Override
    public void finish() {

        VideoLayout videoLayout = findViewById(R.id.module_mediaplayer_test);
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
        VideoLayout videoLayout = findViewById(R.id.module_mediaplayer_test);
        videoLayout.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        VideoLayout videoLayout = findViewById(R.id.module_mediaplayer_test);
        videoLayout.pause();
    }

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